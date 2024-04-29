<script setup lang="ts">
import {onMounted, ref, computed} from "vue";
import {Transaction} from "src/interfaces/transactions.interface";
import DialogCreateTransaction from "components/Groups/DialogCreateTransaction.vue";
import {useQuasar} from "quasar";
import {api} from "boot/axios";
import {formatDate, formatNumber} from "stores/globalFunctionsStore";
import DialogConsultTransaction from "components/Groups/DialogConsultTransaction.vue";
import {useRouter} from "vue-router";


let  tab = ref('transactions')
const transactionList = ref<Transaction[]>([]);
let sortedTransactionList = ref<Transaction[]>([]);
let dialogCreateTransaction = ref(false);
let dialogConsultTransaction = ref(false);
let currentSort = ref('date');
const $q = useQuasar();
let catList = ref([]);
const router = useRouter();


const props = defineProps({
  groupId: Number,
  userId: Boolean,
});

onMounted(async () => {
  await getTransactionList()
  await getCat();
});

async function getTransactionList(){
  const response = await api.get(`groups/${props.groupId}/transactions`);
  transactionList.value = response.data;
  sortTransaction('date');
}

function openDialogCreateTransaction(){
  dialogCreateTransaction.value = true;
  $q.dialog({
    component: DialogCreateTransaction,

    componentProps: {
      isOpen: dialogCreateTransaction,
      groupId: props.groupId,
      userId: props.userId,
    }
  }).onOk(() => {
    console.log('OK')
  }).onCancel(() => {
    console.log('Cancel')
  }).onDismiss(() => {
    dialogCreateTransaction.value = false;
    getTransactionList();
  })
}


function openDialogConsultTransaction(transactionId:number){
  dialogConsultTransaction.value = true;
  $q.dialog({
    component: DialogConsultTransaction,

    componentProps: {
      isOpen: dialogConsultTransaction,
      groupId: props.groupId,
      userId: props.userId,
      transactionId: transactionId
    }
  }).onDismiss(() => {
    router.push(`/groups/${props.groupId}`);
    dialogConsultTransaction.value = false;
  })
}

const sortTransaction = (type:string) => {
  if(type === 'date'){
    sortedTransactionList.value = transactionList.value.sort((a, b) => {
      return new Date(b.date) - new Date(a.date);
    });
  }
  else if(type === 'amount'){
    sortedTransactionList.value = transactionList.value.sort((a, b) => {
      return b.total_amount - a.total_amount;
    });
  }
  else if(type === 'title'){
    sortedTransactionList.value = transactionList.value.sort((a, b) => {
      return a.label.localeCompare(b.label);
    });
  }
  currentSort.value = type;
}

const currentFilterText = computed(() => {
  switch (currentSort.value) {
    case 'title':
      return 'Titre A-Z';
    case 'amount':
      return 'Montant';
    case 'date':
      return 'Date';
    default:
      return '';
  }
});

async function getCat(){
  try {
    const response = await api.get(`/transactionCategories/`);
    catList.value = response.data;
  }
  catch (error) {
    console.error(error);
  }
}

function getCatIcon(catId: number) {
  return catList.value.find(cat => cat.id === catId)?.icon;
}
function getCatColor(catId: number) {
  return catList.value.find(cat => cat.id === catId)?.color;
}

</script>

<template>
  <div class="q-pa-md">
    <div class="q-gutter-y-md" style="">
      <q-card class="card-tab">
        <q-tabs
          v-model="tab"
          dense
          class="text-primary tableau bg-accent"
          active-color="secondary"
          indicator-color="secondary"
          align="justify"
          narrow-indicator
        >
          <q-tab name="transactions" label="Transactions" />
          <q-tab name="remboursements" label="Remboursements" />
          <q-tab name="statistiques" label="Statistiques" />
        </q-tabs>

        <q-separator />

        <q-tab-panels v-model="tab" class="bg-accent" animated>
          <q-tab-panel name="transactions">
            <div class="row">
              <q-btn-dropdown
                color="primary"
                label="Trier par"
                no-caps
                rounded
              >
                <q-list>
                  <q-item class="bg-primary" clickable v-close-popup @click="sortTransaction('date')">
                    <q-item-section>
                      <q-item-label>Date</q-item-label>
                    </q-item-section>
                  </q-item>

                  <q-item class="bg-primary" clickable v-close-popup @click="sortTransaction('amount')">
                    <q-item-section>
                      <q-item-label>Montant</q-item-label>
                    </q-item-section>
                  </q-item>

                  <q-item  class="bg-primary" clickable v-close-popup @click="sortTransaction('title')">
                    <q-item-section>
                      <q-item-label>Titre A-Z</q-item-label>
                    </q-item-section>
                  </q-item>
                </q-list>
              </q-btn-dropdown>

              <q-space>

              </q-space>
              <q-btn
                color="secondary"
                icon-right="add"
                label="Nouvelle transaction"
                @click="openDialogCreateTransaction"
                no-caps/>
            </div>
            <q-card-section>
              <q-item>
                <q-item-section avatar>
                  <q-avatar round text-color="white" icon="group"/>
                </q-item-section>

                <q-item-section>
                  <q-item-label class="">Titre</q-item-label>
                </q-item-section>

                <q-item-section>
                  <q-item-label class="">Date</q-item-label>
                </q-item-section>

                <q-item-section>
                  <q-item-label class="">Somme</q-item-label>
                </q-item-section>

                <q-item-section>
                  <q-item-label class="">Action</q-item-label>
                </q-item-section>

              </q-item>
            </q-card-section>
            <q-separator/>
            <q-card-section v-if="transactionList.length > 0">
              <q-scroll-area style="height: 300px">
              <span class="q-pa-sm"><q-icon class="q-mx-sm" name="swap_vert" />{{currentFilterText}}</span>
              <q-item v-for="transaction in sortedTransactionList" :key="transaction.id">
                <q-item-section avatar>
                  <q-avatar :style="{ 'background-color': getCatColor(transaction.categoryId) }" text-color="white" :icon="getCatIcon(transaction.categoryId)" />
                </q-item-section>

                <q-item-section>
                  <q-item-label class="">{{ transaction.label}}</q-item-label>
                </q-item-section>

                <q-item-section>
                  <q-item-label class="">{{ formatDate(transaction.date) }}</q-item-label>
                </q-item-section>

                <q-item-section>
                  <q-item-label class="">{{formatNumber(transaction.total_amount)}}€</q-item-label>
                </q-item-section>

                <q-item-section>
                  <q-btn outline color="secondary" rounded @click="openDialogConsultTransaction(transaction.id)">Consulter</q-btn>
                </q-item-section>

              </q-item>
              </q-scroll-area>
            </q-card-section>
          </q-tab-panel>

          <q-tab-panel name="remboursements">
            <div class="text-h6">Alarms</div>
            Lorem ipsum dolor sit amet consectetur adipisicing elit.
          </q-tab-panel>

          <q-tab-panel name="statistiques">
            <div class="text-h6">Movies</div>
            Lorem ipsum dolor sit amet consectetur adipisicing elit.
          </q-tab-panel>
        </q-tab-panels>
      </q-card>
    </div>
  </div>
</template>

<style scoped>
.tableau{
  width: 100%;
}

.card-tab{
  width: 100%;
  border-radius: 15px;
  margin-top: 50px;
}
</style>

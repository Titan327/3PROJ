<script setup lang="ts">
import {onMounted, ref, computed} from "vue";
import {Transaction} from "src/interfaces/transactions.interface";
import DialogCreateTransaction from "components/Groups/DialogCreateTransaction.vue";
import {useQuasar} from "quasar";
import {api} from "boot/axios";
import {formatDate, formatNumber} from "stores/globalFunctionsStore";
import DialogConsultTransaction from "components/Groups/DialogConsultTransaction.vue";
import {useRouter} from "vue-router";
import { Refund } from 'src/interfaces/refund.interface';
import { DefaultGroup } from 'src/interfaces/group.interface';
import { getGroup, getUserGroupData } from 'stores/groupStore';
import DialogRefund from 'components/Groups/DialogRefund.vue';
import { io } from 'socket.io-client';
import { NotificationBus} from 'boot/eventBus';
import DialogPrivateMessage from "components/Common/DialogPrivateMessage.vue";


let  tab = ref('transactions')
const transactionList = ref<Transaction[]>([]);
let refundsList = ref<Refund[]>([]);
let doneRefundList = ref<Refund[]>([]);
let sortedTransactionList = ref<Transaction[]>([]);
let dialogCreateTransaction = ref(false);
let dialogConsultTransaction = ref(false);
let dialogRefundTransaction = ref(false);
let dialogCreateMessage = ref(false);

let currentSort = ref('date');
const $q = useQuasar();
let catList = ref([]);
const router = useRouter();
let group = ref(DefaultGroup());
let width = ref(0);


const props = defineProps({
  groupId: Number,
  userId: Number,
});

onMounted(async () => {
  await getTransactionList()
  await getOptimalRefundList();
  await getCat();
  group.value = await getGroup(props.groupId);

  function getWidth() {
    width.value = window.innerWidth;
  }
  getWidth();
  window.addEventListener('resize', getWidth);
});

const socket = io(process.env.URL_BACKEND);

socket.on('connect', () => {
  console.log('Connected to server');
});

socket.on(`new-transaction-${props.groupId}`, () => {
  getTransactionList();
  getOptimalRefundList();
  NotificationBus.emit('new-notif');
});

async function getTransactionList(){
  try{
    const response = await api.get(`groups/${props.groupId}/transactions`);
    transactionList.value = response.data;
    sortTransaction('date');
  }
  catch (e){
    console.error(e)
  }
}

async function getOptimalRefundList(){
  try {
    const response = await api.get(`groups/${props.groupId}/refunds`);
    refundsList.value = response.data;

    const refundedResponse = await api.get(`groups/${props.groupId}/refunds/done`);
    doneRefundList.value = refundedResponse.data;
  }
  catch (e){
    console.error(e)
  }
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
    getOptimalRefundList();
  })
}

function openDialogPrivateMessage(user2:number){
  dialogCreateMessage.value = true;
  $q.dialog({
    component: DialogPrivateMessage,

    componentProps: {
      isOpen: dialogCreateMessage,
      groupId: props.groupId,
      user2id: user2,
    }
  }).onOk(() => {
    console.log('OK')
  }).onCancel(() => {
    console.log('Cancel')
  }).onDismiss(() => {
    dialogCreateMessage.value = false;
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

function openDialogRefund(refundId:number, refundedUserId:number, amount:0){
  dialogRefundTransaction.value = true;
  $q.dialog({
    component: DialogRefund,

    componentProps: {
      isOpen: dialogRefundTransaction,
      groupId: props.groupId,
      userId: props.userId,
      refundId: refundId,
      userToPaidID: refundedUserId,
      amount: amount
    }
  }).onDismiss(() => {
    getOptimalRefundList();
    dialogRefundTransaction.value = false;
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
          <q-tab name="soldes" label="Soldes" />
        </q-tabs>

        <q-separator />

        <q-tab-panels v-model="tab" class="bg-accent" animated>

          <!-- ----------------------------------TRANSACTIONS--------------------------------------------- -->
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
            <q-card-section v-if="transactionList.length == 0">
              <q-item-label class="text-h6 q-mx-auto">Rien à afficher</q-item-label>
            </q-card-section>
          </q-tab-panel>

          <!-- ----------------------------------REMBOURSEMENTS--------------------------------------------- -->
          <q-tab-panel name="remboursements">
            <q-card-section>
              <q-item-label class="text-h6 q-pa-lg">Liste des remboursements optimisés : </q-item-label>
              <q-item v-if="refundsList.length>0">
                <q-item-section avatar>
                  <q-avatar round text-color="white" icon="group"/>
                </q-item-section>
                  <q-space></q-space>
                <q-item-section v-if="width>800">
                  <q-item-label class="q-mx-xl">Payeur
                    <q-icon
                    size="xs"
                    name="arrow_forward"
                  /> Receveur</q-item-label>
                </q-item-section>
                  <q-space></q-space>
                <q-item-section>
                  <q-item-label class="q-mx-xl">Montant</q-item-label>
                </q-item-section>
                <q-space></q-space>
                  <q-space></q-space>
                <q-item-section>
                  <q-item-label class="q-mx-auto">Action</q-item-label>
                </q-item-section>

              </q-item>
              <q-item v-if="refundsList.length==0">
                <q-item-section>
                  <q-item-label class="text-h6">Rien à afficher</q-item-label>
                </q-item-section>
              </q-item>
            </q-card-section>
            <q-separator/>
            <q-card-section v-if="refundsList.length > 0">
              <q-scroll-area style="height: 300px">
                <q-item v-for="refund in refundsList" :key="refund.id">
                  <q-item-section avatar>
                    <q-avatar
                      size="40px"
                      class="overlapping"
                      :style="`left: ${1 * 20}px`"
                    >
                      <img :src="getUserGroupData(refund.refundingUserId)?.profile_picture ? getUserGroupData(refund.refundingUserId)?.profile_picture[2] : 'assets/defaults/user-default.webp'">
                    </q-avatar>
                    <q-avatar
                      size="40px"
                      class="overlapping"
                      :style="`left: ${2 * 20}px`"
                    >
                      <img :src="getUserGroupData(refund.refundedUserId)?.profile_picture ? getUserGroupData(refund.refundedUserId)?.profile_picture[2] : 'assets/defaults/user-default.webp'">
                    </q-avatar>
                   </q-item-section>
                  <q-space></q-space>
                  <q-item-section v-if="width>800">
                    <q-item-label class="q-mx-xl">{{getUserGroupData(refund.refundingUserId)?.username}}
                      <q-icon
                      size="xs"
                      name="arrow_forward"
                    />
                      {{getUserGroupData(refund.refundedUserId)?.username}}</q-item-label>
                  </q-item-section>
                  <q-space></q-space>
                  <q-item-section>
                    <q-item-label class="q-mx-xl">{{formatNumber(refund.amount)}}€</q-item-label>
                  </q-item-section>
                  <q-space></q-space>
                  <q-space></q-space>
                  <q-item-section class="q-mx-auto">
                  <q-btn outline class="w-60 q-mx-auto" color="secondary" v-if="refund.refundingUserId == props.userId" rounded @click="openDialogRefund(refund.id,refund.refundedUserId, refund.amount)">Effectuer le remboursement</q-btn>
                  <span v-else class="q-mx-auto">Rien à effectuer</span>
                  </q-item-section>

                </q-item>
                <q-item-section class="text-h6" v-if="doneRefundList.length > 0">Remboursements effectués</q-item-section>
                <q-item v-for="refund in doneRefundList" :key="refund.id">
                  <q-item-section avatar>
                    <q-avatar
                      size="40px"
                      class="overlapping"
                      :style="`left: ${1 * 20}px`"
                    >
                      <img :src="getUserGroupData(refund.refundingUserId)?.profile_picture ? getUserGroupData(refund.refundingUserId)?.profile_picture[2] : 'assets/defaults/user-default.webp'">
                    </q-avatar>
                    <q-avatar
                      size="40px"
                      class="overlapping"
                      :style="`left: ${2 * 20}px`"
                    >
                      <img :src="getUserGroupData(refund.refundedUserId)?.profile_picture ? getUserGroupData(refund.refundedUserId)?.profile_picture[2] : 'assets/defaults/user-default.webp'">
                    </q-avatar>
                  </q-item-section>
                  <q-space></q-space>
                  <q-item-section v-if="width>800">
                    <q-item-label class="q-mx-xl">{{getUserGroupData(refund.refundingUserId)?.username}}
                      <q-icon
                        size="xs"
                        name="arrow_forward"
                      />
                      {{getUserGroupData(refund.refundedUserId)?.username}}</q-item-label>
                  </q-item-section>
                  <q-space></q-space>
                  <q-item-section>
                    <q-item-label class="q-mx-xl">{{formatNumber(refund.amount)}}€</q-item-label>
                  </q-item-section>
                  <q-space></q-space>
                  <q-space></q-space>
                  <q-item-section class="q-mx-auto">
                    <q-btn outline class="w-60 q-mx-auto" color="secondary" v-if="refund.refundingUserId == props.userId" rounded @click="openDialogRefund(refund.id,refund.refundedUserId, refund.amount)">Effectuer le remboursement</q-btn>
                    <span v-else class="q-mx-auto">Rien à effectuer</span>
                  </q-item-section>

                </q-item>
              </q-scroll-area>
            </q-card-section>
          </q-tab-panel>

          <q-tab-panel name="soldes">
            <div class="container row">
                <q-card class="card-user-balance" flat dark  bordered v-for="user in group.Users" :key="user.id">
                  <img :src="user.profile_picture ? user.profile_picture[2] :   'assets/defaults/user-default.webp'">

                  <q-card-section>
                    <div class="text-h6"> {{user.username}}</div>
                    <div class="text-subtitle2"> {{formatNumber(user.UserGroup.balance)}}€</div>
                    <q-card-actions align="right">
                      <q-btn :disable="user.id == props.userId" flat round color="secondary" icon="message" @click="openDialogPrivateMessage(Number(user?.id))"><q-tooltip v-if="props.userId != user.id">Envoyer un message privé à {{user.username}}</q-tooltip><q-tooltip class="bg-red" v-if="props.userId == user.id">Vous ne pouvez pas envoyer de messages à vous même</q-tooltip></q-btn>
                    </q-card-actions>
                  </q-card-section>
                </q-card>
            </div>
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

.overlapping {
  position: absolute;
}

.card-user-balance{
  width: calc(25% - 10px);
  margin: 5px;
}

container{
  display: flex;
  flex-wrap: wrap;
  justify-content: space-around;
  flex-direction: row;
}

@media screen and (max-width: 1500px) {
  .card-user-balance{
    width: calc(33% - 10px);
    margin: 5px;
  }
}
@media screen and (max-width: 1200px) {
  .card-user-balance{
    width: calc(50% - 10px);
    margin: 5px;
  }
}
@media screen and (max-width: 600px) {
  .card-user-balance{
    width: calc(100% - 10px);
    margin: 5px;
  }
}

</style>

<script setup lang="ts">
import { useQuasar } from 'quasar'
import {onMounted, ref} from "vue";
import {DefaultGroup} from "src/interfaces/group.interface";
import {api} from "boot/axios";
import {DefaultTransactionCreated} from "src/interfaces/transactions.interface";
import {getUser} from "stores/userStore";
import {DefaultUser} from "src/interfaces/user.interface";
const $q = useQuasar();

let isOpen = ref(false);
let loading = ref(false);

let User = ref(DefaultUser());
let group = ref(DefaultGroup());
const _transaction = ref(DefaultTransactionCreated());
const catOptn = ref([]);
let mounted = ref(false);

const props = defineProps({
  groupId: Number,
  userId: Boolean,
});

onMounted(async () => {
  await getGroup();
});

const checkNotNull = (value) => {
  return !!value || "Champ Obligatoire";
};
async function createTransaction() {

  loading.value = true;
    try {
      const response = await api.post("transaction", {
        "groupId": props.groupId,
        "label": _transaction.value.label,
        "total_amount": _transaction.value.total_amount,
        "date": new Date(),
        "receipt": "receipt test",
        "senderId": props.userId,
        "categoryId": _transaction.value.categoryId,
        "details": _transaction.value.details
      });
      if (response.data) {
        $q.notify({
          type: 'positive',
          message: 'Transaction créée'
        })
        isOpen.value= false;
      }
    }
    catch (error) {
      $q.notify({
        type: 'negative',
        message: 'Une erreur s\'est produite lors de la création de la transaction'
      })

    }
  loading.value = false;
}

function createDetail() {
  const detailList = [];
  group.value.Users.forEach(user => {
    detailList.push({
      [`detail${user.id}`]: {
        "userId": user.id,
        "amount": 0,
      }
    });
  });
  _transaction.value.details = detailList;
}

async function getGroup() {
  User.value = await getUser();
  try {
    const response = await api.get(`/group/${props.groupId}`);
    group.value = response.data;
    createDetail();
    mounted.value = true;
  }
  catch (error) {
    console.error(error);
  }
}

function calculateTotal() {
  let total = 0;
  _transaction.value.details.forEach(detail => {
    for (const key in detail) {
      if (Object.hasOwnProperty.call(detail, key)) {
        total += detail[key].amount;
      }
    }
  });
  _transaction.value.total_amount = total;
  console.log(_transaction.value)
  return total;
}

</script>


<template>
  <q-dialog v-model="isOpen">
    <q-card class="bg-primary" style="width: 800px; max-width: 80vw;">
      <q-card-section>
        <div class="text-h5">Nouvelle dépense</div>
      </q-card-section>
      <q-form
        @submit="createTransaction"
      >
        <div class="form q-pa-lg q-ma-lg">
          <div class="inputs row">
              <q-input
                outlined
                class="title"
                v-model="_transaction.label"
                label="Titre de la dépense"
                dark
                color="secondary"
                :rules="[checkNotNull]"
              />
            <q-space></q-space>
              <q-select
                outlined
                class="cat"
                v-model="_transaction.categoryId"
                label="Description"
                dark
                color="secondary"
                autogrow
                :options="['test','test']"
                :rules="[checkNotNull]"
                hide-bottom-space
              />
          </div>
          <div class="q-mx-auto">
            <q-list class="q-mx-auto q-pa-md">
              <q-scroll-area style="height: 300px;">
                <div v-for="(user) in group.Users"
                :key="user.id" class="q-py-xs">
                  <q-item clickable v-ripple>
                    <q-item-section avatar>
                      <q-avatar>
                      <img :src="user.profile_picture ? `${user.profile_picture[0]}` : 'assets/defaults/user-default.webp'"/>
                      </q-avatar>
                    </q-item-section>

                    <q-item-section>{{user.username}}</q-item-section>

                    <q-input
                      type="number"
                      outlined
                      :v-model="_transaction.details.find(detail => detail.userId === userId)"
                      label="Montant"
                      dark
                      color="secondary"
                      suffix="€"
                      @update:model-value="calculateTotal()"
                    />
                  </q-item>
                </div>
              </q-scroll-area>
            </q-list>
          </div>
          <div class="q-mx-auto div-total row">
            <q-space></q-space>
         <q-item-label class="text-h6">   Total: {{_transaction.total_amount}} €</q-item-label>
          </div>
          <div class="inputs row">
            <div class="row q-mx-auto">
              <q-item-label class=" q-pa-sm">Ticket de caisse :</q-item-label>
              <q-btn
                class="btn"
                color="secondary"
                outline
                label="Joindre"
                :loading="loading"
                hide-bottom-space
              />
            </div>
            <q-space>
            </q-space>
            <div class="q-mx-auto">
              <q-btn
                class="btn"
                color="secondary"
                text-color="white"
                unelevated
                label="Créer la dépense"
                type="submit"
                :loading="loading"
                hide-bottom-space
              />
            </div>
          </div>
        </div>
      </q-form>
    </q-card>
  </q-dialog>

</template>

<style scoped>
.title {
  width: 30%;
}
.cat {
  width: 40%;
}

.btn {
  border-radius: 10px;
}
.div-total{
  margin: 20px;
}
</style>

<script setup lang="ts">
import { useQuasar } from 'quasar'
import {computed, onMounted, ref} from "vue";
import {DefaultGroup} from "src/interfaces/group.interface";
import {api} from "boot/axios";
import {getGroup, getUserGroupData} from '../../stores/groupStore';
import {formatNumber, redirectBankWebSite, redirectToPaypal} from "stores/globalFunctionsStore";
import {Transaction} from "src/interfaces/transactions.interface";
import {IPaymentMethod} from "src/interfaces/paymentMethod.interface";
const $q = useQuasar();

let isOpen = ref(false);
let newGroup = ref(DefaultGroup());
let loading = ref(false);

let refund = ref({
  id: 0,
  userToPaidID: 0,
  amount: 0,
  userId: 0,
  groupId: 0,
});

let friendPaymentMethod = ref();
let paymentMethod = ref();

const props = defineProps({
  groupId: Number,
  userId: Boolean,
  refundId: Number,
  userToPaidID: Number,
  amount: Number,
});

onMounted(async () => {
  refund.value.id =  <number>props.refundId;
  refund.value.userToPaidID =  <number>props.userToPaidID;
  refund.value.amount =  <number>props.amount;
  refund.value.userId =  <any>props.userId;
  refund.value.groupId = <number>props.groupId;

  await getFriendPaymentMethod();
  await getMethod();
});

async function Refund() {

  loading.value = true;
    try {
      const response = await api.post(`groups/${props.groupId}/refund/${props.refundId}`, {});
      if (response.data) {
        $q.notify({
          type: 'positive',
          message: 'Remboursement effectué'
        })
        isOpen.value= false;
      }
    }
    catch (error) {
      $q.notify({
        type: 'negative',
        message: 'Une erreur s\'est produite lors du remboursement'
      })

    }
  loading.value = false;
  isOpen.value = false;
}

async function getFriendPaymentMethod(){
  loading.value = true;
  try {
    const response = await api.get(`users/${refund.value.userToPaidID}/${refund.value.groupId}/paymentMethode`);
    if (response.data) {
      friendPaymentMethod.value = response.data;
    }
  }
  catch (error) {
    $q.notify({
      type: 'negative',
      message: 'Une erreur s\'est produite lors de la récupération du moyen de paiement'
    })
    loading.value = false;
  }
  loading.value = false;
}

async function getMethod(){
  try {
    const response = await api.get(`/users/me/paymentMethode`)
    paymentMethod.value = response.data
  }
  catch(e){
    console.error(e)
  }
}

async function copyLink() {
  await navigator.clipboard.writeText(computeFriendIban.value)
    .then(() => {
      $q.notify({
        type: 'positive',
        message: 'Lien copié'
      })
    })
    .catch(err => {
      console.error("Impossible de copier l'IBAN dans le presse-papiers : ");
    });
}

function getRibTransaction(transactions: IPaymentMethod[]): IPaymentMethod | null {
  return transactions.find(transaction => transaction.type === 'RIB') || null;
}

const computeBankLink = computed(() => {
  const rib = paymentMethod.value ? getRibTransaction(paymentMethod.value) : null;
  return rib ? rib.bank_link : '';
});

const computeFriendIban = computed(() => {
  const rib = paymentMethod.value ? getRibTransaction(friendPaymentMethod.value) : null;
  return rib ? rib.value.IBAN : null;
});

function getPaypalTransaction(transactions: IPaymentMethod[]): IPaymentMethod | null {
  return transactions.find(transaction => transaction.type === 'Paypal') || null;
}
const computeFriendPaypalLink = computed(() => {
  const paypal = paymentMethod.value ? getPaypalTransaction(friendPaymentMethod.value) : null;
  return paypal ? paypal.value.user_paypal : null;
});

</script>

<template>
  <q-dialog v-model="isOpen">
    <q-card class="bg-primary" style="width: 600px; max-width: 80vw;">
      <q-card-section>
        <div class="text-h5">Rembourser {{formatNumber(refund.amount)}}€ à {{getUserGroupData(refund.userToPaidID)?.username}}</div>
      </q-card-section>
      <q-card-section>
        <q-item-label v-if="computeFriendIban &&  computeFriendPaypalLink" class="text-subtitle2 q-pa-md">Vous pouvez copier l'IBAN de {{getUserGroupData(refund.userToPaidID)?.username}} pour effectuer le virement bancaire ensuite ou effectuer le remboursement via PayPal.</q-item-label>
        <q-item-label v-if="computeFriendIban && !computeFriendPaypalLink" class="text-subtitle2 q-pa-md">Vous pouvez copier l'IBAN de {{getUserGroupData(refund.userToPaidID)?.username}} pour effectuer le virement bancaire ensuite.</q-item-label>
        <q-item-label v-if="computeFriendPaypalLink && !computeFriendIban" class="text-subtitle2 q-pa-md">En cliquant sur "Rembourser via Paypal" vous serez redirigé vers le profil de {{getUserGroupData(refund.userToPaidID)?.username}} afin d'effectuer le remboursement.</q-item-label>
        <q-item-label v-if="!computeFriendPaypalLink && !computeFriendIban" class="text-subtitle2 q-pa-md">{{getUserGroupData(refund.userToPaidID)?.username}} n'a ajouté aucun moyen de paiement, vous pouvez toujours effectuer un remboursement par moyen externe.</q-item-label>

        <div class="bloc-link row items-center justify-evenly" v-if="computeFriendIban">
          <q-input v-model="computeFriendIban" outlined dense readonly dark class="link" color="secondary"></q-input>
          <q-btn color="secondary" class="btn" outline @click="copyLink" icon="content_copy" > </q-btn>
        </div>
        <div class="q-ma-lg row">
          <q-btn
            class="bg-paypal-color q-mx-auto"
            text-color="white"
            unelevated
            label="Rembourser via Paypal"
            type="submit"
            :loading="loading"
            hide-bottom-space
            no-caps
            @click="redirectToPaypal(computeFriendPaypalLink)"
            v-if="computeFriendPaypalLink"
          />
          <q-space v-if="computeFriendIban"></q-space>
          <q-btn
            class="q-mx-auto"
            color="secondary"
            text-color="white"
            unelevated
            v-if="computeFriendIban"
            label="Rembourser par virement bancaire"
            type="submit"
            :disable="!computeBankLink"
            :loading="loading"
            hide-bottom-space
            no-caps
            @click="redirectBankWebSite(computeBankLink)">
            <q-tooltip class="bg-red" v-if="!computeBankLink">
              Vous devez avoir un RIB d'enregistré avec une banque reconnue pour utiliser cette fonctionnalité
            </q-tooltip>
          </q-btn>
        </div>
        <div class="q-ma-md row">
          <q-space></q-space>
          <q-btn
            color="secondary"
            text-color="secondary"
            unelevated
            label="J'ai déja effectué le remboursement"
            type="submit"
            outline
            :loading="loading"
            hide-bottom-space
            rounded
            no-caps
            @click="Refund"
          />
        </div>
      </q-card-section>
    </q-card>
  </q-dialog>

</template>

<style scoped>
.btn {
  border-radius: 10px;
  margin: 0 5px;
  height: 42px;
}
.link{
  border-radius: 10px;
  margin: 0 5px;
  width: 80%;
}
.bloc-link{
  width: 100%;
  margin: 20px auto;
  height: 42px;
}
</style>

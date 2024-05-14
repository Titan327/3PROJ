<script setup lang="ts">

import {onMounted, ref } from "vue";
import {api} from "boot/axios";
import {getUser} from "stores/userStore";
import {Transaction} from "src/interfaces/transactions.interface";
import {DefaultUser} from "src/interfaces/user.interface";
import {useRouter} from "vue-router";
import {formatDate, formatNumber} from "stores/globalFunctionsStore";

const transactionList = ref<Transaction[]>([]);
const User = ref(DefaultUser());
const router = useRouter();
const width = ref(0);
let loading = ref(false);

onMounted(async () => {

  loading.value = true;
  try {
    User.value = await getUser();
    const response = await api.get(`users/${User.value.id}/lastTransactions?limit=5`);
    transactionList.value = response.data;
    loading.value = false;
  }
  catch (e) {
    console.error('Error')
  }

  function getWidth() {
    width.value = window.innerWidth;
  }
  getWidth();
  window.addEventListener('resize', getWidth);
});

function goToTransaction(groupId, transactionId) {
  router.push(`/groups/${groupId}/transactions/${transactionId}`);
}

</script>
<template>
  <q-card class="bloc-paye bg-accent">
    <q-card-section>
      <h3 class="text-h6">Dernieres dépenses</h3>
    </q-card-section>
    <q-card-section>
      <q-item class="row justify-between full-width">
        <q-item-section avatar>
          <q-avatar round text-color="white" icon="group" />
        </q-item-section>

        <q-item-section>
          <q-item-label class="">Nom</q-item-label>
        </q-item-section>

        <q-item-section  v-if="width>500">
          <q-item-label class="">Date</q-item-label>
        </q-item-section>

        <q-item-section v-if="width>500">
          <q-item-label class="">Somme</q-item-label>
        </q-item-section>

        <q-item-section  v-if="width>500">
          <q-item-label class="">Action</q-item-label>
        </q-item-section>

      </q-item>
    </q-card-section>
    <q-separator/>
    <q-card-section v-if="transactionList.length > 0">
      <q-item v-for="transaction in transactionList" :key="transaction.id" clickable @click="goToTransaction(transaction.Transaction.groupId, transaction.Transaction.id)">
        <q-item-section avatar>
          <q-avatar round text-color="white">
            <img :src="transaction.Transaction.Group.picture ? `${transaction.Transaction.Group.picture[0]}` : 'assets/defaults/group-default.webp'"/>
          </q-avatar>
        </q-item-section>

        <q-item-section>
          <q-item-label class="">{{ transaction.Transaction.label}}</q-item-label>
        </q-item-section>

        <q-item-section  v-if="width>500">
          <q-item-label class="">{{ formatDate(transaction.Transaction.date) }}</q-item-label>
        </q-item-section>

        <q-item-section v-if="width>500">
          <q-item-label class="">{{ formatNumber(transaction.amount) }}€</q-item-label>
        </q-item-section>

        <q-item-section v-if="width>500">
         <q-btn outline color="secondary" rounded @click="goToTransaction(transaction.Transaction.groupId, transaction.Transaction.id)">Consulter</q-btn>
        </q-item-section>

      </q-item>
    </q-card-section>
    <q-card-section v-if="transactionList.length == 0 && !loading">
      <q-item-section>
        <q-item-label class="text-h6">Rien à afficher</q-item-label>
      </q-item-section>
    </q-card-section>
    <div class="row" v-if="loading">
      <q-space></q-space>
      <q-spinner size="50px" class="q-pa-xs q-mx-auto" color="secondary" />
      <q-space></q-space>
    </div>
  </q-card>
</template>

<style scoped>

.bloc-paye{
  width: 100%;
  border-radius: 15px;
  margin-top: 50px;
}
</style>

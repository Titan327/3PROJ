<script setup lang="ts">

import {onMounted, ref } from "vue";
import {api} from "boot/axios";
import {getUser} from "stores/userStore";
import {Transaction} from "src/interfaces/transactions.interface";

const transactionList = ref<Transaction[]>([]);


onMounted(async () => {

  const userData = await getUser();

  const response = await api.get(`user/${userData.id}/lastTransactions?limit=5`);

  transactionList.value = response.data;

  console.log(transactionList.value);
});

const formatDate = (dateString) => {
  const date = new Date(dateString);
  const day = date.getDate();
  const month = date.getMonth() + 1; // Ajouter 1 car les mois sont indexés à partir de 0
  return `${day < 10 ? '0' : ''}${day}/${month < 10 ? '0' : ''}${month}`;
};

</script>
<template>
  <q-card class="bloc-paye bg-accent">
    <q-card-section>
      <h3 class="text-h6">Dernieres dépenses</h3>
    </q-card-section>
    <q-card-section>
      <q-item>
        <q-item-section avatar>
          <q-avatar rounded text-color="white" icon="account_circle" />
        </q-item-section>

        <q-item-section>
          <q-item-label class="">Nom</q-item-label>
        </q-item-section>

        <q-item-section>
          <q-item-label class="">Date</q-item-label>
        </q-item-section>

        <q-item-section>
          <q-item-label class="">Somme</q-item-label>
        </q-item-section>
      </q-item>
    </q-card-section>
    <q-separator/>
    <q-card-section v-if="transactionList.length > 0">
      <q-item v-for="transaction in transactionList" :key="transaction.id">
        <q-item-section avatar>
          <q-avatar rounded color="secondary" text-color="white">
            {{ transaction.Transaction.group.picture }}
          </q-avatar>
        </q-item-section>

        <q-item-section>
          <q-item-label class="text-h6">{{ transaction.Transaction.id}}</q-item-label>
        </q-item-section>

        <q-item-section>
          <q-item-label class="text-h6">{{ formatDate(transaction.Transaction.date) }}</q-item-label>
        </q-item-section>

        <q-item-section>
          <q-item-label class="text-h6">{{ transaction.Transaction.total_amount }}</q-item-label>
        </q-item-section>
      </q-item>
    </q-card-section>
    <q-card-section v-if="transactionList.length == 0">
      <q-item-section>
        <q-item-label class="text-h6">Rien à afficher</q-item-label>
      </q-item-section>
    </q-card-section>
  </q-card>
</template>

<style scoped>

.bloc-paye{
  width: 100%;
  border-radius: 15px;
  margin-top: 50px;
}
</style>

<script setup lang="ts">

import {onMounted, ref } from "vue";
import {Transaction} from "src/interfaces/transactions.interface";

let transactionList = ref<Transaction[]>([]);


onMounted(() => {
  console.log("LastPaiements mounted");
});
</script>


<template>
  <q-card class="bloc-paye bg-primary">
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

        <q-item-section>
          <q-item-label class="">Statut</q-item-label>
        </q-item-section>
      </q-item>
    </q-card-section>
    <q-separator/>
    <q-card-section v-if="transactionList.length > 0">
      <q-item v-for="transaction in transactionList" :key="transaction.id">
        <q-item-section avatar>
          <q-avatar rounded color="secondary" text-color="white">
            {{ transaction.group.picture }}
          </q-avatar>
        </q-item-section>

        <q-item-section>
          <q-item-label class="text-h6">{{ transaction.id }}</q-item-label>
        </q-item-section>

        <q-item-section>
          <q-item-label class="text-h6">{{ transaction.date }}</q-item-label>
        </q-item-section>

        <q-item-section>
          <q-item-label class="text-h6">{{ transaction.total_amount }}</q-item-label>
        </q-item-section>

        <q-item-section>
          <q-chip class="chip-status" color="green" text-color="white">Payé</q-chip>
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
  width: 88%;
  border-radius: 15px;
}
</style>

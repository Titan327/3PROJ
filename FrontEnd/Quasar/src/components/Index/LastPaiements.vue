<script setup lang="ts">

import {onMounted, ref } from "vue";
import {api} from "boot/axios";
import {getUser} from "stores/userStore";
import {Transaction} from "src/interfaces/transactions.interface";
import {DefaultUser} from "src/interfaces/user.interface";
import {useRouter} from "vue-router";

const transactionList = ref<Transaction[]>([]);
const User = ref(DefaultUser());
const router = useRouter();

onMounted(async () => {

  User.value = await getUser();
  const response = await api.get(`user/${User.value.id}/lastTransactions?limit=5`);
  transactionList.value = response.data;
});

const formatDate = (dateString) => {
  const date = new Date(dateString);
  const day = date.getDate();
  const month = date.getMonth() + 1;
  const year = date.getFullYear();
  return `${day < 10 ? '0' : ''}${day}/${month < 10 ? '0' : ''}${month}/${year}`;
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
          <q-avatar round text-color="white" icon="group" />
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
          <q-item-label class="">Action</q-item-label>
        </q-item-section>

      </q-item>
    </q-card-section>
    <q-separator/>
    <q-card-section v-if="transactionList.length > 0">
      <q-item v-for="transaction in transactionList" :key="transaction.id">
        <q-item-section avatar>
          <q-avatar round color="secondary" text-color="white">
            <img :src="transaction.Transaction.Group.picture ? `${transaction.Transaction.Group.picture}/100` : 'assets/defaults/group-default.webp'"/>
          </q-avatar>
        </q-item-section>

        <q-item-section>
          <q-item-label class="">{{ transaction.Transaction.label}}</q-item-label>
        </q-item-section>

        <q-item-section>
          <q-item-label class="">{{ formatDate(transaction.Transaction.date) }}</q-item-label>
        </q-item-section>

        <q-item-section>
          <q-item-label class="">{{ transaction.amount }}€</q-item-label>
        </q-item-section>

        <q-item-section>
         <q-btn outline color="secondary" rounded  @click="router.push(`/group/${transaction.Transaction.groupId}`)">Consulter</q-btn>
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

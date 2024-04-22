<script setup lang="ts">
import { useQuasar } from 'quasar'
import {onMounted, ref} from "vue";
import {DefaultGroup} from "src/interfaces/group.interface";
import {api} from "boot/axios";
import {DefaultTransactionCreated} from "src/interfaces/transactions.interface";
import {getUser} from "stores/userStore";
import {DefaultUser} from "src/interfaces/user.interface";
import {formatDate, formatNumber} from "stores/globalFunctionsStore";
const $q = useQuasar();

let isOpen = ref(false);
let loading = ref(false);
let mounted = ref(false);
let catList = ref([]);

let User = ref(DefaultUser());
let group = ref(DefaultGroup());
const _transaction = ref();

const props = defineProps({
  groupId: Number,
  userId: Boolean,
  transactionId: Number,
});


onMounted(async () => {
  await getTransaction()
  await getGroup();
  await getCat();
  mounted.value=true;

});

async function getGroup() {
  User.value = await getUser();
  try {
    const response = await api.get(`/group/${props.groupId}`);
    group.value = response.data;
  }
  catch (error) {
    console.error(error);
  }
}

async function getTransaction() {
  try {
    const response = await api.get(`/transaction/${props.transactionId}`);
    _transaction.value = response.data;
    mounted.value = true;
  }
  catch (error) {
    console.error(error);
  }
}

async function getCat(){
  try {
    const response = await api.get(`/transactionCategory/`);
    catList.value = response.data;
  }
  catch (error) {
    console.error(error);
  }
}

function getUserAmount(userId: number) {
  return _transaction.value.TransactionUsers.find(user => user.userId === userId)?.amount;
}

function getCatText(catId: number) {
  return catList.value.find(cat => cat.id === catId)?.label;
}


</script>


<template>
  <q-dialog v-model="isOpen">
    <q-card class="bg-primary" v-if="mounted" style="width: 600px; max-width: 80vw;">
      <q-card-section class="row justify-evenly">
        <div class="text-head">
          <div class="text-h5">{{_transaction.label}}</div>
          <div class="text-h6">{{formatNumber(_transaction.total_amount)}}€</div>
          <span>{{ getCatText(_transaction.categoryId) }}</span>
          <br>
          <span>Créé le {{formatDate(_transaction.date)}}</span>
        </div>
        <div class="picture">
          <q-avatar
            size="128px">
            <img src="assets/defaults/user-default.webp"/>
          </q-avatar>
        </div>
      </q-card-section>
      <q-card-section>
        <div class="q-mx-auto">
          <q-list class="q-mx-auto q-pa-md">
            <q-scroll-area style="height: 300px;">
              <div v-for="(user) in group.Users"
                   :key="user.id" class="q-py-xs">
                <q-item v-if="getUserAmount(user.id)">
                  <q-item-section avatar>
                    <q-avatar>
                      <img :src="user.profile_picture ? `${user.profile_picture[0]}` : 'assets/defaults/user-default.webp'"/>
                    </q-avatar>
                  </q-item-section>

                  <q-item-section>{{user.username}}</q-item-section>

                  <q-item-section avatar>
                    {{getUserAmount(user.id)}}€
                  </q-item-section>
                </q-item>
                <q-separator color="secondary"></q-separator>
              </div>
            </q-scroll-area>
          </q-list>
        </div>
      </q-card-section>
    </q-card>
  </q-dialog>

</template>

<style scoped>
.picture{
  width: 30%;
}
.text-head{
  width: 70%;
}
</style>

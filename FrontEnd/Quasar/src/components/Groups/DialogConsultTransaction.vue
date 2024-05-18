<script setup lang="ts">
import {onMounted, ref} from "vue";
import {DefaultGroup} from "src/interfaces/group.interface";
import {api} from "boot/axios";
import {getUser} from "stores/userStore";
import {DefaultUser} from "src/interfaces/user.interface";
import {formatDate, formatNumber} from "stores/globalFunctionsStore";
import { getUserGroupData } from 'stores/groupStore';

let isOpen = ref(false);
let mounted = ref(false);
let catList = ref([]);

let User = ref(DefaultUser());
let group = ref(DefaultGroup());
const _transaction = ref();
let file = ref(null);
let ticketLoading = ref(false);

const props = defineProps({
  groupId: Number,
  userId: Number,
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
    const response = await api.get(`/groups/${props.groupId}`);
    group.value = response.data;
  }
  catch (error) {
    console.error(error);
  }
}

async function getTransaction() {
  try {
    const response = await api.get(`/groups/${props.groupId}/transactions/${props.transactionId}`);
    _transaction.value = response.data;
    mounted.value = true;
  }
  catch (error) {
    console.error(error);
  }
}

async function getCat(){
  try {
    const response = await api.get(`/transactionCategories/`);
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
function getCatIcon(catId: number) {
  return catList.value.find(cat => cat.id === catId)?.icon;
}
function getCatColor(catId: number) {
  return catList.value.find(cat => cat.id === catId)?.color;
}

async function joinTicket(transactionId: number) {
  ticketLoading.value = true;
  try {
    const formData = new FormData();
    formData.append('file', file?.value);
    const response = await api.post(`img/ticket/${props.groupId}/${transactionId}`, formData);
    ticketLoading.value = false;
    await getTransaction();
  }
  catch (error) {
    console.error(error);
  }
  ticketLoading.value = false;
}

async function getRecieptFile(id:number){
  try {
    const response = await api.get(`/img/ticket/${props.groupId}/${id}`, { responseType: 'blob' });
    const blob = new Blob([response.data], { type: response.headers['content-type'] });
    const url = URL.createObjectURL(blob);

    window.open(url, '_blank');
    //URL.revokeObjectURL(url);

    //return url;
  } catch(e) {
    console.error(e);
    return false;
  }
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
          <span>Créé le {{formatDate(_transaction.date)}} par {{getUserGroupData(_transaction.senderId)?.username}}</span>
          <br>
          <q-btn v-if="_transaction.receipt != '' " class="text-secondary q-pa-none" no-caps flat @click="getRecieptFile(_transaction.id)">Afficher le ticket de caisse</q-btn>
          <q-file
            outlined
            class="w-60"
            v-if="_transaction.receipt == ''"
            v-model="file"
            label="Ajouter ticket de caisse"
            dark
            :loading="ticketLoading"
            dense
            color="secondary"
            hide-hint
            @update:model-value="joinTicket(props.transactionId)"
            hint="Format accepté : PDF, JPEG, PNG"
          >
            <template v-slot:prepend>
              <q-icon name="receipt_long"/>
            </template>
          </q-file>

        </div>
        <div class="picture">
          <q-avatar size="128px" font-size="52px" :style="{ 'background-color': getCatColor(_transaction.categoryId) }" text-color="white" :icon="getCatIcon(_transaction.categoryId)">
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
                    {{formatNumber(getUserAmount(user.id))}}€
                  </q-item-section>
                </q-item>
                <q-separator v-if="getUserAmount(user.id)" color="secondary"></q-separator>
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
.icon-user{
  margin: 0 0 0 0;
}

</style>

<template>
  <q-layout view="lHh Lpr lFf" v-if="isLog">

    <left-pannel>

    </left-pannel>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>

  <q-page-container v-if="!isLog">
    <div class="full-width full-height justify-around">
      <h3 class="text-h4">Vous avez été déconnecté &#x1F44B;</h3>

      <div class="row justify-evenly full-height">
        <q-btn outline color="secondary" rounded @click="router.push('login')">Retour à l'écran de connexion</q-btn>
      </div>
    </div>
    <router-view />
  </q-page-container>

</template>

<script setup lang="ts">

import {onMounted,ref} from "vue";
import {useRouter} from "vue-router";
import LeftPannel from "components/Common/LeftPannel.vue";
import {EtatTotalPaidComponent} from "src/interfaces/types";
import TotalPaid from "components/Index/TotalPaid.vue";
import PaymentsMethods from "components/Index/PaymentsMethods.vue";
import LastPaiements from "components/Index/LastPaiements.vue";
import LastGroups from "components/Index/LastGroups.vue";
const router = useRouter();
const isLog= ref(false);

onMounted(() => {

  try {
    const token = sessionStorage.getItem('userToken');
    if (!token){
      router.push('login');
    }
    else {
      isLog.value = true;
    }
  } catch (error) {
    router.push('login');
    console.error(error);
  }
});

</script>
<style>
body{
  background: #1b1b1b;
  //background: linear-gradient(345deg, rgba(181,0,150,0.8855917366946778) 0%, rgba(22,20,101,1) 42%);
  //background: linear-gradient(145deg, rgba(9,9,121,1) 0%, rgba(197,0,203,1) 100%);
  color: #ffff
}
</style>

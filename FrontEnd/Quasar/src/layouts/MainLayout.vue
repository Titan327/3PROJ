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
import { useRoute, useRouter } from 'vue-router';
import LeftPannel from "components/Common/LeftPannel.vue";
const router = useRouter();
const isLog= ref(false);
const route = useRoute();

function setCookie(name, value, minutes) {
  const date = new Date();
  date.setTime(date.getTime() + (minutes * 60 * 1000));
  const expires = "expires=" + date.toUTCString();
  document.cookie = name + "=" + value + ";" + expires + ";path=/";
}

onMounted(() => {
  try {
    const tokenGroup = route.params.token;
    const token = sessionStorage.getItem('userToken');
    if (!token){
      if(tokenGroup){
        setCookie('join-group', tokenGroup, 5);
      }
      router.push('/login');
    }
    else {
      isLog.value = true;
    }
  } catch (error) {
    router.push('/login');
    console.error(error);
  }
});

</script>
<style>
body{
  background: #1b1b1b;
  color: #ffff
}
</style>

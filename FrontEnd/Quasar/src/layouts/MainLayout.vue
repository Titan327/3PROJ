<template>
  <q-layout view="lHh LpR lFf">

    <left-pannel>

    </left-pannel>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script setup lang="ts">

import {onMounted,ref} from "vue";
import {useRouter} from "vue-router";
import LeftPannel from "components/Common/LeftPannel.vue";
const router = useRouter();

onMounted(() => {

  const token = localStorage.getItem('userToken');
  if (!token){
    router.push('/login');
  }

});
</script>
<style>
body{
  background: #141332;
  color: #ffff
}
</style>

<template>
  <q-layout view="lHh Lpr lFf">

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
import RightPannel from "components/Index/RightPannel.vue";
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
  background: #1b1b1b;
  //background: linear-gradient(345deg, rgba(181,0,150,0.8855917366946778) 0%, rgba(22,20,101,1) 42%);
  //background: linear-gradient(145deg, rgba(9,9,121,1) 0%, rgba(197,0,203,1) 100%);
  color: #ffff
}
</style>

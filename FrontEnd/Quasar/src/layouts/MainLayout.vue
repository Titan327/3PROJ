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

import {onMounted, ref} from "vue";
import {useRouter} from "vue-router";
import LeftPannel from "components/Common/LeftPannel.vue";
const router = useRouter();

let authToken = ref(localStorage.getItem('authToken'));

onMounted(() => {
  if (authToken.value) {
    console.log("Token OK");
  } else {
    router.push('/login')
  }
});
</script>

<template>
  <q-layout view="lHh Lpr lFf">
    <q-header elevated>
      <q-toolbar>
        <q-toolbar-title>
          Quasar App
        </q-toolbar-title>

        <div>Quasar v{{ $q.version }}</div>
      </q-toolbar>
    </q-header>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script setup lang="ts">

import {onMounted, ref} from "vue";
import {useRouter} from "vue-router";
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

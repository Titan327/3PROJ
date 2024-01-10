<script setup lang="ts">
import {ref} from "vue";

let userName = ref();
let password = ref();

</script>

<template>
  <div class="q-pa-md form">
    <div class="q-gutter-md" style="max-width: 300px">
      <q-input
        outlined
        v-model="userName"
        label="Nom d'utilisateur"
      />
      <q-input
        outlined
        v-model="password"
        label="Mot de passe"
        type="password"
      />
      <q-btn
        text-color="primary"
        unelevated
        outline
        label="Connexion avec Google"
      />
      <q-btn
        color="primary"
        text-color="white"
        unelevated
        label="Se connecter"
      />
      <br>
      <a href="/register">Je suis nouveau</a>
    </div>
  </div>
</template>

<style scoped>

</style>

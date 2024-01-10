<script setup lang="ts">
import {ref} from "vue";
import {DefaultUser} from "src/interfaces/user.interface";
import {api} from "boot/axios";

let newUser = ref(DefaultUser());
let pass = ref();
let passConfirmation = ref()

async function register(){
  console.log(newUser)
  const response = await api.post("/auth/register")
  console.log(response)
}

</script>

<template>
  <div class="q-pa-md form">
    <div class="q-gutter-md" style="max-width: 300px">
      <q-form
        @submit="register"
      >
        <q-input
          outlined
          v-model="newUser.firstName"
          label="Nom"
        />
        <q-input
          outlined
          v-model="newUser.lastName"
          label="Prénom"
        />
        <q-input
          outlined
          v-model="newUser.email"
          label="Adresse email"
          type="email"
        />
        <q-input
          outlined
          v-model="newUser.username"
          label="Nom d'utilisateur"
        />
        <q-input
          outlined
          v-model="newUser.birthdate"
          label="Date de naissance"
          type="date"
        />
        <q-input
          outlined
          v-model="pass"
          label="Mot de passe"
          type="password"
        />
        <q-input
          outlined
          v-model="passConfirmation"
          label="Confirmer"
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
          label="Je créé mon compte"
          type="submit"
        />
      </q-form>
      <br>
    </div>
  </div>
</template>

<style scoped>

</style>

<script setup lang="ts">
import {ref} from 'vue';

let mail = ref();

</script>

<template>
  <div class="form q-pa-lg q-ma-lg">
    <div class="logo">
      <q-img
        width="100%"
        src="../assets/logo-web.webp"
        :ratio="1"
      />
    </div>
    <span class="text-primary text-h5">Mot de passe oublié</span>
    <div class="inputs">
      <q-input
        class="input"
        outlined
        v-model="mail"
        icon="mail"
        label="Adresse Email"
        type="email"
        color="secondary">
        <template v-slot:prepend>
          <q-icon name="mail"/>
        </template>
      </q-input>
      <div class="links">
        <a href="#/login">Se connecter</a>
        <a href="#/register"><b>Je n'ai pas de compte</b></a>
      </div>
      <q-btn
        class="btn "
        color="primary"
        text-color="white"
        unelevated
        label="Envoyer un code de récupération"
      />
    </div>
  </div>
</template>

<style scoped>

.form {
  width: 90%;
  max-width: 400px;
  margin: auto;
  display: flex;
  flex-direction: column;
}

.form .input, .form .logo, .form{
  margin-top: 20px;
}

.links {
  margin-top: 5px;
  margin-bottom: 25px;
  display: flex;
  justify-content: space-between;
  flex-direction: row;
}

.btn {
  width: 70%;
  border-radius: 10px;
  margin: 10px 15%;
}

.text-primary {
  font-family: 'lato', sans-serif;
  font-weight: bold;
}

.links * {
  text-decoration: none;
  color: #4FA4BB;
}

.btn-log img {
  width: 32px;
  height: 32px;
}

.logo {
  margin-bottom: 20px;
}

/* RESPONSIVE */
@media screen and (min-width: 600px) {
  .form {
    width: 60%;
  }
}

@media screen and (min-width: 1200px) {
  .form {
    width: 40%;
  }
}
</style>

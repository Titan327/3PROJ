<script setup lang="ts">
import {ref} from 'vue';

let userName = ref();
let password = ref();

</script>

<template>
  <div class="form q-pa-lg q-ma-lg">
    <div class="logo">
      <q-img
        width="100%"
        src="../assets/logo.png"
        :ratio="1"
      />
    </div>
    <span class="text-primary text-h5">Me connecter</span>
    <div class="inputs">
      <q-input
        class="input"
        outlined
        v-model="userName"
        icon="user"
        label="Nom d'utilisateur"
        color="secondary">
        <template v-slot:prepend>
          <q-icon name="person"/>
        </template>
      </q-input>
      <q-input
        class="input"
        outlined
        v-model="password"
        label="Mot de passe"
        type="password"
        color="secondary">
        <template v-slot:prepend>
          <q-icon name="key" />
        </template>
      </q-input>
      <div class="links">
        <a href="#/register">S'inscrire</a>
        <a href="#/reset-pwd"><b>Mot de passe oublié</b></a>
      </div>
      <q-btn
        class="btn "
        color="primary"
        text-color="white"
        unelevated
        label="Se connecter"
      />
      <div class="external-services">
        <q-item-label class="text-secondary">Connexion avec:</q-item-label>

        <div class="btns">
          <q-btn
            @click="console.log('Google')"
            class="btn-log shadow-6">
            <img src="/../src/assets/icons/googleIcon.svg" alt="Google"/>
          </q-btn>
          <q-btn
            @click="console.log('Apple')"
            class="btn-log shadow-6">
            <img src="/../src/assets/icons/appleIcon.svg" alt="Apple"/>
          </q-btn>
          <q-btn
            @click="console.log('Microsoft')"
            class="btn-log shadow-6">
            <img src="/../src/assets/icons/microsoftIcon.svg" alt="Microsoft"/>
          </q-btn>
          <q-btn
            @click="console.log('Facebook')"
            class="btn-log shadow-6">
            <img src="/../src/assets/icons/facebookIcon.svg" alt="Facebook"/>
          </q-btn>
        </div>
      </div>
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

.form .input, .form .logo, .form .external-services {
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
  height: 32px;
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

.btn-log {
  width: 48px;
  height: 48px;
  margin: 10px 10px;
}

.btn-log img {
  width: 32px;
  height: 32px;
}

.external-services .btns {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-evenly;
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

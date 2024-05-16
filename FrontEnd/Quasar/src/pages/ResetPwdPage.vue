<script setup lang="ts">
import {ref} from 'vue';
import { api } from 'boot/axios';
import { useQuasar } from 'quasar';

let mail = ref();
const $q = useQuasar();

async function sendMail() {
  if(!mail.value){
    $q.notify({
      type: 'negative',
      message: 'Veuillez renseigner votre adresse email'
    })
    return
  }
  try{
    const response = await api.post('/auth/forgottenPassword', {email: mail.value})
    if (response.data) {
      $q.notify({
        type: 'positive',
        message: 'Un email de récupération vous a été envoyé'
      })
    }
  }
  catch (e) {
    console.log(e)
    if(e.response.status === 449){
      $q.notify({
        type: 'negative',
        message: 'Aucun compte n\'est associé à cette adresse email'
      })
    }
    else
    $q.notify({
      type: 'negative',
      message: 'Une erreur s\'est produite lors de l\'envoi de l\'email de récupération'
    })
  }
}

const checkBasicEmailSyntax = (value) => {
  const basicEmailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return basicEmailRegex.test(value) || 'Adresse e-mail invalide';
};
</script>

<template>
  <div class="form q-pa-lg q-ma-lg">
    <div class="logo">
      <q-img
        width="100%"
        src="assets/logo-web.webp"
        :ratio="1"
      />
    </div>
    <span class="text-primary text-h5 text-grey-3">Mot de passe oublié</span>
    <div class="inputs">
      <q-input
        class="input"
        outlined
        v-model="mail"
        icon="mail"
        label="Adresse Email"
        type="email"
        color="secondary"
        dark
        :rules="[checkBasicEmailSyntax]"
      >
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
        @click="sendMail"
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
  color: #ffa31a;
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

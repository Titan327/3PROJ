<script setup lang="ts">
import { computed, ref } from 'vue';
import {back } from 'boot/axios';
import { useQuasar } from 'quasar';
import { useRoute, useRouter } from 'vue-router';

const $q = useQuasar();
const router = useRouter();
const route = useRoute();
const token = route.params.token;

let pass = ref();
let passConfirmation = ref()
let loading = ref(false)

const checkPasswordMatch = (value) => {
  return new Promise((resolve) => {
    setTimeout(() => {
        resolve((value === pass.value) || "Les mots de passe ne correspondent pas");
      },
    );
  });
};

const checkPasswordComplexity = (value) => {
  //regex mot de passe
  const hasLowerCase = /[a-z]/.test(value);
  const hasUpperCase = /[A-Z]/.test(value);
  const hasDigit = /\d/.test(value);
  const hasSpecialChar = /[@$!%*?&-_]/.test(value);

  let errorMessage = "Le mot de passe doit contenir au moins";
  if (!hasLowerCase) errorMessage += " des minuscules,";
  if (!hasUpperCase) errorMessage += " des majuscules,";
  if (!hasDigit) errorMessage += " des chiffres,";
  if (!hasSpecialChar) errorMessage += " un caractère spécial,";

  if (!hasLowerCase || !hasUpperCase || !hasDigit || !hasSpecialChar) {
    errorMessage = errorMessage.slice(0, -1);
  }

  return (hasLowerCase && hasUpperCase && hasDigit && hasSpecialChar && value.length >= 8) || errorMessage;
};

const checkNotNull = (value) => {
  return !!value || "Champ Obligatoire";
};

const computeIsMatchingPassword = computed(() => {
  if(passConfirmation.value === pass.value){
    return true;
  }
  return false;

});

async function changePassword() {
  loading.value = true;
  if(computeIsMatchingPassword.value){
    try {
      const response = await back.post("api/auth/resetPassword", {
          password : pass.value
      },
        {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });
      if (response.data) {
        $q.notify({
          type: 'positive',
          message: 'Mot de passe modifié'
        })
        router.push('/#/login');
      }
    }
    catch (error) {
      $q.notify({
        type: 'negative',
        message: 'Une erreur est survenue'
      })

    }
  }
  else {
    $q.notify({
      type: 'negative',
      message: 'Les mots de passe ne correspondent pas',
    })
  }
  loading.value = false;
}
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
        v-model="pass"
        label="Nouveau mot de passe"
        hide-bottom-space
        type="password"
        :rules="[checkNotNull, checkPasswordComplexity]"
        dark
        color="secondary"

      />
      <q-input
        class="input"
        outlined
        v-model="passConfirmation"
        label="Confirmer"
        type="password"
        :rules="[checkNotNull, checkPasswordMatch]"
        dark
        color="secondary"

      />
      <div class="links text-secondary">
        <a class="text-secondary" href="#/login">Se connecter</a>
        <a class="text-secondary" href="#/register"><b>Je n'ai pas de compte</b></a>
      </div>
      <q-btn
        class="btn"
        color="secondary"
        text-color="white"
        unelevated
        @click="changePassword"
        :loading="loading"
        label="Réinitialiser mon mot de passe"
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

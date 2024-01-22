<script setup lang='ts'>
import {ref} from 'vue';
import {DefaultUser} from 'src/interfaces/user.interface';
import {api} from 'boot/axios';

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
  <div class="form q-pa-lg q-ma-lg">
    <div class="inputs">
      <q-form
        @submit="register"
      >
        <span class="text-primary text-h5">Créer un compte</span>
        <q-input
          outlined
          class="input"
          v-model="newUser.firstName"
          label="Nom"
        />
        <q-input
          outlined
          class="input"
          v-model="newUser.lastName"
          label="Prénom"
        />
        <q-input
          outlined
          class="input"
          v-model="newUser.email"
          label="Adresse email"
          type="email"
        />
        <q-input
          outlined
          class="input"
          v-model="newUser.username"
          label="Nom d'utilisateur"
        />
        <q-input
          outlined
          class="input"
          v-model="newUser.birthdate"
          label="Date de naissance"
          type="date"
        />
        <q-input
          class="input"
          outlined
          v-model="pass"
          label="Mot de passe"
          type="password"
        />
        <q-input
          class="input"
          outlined
          v-model="passConfirmation"
          label="Confirmer"
          type="password"
        />
        <div class="links">
          <a href="#/login"><b>Déja un compte ?</b></a>
        </div>
        <q-btn
          class="btn "
          color="primary"
          text-color="white"
          unelevated
          label="Créer mon compte"
          type="submit"
        />
        <div class="external-services">
          <q-item-label class="text-secondary">Inscription avec:</q-item-label>

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
      </q-form>
      <br>
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

.form .input, .form .external-services {
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

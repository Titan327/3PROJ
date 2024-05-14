<script setup lang="ts">
import {ref} from 'vue';
import {api} from 'boot/axios';
import {useQuasar} from 'quasar'
import {useRouter} from 'vue-router';
import {updateUser} from 'stores/userStore';
import * as process from 'node:process';
import GoogleOauth2 from 'components/Oauth2/GoogleOauth2.vue';

const $q = useQuasar();
let loading = ref(false)
const router = useRouter();

let userName = ref(null);
let password = ref(null);
let email = ref(null);
let isPwd = ref(true);


function getCookie(name: string): string | undefined {
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) return parts.pop()?.split(';').shift();
}

(async () => {
  const token = getCookie('jwt');

  if (token) {
    console.log('JWT Token:', token);
  }

})();

async function login() {
  loading.value = true;
  if((userName.value !=null && userName.value!='') && (password.value != null && password.value!='')){
    try {
      let sendUserLogin = null;
      if (userName.value.includes('@')) {
        email.value = userName.value;
      } else {
        sendUserLogin = userName.value;
      }
      const response = await api.post('auth/login', {
        username: sendUserLogin,
        password: password.value,
        email: email.value,
      });

      if (response.data) {
        updateUser(response.data.token);
        await router.push('../');
      }
    }
    catch (error) {
      if (error.response.status === 400) {
        $q.notify({
          type: 'negative',
          message: 'Nom d\'utilisateur ou mot de passe incorrect'
        })
      }
      else {
        $q.notify({
          type: 'negative',
          message: 'Une erreur s\'est produite lors de la connexion'
        })
      }
      loading.value = false;
    }
  }
  else {
    $q.notify({
      type: 'negative',
      message: 'Tout les champs ne sont pas remplis'
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
    <span class="text-grey-2 text-h5">Connexion</span>
    <q-form  @submit="login">
    <div class="inputs">
      <q-input
        class="input"
        outlined
        v-model="userName"
        icon="user"
        label="Nom d'utilisateur"
        dark
        color="secondary">
        <template v-slot:prepend>
          <q-icon name="person"/>
        </template>
      </q-input>
      <q-input
        dark
        class="input"
        outlined
        v-model="password"
        label="Mot de passe"
        :type="isPwd ? 'password' : 'text'"
        color="secondary">
        <template v-slot:prepend>
          <q-icon name="key"/>
        </template>
        <template v-slot:append>
          <q-icon
            :name="isPwd ? 'visibility_off' : 'visibility'"
            class="cursor-pointer"
            @click="isPwd = !isPwd"
          />
        </template>
      </q-input>
      <div class="links">
        <a href="#/register">S'inscrire</a>
        <a href="#/reset-pwd"><b>Mot de passe oublié</b></a>
      </div>
      <q-btn
        class="btn "
        color="secondary"
        text-color="white"
        unelevated
        label="Se connecter"
        type="submit"
        :loading="loading"
      />
      <div class="external-services">
        <q-item-label class="text-secondary q-pa-md">Connexion avec:</q-item-label>

        <div class="btns">

          <GoogleOauth2></GoogleOauth2>

        </div>
      </div>
    </div>
    </q-form>
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

.form .input, .form .logo, .form {
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

.links * {
  text-decoration: none;
  color: #ffa31a;
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

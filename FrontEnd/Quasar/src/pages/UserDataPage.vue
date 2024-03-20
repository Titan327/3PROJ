<script setup lang="ts">

import {computed, ref} from "vue";
import {disconnectUser, getUser} from "stores/userStore";
import {useRouter} from "vue-router";
import {DefaultUser} from "src/interfaces/user.interface";
import {useQuasar} from "quasar";
import {api} from "boot/axios";

const router = useRouter();
let User = ref(DefaultUser());
let isPhotoHover = ref(false);
let loading = ref(false);
const $q = useQuasar();


(async () => {
  const userData = await getUser();
  if (userData != null) {
    User.value = userData;
  }
})();

function disconnect(){
  disconnectUser()
  router.push('/login');
}

let pass = ref();
let passConfirmation = ref()

const checkBasicEmailSyntax = (value) => {
  const basicEmailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return basicEmailRegex.test(value) || 'Adresse e-mail invalide';
};
const checkPasswordMatch = (value) => {
  return new Promise((resolve) => {
    setTimeout(() => {
        resolve((value === pass.value) || "Les mots de passe ne correspondent pas");
      },
    );
  });
};

const computeIsMatchingPassword = computed(() => {
  if(passConfirmation.value === pass.value){
    return true;
  }
  return false;

});


async function modify() {

  loading.value = true;
  if(computeIsMatchingPassword.value){
    try {

      const response = await api.post("auth/register", {
        userInfos:{
          firstname : User.value.firstname,
          lastname : User.value.lastname,
          username : User.value.username,
          email : User.value.email,
          birth_date : User.value.birth_date,
          password : pass.value
        }

      });
      if (response.data) {
        $q.notify({
          type: 'positive',
          message: 'Utilisateur créé'
        })
        router.push('/#/login');
      }
    }
    catch (error) {
      $q.notify({
        type: 'negative',
        message: 'Une erreur s\'est produite lors de l\'inscription'
      })

    }
  }
}
</script>

<template>
  <q-page class="q-pa-md">
    <div class="div-first-last-name">
      <q-card class="transparent no-box-shadow">
        <q-card-section horizontal>
            <div class="q-pa-md q-gutter-sm">
              <q-avatar
                @click="console.log('change img')"
                @mouseenter ="isPhotoHover=true"
                @mouseleave ="isPhotoHover=false"
                size="200px"
              >
                <img :src="User.profile_picture ? User.profile_picture : 'public/assets/defaults/user-default.webp'">
                <div class="absolute-full text-subtitle2 flex flex-center"
                v-if="isPhotoHover">
                  Modifier
                </div>
              </q-avatar>
            </div>
          <q-card-section>
            <q-item-label class="text-h4">{{ `${User.firstname} ${User.lastname}` }}</q-item-label>
            <q-item-label class="text-subtitle1">{{ `@${User.username}` }}</q-item-label>

          </q-card-section>
        </q-card-section>
      </q-card>

    </div>
    <div class="inputs">
      <q-form
        @submit="modify"
      >
        <q-input
          style="margin-top: 20px;"
          outlined
          class="input"
          v-model="User.firstname"
          label="Nom"
          dark
          hide-bottom-space
        />
        <q-input
          outlined
          class="input"
          v-model="User.lastname"
          label="Prénom"
          dark
          hide-bottom-space
        />
        <q-input
          outlined
          class="input"
          v-model="User.email"
          label="Adresse email"
          type="email"
          :rules="[checkBasicEmailSyntax]"
          dark
          hide-bottom-space
        />
        <q-input
          outlined
          class="input"
          v-model="User.username"
          label="Nom d'utilisateur"
          dark
          hide-bottom-space
        />
        <q-input
          outlined
          class="input"
          v-model="User.birth_date"
          label="Date de naissance"
          type="date"
          dark
          hide-bottom-space
        />
        <q-input
          class="input"
          outlined
          v-model="pass"
          label="Mot de passe"
          type="password"
          dark
          hide-bottom-space
        />
        <q-input
          class="input"
          outlined
          v-model="passConfirmation"
          label="Confirmer"
          type="password"
          :rules="[checkPasswordMatch]"
          dark
          hide-bottom-space
        />
      </q-form>
      <br>
    </div>
    <div class="row items-center justify-evenly">
      <q-btn clickable :onclick="disconnect" color="red" class="text-white q-mx-auto" rounded>
        Se déconnecter
      </q-btn>
    </div>
  </q-page>
</template>

<style scoped>
.inputs {
  width: 80%;
  display: flex;
  flex-direction: column;
}
.input{
  margin-bottom: 12px;
}

</style>

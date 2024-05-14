<script setup lang='ts'>
import {computed, ref} from 'vue';
import {DefaultUser} from 'src/interfaces/user.interface';
import {api} from 'boot/axios';
import {useQuasar} from 'quasar';
import {useRouter} from 'vue-router';
import process from "node:process";
import GoogleOauth2 from "components/Oauth2/GoogleOauth2.vue";

const $q = useQuasar();
let loading = ref(false)
const router = useRouter();

let newUser = ref(DefaultUser());
let pass = ref();
let passConfirmation = ref()

const checkBasicEmailSyntax = (value) => {
  const basicEmailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return basicEmailRegex.test(value) || 'Adresse e-mail invalide';
};
const checkPasswordComplexity = (value) => {
  //regex mot de passe
  const hasLowerCase = /[a-z]/.test(value);
  const hasUpperCase = /[A-Z]/.test(value);
  const hasDigit = /\d/.test(value);
  const hasSpecialChar = /[@$!%*?&]/.test(value);

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
const checkAge = (value) => {
  if (!value) {
    return "Veuillez fournir votre date de naissance.";
  }

  const birthdateTimestamp = new Date(value).getTime();
  const eighteenYearsAgo = new Date();
  eighteenYearsAgo.setFullYear(eighteenYearsAgo.getFullYear() - 18);
  const eighteenYearsAgoTimestamp = eighteenYearsAgo.getTime();

  return birthdateTimestamp <= eighteenYearsAgoTimestamp || "Vous devez avoir plus de 18 ans.";
};
const checkPasswordMatch = (value) => {
  return new Promise((resolve) => {
    setTimeout(() => {
      // Votre logique de validation asynchrone ici
      resolve((value === pass.value) || "Les mots de passe ne correspondent pas");
    },
    );
  });
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
async function register() {

  loading.value = true;
  if(computeIsMatchingPassword.value){
    try {

      const response = await api.post("auth/register", {
      userInfos:{
        firstname : newUser.value.firstname,
        lastname : newUser.value.lastname,
        username : newUser.value.username,
        email : newUser.value.email,
        birth_date : newUser.value.birth_date,
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
  else {
    $q.notify({
      type: 'negative',
      message: 'Tout les champs ne sont pas remplis'
    })
  }
  loading.value = false;
}


async function loginGoogle() {

  const authWindow = window.open(`${process.env.URL_BACKEND}api/oauth2/google`, '_blank', 'height=600,width=600');

  window.addEventListener('message', (event) => {

    if (event.data.token){
      console.log('Message reçu de la fenêtre enfant :', event.data.token);
      sessionStorage.setItem('userToken', event.data.token);
      window.location.href = '/';
    }

  });
}
</script>

<template>
    <div class="form q-pa-lg q-ma-lg flex row">
      <div class="inputs">
        <q-form
          @submit="register"
        >
          <span class="text-grey-2 text-h5">Créer un compte</span>
          <q-input
            style="margin-top: 20px;"
            outlined
            class="input"
            v-model="newUser.lastname"
            label="Nom"
            :rules="[checkNotNull]"
            dark
            color="secondary"

          />
          <q-input
            outlined
            class="input"
            v-model="newUser.firstname"
            label="Prénom"
            :rules="[checkNotNull]"
            dark
            color="secondary"

          />
          <q-input
            outlined
            class="input"
            v-model="newUser.email"
            label="Adresse email"
            type="email"
            :rules="[checkNotNull, checkBasicEmailSyntax]"
            dark
            color="secondary"

          />
          <q-input
            outlined
            class="input"
            v-model="newUser.username"
            label="Nom d'utilisateur"
            :rules="[checkNotNull]"
            dark
            color="secondary"

          />
          <q-input
            outlined
            class="input"
            v-model="newUser.birth_date"
            label="Date de naissance"
            type="date"
            :rules="[checkNotNull, checkAge]"
            dark
            color="secondary"

          />
          <q-input
            class="input"
            outlined
            v-model="pass"
            label="Mot de passe"
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
          <div class="links">
            <a href="#/login"><b>Déja un compte ?</b></a>
          </div>
          <q-btn
            class="btn"
            color="secondary"
            text-color="white"
            unelevated
            label="Créer mon compte"
            type="submit"
            :loading="loading"
          />
          <div class="external-services">
            <q-item-label class="text-secondary q-pa-md">Inscription avec:</q-item-label>

            <div class="btns">
              <GoogleOauth2></GoogleOauth2>
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

 .form .external-services {
  margin-top: 10px;
}
.input{
  margin-bottom: 5px;
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
  color: #ffa31a;
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

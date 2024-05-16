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

let isPwd = ref(true);
let isPwd2 = ref(true);

const checkBasicEmailSyntax = (value) => {
  const basicEmailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return basicEmailRegex.test(value) || 'Adresse e-mail invalide';
};
const checkPasswordComplexity = (value) => {
  // Regex mot de passe
  const hasLowerCase = /[a-z]/.test(value);
  const hasUpperCase = /[A-Z]/.test(value);
  const hasDigit = /\d/.test(value);
  const specialCharRegex = /[@$!%*?&]/g;
  const specialCharMatches = value.match(specialCharRegex);
  const hasTwoSpecialChars = specialCharMatches && specialCharMatches.length >= 2;

  let errorMessage = "Le mot de passe doit contenir au moins";
  if (!hasLowerCase) errorMessage += " des minuscules,";
  if (!hasUpperCase) errorMessage += " des majuscules,";
  if (!hasDigit) errorMessage += " des chiffres,";
  if (!hasTwoSpecialChars) errorMessage += " 2 caractères spéciaux (@ $ ! % * ? &),";
  if (value.length < 8) errorMessage += " et doit avoir au moins 8 caractères.";

  if (!hasLowerCase || !hasUpperCase || !hasDigit || !hasTwoSpecialChars || value.length < 8) {
    errorMessage = errorMessage.slice(0, -1);
  }

  return (hasLowerCase && hasUpperCase && hasDigit && hasTwoSpecialChars && value.length >= 8) || errorMessage;
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
      if (error.response.data.message == "Email already taken") {
        $q.notify({
          type: 'negative',
          message: 'Cette adresse email est déjà utilisée'
        })
      }
      else if (error.response.data.message == "Username already taken") {
        $q.notify({
          type: 'negative',
          message: 'Ce nom d\'utilisateur est déjà utilisé'
        })
      }
      else{
        $q.notify({
          type: 'negative',
          message: 'Une erreur s\'est produite lors de l\'inscription'
        })
      }
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
  <q-page class="row items-center justify-center">
    <div class="form q-pa-lg q-ma-lg flex row w-50">
      <div class="inputs">
        <q-form
          @submit="register"
        >
          <span class="text-grey-2 text-h5">Créer un compte Bill Cutting</span>
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
            :type="isPwd ? 'password' : 'text'"
            :rules="[checkNotNull, checkPasswordComplexity]"
            dark
            color="secondary"
          > <template v-slot:append>
            <q-icon
              :name="isPwd ? 'visibility_off' : 'visibility'"
              class="cursor-pointer"
              @click="isPwd = !isPwd"
            />
          </template></q-input>
          <q-input
            class="input"
            outlined
            v-model="passConfirmation"
            label="Confirmer"
            :type="isPwd2 ? 'password' : 'text'"
            :rules="[checkNotNull, checkPasswordMatch]"
            dark
            color="secondary"
          > <template v-slot:append>
            <q-icon
              :name="isPwd2 ? 'visibility_off' : 'visibility'"
              class="cursor-pointer"
              @click="isPwd2 = !isPwd2"
            />
          </template></q-input>

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
      </div>
    </div>
    <div class="q-pa-lg q-ma-lg w-50  items-center justify-center">
      <div class="logo">
        <q-img
          width="60%"
          src="assets/logo-1800.png"
          :ratio="1"
        />
      </div>
    </div>
  </q-page>
</template>

<style scoped>

.logo {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}
.form {
  width: 100%;
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

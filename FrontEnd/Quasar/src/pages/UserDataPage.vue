<script setup lang="ts">

import {computed, ref} from "vue";
import {disconnectUser, getUser} from "stores/userStore";
import {useRouter} from "vue-router";
import {DefaultUser} from "src/interfaces/user.interface";
import {useQuasar} from "quasar";
import {api} from "boot/axios";
import DialogInvitintoGroup from "components/Groups/DialogInvitintoGroup.vue";
import DialogUpdateImage from "components/Common/DialogUpdateImage.vue";

const router = useRouter();
let User = ref(DefaultUser());
let modifiedUser = ref(DefaultUser());
const $q = useQuasar();

let loading = ref(false);

let isPhotoHover = ref(false);
let expendUserDatas = ref(true);
let expendPasswordData = ref(false);
let expendPaymentMethod = ref(false);
let dialogModifyPp = ref(false);

let isUserDataModified = ref(false);
let isMailModified = ref(false);


let pass = ref();
let pass2 = ref();
let newPass = ref();
let newPassConfirmation = ref();

const paiementsMethod = ref();

(async () => {
  await getUserData();
})();

async function getUserData() {
  const userData = await getUser();
  if (userData != null) {
    User.value = userData;
    modifiedUser.value = userData;
  }
}

function disconnect(){
  disconnectUser()
  router.push('/login');
}


async function openDialogPP(){

       dialogModifyPp.value = true;
      $q.dialog({
        component: DialogUpdateImage,

        componentProps: {
          isOpen: dialogModifyPp,
        }
      }).onDismiss(() => {
        window.location.reload();
      })
}

async function changeUserPassword(){
  try {
    if (newPass.value != newPassConfirmation.value ){
      $q.notify({
        type: 'negative',
        message: 'Les mots de passe ne correspondent pas'
      })
      return
    }
    loading.value = true;
    const response = await api.put(`user/${User.value.id}/password`, {
      "password": pass2.value,
      "userInfos": {
        "password": newPass.value,
        "passwordConfirm": newPassConfirmation.value
      }

    });
    if (response.data) {
      $q.notify({
        type: 'positive',
        message: 'Mot de passe mis à jour'
      })
    }
  }
  catch (error) {
    if (error.response.status===401) {
      $q.notify({
        type: 'negative',
        message: 'Mot de passe actuel incorrect'
      })
    }
    else {
      $q.notify({
        type: 'negative',
        message: 'Une erreur s\'est produite, vérifiez que votre nouveau mot de passe correspond aux critères de sécurité'
      })
    }
  }
  loading.value = false;
}

async function changeUserData(){
  try {
    loading.value = true;
    const response = await api.put(`user/${User.value.id}`, {
      "userInfos":
        {
          "firstname": User.value.firstname,
          "lastname": User.value.lastname,
          "username": User.value.username,
          "email": User.value.email,
          "birth_date": User.value.birth_date
        },
        "password": pass.value,
    });
    if (response.data) {
      $q.notify({
        type: 'positive',
        message: 'Vos informations ont été mises à jour'
      })
    }
  }
  catch (error) {
    if (error.response.status===401) {
      $q.notify({
        type: 'negative',
        message: 'Mot de passe incorrect'
      })
    }
    else if (error.response.status===409) {
      $q.notify({
        type: 'negative',
        message: 'Ce nom d\'utilisateur ou cette adresse email est déjà utilisé'
      })
    }
    else {
      $q.notify({
        type: 'negative',
        message: 'Une erreur s\'est produite'
      })
    }
  }
  loading.value = false;
}

</script>

<template>
  <q-page class="q-pa-md">
    <div class="div-first-last-name">
      <q-card class="transparent no-box-shadow">
        <q-card-section horizontal>
            <div class="q-pa-md q-gutter-sm">
              <q-avatar
                @click="openDialogPP"
                @mouseenter ="isPhotoHover=true"
                @mouseleave ="isPhotoHover=false"
                size="200px"
              >
                <img :src="User.profile_picture ? User.profile_picture[2] : 'assets/defaults/user-default.webp'">
                <div class="absolute-full text-subtitle2 flex flex-center text-secondary"
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
    <div class="q-pa-md row items-start q-gutter-md">
      <q-card class="card-user-data bg-primary rounded-borders">
        <q-card-actions>
          <q-item-label class="text-h6 text-head-card q-pa-md" @click="expendUserDatas = !expendUserDatas">Informations personnelles</q-item-label>

          <q-space />

          <q-btn
            color="grey"
            round
            flat
            dense
            :icon="expendUserDatas ? 'keyboard_arrow_up' : 'keyboard_arrow_down'"
            @click="expendUserDatas = !expendUserDatas"
          />
        </q-card-actions>

        <q-slide-transition>
          <div v-show="expendUserDatas">
            <q-separator />
            <q-card-section class="text-subtitle2">
              <div class="inputs">
                <q-form
                  @submit="changeUserData"
                >
                  <q-input
                    style="margin-top: 20px;"
                    outlined
                    class="input"
                    v-model="modifiedUser.lastname"
                    color="secondary"
                    label="Nom"
                    dark
                    hide-bottom-space
                    @update:modelValue="isUserDataModified = true"
                  />
                  <q-input
                    outlined
                    class="input"
                    v-model="modifiedUser.firstname"
                    label="Prénom"
                    color="secondary"
                    dark
                    hide-bottom-space
                    @update:modelValue="isUserDataModified = true"

                  />
                  <q-input
                    outlined
                    class="input"
                    v-model="modifiedUser.email"
                    label="Adresse email"
                    type="email"
                    dark
                    color="secondary"
                    hide-bottom-space
                    @update:modelValue="isUserDataModified = true;isMailModified = true"

                  />
                  <q-input
                    outlined
                    class="input"
                    v-model="modifiedUser.username"
                    label="Nom d'utilisateur"
                    dark
                    color="secondary"
                    hide-bottom-space
                    @update:modelValue="isUserDataModified = true"

                  />
                  <q-input
                    outlined
                    class="input"
                    v-model="modifiedUser.birth_date"
                    label="Date de naissance"
                    type="date"
                    dark
                    color="secondary"
                    hide-bottom-space
                    @update:modelValue="isUserDataModified = true"

                  />
                  <q-input
                    class="input confirm-pwd"
                    outlined
                    v-model="pass"
                    label="Entrez le mot de passe pour confirmer le changement d'Email"
                    type="password"
                    v-if="isMailModified"
                    dark
                    color="secondary"
                    hide-bottom-space
                  />
                  <q-btn
                    v-if="isUserDataModified"
                    color="green"
                    class="save-btn"
                    rounded
                    type="submit"
                    :loading="loading"
                  >
                    Enregistrer
                  </q-btn>
                </q-form>
                <br>
              </div>
            </q-card-section>
          </div>
        </q-slide-transition>
      </q-card>
    </div>

    <div class="q-pa-md row items-start q-gutter-md">
      <q-card class="card-user-data bg-primary rounded-borders">
        <q-card-actions>
          <q-item-label class="text-h6 text-head-card q-pa-md" @click="expendPasswordData = !expendPasswordData">Modifier le mot de passe</q-item-label>

          <q-space />

          <q-btn
            color="grey"
            round
            flat
            dense
            :icon="expendPasswordData ? 'keyboard_arrow_up' : 'keyboard_arrow_down'"
            @click="expendPasswordData = !expendPasswordData"
          />
        </q-card-actions>

        <q-slide-transition>
          <div v-show="expendPasswordData">
            <q-separator />
            <q-card-section class="text-subtitle2">
              <div class="inputs">
                <q-form
                  @submit="changeUserPassword"
                >
                  <q-input
                    class="input"
                    outlined
                    v-model="pass2"
                    label="Mot de passe actuel"
                    type="password"
                    dark
                    color="secondary"
                    hide-bottom-space
                  />
                  <q-input
                    class="input"
                    outlined
                    v-model="newPass"
                    label="Nouveau mot de passe"
                    type="password"
                    dark
                    color="secondary"
                    hide-bottom-space
                  />
                  <q-input
                    class="input"
                    outlined
                    v-model="newPassConfirmation"
                    label="Confirmer nouveau mot de passe"
                    type="password"
                    color="secondary"
                    dark
                    hide-bottom-space
                  />
                  <q-item-label class="text-negative" v-if="newPass != newPassConfirmation">Le nouveau mot de passe et la confirmation ne correspondent pas.</q-item-label>

                  <q-btn
                    v-if="newPass && newPassConfirmation && pass2"
                    color="green"
                    class="save-btn"
                    rounded
                    type="submit"
                    :loading="loading"
                  >
                    Enregistrer
                  </q-btn>
                </q-form>
                <br>
              </div>
            </q-card-section>
          </div>
        </q-slide-transition>
      </q-card>
    </div>

    <div class="q-pa-md row items-start q-gutter-md">
      <q-card class="card-user-data bg-primary rounded-borders">
        <q-card-actions>
          <q-item-label class="text-h6 text-head-card q-pa-md" @click="expendPaymentMethod = !expendPaymentMethod">Moyens de paiement</q-item-label>

          <q-space />

          <q-btn
            color="grey"
            round
            flat
            dense
            :icon="expendPaymentMethod ? 'keyboard_arrow_up' : 'keyboard_arrow_down'"
            @click="expendPaymentMethod = !expendPaymentMethod"
          />
        </q-card-actions>

        <q-slide-transition>
          <div v-show="expendPaymentMethod">
            <q-separator />
            <q-card-section class="text-subtitle2 row items-center justify-evenly">
                <q-img src="assets/card/paypal-card.webp"
                       class="rounded-borders paiement"
              >
                  <div class="absolute-bottom text-subtitle2 text-center">
                    {{ 'test' }}
                  </div>
                </q-img>
            </q-card-section>
          </div>
        </q-slide-transition>
      </q-card>
    </div>
    <div class="row items-center justify-evenly">
      <q-btn clickable :onclick="disconnect" color="red" class="text-white q-mx-auto" rounded>
        Se déconnecter
      </q-btn>
    </div>
  </q-page>
</template>

<style scoped>
.card-user-data{
  width: 100%;
}
.text-head-card{
  margin-bottom: 15px;
}
.inputs {
  width: 80%;
  display: flex;
  margin: auto;
  flex-direction: column;
}
.input{
  margin-bottom: 12px;
}
.save-btn{
  width: 15%;
  margin: 20px 0 0 0;
}
.confirm-pwd{
  margin-top: 50px;
}
.paiement{
  width: 30%;
  margin: 20px;
}

@media screen and (max-width: 1200px) {
  .paiement{
    width: 40%;
  }
}

@media screen and (max-width: 700px) {
  .paiement {
    width: 90%;
  }
}
</style>

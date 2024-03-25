<script setup lang="ts">

import {computed, ref} from "vue";
import {disconnectUser, getUser} from "stores/userStore";
import {useRouter} from "vue-router";
import {DefaultUser} from "src/interfaces/user.interface";
import {useQuasar} from "quasar";
import {api} from "boot/axios";

const router = useRouter();
let User = ref(DefaultUser());
let modifiedUser = ref(DefaultUser());
const $q = useQuasar();

let loading = ref(false);

let isPhotoHover = ref(false);
let expendUserDatas = ref(true);
let expendPasswordData = ref(false);
let expendPaymentMethod = ref(false);

let isUserDataModified = ref(false);
let isMailModified = ref(false);


let pass = ref();
let pass2 = ref();
let newPass = ref();
let newPassConfirmation = ref();

const paiementsMethod = ref();

(async () => {
  const userData = await getUser();
  if (userData != null) {
    User.value = userData;
    modifiedUser.value = userData;
  }
})();

function disconnect(){
  disconnectUser()
  router.push('/login');
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
                <img :src="User.profile_picture ? User.profile_picture : 'assets/defaults/user-default.webp'">
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
                  @submit="modify"
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
                </q-form>
                <q-btn
                v-if="isUserDataModified"
                color="green"
                class="save-btn"
                rounded
                >
                  Enregistrer
                </q-btn>
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
                  @submit="modifyPass"
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
                    :rules="[checkPasswordMatch]"
                    dark
                    hide-bottom-space
                  />
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

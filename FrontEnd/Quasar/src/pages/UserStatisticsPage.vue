<script setup lang="ts">

import {computed, onMounted, ref} from "vue";
import {disconnectUser, getUser} from "stores/userStore";
import {useRouter} from "vue-router";
import {DefaultUser} from "src/interfaces/user.interface";
import {useQuasar} from "quasar";
import {api} from "boot/axios";
import {convertIBAN, redirectBankWebSite, redirectToPaypal} from "stores/globalFunctionsStore"
import DialogUpdateImage from "components/Common/DialogUpdateImage.vue";
import DialogAddPaymentMethod from "components/Common/DialogAddPaymentMethod.vue";
import {UserStatistics} from "src/interfaces/userStatistics.interface";

const router = useRouter();
let User = ref(DefaultUser());
let modifiedUser = ref(DefaultUser());
const $q = useQuasar();

let loading = ref(false);
let mounted = ref(false);
const width = ref(0);

let isPhotoHover = ref(false);
let expendGlobalStats = ref(true);
let dialogModifyPp = ref(false);

const stats = ref<UserStatistics[]>([]);


onMounted(async () => {

  await getUserData();
  await getStats()
  mounted.value=true;

  function getWidth() {
    width.value = window.innerWidth;
  }
  getWidth();
  window.addEventListener('resize', getWidth);
});

async function getUserData() {
  const userData = await getUser();
  if (userData != null) {
    User.value = userData;
    modifiedUser.value = userData;
  }
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

async function getStats(){
  try {
    const response = await api.get(`/users/${User.value.id}/statistics`)
    stats.value = response.data
    console.log(stats.value)
  }
  catch(e){
    console.error(e)
  }
}

</script>

<template>
  <q-page class="q-pa-md">
    <div class="div-first-last-name">
      <q-card class="transparent no-box-shadow">
        <q-card-section :horizontal="width<500? false:true">
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
          <q-item-label class="text-h6 text-head-card q-pa-md" @click="expendGlobalStats = !expendGlobalStats">Statistiques</q-item-label>

          <q-space />

          <q-btn
            color="grey"
            round
            flat
            dense
            :icon="expendGlobalStats ? 'keyboard_arrow_up' : 'keyboard_arrow_down'"
            @click="expendGlobalStats = !expendGlobalStats"
          />
        </q-card-actions>

        <q-slide-transition>
          <div v-show="expendGlobalStats">
            <q-separator />
            <q-card-section class="text-subtitle2" v-for="stat in stats" :key="stat.statistic">
              <div class="column" v-if="stat.statistic=='Average transaction price'">
                <q-item-label class="text-h6">{{stat.transactions}} transactions au total</q-item-label>
                <q-item-label class="text-h6">Prix moyen des transactions : {{stat.average}}€</q-item-label>
              </div>
              <div class="row" v-else-if="stat.statistic=='All transactions amount and number by category'">
                <q-knob
                  v-for="cat in stat.amountByCategories"
                  :key="cat"
                  show-value
                  font-size="12px"
                  size="150px"
                  :v-model="cat"
                  rounded
                  :thickness="0.22"
                  color="teal"
                  track-color="grey-3"
                  class="q-ma-md"
                >
                  {{50}}%
                </q-knob>
              </div>
            </q-card-section>
          </div>
        </q-slide-transition>
      </q-card>
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
</style>

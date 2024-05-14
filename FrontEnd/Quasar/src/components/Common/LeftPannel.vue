<script setup lang="ts">
import { onMounted, ref, watch } from "vue";
import { disconnectUser, getUser } from "stores/userStore";
import { useRouter } from "vue-router";
import {getNotificationsCountData} from "stores/notificationsStore";
import { NotificationBus } from 'boot/eventBus';

const drawer = ref(false);
const miniState = ref(true);
const router = useRouter();
const userString = ref('');
const pictureUrl = ref('');
const notifCount = ref(0);

let width = ref(350);
let height = ref(350);

onMounted(async () => {
  const userData = await getUser();
  if (userData != null) {
    userString.value = `${userData.firstname} ${userData.lastname}`;
    if (userData.profile_picture != null) {
      pictureUrl.value = userData.profile_picture[0];
    }
  }
  function setDrawerState() {
    height.value = window.innerHeight;
    if (window.innerWidth < 1000) {
      miniState.value = true;
    } else {
      miniState.value = false;
    }
  }
  await getCountNotifs()
  setDrawerState();

  window.addEventListener('resize', setDrawerState);
});

NotificationBus.on('new-notif', async () => {
  console.log('get count')
  await getCountNotifs();
});

function disconnect() {
  disconnectUser();
  router.push('/login');
}

async function getCountNotifs(){
  notifCount.value = await getNotificationsCountData(true);
}
</script>

<template>
  <q-drawer
    v-model="drawer"
    :width="width"
    :mini="miniState"
    show-if-above
    :breakpoint="300"

    class="bg-primary text-white border-radius">
      <q-list>
        <q-img
          class="logo-drawer"
          src="assets/logo-500.png"
          style="max-width: 150px"
        />
        <q-item clickable v-ripple  @click="router.push('../');getCountNotifs()">
          <q-item-section avatar>
            <q-icon color="secondary" name="dashboard" />
          </q-item-section>

          <q-item-section>Accueil</q-item-section>
        </q-item>

        <q-item clickable v-ripple @click="router.push('/notifications');getCountNotifs()">
          <q-item-section avatar>
            <q-icon color="secondary" name="notifications"></q-icon>
          </q-item-section>

          <q-item-section>
            <span>Notifications <q-badge v-if="notifCount>0" rounded color="red">{{notifCount}}</q-badge></span>
          </q-item-section>
        </q-item>

        <q-item clickable v-ripple @click="router.push('/groups');getCountNotifs()">
          <q-item-section avatar>
            <q-icon color="secondary" name="group" />
          </q-item-section>

          <q-item-section>Groupes</q-item-section>
        </q-item>

        <q-item clickable v-ripple @click="router.push('/statistics');getCountNotifs()">
          <q-item-section avatar>
            <q-icon color="secondary" name="insights" />
          </q-item-section>

          <q-item-section>Statistiques</q-item-section>
        </q-item>

        <q-item clickable v-ripple  @click="router.push('/user-data');getCountNotifs()">
          <q-item-section avatar>
            <q-icon color="secondary" name="manage_accounts" />
          </q-item-section>

          <q-item-section>Paramètres</q-item-section>
        </q-item>
      </q-list>

    <q-item class="absolute-bottom user-drawer" v-if="!miniState && height>500">
      <q-item-section top avatar>
        <q-avatar>
          <img :src="pictureUrl ? pictureUrl : 'assets/defaults/user-default.webp' ">
        </q-avatar>
      </q-item-section>
      <q-item-section>{{ userString }}</q-item-section>
      <q-item-section avatar clickable @click="disconnect" style="cursor: pointer">
        <q-icon color="red" name="logout" />
        <q-tooltip>
          Se déconnecter
        </q-tooltip>
      </q-item-section>
    </q-item>
    <q-item class="absolute-bottom user-drawer" v-if="miniState && height>500">
      <q-item-section v-ripple top avatar clickable @click="disconnect" >
        <q-avatar>
          <q-icon color="red" name="logout" />
        </q-avatar>
      </q-item-section>
      <q-item-section>{{ userString }}</q-item-section>
    </q-item>
  </q-drawer>

</template>

<style scoped>
.logo-drawer{
  margin-bottom: 30px;
}

.user-drawer{
  margin-bottom: 20px;
}

@media (max-height: 920px) {
  .user-drawer{
    margin-top: 30vh;
  }
}
@media (max-height: 720px) {
  .user-drawer{
    margin-top: 20vh;
  }
}

</style>

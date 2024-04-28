<script setup lang="ts">
import { onMounted, ref, watch } from "vue";
import { disconnectUser, getUser } from "stores/userStore";
import { useRouter } from "vue-router";
import { api } from "boot/axios";

const drawer = ref(false);
const miniState = ref(true);
const router = useRouter();
const userString = ref('');
const pictureUrl = ref('');
const notifCount = ref(0);

let width = ref(350);

onMounted(async () => {
  const userData = await getUser();
  if (userData != null) {
    userString.value = `${userData.firstname} ${userData.lastname}`;
    if (userData.profile_picture != null) {
      pictureUrl.value = userData.profile_picture[0];
    }
    await getNotifications();
  }

  function setDrawerState() {
    if (window.innerWidth < 1000) {
      miniState.value = true;
    } else {
      miniState.value = false;
    }
  }

  setDrawerState();

  window.addEventListener('resize', setDrawerState);
});


function disconnect() {
  disconnectUser();
  router.push('/login');
}



async function getNotifications() {
  try {
    const response = await api.get('notifs/count');
    notifCount.value = response.data.num;
  } catch (error) {
    console.error(error);
  }
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
        <q-item clickable v-ripple  @click="router.push('../')">
          <q-item-section avatar>
            <q-icon color="secondary" name="dashboard" />
          </q-item-section>

          <q-item-section>Accueil</q-item-section>
        </q-item>

        <q-item clickable v-ripple @click="router.push('/notifications')">
          <q-item-section avatar>
            <q-icon color="secondary" name="notifications"></q-icon>
          </q-item-section>

          <q-item-section>
            <span>Notifications <q-badge v-if="notifCount>0" rounded color="red">{{ notifCount }}</q-badge></span>
          </q-item-section>
        </q-item>

        <q-item clickable v-ripple @click="router.push('/groups')">
          <q-item-section avatar>
            <q-icon color="secondary" name="group" />
          </q-item-section>

          <q-item-section>Groupes</q-item-section>
        </q-item>

        <q-item clickable v-ripple @click="router.push('/user-data')">
          <q-item-section avatar>
            <q-icon color="secondary" name="person" />
          </q-item-section>

          <q-item-section>Compte</q-item-section>
        </q-item>

        <q-item clickable v-ripple  @click="router.push('/user-data')">
          <q-item-section avatar>
            <q-icon color="secondary" name="settings" />
          </q-item-section>

          <q-item-section>Paramètres</q-item-section>
        </q-item>
      </q-list>
    <q-expansion-item
    class="user-drawer">
      <template v-slot:header="{}">
        <q-item-section avatar>
          <q-avatar>
            <img :src="pictureUrl ? pictureUrl : 'assets/defaults/user-default.webp' ">
          </q-avatar>
        </q-item-section>

        <q-item-section q-mini-drawer-hide>
          {{ userString }}
        </q-item-section>
      </template>

      <q-card>
        <q-card-section class="disconnect-user">
          <q-chip clickable :onclick="disconnect" color="red" class="text-white">
            Se déconnecter
          </q-chip>
        </q-card-section>
      </q-card>
    </q-expansion-item>
  </q-drawer>

</template>

<style scoped>
.logo-drawer{
  margin-bottom: 30px;
}

.user-drawer{
  margin-top: 40vh;
}

.disconnect-user{
background-color: #1b1b1b;
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

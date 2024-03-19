<script setup lang="ts">

import {ref} from "vue";
import {disconnectUser, getUser} from "stores/userStore";
import {useRouter} from "vue-router";

const drawer = ref(true)
const router = useRouter();
const userString = ref('');
const pictureUrl = ref('');


(async () => {
  const userData = await getUser();
  if (userData != null) {
    userString.value = `${userData.firstname} ${userData.lastname}`;
    if (userData.profile_picture != null) {
      pictureUrl.value = userData.profile_picture;
    }
  }
})();

function disconnect(){
  disconnectUser()
  router.push('/login');
}

</script>

<template>
  <q-drawer
    v-model="drawer"
    :width="350"
    mini-width="250"
    elevated
    class="bg-primary text-white border-radius">
      <q-list>
        <q-img
          class="logo-drawer"
          src="src/assets/logo-500.png"
          style="height: 140px; max-width: 150px"
        />
        <q-item clickable v-ripple  @click="router.push('/#')">
          <q-item-section avatar>
            <q-icon color="secondary" name="dashboard" />
          </q-item-section>

          <q-item-section>Accueil</q-item-section>
        </q-item>

        <q-item clickable v-ripple>
          <q-item-section avatar>
            <q-icon color="secondary" name="notifications" />
          </q-item-section>

          <q-item-section>Notifications</q-item-section>
        </q-item>

        <q-item clickable v-ripple>
          <q-item-section avatar>
            <q-icon color="secondary" name="send" />
          </q-item-section>

          <q-item-section>Messagerie</q-item-section>
        </q-item>

        <q-item clickable v-ripple @click="router.push('/groups')">
          <q-item-section avatar>
            <q-icon color="secondary" name="group" />
          </q-item-section>

          <q-item-section>Groupes</q-item-section>
        </q-item>

        <q-item clickable v-ripple>
          <q-item-section avatar>
            <q-icon color="secondary" name="person" />
          </q-item-section>

          <q-item-section>Compte</q-item-section>
        </q-item>

        <q-item clickable v-ripple>
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
            <img :src="pictureUrl ? pictureUrl : 'src/assets/defaults/user-default.webp'">
          </q-avatar>
        </q-item-section>

        <q-item-section>
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
background-color: #171733;
}

</style>

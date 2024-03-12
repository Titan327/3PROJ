<script setup lang="ts">

import {ref} from "vue";
import {getUser} from "stores/userStore";

const drawer = ref(true)

const userString = ref('');

(async () => {
  const userData = await getUser();
  if (userData.firstName != null) {
    userString.value = userData.firstName;
  }})

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
        <q-item clickable v-ripple>
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
            <img src="https://cdn.quasar.dev/img/boy-avatar.png">
          </q-avatar>
        </q-item-section>

        <q-item-section>
          {{ userString }}
        </q-item-section>
      </template>

      <q-card>
        <q-card-section class="bg-primary">
            Se déconnecter
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

</style>

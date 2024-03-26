<script setup lang="ts">
import { ref } from 'vue';
import {EtatTotalPaidComponent} from "src/interfaces/types";
import PaymentsMethods from "components/Index/PaymentsMethods.vue";
import TotalPaid from "components/Index/TotalPaid.vue";

const drawerOpen = ref(false);
const messages = ref ([]);

function openCloseDrawer() {
  drawerOpen.value = !drawerOpen.value;
}
</script>

<template>
  <q-btn
    class="q-ma-md"
    @click="openCloseDrawer"
    color="secondary"
    icon="chat"
    fab
    round
  />
  <q-page-sticky position="bottom-right" :offset="[18, 18]">
    <q-btn fab  icon="chat" color="secondary" @click="openCloseDrawer" />
  </q-page-sticky>

  <q-drawer
    side="right"
    v-model="drawerOpen"
    :width="400"
    mini-width="300"
    class="message bg-primary">

    <div class="content">

      <q-item-label class="text-h4 q-pa-md row">Messages</q-item-label>

      <div class="q-pa-md row justify-center">
        <div style="width: 100%; max-width: 400px">
          <q-chat-message
            name="Ryan"
            avatar="https://cdn.quasar.dev/img/avatar1.jpg"
            :text="['Selem, ça va?']"
            sent
            stamp="10:20"

          />
          <q-chat-message
            name="Titan"
            bg-color="secondary"
            avatar="https://cdn.quasar.dev/img/avatar2.jpg"
            :text="['Non les photo marchent pas']"
            stamp="10:23"

          />
        </div>
      </div>
    </div>

  </q-drawer>
</template>

<style scoped>
.message{
  border-radius: 15px;
}
</style>

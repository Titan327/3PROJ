<script setup lang="ts">


import {ref} from "vue";
import PaymentsMethods from "components/Index/PaymentsMethods.vue";

const drawer = ref(true)

</script>

<template>
  <q-drawer
    side="right"
    v-model="drawer"
    :width="400"
    mini-width="300"
    class="right-drawer bg-dark">

    <PaymentsMethods etat="positive"></PaymentsMethods>
  </q-drawer>

</template>

<style scoped>
.right-drawer{
  background: #141332;
}
</style>

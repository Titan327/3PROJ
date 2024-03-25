<script setup lang="ts">


import {ref} from "vue";
import PaymentsMethods from "components/Index/PaymentsMethods.vue";
import {EtatTotalPaidComponent} from "src/interfaces/types";
import TotalPaid from "components/Index/TotalPaid.vue";

const drawer = ref(true)

</script>

<template>
  <q-drawer
    side="right"
    v-model="drawer"
    :width="400"
    mini-width="300"
    class="right-drawer bg-dark">

    <div class="content">

    <total-paid :etat="EtatTotalPaidComponent.Positive" />

    <total-paid :etat="EtatTotalPaidComponent.Negative" />

    <PaymentsMethods></PaymentsMethods>
    </div>

  </q-drawer>

</template>

<style scoped>

.right-drawer{
  background: #141332;
}

.content{
  margin-top: 41%;
}
</style>

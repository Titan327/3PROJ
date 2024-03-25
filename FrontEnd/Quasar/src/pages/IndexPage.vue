<script setup lang="ts">
import TotalPaid from "components/Index/TotalPaid.vue";
import LastPaiements from "components/Index/LastPaiements.vue";
import LastGroups from "components/Index/LastGroups.vue";
import { getUser } from "stores/userStore";
import { ref } from "vue";
import {EtatTotalPaidComponent} from "src/interfaces/types";
import RightPannel from "components/Index/RightPannel.vue";

const user = ref(getUser());

const userFirstName = ref('');

(async () => {
  const userData = await getUser();
  if (userData.firstname != null) {
    userFirstName.value = userData.firstname;
  }
})();
</script>

<template>
  <right-pannel></right-pannel>
  <q-page class="row items-center justify-evenly">

    <h3 class="text-h4">Bienvenue {{ userFirstName }} &#x1F44B;</h3>


    <last-groups />

    <last-paiements />

  </q-page>
</template>
<style scoped>
</style>

<script setup lang="ts">
import TotalPaid from "components/Index/TotalPaid.vue";
import LastPaiements from "components/Index/LastPaiements.vue";
import LastGroups from "components/Index/LastGroups.vue";
import { getUser } from "stores/userStore";
import { ref } from "vue";
import {EtatTotalPaidComponent} from "src/interfaces/types";

const user = ref(getUser());

const userFirstName = ref('');

(async () => {
  const userData = await getUser();
  if (userData.firstName != null) {
    userFirstName.value = userData.firstName;
  }
})();
</script>

<template>
  <q-page class="row items-center justify-evenly">

    <h3 class="text-h4">Bienvenue {{ userFirstName }} &#x1F44B;</h3>

    <div class="totalPaid">
      <total-paid :etat="EtatTotalPaidComponent.Positive" />
      <total-paid :etat="EtatTotalPaidComponent.Negative" />
    </div>

    <last-groups />

    <last-paiements />

  </q-page>
</template>
<style scoped>

.totalPaid{
  display: flex;
  justify-content: space-around;
  width: 100%;
}
</style>

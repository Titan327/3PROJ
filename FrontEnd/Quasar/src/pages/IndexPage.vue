<script setup lang="ts">
import TotalPaid from "components/Index/TotalPaid.vue";
import LastPaiements from "components/Index/LastPaiements.vue";
import LastGroups from "components/Index/LastGroups.vue";
import { getUser } from "stores/userStore";
import { ref } from "vue";
import {EtatTotalPaidComponent} from "src/interfaces/types";
import RightPannel from "components/Index/RightPannel.vue";
import PaymentsMethods from "components/Index/PaymentsMethods.vue";

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
  <q-page class="row items-center justify-evenly">

    <div class="full-width full-height justify-around">
      <h3 class="text-h4">Bienvenue {{ userFirstName }} &#x1F44B;</h3>

      <div class="row justify-evenly full-height">
        <div  class="justify-center group-1">

        <last-groups class="bloc-center"></last-groups>

        <last-paiements class="bloc-center"></last-paiements>
        </div>
        <div class="group-2">
          <total-paid :etat="EtatTotalPaidComponent.Positive"/>

          <total-paid :etat="EtatTotalPaidComponent.Negative"/>

          <PaymentsMethods></PaymentsMethods>
        </div>
      </div>
    </div>

  </q-page>
</template>
<style scoped>
.group-1{
  width: 65%;
  margin: 10px auto;
}
.group-2{
  width: 25%;
  margin: 10px auto;
}
.text-h4{
  margin: 10px 50px;
}
@media (max-width: 900px) {
  .group-1{
    width: 90%;
  }
  .group-2{
    width: 90%;
  }
}

</style>

<script setup lang="ts">
import TotalPaid from "components/Index/TotalPaid.vue";
import LastPaiements from "components/Index/LastPaiements.vue";
import LastGroups from "components/Index/LastGroups.vue";
import { getUser } from "stores/userStore";
import {onMounted, ref} from "vue";
import {EtatTotalPaidComponent} from "src/interfaces/types";
import PaymentsMethods from "components/Index/PaymentsMethods.vue";
import {DefaultUser} from "src/interfaces/user.interface";
import DialogWarnAuth from "components/Index/DialogWarnAuth.vue";
import {useQuasar} from "quasar";
import DialogJoinGroup from 'components/Groups/DialogJoinGroup.vue';

const User = ref(DefaultUser());
const $q = useQuasar();
let dialog0Auth = ref(false);

onMounted(async () => {

  User.value = await getUser();
  CheckIs0Auth();

  const tokenGroupCookie = getCookie('join-group');
  if (tokenGroupCookie) {
   openDialogJoin(tokenGroupCookie);
  }
});

function openDialogJoin(tokenGroup: string){
    $q.dialog({
      component: DialogJoinGroup,

      componentProps: {
        tokenGroup: tokenGroup,
      }
    })
}

function CheckIs0Auth(){
  if (User.value.firstname == User.value.lastname){
    dialog0Auth.value = true;
      $q.dialog({
        component: DialogWarnAuth,

        componentProps: {
          isOpen: dialog0Auth,
        }
      })
    return true;
  }
  else{
    return false;
  }
}
function getCookie(name) {
  const cookieArray = document.cookie.split(';');
  for (let cookie of cookieArray) {
    const [cookieName, cookieValue] = cookie.split('=');
    if (cookieName.trim() === name) {
      return cookieValue;
    }
  }
  return null;
}

</script>

<template>
  <q-page class="row items-center justify-evenly">

    <div class="full-width full-height justify-around">
      <h3 class="text-h4">Bienvenue {{ User.firstname }} &#x1F44B;</h3>

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

@media (max-width: 1900px) {
  .group-1{
    width: 55%;
  }
  .group-2{
    width: 35%;
  }
}

@media (max-width: 1300px) {
  .group-1{
    width: 90%;
  }
  .group-2{
    width: 90%;
  }
}

</style>

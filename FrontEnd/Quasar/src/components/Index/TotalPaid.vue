<script setup lang="ts"> import {EtatTotalPaidComponent} from "src/interfaces/types";

import { defineProps, onMounted, ref } from "vue";
import {Group} from "src/interfaces/group.interface";
import {getUser} from "stores/userStore";
import {api} from "boot/axios";

const montantTotal = ref(0);
const displayColor = ref('red');
const titleText = ref('');
const operations = ref(0);
const icon = ref("south_west");

const props = defineProps<{
  etat: EtatTotalPaidComponent;
}>();

const userData = ref(getUser());

onMounted(() => {

  if (props.etat !== EtatTotalPaidComponent.Positive) {
    displayColor.value = 'red';
    titleText.value = 'Reste à payer';
    operations.value = 21;
    montantTotal.value = 12345;
    icon.value = 'north_east'
    const response = api.get(`/user/${userData.value.id}/`);
    //montantTotal.value = response.data;
  }
  else {
    displayColor.value = 'green';
    titleText.value = 'Total payé ce mois-ci';
    operations.value = 12;
    montantTotal.value = 76567;
    icon.value = 'south_west'
  }
});

</script>


<template>

  <q-card class="bloc-paye bg-primary">
    <q-card-section >
      <q-item>
        <q-item-section avatar>
          <q-avatar rounded color="secondary" text-color="white" :icon="icon" />
        </q-item-section>

        <q-item-section>
          <q-item-label class="text-grey-5 text-h6">{{titleText}}</q-item-label>
          <q-item-label class="text-white" caption lines="2">{{montantTotal}}€</q-item-label>
        </q-item-section>

        <q-chip class="chip-status" :color="displayColor" text-color="white">{{operations}} Opérations</q-chip>
      </q-item>
    </q-card-section>
  </q-card>
</template>

<style scoped>

.bloc-paye{
  width: 85%;
  height: 120px;
  border-radius: 15px;
  margin-top: 10%;
}
</style>

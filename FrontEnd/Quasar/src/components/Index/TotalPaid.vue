<script setup lang="ts">
import { EtatTotalPaidComponent } from "src/interfaces/types";
import { defineProps, onMounted, ref } from "vue";
import { Group } from "src/interfaces/group.interface";
import { getUser } from "stores/userStore";
import { api } from "boot/axios";
import {DefaultUser} from "src/interfaces/user.interface";

const montantTotal = ref(0);
const displayColor = ref('red');
const titleText = ref('');
const operations = ref(0);
const icon = ref();
const User = ref(DefaultUser());
const width = ref(0);

const props = defineProps<{
  etat: EtatTotalPaidComponent;
}>();

onMounted(async () => {
  User.value = await getUser()
  try {
    if (props.etat !== EtatTotalPaidComponent.Positive) {
      displayColor.value = 'red';
      titleText.value = 'Reste à payer';
      icon.value = 'north_east';
      const response = await api.get(`users/${User.value.id}/transactions/notRefunded`);
      montantTotal.value = response.data.amount;
      operations.value = response.data.transactions;
    } else {
      displayColor.value = 'green';
      titleText.value = 'Total payé ce mois-ci';
      icon.value = 'south_west';
      const response = await api.get(`users/${User.value.id}/transactions/thisMonth`);
      montantTotal.value = response.data.amount;
      operations.value = response.data.transactions;
    }
  } catch (error) {
    console.error("Une erreur s'est produite lors de la récupération des données :", error);
  }

  function getWidth() {

    width.value = window.innerWidth;
    console.log(width.value);
  }

  getWidth();

  window.addEventListener('resize', getWidth);
});
</script>


<template>

  <q-card class="bloc-paye bg-accent">
    <q-card-section >
      <q-item>
        <q-item-section avatar>
          <q-avatar rounded color="secondary" text-color="white" :icon="icon" />
        </q-item-section>

        <q-item-section>
          <q-item-label class="text-grey-5 text-body1">{{titleText}}</q-item-label>
          <q-item-label class="text-white text-subtitle1" caption lines="2">{{montantTotal}}€</q-item-label>
        </q-item-section>

        <q-chip v-if="operations>0 && width>500" class="chip-status" :color="displayColor" text-color="white">{{operations}} Opérations</q-chip>
        <q-chip v-if="operations==0 && width>500" class="chip-status" :color="displayColor" text-color="white">Pas d'opération</q-chip>
      </q-item>
    </q-card-section>
  </q-card>
</template>

<style scoped>

.bloc-paye{
  width: 100%;
  height: 100px;
  border-radius: 15px;
  margin-top: 50px;
}
</style>

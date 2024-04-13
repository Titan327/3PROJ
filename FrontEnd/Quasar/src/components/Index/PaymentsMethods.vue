<script setup lang="ts"> import {EtatTotalPaidComponent} from "src/interfaces/types";

import { defineProps, onMounted, ref } from "vue";
import {Group} from "src/interfaces/group.interface";
import {getUser} from "stores/userStore";
import {api} from "boot/axios";


const userData = ref(getUser());
const  slide = ref('slide-0');
const paiementsMethod = ref(['carte1','carte2'])

onMounted(() => {
  console.log("payment mounted")
});

</script>


<template>

  <q-card class="bloc-paye bg-accent">
    <q-card-section
      class="q-mx-auto flex">
      <h3 class="text-h6">Moyens de paiement</h3>
      <q-btn
        class="btn"
        unelevated color="secondary"
        label="+"
        round
      />
    </q-card-section>
    <q-card-section
      v-if="paiementsMethod.length > 0">
      <q-carousel
        v-model="slide"
        swipeable
        animated
        control-color="secondary"
        navigation
        arrows
        height="250px"
        class="text-purple rounded-borders caroussel bg-accent q-mx-auto"
      >
          <q-carousel-slide
            v-for="(paiement, index) in paiementsMethod"
            :key="index"
            :name="`slide-${index}`"
          >
            <q-card class="method rounded-borders">
              <q-img src="/public/assets/card/paypal-card.webp" class="rounded-borders">
                <div class="absolute-bottom text-subtitle2 text-center">
                  {{ paiement }}
                </div>
              </q-img>
            </q-card>
          </q-carousel-slide>

      </q-carousel>
    </q-card-section>
    <q-card-section
      v-if="paiementsMethod.length == 0"
      class="q-mx-auto">

      Vous n'avez aucun moyen de paiement enregistré
    </q-card-section>
  </q-card>
</template>

<style scoped>

.bloc-paye{
  width: 100%;
  height: 420px;
  border-radius: 15px;
  display: flex;
  flex-direction: column;
  margin-top: 50px;
}

.caroussel{
  width: 100%;
}
.btn{
  margin: 20px;
}
</style>

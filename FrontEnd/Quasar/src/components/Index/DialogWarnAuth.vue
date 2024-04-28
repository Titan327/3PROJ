<script setup lang="ts">
import { ref } from "vue";
import {useRouter} from "vue-router";

let isOpen = ref(false);
const router = useRouter();

</script>

<template>
  <q-dialog v-model="isOpen">
    <q-card class="bg-primary" style="width: 500px; max-width: 80vw;">
      <q-card-section>
        <div class="text-h5">Bienvenue sur Bill Cutting</div>
      </q-card-section>
      <q-card-section>
        <div class="q-pa-md column justify-between">
           <p class="text-center">
             Nous remarquons que vous vous êtes inscrit(e) en utilisant une méthode d'authentification externe.
             Pour personnaliser au mieux votre expérience avec nous, veuillez nous fournir quelques informations supplémentaires vous concernant.
             <br>
             <br>
             Nous sommes impatients d'en savoir plus sur vous !
           </p>

          <div class="justify-evenly row">
            <q-btn
              color="secondary"
              @click="isOpen= false"
              label="Plus tard"
              class="q-mt-md btn"
              outline
              rounded
              type="dismiss">

            </q-btn>
            <q-btn
              color="secondary"
              @click="router.push('/user-data')"
              label="J'y vais"
              class="q-mt-md btn"
              rounded
              type="confirm">
            </q-btn>
          </div>
          </div>
      </q-card-section>
    </q-card>
  </q-dialog>

</template>

<style scoped>
.btn{
  width: 30%;
  margin-bottom: 15px;
}
</style>

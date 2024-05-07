<script setup lang="ts">
import { useQuasar } from 'quasar';
import {ref, defineProps, onMounted} from "vue";
const $q = useQuasar();

let isOpen = ref(false);
let inviteLink = ref('');

const props = defineProps({
  link: String,
  name: String,
});
onMounted(async () => {

  inviteLink.value = `${process.env.URL_PROD}#/join-group/${props.link}`;

});

async function copyLink() {
  await navigator.clipboard.writeText(inviteLink.value)
    .then(() => {
      $q.notify({
        type: 'positive',
        message: 'Lien copié'
      })
      isOpen.value = false;
    })
    .catch(err => {
      console.error('Impossible de copier la variable dans le presse-papiers : ', err);
    });
}
</script>

<template>
  <q-dialog v-model="isOpen">
    <q-card class="bg-primary" style="width: 500px; max-width: 80vw;">
      <q-card-section class="row items-center">
        <div class="text-h5">
          <q-item-label class="text-h5">Créer une invitation</q-item-label>
          <q-item-label class="text-subtitle1">{{props.name}}</q-item-label>
        </div>
        <q-space />
        <q-btn icon="close" flat round dense v-close-popup />

      </q-card-section>
      <q-card-section>
        <q-item-label class="text-subtitle2">Vous pouvez copier ce lien d'invitation et l'envoyer à vos amis</q-item-label>
        <div class="bloc-link row items-center justify-evenly">
          <q-input v-model="inviteLink" outlined dense readonly dark class="link" color="secondary"></q-input>
          <q-btn color="secondary" class="btn" outline @click="copyLink" icon="content_copy" > </q-btn>
        </div>
      </q-card-section>
    </q-card>
  </q-dialog>

</template>

<style scoped>

.btn {
  border-radius: 10px;
  margin: 0 5px;
  height: 42px;
}
.link{
  border-radius: 10px;
  margin: 0 5px;
  width: 80%;
}
.bloc-link{
  width: 100%;
  margin: 20px auto;
  height: 42px;
}
</style>

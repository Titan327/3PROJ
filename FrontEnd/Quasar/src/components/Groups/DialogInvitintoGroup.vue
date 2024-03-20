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

  inviteLink.value = `https://3proj-front.tristan-tourbier.com/#/join-group/${props.link}`;

});

async function copyLink() {
  await navigator.clipboard.writeText(inviteLink.value)
    .then(() => {
      $q.notify({
        type: 'positive',
        message: 'Lien copié'
      })
    })
    .catch(err => {
      console.error('Impossible de copier la variable dans le presse-papiers : ', err);
    });
}
</script>

<template>
  <q-dialog v-model="isOpen">
    <q-card class="bg-primary" style="width: 500px; max-width: 80vw;">
      <q-card-section>
        <q-item-label class="text-h5">Créer une invitation</q-item-label>
        <q-item-label class="text-subtitle1">{{props.name}}</q-item-label>
      </q-card-section>
      <q-card-section>
        <q-item-label class="text-subtitle2">Vous pouvez copier ce lien d'invitation et l'envoyer à vos amis</q-item-label>
        <div class="bloc-link column">
          <q-input v-model="inviteLink" outlined dense readonly dark color="secondary"></q-input>
          <q-btn
            @click="copyLink"
            label="Copier"
            color="secondary"
            class="btn">
          </q-btn>
        </div>
      </q-card-section>
    </q-card>
  </q-dialog>

</template>

<style scoped>

.btn {
  border-radius: 10px;
  margin: 10px 25%;
}
.bloc-link{
  width: 80%;
  margin: 20px auto;
}
</style>

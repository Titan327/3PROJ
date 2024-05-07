<script setup lang="ts">
import { useQuasar } from 'quasar';
import {ref, defineProps} from "vue";
import {api} from "boot/axios";
const $q = useQuasar();

let isOpen = ref(false);
let inviteLink = ref('');
let loading = ref(false);

let usages = ref(5);
let validity = ref(7);
let linkGenerated = ref(false);

const props = defineProps({
  groupName: String,
  groupId: Number,
});

async function generateLink() {

  loading.value = true;
  const today = new Date();
  const expirationDate = new Date(today);
  expirationDate.setDate(today.getDate() + validity.value);

  try {
    const response = await api.post(`groups/${props.groupId}/createInvitation`, {

      remaining_uses: usages.value,
      expiration_date:expirationDate

    });
    inviteLink.value = `${process.env.URL_PROD}#/join-group/${response.data.invitation.token}`;
    linkGenerated.value = true;
  }
  catch (e) {
    console.error(e)
  }
  loading.value = false;
}

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
          <q-item-label class="text-h5">Créer une invitation personnalisée</q-item-label>
          <q-item-label class="text-subtitle1">{{props.groupName}}</q-item-label>
        </div>
        <q-space />
        <q-btn icon="close" flat round dense v-close-popup />
      </q-card-section>
      <q-card-section class="column">
        <q-input
          @update:model-value="linkGenerated=false"
          class="input"
          type="number"
          v-model.number="usages"
          outlined  label="Quantité d'utilisations possibles"
          dark
          color="secondary">
        </q-input>
        <q-input
          @update:model-value="linkGenerated=false"
          class="input"
          type="number"
          v-model.number="validity"
          outlined label="Durée de validité"
          dark
          color="secondary"
          suffix="Jours"></q-input>
        <q-space></q-space>
        <q-btn
          color="secondary"
          class="q-pa-sm q-mx-auto"
          label="Générer le lien"
          @click="generateLink()"
          :loading="loading">
        </q-btn>
      </q-card-section>
      <q-card-section v-if="linkGenerated">
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

.input{
  margin: 10px auto;
  width: 80%;
}
</style>

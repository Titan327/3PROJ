<script setup lang="ts">
import { useQuasar } from 'quasar'
import {ref} from "vue";
import {DefaultGroup} from "src/interfaces/group.interface";
import { api, back } from 'boot/axios';
import { getUser } from 'stores/userStore';
import { useRouter } from 'vue-router';

let isOpen = ref(false);
const $q = useQuasar()
const router = useRouter();

const props = defineProps({
  tokenGroup: String,
});

function deleteCookie(name) {
  document.cookie = name + "=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
}

async function joinGroup() {
  try {
    const response = await back.post(`${props.tokenGroup}`);
    if (response.data) {
      $q.notify({
        type: 'positive',
        message: 'Vous avez rejoint le groupe'
      })
      deleteCookie('nomDuCookie');
      router.push('/#/groups');
    }
  }
  catch (error) {
    $q.notify({
      type: 'negative',
      message: 'Une erreur s\'est produite'
    })

  }
}
</script>

<template>
  <q-dialog v-model="isOpen">
    <q-card class="bg-primary" style="width: 500px; max-width: 80vw;">
      <q-card-section>
        <div class="text-h5">Invitation en attente</div>
      </q-card-section>
        <div class="form q-pa-lg q-ma-lg flex row">
          <q-item-label class="text-h6">Vous avez été invité a rejoindre un groupe</q-item-label>
          <q-btn
            @click="joinGroup"
            label="Rejoindre"
            color="secondary"
            class="btn"
          ></q-btn>
        </div>
    </q-card>
  </q-dialog>

</template>

<style scoped>
.input{
  margin-bottom: 15px;
}

.form {
  width: 90%;
  max-width: 600px;
  margin: auto;
  display: flex;
  flex-direction: column;
}

.btn {
  width: 50%;
  border-radius: 10px;
  margin: 10px 25%;
}
</style>

<script setup lang="ts">
import { useQuasar } from 'quasar'
import {ref} from "vue";
import {DefaultGroup} from "src/interfaces/group.interface";
import {api} from "boot/axios";
const $q = useQuasar();

let isOpen = ref(false);
let newGroup = ref(DefaultGroup());
let loading = ref(false);

const checkNotNull = (value) => {
  return !!value || "Champ Obligatoire";
};
async function createGroup() {

  loading.value = true;
    try {
      const response = await api.post("group/create", {

        name: newGroup.value.name,
        description: newGroup.value.description
      });
      if (response.data) {
        $q.notify({
          type: 'positive',
          message: 'Groupe créé'
        })
        isOpen.value= false;
      }
    }
    catch (error) {
      $q.notify({
        type: 'negative',
        message: 'Une erreur s\'est produite lors de la création du groupe'
      })

    }
  loading.value = false;
}


</script>

<template>
  <q-dialog v-model="isOpen">
    <q-card class="bg-primary" style="width: 500px; max-width: 80vw;">
      <q-card-section>
        <div class="text-h5">Créer un groupe</div>
      </q-card-section>
        <div class="form q-pa-lg q-ma-lg flex row">
          <div class="inputs">
            <q-form
              @submit="createGroup"
            >
              <q-input
                style="margin-top: 20px;"
                outlined
                class="input"
                v-model="newGroup.name"
                label="Nom du groupe"
                dark
                color="secondary"
                :rules="[checkNotNull]"
              />
              <q-input
                outlined
                class="input"
                v-model="newGroup.description"
                label="Description"
                dark
                color="secondary"
                autogrow
                :rules="[checkNotNull]"
                hide-bottom-space
              />
              <q-btn
                class="btn"
                color="secondary"
                text-color="white"
                unelevated
                label="Créer le groupe"
                type="submit"
                :loading="loading"
                hide-bottom-space
              />
            </q-form>
          </div>
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

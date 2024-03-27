<script setup lang="ts">
import { useQuasar } from 'quasar'
import { ref } from "vue";
import { DefaultGroup } from "src/interfaces/group.interface";
import { api } from "boot/axios";
const $q = useQuasar();

let isOpen = ref(false);
let loading = ref(false);

let file = ref(null);
function chackHasFile(): boolean {
  if(file.value == null){
    $q.notify({
      type: 'negative',
      message: 'Veuillez choisir une image'
    })
    loading.value = false;
    return false;
  }
  return true;
}
async function update() {
  loading.value = true;
  try {
    if (!chackHasFile()) {
      return;
    }
    const formData = new FormData();
    formData.append('image', file.value);

    const response = await api.post("/img/upload/profile-picture", formData);

    if (response.data) {
      $q.notify({
        type: 'positive',
        message: 'L\'image a été mise à jour'
      })
      isOpen.value= false;
    }
  }
  catch (error) {
    $q.notify({
      type: 'negative',
      message: 'Une erreur s\'est produite lors de la mise à jour de l\'image'
    })
  }
  loading.value = false;
}
</script>

<template>
  <q-dialog v-model="isOpen">
    <q-card class="bg-primary" style="width: 500px; max-width: 80vw;">
      <q-card-section>
        <div class="text-h5">Modifier l'image</div>
      </q-card-section>
      <q-card-section>
        <div class="q-pa-md column justify-between">
            <q-file
              v-model="file"
              label="Choisir une image"
              filled
              dark
              color="secondary"
            >
              <template v-slot:prepend>
                <q-icon name="image" />
              </template>
            </q-file>

          <q-btn
            color="secondary"
            @click="update"
            :loading="loading"
            label="Mettre à jour"
            class="q-mt-md"
          type="confirm">
          </q-btn>
        </div>
      </q-card-section>
    </q-card>
  </q-dialog>

</template>

<style scoped>
.input{
  margin-bottom: 15px;
}
</style>

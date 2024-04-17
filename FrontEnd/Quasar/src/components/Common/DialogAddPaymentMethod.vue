<script setup lang="ts">
import { useQuasar } from 'quasar'
import {onMounted, ref} from "vue";
import { api } from "boot/axios";
const $q = useQuasar();

let isOpen = ref(false);
let loading = ref(false);
const optn = ref(["RIB","PayPal"]);
let paymentType = ref("RIB")

let paypalId = ref("");
let file = ref(null);

const props = defineProps({
  userId: Number,
  type : String,
});

onMounted(async () => {
  paymentType.value = props.type;
});

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

async function postRib() {
  loading.value = true;
  try {
    if (!chackHasFile()) {
      return;
    }
    const formData = new FormData();
    formData.append('image', file.value);

    let response = null;

    if(props.groupId){
      response = await api.post(`/img/upload/group-picture/${props.groupId}`, formData);
    }
    else{
      response = await api.post("/img/upload/profile-picture", formData);
    }

    if (response.data) {
      $q.notify({
        type: 'positive',
        message: 'Moyen de paiement ajouté avec succès'
      })
      isOpen.value= false;
    }
  }
  catch (error) {
    $q.notify({
      type: 'negative',
      message: 'Une erreur s\'est produite'
    })
  }
  loading.value = false;
}

async function postPayPal() {
  loading.value = true;
  try {
    const response = await api.post(`/user/${props.userId}/paymentMethode`, {
      "type": "Paypal email",
      "value": paypalId,
    });

    if (response.data) {
      $q.notify({
        type: 'positive',
        message: 'Profil PayPal ajouté avec succès'
      });
      isOpen.value = false;
    }
    loading.value = false;
  } catch (error) {
    loading.value = false;
    if (error.code === 409) {
      $q.notify({
        type: 'negative',
        message: 'Un moyen de paiement similaire existe déjà'
      });
    } else {
      console.error(error);
      $q.notify({
        type: 'negative',
        message: 'Une erreur s\'est produite'
      });
    }
  }
}
const redirectToPaypal = () => {
  if(paypalId.value != ''){
    window.open(`https://paypal.me/${paypalId.value}`, '_blank');
  }
};
</script>

<template>
  <q-dialog v-model="isOpen">
    <q-card class="bg-primary" style="width: 500px; max-width: 80vw;">
      <q-card-section>
        <div class="text-h5">Ajouter un moyen de paiement</div>
      </q-card-section>
      <q-card-section>
          <q-select
            outlined
            v-model="paymentType"
            :options="optn"
            label="Type de moyen de paiement"
            color="secondary"
            dark
            stack-label
          >
          </q-select>
      </q-card-section>

      <!-- RIB -->
      <q-card-section v-if="paymentType=='RIB'">
        <div class="column">
            <q-file
              outlined
              v-model="file"
              label="Choisir un document"
              dark
              color="secondary"
              hint="Format accepté : PDF, JPEG, PNG"
            >
              <template v-slot:prepend>
                <q-icon name="description"/>
              </template>
            </q-file>

          <q-btn
            color="secondary"
            @click="postRib"
            :loading="loading"
            label="Ajouter"
            class="q-mt-md btn"
          type="confirm">
          </q-btn>
        </div>
      </q-card-section>

      <!-- PAYPAL -->
      <q-card-section v-if="paymentType=='PayPal'">
        <div class="column justify-between">
          <q-input
            dark
            outlined
            label="Pseudo PayPal"
            prefix="@"
            color="secondary"
            stack-label
            hide-hint
            hint="Exemple : @johndoe (ne pas inclure @)"
            v-model="paypalId">
          <template v-slot:append>
              <q-icon name="open_in_new" @click="redirectToPaypal" style="cursor: pointer;" v-if="paypalId.length>=3" />
          </template>
          </q-input>

          <q-btn
            color="secondary"
            @click="postPayPal"
            :loading="loading"
            label="Ajouter"
            class="q-mt-md btn"
            type="confirm">
          </q-btn>
        </div>
      </q-card-section>
    </q-card>
  </q-dialog>

</template>

<style scoped>
.btn{
  width: 50%;
  margin: 10px auto;
}
</style>

<script setup lang="ts">
import { useQuasar } from 'quasar'
import {onMounted, ref} from "vue";
import { api } from "boot/axios";
import {DefaultRib} from "src/interfaces/paymentMethod.interface";
const $q = useQuasar();

let isOpen = ref(false);
let loading = ref(false);
const optn = ref(["RIB","PayPal"]);
let paymentType = ref("RIB")

let paypalId = ref("");
let rib = ref(DefaultRib());
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
    //if (!chackHasFile()) {
      //return;
    //}
    const formData = new FormData();
    //formData.append('image', file.value);

    const response = await api.post(`/users/paymentMethode`, {
      "type": "RIB",
      "name": rib.value.name,
      "surname": rib.value.surname,
      "bank_name":rib.value.bank_name,
      "bank_number": rib.value.bank_number,
      "box_code": rib.value.box_code,
      "account_number": rib.value.account_number,
      "RIB_key": rib.value.RIB_key,
      "IBAN": rib.value.IBAN,
    });

    if (response.data) {
      $q.notify({
        type: 'positive',
        message: 'RIB ajouté avec succès'
      })
      isOpen.value= false;
    }
  }
  catch (error) {
    if(error.code === 409){
      $q.notify({
        type: 'negative',
        message: 'Un moyen de paiement similaire existe déjà'
      })}
      else {
        $q.notify({
          type: 'negative',
          message: 'Une erreur s\'est produite'
        })
      }
  }
  loading.value = false;
}

async function postPayPal() {
  loading.value = true;
  try {
    const response = await api.post(`/users/paymentMethode`, {
      "type": "Paypal",
      "paypal_username": paypalId.value,
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
    <q-card class="bg-primary" style="width: 600px; max-width: 80vw;">
      <q-card-section>
        <div class="text-h5">Ajouter un moyen de paiement</div>
      </q-card-section>
      <q-card-section>
          <q-select
            class="input"
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

          <div class="row justify-evenly">
            <q-input
              dark
              class="input-2"
              outlined
              label="Nom du tilulaire"
              color="secondary"
              stack-label
              v-model="rib.name">
            </q-input>

            <q-input
              dark
              class="input-2"
              outlined
              label="Prénom du tilulaire"
              color="secondary"
              stack-label
              v-model="rib.surname">
            </q-input>

          </div>

          <div class="row justify-center">
            <q-input
              dark
              outlined
              class="input"
              label="Banque"
              color="secondary"
              stack-label
              v-model="rib.bank_name">
            </q-input>
          </div>

          <span>R.I.B</span>

          <div class="row justify-evenly">
            <q-input
              dark
              outlined
              class="input-4"
              label="Code Banque"
              color="secondary"
              stack-label
              v-model="rib.bank_number">
            </q-input>
            <q-input
              dark
              outlined
              class="input-4"
              label="Code Guichet"
              color="secondary"
              stack-label
              v-model="rib.box_code">
            </q-input>
            <q-input
              class="input-4"
              dark
              outlined
              label="Numéro de compte"
              color="secondary"
              stack-label
              v-model="rib.account_number">
            </q-input>
            <q-input
              class="input-4"
              dark
              outlined
              label="Clé RIB"
              color="secondary"
              stack-label
              v-model="rib.RIB_key">
            </q-input>
          </div>
          <q-input
            class="input"
            dark
            outlined
            label="IBAN"
            color="secondary"
            stack-label
            v-model="rib.IBAN">
          </q-input>

            <q-file
              outlined
              class="input"
              v-model="file"
              label="Justificatif"
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
            class="input"
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
.input{
  margin: 10px auto;
  width: 95%;
}

.input-2{
  margin: 10px auto;
  width: 45%;
}

.input-4{
  margin: 10px 0;
  width: 22%;
}
</style>

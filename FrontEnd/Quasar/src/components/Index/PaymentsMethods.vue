<script setup lang="ts"> import {EtatTotalPaidComponent} from "src/interfaces/types";

import { defineProps, onMounted, ref } from "vue";
import {Group} from "src/interfaces/group.interface";
import {getUser} from "stores/userStore";
import {api} from "boot/axios";
import DialogUpdateImage from "components/Common/DialogUpdateImage.vue";
import DialogAddPaymentMethod from "components/Common/DialogAddPaymentMethod.vue";
import {DefaultUser} from "src/interfaces/user.interface";
import {useQuasar} from "quasar";
import {DefaultPaymentMethod, DefaultRib} from "src/interfaces/paymentMethod.interface";

const  slide = ref('slide-0');
const paiementsMethod = ref();
let dialogIsOpen = ref(false);
let User = ref(DefaultUser());
const $q = useQuasar();
let mounted = ref(false);


onMounted(async () => {
  User.value = await getUser();
  mounted.value=true;
  await getMethod();
});

async function getMethod(){
  try {
    console.log(paiementsMethod.value)
    const response = await api.get(`/users/me/paymentMethode`)
    paiementsMethod.value = response.data
  }
  catch(e){
    console.error(e)
  }
}


async function openDialgAddPayment(type:string){

  dialogIsOpen.value = true;
  $q.dialog({
    component: DialogAddPaymentMethod,

    componentProps: {
      isOpen: dialogIsOpen,
      userId: User.value.id,
      type: type
    }
  }).onDismiss(() => {
    getMethod();
  })
}

const redirectToPaypal = (username:string) => {
  window.open(`https://paypal.me/${username}`, '_blank');
};

function convertIBAN(iban:string){
  const firstFour = iban.substring(0, 4);
  const lastFour = iban.substring(iban.length - 4);
  const middleStars = '*'.repeat(iban.length - 8);
  const maskedIBAN = firstFour + middleStars + lastFour;
  return maskedIBAN.match(/.{1,4}/g).join(' ')
}
</script>


<template>

  <q-card class="bloc-paye bg-accent" style="height: 500px" v-if="mounted">
    <q-card-section
      class="q-pa-md row">
      <h3 class="text-h6">Moyens de paiement</h3>
      <q-btn
        class="btn q-mx-auto"
        unelevated color="secondary"
        label="+"
        round
        @click="openDialgAddPayment('RIB')"
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
        style="max-height: 350px"
        class="text-purple rounded-borders caroussel bg-accent q-mx-auto"
      >
          <q-carousel-slide
            v-for="(paiement, index) in paiementsMethod"
            :key="index"
            :name="`slide-${index}`"
          >
            <q-card class="method rounded-borders" v-if="paiement.type=='Paypal'">
              <q-img
                src="/assets/card/paypal-card.webp"
                class="rounded-borders"
              >
                <div class="absolute-bottom text-subtitle2 row">
                  <q-item-label class="q-pa-xs">@{{paiement.value.user_paypal}}</q-item-label>
                  <q-space></q-space>
                  <q-item-label class="text-h6" v-if="paiement.type=='Paypal'">
                    <q-icon name="open_in_new" @click="redirectToPaypal(paiement.value.user_paypal)" style="cursor: pointer;"
                    />
                  </q-item-label>
                </div>
              </q-img>
            </q-card>

            <q-card class="method rounded-borders" v-if="paiement.type=='RIB'">
              <q-img
                src="/assets/card/rib-card.webp"
                class="rounded-borders"
              >
                <div class="absolute-bottom text-subtitle2 row">
                  <q-item-label class="q-pa-xs">{{paiement.value.bank_name}}: {{paiement.value.surname}} {{paiement.value.name}}</q-item-label>
                  <q-item-label class="q-pa-xs"></q-item-label>
                  <q-space></q-space>
                  <q-item-label class="q-pa-xs">{{convertIBAN(paiement.value.IBAN)}}</q-item-label>
                </div>
              </q-img>
            </q-card>
          </q-carousel-slide>

      </q-carousel>
    </q-card-section>
    <q-card-section
      v-if="paiementsMethod.length == 0"
      class="q-mx-auto q-pa-md">

      <div class="q-mx-auto q-pa-md column">
        <q-item-label class="btn-no-payment text-h6">
          Vous n'avez aucun moyen de paiement enregistré
        </q-item-label>

        <q-btn
          class="btn-no-payment q-pa-md"
          rounded
          color="secondary"
          @click="openDialgAddPayment('PayPal')"
        >
          Ajouter PayPal
        </q-btn>
        <q-space></q-space>
        <q-btn
          rounded
          color="secondary"
          @click="openDialgAddPayment('RIB')"
          class="btn-no-payment q-pa-md">
          Ajouter RIB
        </q-btn>
      </div>

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
  min-width: 350px;
}

.caroussel{
  width: 100%;
}
.btn{
  margin: auto 10px;
}

.btn-no-payment{
  margin: 30px auto;
  width: 80%;
  text-align: center;
}
</style>

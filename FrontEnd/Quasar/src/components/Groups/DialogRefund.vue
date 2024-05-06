<script setup lang="ts">
import { useQuasar } from 'quasar'
import {ref} from "vue";
import {DefaultGroup} from "src/interfaces/group.interface";
import {api} from "boot/axios";
import { getUserGroupData } from '../../stores/groupStore';
const $q = useQuasar();

let isOpen = ref(false);
let newGroup = ref(DefaultGroup());
let loading = ref(false);

const props = defineProps({
  groupId: Number,
  userId: Boolean,
  refundId: Number,
  userToPaidID: Number,
  amount: Number,
});

async function Refund() {

  loading.value = true;
    try {
      const response = await api.post(`groups/${props.groupId}/refund/${props.refundId}`, {});
      if (response.data) {
        $q.notify({
          type: 'positive',
          message: 'Remboursement effectué'
        })
        isOpen.value= false;
      }
    }
    catch (error) {
      $q.notify({
        type: 'negative',
        message: 'Une erreur s\'est produite lors du remboursement'
      })

    }
  loading.value = false;
}


</script>

<template>
  <q-dialog v-model="isOpen">
    <q-card class="bg-primary" style="width: 500px; max-width: 80vw;">
      <q-card-section>
        <div class="text-h5">Rembourser</div>
      </q-card-section>
        <div class="form q-pa-lg q-ma-lg flex row">
          <div class="inputs">
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

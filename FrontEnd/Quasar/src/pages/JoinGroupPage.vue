<script setup lang="ts">
import { getUser } from "stores/userStore";
import { ref } from "vue";
import {useRoute, useRouter} from "vue-router";
import {useQuasar} from "quasar";
import {api} from "boot/axios";

const $q = useQuasar()
const user = ref(getUser());
const router = useRouter();

const userFirstName = ref('');
const route = useRoute();
const token = route.params.token;

(async () => {
  const userData = await getUser();
  if (userData.firstname != null) {
    userFirstName.value = userData.firstname;
  }
})();

async function joinGroup() {
  try {
    const response = await api.post(`{token}`, {});
    if (response.data) {
      $q.notify({
        type: 'positive',
        message: 'Vous avez rejoint le groupe'
      })
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
  <q-page class="column items-center justify-evenly">

    <h3 class="text-h4">Bienvenue {{ userFirstName }} &#x1F44B;</h3>

    <q-item-label class="text-h6">Vous avez été invité a rejoindre un groupe</q-item-label>
    <q-btn
      @click="joinGroup"
      label="Rejoindre"
      color="secondary"
      class="btn"
    ></q-btn>

  </q-page>
</template>
<style scoped>
.btn {
  width: 30%;
  height: 50px;
  border-radius: 10px;
  margin: 10px 40%;
}
</style>

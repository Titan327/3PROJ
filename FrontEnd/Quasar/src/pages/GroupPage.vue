<script setup lang="ts">
import { ref } from "vue";
import {getUser} from "stores/userStore";

const user = ref(getUser());

const userFirstName = ref('');

(async () => {
  const userData = await getUser();
  if (userData.firstname != null) {
    userFirstName.value = userData.firstname;
  }
})();
</script>

<template>
  <q-page class="row items-center justify-evenly">

    <h3 class="text-h4">Mes Groupes</h3>

  </q-page>
</template>
<style scoped>
</style>

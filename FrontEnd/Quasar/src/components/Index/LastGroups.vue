<script setup lang="ts">

import {onMounted, ref } from "vue";
import {Group} from "src/interfaces/group.interface";
import {api} from "boot/axios";
import {getUser} from "stores/userStore";

let groupList = ref<Group[]>([]);

onMounted(async () => {

  const userData = await getUser();

  const response = await api.get(`/user/${userData.id}/groups?limit=5`);

  groupList.value = response.data;
  console.log(groupList.value);
});

</script>


<template>
  <q-card class="bloc-paye bg-primary">
    <q-card-section>
      <h3 class="text-h6">Mes Groupes</h3>
    </q-card-section>
    <q-card-section>
      <div class="q-gutter-md q-ml-none">
        <q-btn
          v-for="group in groupList" :key="group.id"
          rounded
          flat
        >
          <q-avatar
            size="100px"
          >
            <img :src="group.picture ? group.picture : 'assets/defaults/group-default.webp'">
            <q-badge color="red" rounded floating >{{group.id}}</q-badge>

          </q-avatar>
        </q-btn>
      </div>
    </q-card-section>
    <q-separator/>
  </q-card>
</template>

<style scoped>

.bloc-paye{
  width: 88%;
  border-radius: 15px;
}
</style>

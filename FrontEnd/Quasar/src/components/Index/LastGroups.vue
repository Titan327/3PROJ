<script setup lang="ts">

import {onMounted, ref } from "vue";
import {Group} from "src/interfaces/group.interface";
import {api} from "boot/axios";
import {getUser} from "stores/userStore";
import {useRouter} from "vue-router";

let groupList = ref<Group[]>([]);
const router = useRouter();

onMounted(async () => {

  const userData = await getUser();

  const response = await api.get(`/user/${userData.id}/groups?limit=5`);

  groupList.value = response.data;
  console.log(groupList.value);
});

</script>


<template>
  <q-card class="bloc-group bg-accent">
    <q-card-section>
      <h3 class="text-h6">Mes Groupes</h3>
    </q-card-section>
    <q-card-section>
      <div class="q-gutter-md q-ml-none">
        <q-avatar
            v-for="group in groupList" :key="group.id"
            size="100px"
            @click="router.push(`/group/${group.id}`)"
          >
            <img :src="group.picture ? group.picture+'/200' : 'assets/defaults/group-default.webp'">
            <q-badge color="red" rounded floating >{{group.id}}</q-badge>
          </q-avatar>
      </div>
    </q-card-section>
    <q-separator/>
  </q-card>
</template>

<style scoped>

.bloc-group{
  width: 100%;
  border-radius: 15px;
  margin-top: 50px;
}
</style>

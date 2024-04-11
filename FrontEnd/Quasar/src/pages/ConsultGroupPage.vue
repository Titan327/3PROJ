<script setup lang="ts">

import {computed, onMounted, ref} from "vue";
import {disconnectUser, getUser} from "stores/userStore";
import {useRoute, useRouter} from "vue-router";
import {DefaultUser} from "src/interfaces/user.interface";
import {useQuasar} from "quasar";
import {api} from "boot/axios";
import MessageDrawer from "components/ConsultGroup/MessageDrawer.vue";
import {DefaultGroup} from "src/interfaces/group.interface";

const router = useRouter();
let User = ref(DefaultUser());
const $q = useQuasar();
const route = useRoute();
const groupId = route.params.id;
let group = ref(DefaultGroup());
let urlPhoto = ref('');
let isPhotoHover = ref(false);

onMounted(async () => {

  await getGroup()

});

async function getGroup() {
  User.value = await getUser();
  const response = await api.get(`/group/${groupId}`);
  group.value = response.data;
}
</script>

<template>
  <message-drawer :groupId = groupId></message-drawer>
  <q-page class="q-pa-md">
    <div class="div-first-last-name">
      <q-card class="transparent no-box-shadow">
        <q-card-section horizontal>
          <div class="q-pa-md q-gutter-sm">
            <q-avatar
              @click="console.log('change img')"
              @mouseenter ="isPhotoHover=true"
              @mouseleave ="isPhotoHover=false"
              size="200px"
            >
              <img :src="urlPhoto ? urlPhoto : 'assets/defaults/group-default.webp'">
              <div class="absolute-full text-subtitle2 flex flex-center text-secondary"
                   v-if="isPhotoHover">
                Modifier
              </div>
            </q-avatar>
          </div>
          <q-card-section>
            <q-item-label class="text-h4">{{group.name}}</q-item-label>
            <q-item-label class="text-subtitle1">{{group.description}}</q-item-label>
          </q-card-section>
        </q-card-section>
      </q-card>
    </div>
    <div class="q-pa-md q-gutter-sm" style="height: 80px">
      <q-item-label class="text-h6">36 Membres</q-item-label>
      <q-avatar
        v-for="(user, index) in group.Users"
        :key="user.id"
        size="40px"
        class="overlapping"
        :style="{ left: index * 25 + 'px' }"
      >
        <img :src="user.profile_picture ? `${user.profile_picture}/100` : 'assets/defaults/user-default.webp'">
      </q-avatar>
    </div>
  </q-page>
</template>

<style scoped>
.overlapping{
  position: absolute ;
}

@media screen and (max-width: 1200px) {
  .paiement{
    width: 40%;
  }
}

@media screen and (max-width: 700px) {
  .paiement {
    width: 90%;
  }
}
</style>

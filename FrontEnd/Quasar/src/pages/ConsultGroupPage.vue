<script setup lang="ts">

import {computed, onMounted, ref} from "vue";
import {disconnectUser, getUser} from "stores/userStore";
import {useRoute, useRouter} from "vue-router";
import {DefaultUser} from "src/interfaces/user.interface";
import {useQuasar} from "quasar";
import {api} from "boot/axios";
import MessageDrawer from "components/ConsultGroup/MessageDrawer.vue";
import {DefaultGroup} from "src/interfaces/group.interface";
import ActionsGroupTab from "components/Groups/ActionsGroupTab.vue";

const router = useRouter();
let User = ref(DefaultUser());
const $q = useQuasar();
const route = useRoute();
const groupId = route.params.id;
let group = ref(DefaultGroup());
let urlPhoto = ref('');
let isPhotoHover = ref(false);
let mounted = ref(false)
let messageState = ref(false);

onMounted(async () => {

  await getGroup()
  mounted.value=true;
});

async function getGroup() {
  User.value = await getUser();
  try {
    const response = await api.get(`/group/${groupId}`);
    group.value = response.data;
  }
  catch (error) {
    console.error(error);
  }
}

function isGroupFavorite(userId : number) {
  return group.value.Users.some(user => user.userId === userId && user.UserGroup.favorite);
}

function  openCloseMessageDrawer(){
  messageState.value = !messageState.value;
}
function  closeMessageDrawer(){
  messageState.value = false;
}

</script>

<template>
  <message-drawer :groupId = groupId  :open = messageState @updateState="closeMessageDrawer"></message-drawer>
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
            <q-item-label class="text-h4">{{group.name}}<q-icon
              name="star"
              v-if="mounted"
              :color="isGroupFavorite(User.id)? 'yellow' : 'grey'"
              @click="console.log('star')"
              size="32px" /></q-item-label>
            <q-item-label class="text-subtitle1">{{group.description}}</q-item-label>
          </q-card-section>
        </q-card-section>
      </q-card>
    </div>
    <div class="q-pa-md q-gutter-sm" style="height: 80px">
      <q-item-label class="text-h6">
        {{group.activeUsersCount}} {{ group.activeUsersCount === 1 ? 'Membre' : 'Membres' }}
      </q-item-label>
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
    <ActionsGroupTab :groupId = groupId :userId = groupId></ActionsGroupTab>
    <q-page-sticky position="bottom-right" :offset="[18, 18]">
      <q-btn fab icon="message" color="secondary" @click="openCloseMessageDrawer"/>
    </q-page-sticky>
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

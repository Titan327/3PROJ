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
import DialogUpdateImage from "components/Common/DialogUpdateImage.vue";

const router = useRouter();
let User = ref(DefaultUser());
const $q = useQuasar();
const route = useRoute();
const groupId = route.params.id;
let group = ref(DefaultGroup());
let isPhotoHover = ref(false);
let mounted = ref(false)
let messageState = ref(false);
let dialogModifyPp = ref(false);
let newMessageNotification = ref(0);

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

const isGroupFavorite = computed(() => {
  return (userId: number) => {
    return group.value.Users.some(user => user.userId === userId && user.UserGroup.favorite);
  };
});

function  openCloseMessageDrawer(){
  messageState.value = !messageState.value;
}
function  closeMessageDrawer(){
  messageState.value = false;
}

async function openDialogPP(){

  newMessageNotification.value = 0;
  dialogModifyPp.value = true;
  $q.dialog({
    component: DialogUpdateImage,

    componentProps: {
      isOpen: dialogModifyPp,
      groupId: groupId,
    }
  }).onDismiss(() => {
    window.location.reload();
  })
}

async function addOrRemoveFav(id) {
  const isFavorite = isGroupFavorite(id);
  const favoriteValue = !isFavorite;

  group.value.Users.forEach(user => {
    if (user.userId === id) {
      user.UserGroup.favorite = favoriteValue;
    }
  });

  try {
    await api.put(`group/${groupId}/favorite`, { favorite: favoriteValue });
    $q.notify({
      type: 'positive',
      message: 'Ajouté aux favoris'
    })
  } catch (error) {
    console.error('Erreur lors de la mise à jour du favori :', error);
    $q.notify({
      type: 'negative',
      message: 'Une erreur s\'est produite'
    })
  }
}


</script>

<template>
  <message-drawer :groupId = groupId  :open = messageState @updateState="closeMessageDrawer" @messages="newMessageNotification+=1"></message-drawer>
  <q-page class="q-pa-md">
    <div class="div-first-last-name">
      <q-card class="transparent no-box-shadow">
        <q-card-section horizontal>
          <div class="q-pa-md q-gutter-sm">
            <q-avatar
              @click="openDialogPP"
              @mouseenter ="isPhotoHover=true"
              @mouseleave ="isPhotoHover=false"
              size="200px"
            >
              <img :src="group.picture ? group.picture[1] : 'assets/defaults/group-default.webp'">
              <div class="absolute-full text-subtitle2 flex flex-center text-secondary"
                   v-if="isPhotoHover">
                Modifier
              </div>
            </q-avatar>
          </div>
          <q-card-section class="q-pa-xl">
            <q-item-label class="text-h4">{{group.name}}<q-icon
              name="edit"
              class="q-ml-md"
              v-if="mounted"
              color="secondary"
              @click="console.log('star')"
              size="32px" /></q-item-label>
            <q-item-label class="text-subtitle1">{{group.description}}<q-icon
              name="edit"
              class="q-ml-md"
              v-if="mounted"
              color="secondary"
              @click="console.log('star')"
              size="18px" />
            </q-item-label>
            <q-btn class="btn-fav"
                   no-caps
                   v-if="mounted"
                   color="secondary"
                   :outline="isGroupFavorite(User.id)"
                   @click="addOrRemoveFav(User.id)"
                   rounded
            >{{ isGroupFavorite(User.id)? 'Retirer des favoris' : 'Ajouter aux favoris' }}
            </q-btn>
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
        <img :src="user.profile_picture ? `${user.profile_picture[0]}` : 'assets/defaults/user-default.webp'">
      </q-avatar>
    </div>
    <ActionsGroupTab :groupId = groupId :userId = groupId></ActionsGroupTab>
    <q-page-sticky position="bottom-right" :offset="[18, 18]">
      <q-btn fab icon="message" color="secondary" @click="openCloseMessageDrawer"/>
      <q-badge rounded floating color="red" v-if="newMessageNotification>0">{{newMessageNotification}}</q-badge>
    </q-page-sticky>
  </q-page>
</template>

<style scoped>
.overlapping{
  position: absolute ;
}

.text-h4{
  margin-bottom: 10px;
}

.btn-fav{
  margin-top: 30px;
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

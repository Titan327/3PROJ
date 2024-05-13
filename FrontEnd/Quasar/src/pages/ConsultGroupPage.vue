<script setup lang="ts">

import {computed, onMounted, ref, watch} from "vue";
import {disconnectUser, getUser} from "stores/userStore";
import {useRoute, useRouter} from "vue-router";
import {DefaultUser} from "src/interfaces/user.interface";
import {useQuasar} from "quasar";
import {api} from "boot/axios";
import MessageDrawer from "components/ConsultGroup/MessageDrawer.vue";
import {DefaultGroup} from "src/interfaces/group.interface";
import ActionsGroupTab from "components/Groups/ActionsGroupTab.vue";
import DialogUpdateImage from "components/Common/DialogUpdateImage.vue";
import DialogCustomInvitintoGroup from "src/components/Groups/DialogCustomInvitintoGroup.vue";
import DialogConsultTransaction from "components/Groups/DialogConsultTransaction.vue";
import DialogPrivateMessage from "components/Common/DialogPrivateMessage.vue";

const router = useRouter();
let User = ref(DefaultUser());
const $q = useQuasar();
const route = useRoute();
const groupId = route.params.id;
const openedTransactionId = route.params.transactionId;
let group = ref(DefaultGroup());
let isPhotoHover = ref(false);
const width = ref(0);
let mounted = ref(false)
let messageState = ref(false);
let dialogModifyPp = ref(false);
let newMessageNotification = ref(0);
let isFavorite = ref(false);
const dialogConsultTransaction = ref(false);

let isOpenDialogInvite = ref(false);
let dialogCreateMessage = ref(false);

onMounted(async () => {
  await getGroup()
  mounted.value=true;

  if(openedTransactionId >= 0){
    openDialogConsultTransaction(openedTransactionId);
  }

  function getWidth() {

    width.value = window.innerWidth;
  }

  getWidth();

  window.addEventListener('resize', getWidth);
});

watch(dialogConsultTransaction, (newValue, oldValue) => {
  if(newValue == false){
    router.push(`/groups/${groupId}`);
  }
});

async function getGroup() {
  User.value = await getUser();
  try {
    const response = await api.get(`/groups/${groupId}`);
    group.value = response.data;
    console.log(group.value)

    isGroupFavorite();
  }
  catch (error) {
    router.push('/404')
    console.error(error);
  }
}

function isGroupFavorite() {

  isFavorite.value = group.value.Users.some(user => user.id === User.value.id && user.UserGroup.favorite);
}

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

async function openDialogInvite(){

  isOpenDialogInvite.value = true;
      $q.dialog({
        component: DialogCustomInvitintoGroup,

        componentProps: {
          isOpen: isOpenDialogInvite,
          groupName: group.value.name,
          groupId: group.value.id
        }
      })
}

async function addOrRemoveFav() {
  try {
    isFavorite.value = !isFavorite.value;
    await api.put(`/groups/${groupId}/favorite`, { favorite: isFavorite.value });
    $q.notify({
      type: 'positive',
      message: 'Favoris mis à jour'
    })
  } catch (error) {
    console.error('Erreur lors de la mise à jour du favori :', error);
  }
}

async function editGroup() {

  try {
    await api.put(`groups/${groupId}/`, {
      name: group.value.name,
      description: group.value.description
    });
    $q.notify({
      type: 'positive',
      message: 'Modifié avec succès'
    })
  } catch (error) {
    console.error('Erreur lors de la mise à jour du groupe :', error);
    $q.notify({
      type: 'negative',
      message: 'Une erreur s\'est produite'
    })
  }
}

function openDialogConsultTransaction(transactionId:number){
  dialogConsultTransaction.value = true;
  $q.dialog({
    component: DialogConsultTransaction,

    componentProps: {
      isOpen: dialogConsultTransaction,
      groupId: groupId,
      userId: User.value.id,
      transactionId: transactionId
    }
  }).onDismiss(() => {
    dialogConsultTransaction.value = false;
  })
}

function openDialogPrivateMessage(user2:number){
  dialogCreateMessage.value = true;
  $q.dialog({
    component: DialogPrivateMessage,

    componentProps: {
      isOpen: dialogCreateMessage,
      groupId: groupId,
      user2id: user2,
    }
  }).onOk(() => {
    console.log('OK')
  }).onCancel(() => {
    console.log('Cancel')
  }).onDismiss(() => {
    dialogCreateMessage.value = false;
  })
}
</script>

<template>
  <message-drawer :groupId = groupId  :open = messageState @updateState="closeMessageDrawer" @messages="newMessageNotification+=1"></message-drawer>
  <q-page class="q-pa-md">
    <div class="div-first-last-name">
      <q-card class="transparent no-box-shadow">
        <q-card-section :horizontal="width<500? false:true">
          <div class="q-pa-md q-gutter-sm">
            <q-avatar
              @click="openDialogPP"
              @mouseenter ="isPhotoHover=true"
              @mouseleave ="isPhotoHover=false"
              size="200px"
              class="cursor-pointer"
            >
              <q-tooltip>
               Modifier la photo du groupe
              </q-tooltip>
              <img :src="group.picture ? group.picture[2] : 'assets/defaults/group-default.webp'">
              <div class="absolute-full text-subtitle2 flex flex-center text-secondary"
                   v-if="isPhotoHover">
                Modifier
              </div>
            </q-avatar>
          </div>
          <q-card-section class="q-pa-xl" :style="'min-width: ' + width/2+ 'px'">

            <div class="cursor-pointer">
              <q-item-label class="text-h4">{{ group.name }}</q-item-label>
              <q-popup-edit v-if="User.id == group.ownerId" v-model="group.name" class="bg-accent" v-slot="scope" @save="editGroup">
                <q-input dark color="white" v-model="scope.value" dense autofocus counter @keyup.enter="scope.set">
                  <template v-slot:append>
                    <q-icon name="edit" />
                  </template>
                </q-input>
              </q-popup-edit>
              <q-tooltip v-if="User.id== group.ownerId">
                Cliquez pour modifier le nom
              </q-tooltip>
              <q-tooltip v-if="User.id!= group.ownerId" class="bg-red">
                Vous devez être propriétaire du groupe pour modifier son nom
              </q-tooltip>
            </div>
            <div class="cursor-pointer">
              <q-item-label class="text-h6">{{ group.description }}</q-item-label>
              <q-popup-edit v-if="User.id== group.ownerId" v-model="group.description" class="bg-accent" v-slot="scope" @save="editGroup">
                <q-input dark color="white" v-model="scope.value" dense autofocus counter @keyup.enter="scope.set">
                  <template v-slot:append>
                    <q-icon name="edit" />
                  </template>
                </q-input>
              </q-popup-edit>
              <q-tooltip v-if="User.id== group.ownerId">
                Cliquez pour modifier la description
              </q-tooltip>
              <q-tooltip v-if="User.id!= group.ownerId" class="bg-red">
                Vous devez être propriétaire du groupe pour modifier la description
              </q-tooltip>
            </div>

            <br>
            <q-btn class="btn-fav"
                   no-caps
                   v-if="mounted"
                   color="secondary"
                   :outline="isFavorite"
                   @click="addOrRemoveFav(User.id)"
                   rounded
            >{{ isFavorite ? 'Retirer des favoris' : 'Ajouter aux favoris' }}
            </q-btn>
          </q-card-section>
        </q-card-section>
      </q-card>
    </div>
    <div class="q-pa-md">
      <q-item-label class="text-h6">
        {{group.activeUsersCount}} {{ group.activeUsersCount === 1 ? 'Membre' : 'Membres' }}
        <q-btn class="q-mx-auto q-pa-xs"
               no-caps
               icon="person_add"
               v-if="mounted"
               color="secondary"
               @click="openDialogInvite"
               flat
               outline
        > <q-tooltip>
         Inviter d'autres membres
        </q-tooltip></q-btn>
      </q-item-label>
    <div class="group-items q-pa-xs">
      <q-avatar
        v-for="(user, index) in group.Users"
        :key="user.id"
        size="40px"
        class="overlapping"
        :style="{ left: index * 25 + 'px' }"
      >
        <img :src="user.profile_picture ? `${user.profile_picture[0]}` : 'assets/defaults/user-default.webp'">
        <q-tooltip>
          {{ user.username }}
        </q-tooltip>
        <q-menu dark :offset="[-30, 10]">
          <q-list style="min-width: 100px">
            <q-item clickable v-close-popup @click="openDialogPrivateMessage(Number(user.id))">
              <q-item-section>Message privé</q-item-section>
            </q-item>
          </q-list>
        </q-menu>
      </q-avatar>
    </div>
    </div>
    <ActionsGroupTab :groupId = groupId :userId = User.id></ActionsGroupTab>
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
.input-group-name{
  width: 100%;
}
.input-group-desc{
  width: 100%;
}

.btn-fav{
  margin-top: 30px;
}

.group-items *{
  margin-left: 2%;
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

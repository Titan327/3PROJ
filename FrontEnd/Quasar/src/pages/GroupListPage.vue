<script setup lang="ts">

import {onMounted, ref } from "vue";
import {Group} from "src/interfaces/group.interface";
import {api} from "boot/axios";
import {getUser} from "stores/userStore";
import DialogCreateGroup from "components/Groups/DialogCreateGroup.vue";
import {useQuasar} from "quasar";
import DialogInvitintoGroup from "components/Groups/DialogInvitintoGroup.vue";
import {useRouter} from "vue-router";

let groupList = ref<Group[]>([]);
const $q = useQuasar()
const router = useRouter();
const width = ref(0);

let DialogCreate = ref(false);
let DialogInvite = ref(false);
let loading = ref(false);

onMounted(async () => {

  await getGroups()

  function getWidth() {

    width.value = window.innerWidth;
  }
  getWidth();
  window.addEventListener('resize', getWidth);
});

async function getGroups() {
  loading.value = true;
  try {
    const userData = await getUser();
    const response = await api.get(`/users/${userData.id}/groups?limit=50`);
    groupList.value = response.data;

    groupList.value.sort((a, b) => {
      return new Date(b.updatedAt) - new Date(a.updatedAt); //ts error
    });
  }
  catch (e) {
    console.error('Error')}
  loading.value = false;
}

function openDialogCreate(){
  DialogCreate.value = true;
  $q.dialog({
  component: DialogCreateGroup,

  componentProps: {
    isOpen: openDialogCreate,

  }
}).onDismiss(() => {
    getGroups()
})
}

async function openDialogInvite(id: number, name: string){

  try {
    const today = new Date();
    const expirationDate = new Date(today);
    expirationDate.setDate(today.getDate() + 7);

    const response = await api.post(`groups/${id}/createInvitation`, {

      remaining_uses: 5,
      expiration_date:expirationDate
    });

    if (response.data) {

      DialogInvite.value = true;
      $q.dialog({
        component: DialogInvitintoGroup,

        componentProps: {
          isOpen: openDialogInvite,
          link: response.data.invitation.token,
          name: name
        }
      })
    }
  }
  catch (error) {
    $q.notify({
      type: 'negative',
      message: 'Une erreur s\'est produite lors de la création de l\'invitation'
    })
  }
}

async function setFavorite(groupID:number, favorite:boolean){
  try {
    await api.put(`/groups/${groupID}/favorite`,{
      "favorite": favorite
    })
    await getGroups();
  }
  catch (error) {
    console.error('Error')
  }
}
</script>

<template>
  <q-page class="col items-center justify-evenly">

    <h3 class="text-h4 q-pa-md">Mes Groupes  <q-btn color="secondary" round outline @click="openDialogCreate">+</q-btn></h3>

    <q-card class="bloc-groupe bg-accent"
            v-for="group in groupList" :key="group.id">
      <q-separator/>
      <q-card-section>
        <q-item>
          <q-item-section avatar>
            <q-avatar rounded text-color="white"  clickable @click="router.push(`/groups/${group.id}`)">
              <img :src="group.picture ?  group.picture[0] : 'assets/defaults/group-default.webp'">
            </q-avatar>
          </q-item-section>

          <q-space></q-space>

          <q-item-section  clickable @click="router.push(`/groups/${group.id}`)">
            <q-item-label class="text-h6">{{group.name}}</q-item-label>
          </q-item-section>

          <q-space></q-space>
          <q-item-section avatar v-if="width>400">
            <q-btn
              color="secondary"
              unelevated
              rounded
              outline
              class="btn-consulter"
              icon="person_add"
              @click="openDialogInvite(group.id, group.name)"
            >
              <q-tooltip>
                Inviter d'autres membres
              </q-tooltip>
            </q-btn>
          </q-item-section>
          <q-item-section avatar  v-if="width>500">
            <q-btn
              color="secondary"
              label="Afficher"
              unelevated
              rounded
              outline
              class="btn-consulter"
              @click="router.push(`/groups/${group.id}`)"
            >
            </q-btn>
          </q-item-section>
          <q-item-section avatar>
            <q-avatar text-color="white">
              <q-icon
                name="star"
                :color="group.isFavorite ? 'yellow' : 'grey'"
                @click="setFavorite(group.id, !group.isFavorite)"
                style="cursor: pointer;"
                size="30px"
              />
              <q-tooltip>
                {{ group.isFavorite ? 'Retirer des favoris' : 'Ajouter aux favoris'}}
              </q-tooltip>
            </q-avatar>
          </q-item-section>
        </q-item>
      </q-card-section>
    </q-card>
    <q-card-section v-if="groupList.length == 0 && !loading">
      <q-item-section>
        <q-item-label class="text-h6">Rien à afficher</q-item-label>
      </q-item-section>
    </q-card-section>
    <q-inner-loading>
      <template v-if="loading">
        <q-spinner color="secondary" size="50px"/>
      </template>
    </q-inner-loading>
  </q-page>
</template>
<style scoped>

.bloc-groupe{
  width: 88%;
  border-radius: 15px;
  margin: 20px auto;
}

</style>

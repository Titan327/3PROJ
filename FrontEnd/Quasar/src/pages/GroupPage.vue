<script setup lang="ts">

import {onMounted, ref } from "vue";
import {Group} from "src/interfaces/group.interface";
import {api} from "boot/axios";
import {getUser} from "stores/userStore";
import DialogCreateGroup from "components/Groups/DialogCreateGroup.vue";
import {useQuasar} from "quasar";

let groupList = ref<Group[]>([]);
const $q = useQuasar()

let DialogCreate = ref(false);

onMounted(async () => {

  await getGroups()

});

async function getGroups() {
  const userData = await getUser();
  const response = await api.get(`/user/${userData.id}/groups?limit=50`);
  groupList.value = response.data;
}

function openDialogCreate(){
  DialogCreate.value = true;
  $q.dialog({
  component: DialogCreateGroup,

  componentProps: {
    isOpen: openDialogCreate,

  }
}).onOk(() => {
  console.log('OK')
}).onCancel(() => {
  console.log('Cancel')
}).onDismiss(() => {
    getGroups()
})
}
</script>

<template>
  <q-page class="col items-center justify-evenly">

    <h3 class="text-h4 q-pa-md">Mes Groupes  <q-chip color="secondary" class="text-white" clickable @click="openDialogCreate">+</q-chip></h3>

    <q-card class="bloc-groupe bg-primary"
            v-for="group in groupList" :key="group.id">
      <q-separator/>
      <q-card-section>
        <q-item>
          <q-item-section avatar>
            <q-avatar rounded color="secondary" text-color="white">
              <img :src="group.picture ? group.picture : 'assets/defaults/group-default.webp'">
            </q-avatar>
          </q-item-section>

          <q-space></q-space>

          <q-item-section>
            <q-item-label class="text-h6">{{group.name}}</q-item-label>
          </q-item-section>

          <q-space></q-space>
          <q-item-section avatar>
            <q-btn
              color="secondary"
              unelevated
              rounded
              outline
              class="btn-consulter"
              icon="person_add"
            >
            </q-btn>
          </q-item-section>
          <q-item-section avatar>
            <q-btn
              color="secondary"
              label="Afficher"
              unelevated
              rounded
              outline
              class="btn-consulter"
            >
            </q-btn>
          </q-item-section>
          <q-item-section avatar>
            <q-avatar text-color="white">
              <q-icon
                name="star"
                :color="1 ? 'yellow' : 'grey'"
                @click="console.log('star')"
                size="30px" />
            </q-avatar>
          </q-item-section>
        </q-item>
      </q-card-section>
      <q-card-section v-if="groupList.length == 0">
        <q-item-section>
          <q-item-label class="text-h6">Rien à afficher</q-item-label>
        </q-item-section>
      </q-card-section>
    </q-card>

  </q-page>
</template>
<style scoped>

.bloc-groupe{
  width: 88%;
  border-radius: 15px;
  margin: 20px auto;
}

</style>

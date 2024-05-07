<script setup lang="ts">
import { onMounted, ref } from "vue";
import { Group } from "src/interfaces/group.interface";
import { api } from "boot/axios";
import { getUser } from "stores/userStore";
import { useRouter } from "vue-router";
import DialogCreateGroup from "components/Groups/DialogCreateGroup.vue";
import { useQuasar } from "quasar";

let groupList = ref<Group[]>([]);
const router = useRouter();
let dialogCreate = ref(false);
const $q = useQuasar();
let favEnabled = ref(false);

onMounted(async () => {
  await getGroups();
});

async function getGroups(fav = false, set = false) {
  if (!set) {
    fav = localStorage.getItem("fav-homepage") === "true";
  }
  if (set) {
    localStorage.setItem("fav-homepage", fav.toString());
  }
  favEnabled.value = fav;
  const userData = await getUser();
  try {
    const response = await api.get(`/users/${userData.id}/groups`, {
      params: {
        favorite: fav,
        limit: 10,
      },
    });
    groupList.value = response.data;
  } catch (e) {
    console.error(e);
  }
}

function openDialogCreate() {
  dialogCreate.value = true;
  $q.dialog({
    component: DialogCreateGroup,
    componentProps: {
      isOpen: dialogCreate,
    },
  }).onDismiss(() => {
    getGroups();
  });
}
</script>

<template>
  <q-card class="bloc-group bg-accent">
    <q-card-section>
      <div class="row">
        <h3 class="text-h6">Mes Groupes
          <q-badge color="secondary" floating>
            <q-icon v-if="favEnabled" name="star" color="white" />
            <q-icon v-else name="history" color="white" />
          </q-badge>
        </h3>
        <q-space />
        <q-icon name="more_horiz" size="md" color="secondary" class="q-pa-md">
          <q-menu auto-close dark>
            <q-list style="min-width: 50px">
              <q-item clickable @click="getGroups(true, true)">
                <q-item-section>Afficher uniquement les favoris</q-item-section>
              </q-item>
              <q-item clickable @click="getGroups(false, true)">
                <q-item-section>Afficher les derniers groupes</q-item-section>
              </q-item>
              <q-separator color="accent" />
              <q-item clickable @click="openDialogCreate()">
                <q-item-section>Créer un groupe</q-item-section>
              </q-item>
            </q-list>
          </q-menu>
        </q-icon>
      </div>
    </q-card-section>
    <q-card-section>
      <div class="q-gutter-md q-ml-none">
        <q-avatar
          v-for="group in groupList"
          :key="group.id"
          size="100px"
          @click="router.push(`/groups/${group.id}`)"
        >
          <img :src="group.picture ? group.picture[1] : 'assets/defaults/group-default.webp'" />
          <q-badge color="red" floating>{{ group.id }}</q-badge>
        </q-avatar>
      </div>
    </q-card-section>
    <q-separator />
  </q-card>
</template>

<style scoped>
.bloc-group {
  width: 100%;
  border-radius: 15px;
  margin-top: 50px;
}
</style>

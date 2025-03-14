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
let loading = ref(false);

onMounted(async () => {
  await getGroups();
});

async function getGroups(fav = false, set = false) {
  loading.value = true;
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
  loading.value = false;
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
            <q-icon v-if="favEnabled" name="star" color="white" >
              <q-tooltip>
                Seul les favoris sont affichés
              </q-tooltip>
            </q-icon>
            <q-icon v-else name="history" color="white">
              <q-tooltip>
              Seul les groupes les plus récents sont affichés
            </q-tooltip>
            </q-icon>
          </q-badge>
        </h3>
        <q-space />
        <q-icon name="more_horiz" size="md" color="secondary" class="q-pa-md cursor-pointer">
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
    <q-card-section v-if="!loading">
      <div class="q-gutter-md q-ml-none cursor-pointer">
        <q-avatar
          v-for="group in groupList"
          :key="group.id"
          size="100px"
          @click="router.push(`/groups/${group.id}`)"
        >
          <q-img :src="group.picture ? group.picture[1] : 'assets/defaults/group-default.webp'" />
          <q-tooltip>
            {{ group.name }}
          </q-tooltip>
        </q-avatar>
      </div>
    </q-card-section>
    <q-card-section v-if="!loading && groupList.length == 0">
      <div class="q-gutter-md q-ml-none cursor-pointer">
       <q-item-label v-if="favEnabled" class="text-h6">Aucun groupe favoris</q-item-label>
        <q-item-label v-else class="text-h6">Rien à afficher</q-item-label>
      </div>
    </q-card-section>
    <div class="row" v-if="loading">
      <q-space></q-space>
      <q-spinner size="50px" class="q-pa-xs q-mx-auto" color="secondary" />
      <q-space></q-space>
    </div>
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

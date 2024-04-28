<script setup lang="ts">

import {onMounted, ref } from "vue";
import {api} from "boot/axios";
import {useQuasar} from "quasar";
import {useRouter} from "vue-router";
import {INotifications} from "src/interfaces/notications.interface";
import {formatDate} from "stores/globalFunctionsStore";

const $q = useQuasar()
const router = useRouter();
const width = ref(0);
const mounted = ref(false);

let notifList = ref({ allNotif: [] } as INotifications);

onMounted(async () => {

  await getNotifications()

  function getWidth() {

    width.value = window.innerWidth;
  }
  getWidth();
  mounted.value = true;
  window.addEventListener('resize', getWidth);
});

async function getNotifications() {
  const response = await api.get(`notifs`);
  notifList.value = response.data;

  console.log(response.data.allNotif.length)
  notifList.value.allNotif.sort((a, b) => {
    return new Date(b.date) - new Date(a.date);
  });
}

async function deleteNotification(notifId: string) {
  try {
    await api.delete(`notifs/${notifId}`);
    await getNotifications();
    $q.notify({
      type: 'positive',
      message: 'Supprimé'
    })
  }
  catch (error) {
    console.error(error);
  }
}

</script>

<template>
  <q-page class="col items-center justify-evenly">

    <h3 class="text-h4 q-pa-md">Notifications</h3>

    <div
      v-if="mounted"
    >
      <q-card class="bloc-groupe bg-accent"
              v-for="notif in notifList.allNotif" :key="notif._id">
        <q-separator/>
        <q-card-section>
          <q-item>
            <q-item-section  clickable @click="router.push(notif.link)">
              <q-item-label class="text-h6"><q-badge v-if="!notif.seen" align="middle"  color="red" rounded></q-badge> {{notif.message}}</q-item-label>
              <q-item-label class="text-caption">{{formatDate(notif.date)}}</q-item-label>
            </q-item-section>

            <q-space></q-space>

            <q-item-section avatar  v-if="width>500">
              <q-btn
                color="secondary"
                label="Consulter"
                unelevated
                rounded
                outline
                class="btn-consulter"
                @click="router.push(notif.link)"
              >
              </q-btn>
            </q-item-section>
            <q-item-section avatar v-if="width>400">
              <q-btn
                color="negative"
                unelevated
                flat
                outline
                class="btn-consulter"
                icon="delete"
                @click="deleteNotification(notif._id)"
              >
              </q-btn>
            </q-item-section>
          </q-item>
        </q-card-section>
      </q-card>
      <q-card-section v-if="notifList.allNotif.length == 0">
        <q-item-section>
          <q-item-label class="text-h6">Rien à afficher</q-item-label>
        </q-item-section>
      </q-card-section>
    </div>
  </q-page>
</template>
<style scoped>

.bloc-groupe{
  width: 88%;
  border-radius: 15px;
  margin: 20px auto;
}

</style>

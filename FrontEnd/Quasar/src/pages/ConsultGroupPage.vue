<script setup lang="ts">

import {computed, onMounted, ref} from "vue";
import {disconnectUser, getUser} from "stores/userStore";
import {useRoute, useRouter} from "vue-router";
import {DefaultUser} from "src/interfaces/user.interface";
import {useQuasar} from "quasar";
import {api} from "boot/axios";
import MessageDrawer from "components/ConsultGroup/MessageDrawer.vue";

const router = useRouter();
let User = ref(DefaultUser());
const $q = useQuasar();
const route = useRoute();
const groupId = route.params.id;
let group = ref();
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
  <message-drawer></message-drawer>
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
              <img :src="User.profile_picture ? User.profile_picture : 'assets/defaults/user-default.webp'">
              <div class="absolute-full text-subtitle2 flex flex-center text-secondary"
                   v-if="isPhotoHover">
                Modifier
              </div>
            </q-avatar>
          </div>
          <q-card-section>
            <q-item-label class="text-h4">{{ `${User.firstname} ${User.lastname}` }}</q-item-label>
            <q-item-label class="text-subtitle1">{{ `@${User.username}` }}</q-item-label>

          </q-card-section>
        </q-card-section>
      </q-card>
    </div>
  </q-page>
</template>

<style scoped>
.card-user-data{
  width: 100%;
}
.text-head-card{
  margin-bottom: 15px;
}
.inputs {
  width: 80%;
  display: flex;
  margin: auto;
  flex-direction: column;
}
.input{
  margin-bottom: 12px;
}
.save-btn{
  width: 15%;
  margin: 20px 0 0 0;
}
.confirm-pwd{
  margin-top: 50px;
}
.paiement{
  width: 30%;
  margin: 20px;
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

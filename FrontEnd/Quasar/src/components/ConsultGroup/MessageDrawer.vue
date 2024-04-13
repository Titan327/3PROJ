<script setup lang="ts">
import {onMounted, ref} from 'vue';
import { IMessage } from "src/interfaces/message.interface";
import { getUser } from "stores/userStore";
import {DefaultUser, IUser} from "src/interfaces/user.interface";
import { io } from 'socket.io-client';
import {api} from "boot/axios";


const props = defineProps({
  groupId: Number,
});

const socket = io('https://3proj-back.tristan-tourbier.com/');

socket.on('connect', () => {
  console.log('Connected to server');
});

socket.on(`chat-group-${props.groupId}`, (msg, group) => {
  getMessages();
  scrollNewMsg();
});

const drawerOpen = ref(true);
const messages = ref<IMessage[]>([]);
const senders = ref<Partial<IUser>[]>([]);
let writingMessage = ref('');
const scrollAreaRef = ref(null);

const position = ref(0);
const msgPage = ref(1);
const User = ref(DefaultUser());

onMounted(async () => {

  await getUserData();
  await getGroup();
  await getMessages();

});

function scrollNewMsg () {
  scrollAreaRef.value.setScrollPosition('vertical', position.value, 300)
  position.value = position.value + 300
}

async function getGroup() {
  try {
    const response = await api.get(`/group/${props.groupId}`);
    senders.value = response.data.Users;
    console.log(senders.value)
  }
  catch (error) {
    console.error(error);
  }
}

async function getUserData() {
  const userData = await getUser();
  if (userData !== null) {
    User.value = userData;
  }
}

function openCloseDrawer() {
  drawerOpen.value = !drawerOpen.value;
}

async function getMessages() {
  try {
    const response = await api.get(`/message/${props.groupId}?limit=50&page=${msgPage.value}`);
    messages.value = response.data.messages
    messages.value.sort((a, b) => new Date(a.timestamp) - new Date(b.timestamp));

  } catch (error) {
    console.error('Error getting messages:', error);
  }
}

async function sendMessage() {
  if (writingMessage.value.trim() === '') return;
  try {
    const response = await api.post(`/message/${props.groupId}`, {
        text: writingMessage.value,
    });

    if(response.status == 201){
      socket.emit('chat message', writingMessage.value, props.groupId);
      writingMessage.value = '';
      scrollNewMsg()
    }

  } catch (error) {
    console.error('Error sending message:', error);
  }
}

function getSenderPicture(id:number){
  const thisSender = senders.value.find(sender => Number(sender.UserGroup.userId) == id);

  const defaultAvatarUrl = 'assets/defaults/group-default.webp';
  const profilePictureUrl = senders.value.find(sender => sender.UserGroup.userId == id)?.profile_picture;

  return `${profilePictureUrl ? profilePictureUrl + '/100' : defaultAvatarUrl}`;
}

function getSenderPseudo(id:number){
  return senders.value.find(sender => sender.UserGroup.userId == id)?.username;
}

function getColor(id:number){
  const sender = senders.value.find(sender => sender.UserGroup.userId == id)?.username;
  if(sender == User.value.username){
    return 'white'
  }
  else {
    return 'cyan-4'
  }
}

function getDate(timestamp: string) {

  if(new Date(timestamp).getDate() == new Date().getDate()){
    return new Date(timestamp).toLocaleString("fr-FR", { hour: "numeric", minute: "numeric" });
  }
  else {
    return new Date(timestamp).toLocaleString("fr-FR", { day: "numeric", month: "numeric"});
  }
}
</script>

<template>
  <q-drawer
    side="right"
    v-model="drawerOpen"
    :width="400"
    mini-width="300"
    class="message bg-primary"
  >
    <div class="content">

      <q-item-label class="text-h4 q-pa-md row">Messages</q-item-label>

      <div class="q-pa-md row justify-center">
        <q-label v-if="messages.length == 0" class="text-h6 q-pa-md">Aucun message</q-label>
        <div v-if="messages.length > 0" style="width: 100%; max-width: 400px">
          <q-scroll-area ref="scrollAreaRef" class="q-pa-md" style="height: 85vh; max-width: 400px;">
            <q-chat-message
              class="message"
              v-for="(message, index) in messages"
              :bg-color="getColor(message.userId)"
              :key="index"
              :text="[message.text]"
              :stamp="[getDate(message.timestamp)]"
              :name="[getSenderPseudo(message.userId)]"
              :avatar="[getSenderPicture(message.userId)]"
              :sent="getSenderPseudo(message.userId) == User.username"
            />
          </q-scroll-area>
        </div>
        <div class="fixed-bottom bloc-send row items-center justify-evenly bg-primary">
          <q-input
            class="input-msg"
            v-model="writingMessage"
            @keyup.enter="sendMessage"
            placeholder="Ecrivez votre message ici..."
            dark
            outlined
            dense
            color="secondary"
          />
        <q-btn color="secondary" class="btn" outline @click="sendMessage" icon="send" > </q-btn>
        </div>
      </div>
    </div>
  </q-drawer>
</template>

<style scoped>
.message {
  border-radius: 15px;
}
.btn {
  border-radius: 10px;
}
.input-msg{
  border-radius: 10px;
  width: 70%;
}
.bloc-send{
  width: 100%;
  margin: auto;
  padding: 10px;
  height: 50px;
}
</style>

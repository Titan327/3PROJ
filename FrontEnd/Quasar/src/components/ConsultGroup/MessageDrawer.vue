<script setup lang="ts">
import { ref } from 'vue';
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
  messages.value.push({
    text: msg,
    stamp: new Date().toLocaleString("fr-FR", { hour: "numeric", minute: "numeric" }),
    userId: User.value.id
  });
  //getMessages();
  getSenderData(User.value.id);
  scrollNewMsg()
});

const drawerOpen = ref(true);
const messages = ref<IMessage[]>([]);
const senders = ref<Partial<IUser>[]>([]);
let writingMessage = ref('');
const scrollAreaRef = ref(null);

const position = ref(0)
const User = ref(DefaultUser());

function scrollNewMsg () {
  scrollAreaRef.value.setScrollPosition('vertical', position.value, 300)
  position.value = position.value + 300
}

(async () => {
  await getUserData();
})();

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
    const response = await api.get(`/group/${props.groupId}/messages`);
    messages.value = response.data;
    await getSenderData(response.data.userId);
  } catch (error) {
    console.error('Error getting messages:', error);
  }
}

async function getSenderData(id){
  try {
    if(senders.value.find(sender => sender.id === id)) return;
    else {
      const response = await api.get(`/user/${id}`);
      senders.value.push(response.data);
    }
  }
  catch (error) {
    console.error('Error getting sender data:', error);
  }
}

async function sendMessage() {
  if (writingMessage.value.trim() === '') return;
  try {
    //const response = await api.post(`/group/${props.groupId}/messages`, {
      //message: {
        //text: writingMessage.value,
        //stamp: new Date().toLocaleString("fr-FR", { hour: "numeric", minute: "numeric" }),
        //userId:User.value.id
      //}
    //});

    messages.value.push({
      text: writingMessage.value,
      stamp: new Date().toLocaleString("fr-FR", { hour: "numeric", minute: "numeric" }),
      userId: User.value.id
    });
    console.log(messages.value)

    socket.emit('chat message', writingMessage.value, props.groupId);
    writingMessage.value = '';
    scrollNewMsg()
  } catch (error) {
    console.error('Error sending message:', error);
  }
}

function getSenderPicture(id:number){
  return senders.value.find(sender => sender.id === id)?.profile_picture[0];
};
function getSenderPseudo(id:number){
  return senders.value.find(sender => sender.id === id)?.username;
};
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
              :key="index"
              :text="[message.text]"
              :stamp="[message.stamp]"
              :name="[getSenderPseudo(index)]"
              :avatar="[getSenderPicture(index)]"
              :sent="getSenderPseudo(index) == User.username"
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

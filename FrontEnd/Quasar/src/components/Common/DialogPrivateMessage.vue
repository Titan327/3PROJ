<script setup lang="ts">
import {onMounted, ref, watch} from 'vue';
import { IMessage } from "src/interfaces/message.interface";
import { getUser } from "stores/userStore";
import {DefaultUser, IUser} from "src/interfaces/user.interface";
import { io } from 'socket.io-client';
import {api} from "boot/axios";
import {NotificationBus} from "boot/eventBus";

const messages = ref<IMessage[]>([]);
const senders = ref<Partial<IUser>[]>([]);
let writingMessage = ref('');
const scrollAreaRef = ref(null);
const topLoading = ref(false);
const bottomLoading = ref(false);
const msgPage = ref(1);
const User = ref(DefaultUser());
let isOpen = ref(false);

const props = defineProps({
  groupId: Number,
  user2id: Number,
});

const emit = defineEmits(['updateState', 'messages'])

const socket = io(process.env.URL_BACKEND);

socket.on(`chat-private-${props.groupId}`, (msg, group) => {
  getMessages();
  scrollNewMsg();
  if(isOpen.value == false){
    emit('messages')
    NotificationBus.emit('new-notif');
  }
});

onMounted(async () => {

  await getUserData();
  await getGroup();
  await getMessages();
  scrollNewMsg ();
});

function checkScroll() {
  if(topLoading.value || bottomLoading.value) return;
  if(msgPage.value == 1 && messages.value.length < 100) return;
  const scrollArea = scrollAreaRef.value;
  if (scrollArea !== null) {
    const scrollPercentage = scrollArea.getScrollPercentage('vertical');
    if (scrollPercentage.top == 0) {
      msgPage.value++;
      getMessages();
      scrollArea.setScrollPercentage('vertical', 0.2);
    }
    if (scrollPercentage.top >= 0.8 && msgPage.value > 1) {
      msgPage.value--;
      getMessages();
      scrollArea.setScrollPercentage('vertical', 0.8);
    }
  }
}

function scrollNewMsg () {
  scrollAreaRef.value.setScrollPercentage('vertical',100);
}

async function getGroup() {
  try {
    const response = await api.get(`/groups/${props.groupId}`);
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

function CloseDrawer() {
  isOpen.value = false;
  emit('updateState')
}

async function getMessages() {
  bottomLoading.value = true;
  try {
    const response = await api.get(`/messages/private/${props.groupId}/${User.value.id}/${props.user2id}/?limit=100&page=${msgPage.value}`);
    messages.value = response.data.messages
    messages.value.sort((a, b) => new Date(a.timestamp) - new Date(b.timestamp));
  } catch (error) {
    console.error('Error getting messages:', error);
  }
  topLoading.value = false;
  bottomLoading.value = false;
}

async function sendMessage() {
  if (writingMessage.value.trim() === '') return;
  try {
    const response = await api.post(`/messages/private/${props.groupId}/${User.value.id}/${props.user2id}`, {
      text: writingMessage.value,
    });

    if(response.status == 200){
      socket.emit('private message', writingMessage.value, props.groupId);
      writingMessage.value = '';
      scrollNewMsg()
    }

  } catch (error) {
    console.error('Error sending message:', error);
  }
}

function getSenderPicture(id:number){

  const thisSender = senders.value.find(sender => Number(sender.UserGroup.userId) == id);

  const defaultAvatarUrl = 'assets/defaults/user-default.webp';
  const profilePictureUrl = senders.value.find(sender => sender.UserGroup.userId == id)?.profile_picture;

  return `${profilePictureUrl ? profilePictureUrl[0] : defaultAvatarUrl}`;
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
  <q-dialog v-model="isOpen">
    <q-card
      class="message bg-primary"
      style="width: 400px; max-width: 80vw;"
    >
    <div class="content">

      <div class="row">
        <q-item-label class="text-h4 q-pa-md row">Messages privés</q-item-label>
        <q-space></q-space>
        <q-btn
          color="secondary"
          class="q-ma-md"
          outline
          round
          icon="close"
          @click="CloseDrawer">
        </q-btn>
      </div>
      <span class="q-pa-md">{{getSenderPseudo(props.user2id)}}</span>

      <div class="q-pa-md row justify-center">
        <q-label v-if="messages.length == 0" class="text-h6 q-pa-md">Aucun message</q-label>
        <q-spinner
          color="secondary"
          size="2em"
          v-if="topLoading"
        />
        <div v-if="messages.length > 0" style="width: 100%; max-width: 400px">
          <q-scroll-area @scroll="checkScroll" ref="scrollAreaRef" class="q-pa-md" style="height:calc(70vh - 200px); max-width: 400px;">
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
        <q-spinner
          color="secondary"
          size="2em"
          v-if="bottomLoading"
        />
        <div class="bloc-send row items-center justify-evenly bg-primary">
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
          <q-btn color="secondary" class="btn" outline @click="sendMessage" icon="send" round></q-btn>
        </div>
        <q-space></q-space>
      </div>
    </div>
    </q-card>
  </q-dialog>
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
  width: 80%;
}
.bloc-send{
  width: 100%;
  margin: auto;
  padding: 10px;
  height: 50px;
}
</style>

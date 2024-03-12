<template>
  <div class="chat-page">
    <div class="chat-container">
      <div class="chat-messages">
        <!-- Display chat messages here -->
      </div>
      <div class="chat-input">
        <input type="text" v-model="message" @keyup.enter="sendMessage" placeholder="Type your message...">
        <button @click="sendMessage">Send Mes</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      message: '',
      messages: [] // Array to store chat messages
    };
  },
  methods: {
    sendMessage() {
      if (this.message.trim() !== '') {
        this.messages.push(this.message);
        this.message = '';
      }
    }
  }
};
</script>

<style scoped>
.chat-page {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

.chat-container {
  width: 400px;
  border: 1px solid #ccc;
  border-radius: 4px;
  padding: 16px;
}

.chat-messages {
  height: 300px;
  overflow-y: scroll;
  border-bottom: 1px solid #ccc;
}

.chat-input {
  display: flex;
  margin-top: 16px;
}

.chat-input input {
  flex: 1;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.chat-input button {
  margin-left: 8px;
  padding: 8px 16px;
  background-color: #007bff;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
</style>

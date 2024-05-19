<template>
  <div ref="buttonDiv"></div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import axios from 'axios';

const buttonDiv = ref<HTMLDivElement | null>(null);

const handleCredentialResponse = (response: any) => {

  axios.post('https://3proj-back.tristan-tourbier.com/api/oauth2/google', { token: response.credential })
    .then(response => {

      sessionStorage.setItem('userToken', response.data.token);
      window.location.href = '/';
    })
    .catch(error => {
      console.error(error);
      console.error('Internal error server');
    });
};

const initializeGoogleOneTap = () => {
  if (typeof google !== 'undefined' && google.accounts) {
    google.accounts.id.initialize({
      client_id: '1081302926939-apm23gnd9muarl2j6j4l24labnkt6e3r.apps.googleusercontent.com',
      callback: handleCredentialResponse
    });
    google.accounts.id.renderButton(
      buttonDiv.value,
      {theme: 'outline', size: 'large'}
    );
    google.accounts.id.prompt();
  } else {
    console.error("La bibliothèque Google One Tap n'est pas chargée.");
  }
};

onMounted(() => {
  initializeGoogleOneTap();
});
</script>

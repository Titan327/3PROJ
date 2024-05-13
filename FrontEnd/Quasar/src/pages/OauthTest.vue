<template>
  <q-layout>
    <q-page-container>
      <div ref="buttonDiv"></div>
    </q-page-container>
  </q-layout>
</template>

<script>
import axios from "axios";

export default {
  name: 'GoogleOneTap',
  mounted() {
    this.initializeGoogleOneTap();
  },
  methods: {
    handleCredentialResponse(response) {
      console.log('Encoded JWT ID token: ' + response.credential);

      axios.post('http://3proj-back.tristan-tourbier.com/api/oauth2/google', { token: response.credential })
        .then(response => {
          console.log('Réponse du backend :', response.data);

        })
        .catch(error => {
          console.error('Erreur lors de la requête vers le backend :', error);
        });

    },
    initializeGoogleOneTap() {
      if (typeof google !== 'undefined' && google.accounts) {
        google.accounts.id.initialize({
          client_id: '1081302926939-apm23gnd9muarl2j6j4l24labnkt6e3r.apps.googleusercontent.com',
          callback: this.handleCredentialResponse
        });
        google.accounts.id.renderButton(
          this.$refs.buttonDiv,
          {theme: 'outline', size: 'large'}
        );
        google.accounts.id.prompt();
      } else {
        console.error("La bibliothèque Google One Tap n'est pas chargée.");
      }
    }
  }
}
</script>

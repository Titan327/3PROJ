<template>
  <q-layout>
    <q-page-container>
      <div ref="buttonDiv"></div>
    </q-page-container>
  </q-layout>
</template>

<script>
export default {
  name: 'GoogleOneTap',
  mounted() {
    this.initializeGoogleOneTap();
  },
  methods: {
    handleCredentialResponse(response) {
      console.log('Encoded JWT ID token: ' + response.credential);
      // Vous pouvez effectuer des actions supplémentaires ici, comme l'envoi du token à votre serveur, etc.
    },
    initializeGoogleOneTap() {
      if (typeof google !== 'undefined' && google.accounts) {
        google.accounts.id.initialize({
          client_id: '1081302926939-apm23gnd9muarl2j6j4l24labnkt6e3r.apps.googleusercontent.com',
          callback: this.handleCredentialResponse
        });
        google.accounts.id.renderButton(
          this.$refs.buttonDiv,
          {theme: 'outline', size: 'large'}  // customization attributes
        );
        google.accounts.id.prompt(); // affiche également la boîte de dialogue One Tap
      } else {
        console.error("La bibliothèque Google One Tap n'est pas chargée.");
      }
    }
  }
}
</script>

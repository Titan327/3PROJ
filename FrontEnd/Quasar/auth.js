import Vue from 'vue';
import VueAuthenticate from 'vue-authenticate';
import { boot } from '@quasar/app';

Vue.use(VueAuthenticate);

const storage = {
  getItem: (key) => localStorage.getItem(key),
  setItem: (key, value) => localStorage.setItem(key, value),
  removeItem: (key) => localStorage.removeItem(key),
};

boot('auth', {
  storage,
  providers: {
    google: {
      clientId: 'YOUR_GOOGLE_CLIENT_ID',
      redirectUri: 'http://localhost:8080/callback',
      scope: ['profile', 'email'],
    },
    facebook: {
      clientId: 'YOUR_FACEBOOK_CLIENT_ID',
      redirectUri: 'http://localhost:8080/callback',
      scope: ['public_profile', 'email'],
    },
  },
});

export default function (/* { app } */) {
  // You can optionally add logic here if needed (e.g., global event listeners)
}

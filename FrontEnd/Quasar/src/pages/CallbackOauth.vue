<script setup lang="ts">
import { onMounted, ref } from 'vue'

const oauthCode = ref<string | null>(null)

onMounted(() => {
  // Extrait le code OAuth de l'URL de la requête
  const urlParams = new URLSearchParams(window.location.search)
  oauthCode.value = urlParams.get('code')
  console.log('Code OAuth reçu :', oauthCode.value)
})

</script>

<template>
  <div>{{oauthCode}}</div>
</template>

<style scoped>

</style>

<script setup lang="ts">

</script>

<template>

  <q-card class="bloc-paye bg-primary">
    <q-card-section >
      <q-item>
        <q-item-section avatar>
          <q-avatar rounded color="secondary" text-color="white" icon="south_west" />
        </q-item-section>

        <q-item-section>
          <q-item-label class="text-grey-5">Total payé</q-item-label>
          <q-item-label class="text-white" caption lines="2">4567€</q-item-label>
        </q-item-section>
      </q-item>
    </q-card-section>
  </q-card>
</template>

<style scoped>

.bloc-paye{
  width: 40%;
  height: 120px;
  border-radius: 15px;
}

</style>

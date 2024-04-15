<script setup lang="ts">
import {ref} from "vue";
import {Transaction} from "src/interfaces/transactions.interface";

let  tab = ref('transactions')
const transactionList = ref<Transaction[]>([]);


</script>

<template>
  <div class="q-pa-md">
    <div class="q-gutter-y-md" style="">
      <q-card class="card-tab">
        <q-tabs
          v-model="tab"
          dense
          class="text-primary tableau bg-accent"
          active-color="secondary"
          indicator-color="secondary"
          align="justify"
          narrow-indicator
        >
          <q-tab name="transactions" label="Transactions" />
          <q-tab name="remboursements" label="Remboursements" />
          <q-tab name="statistiques" label="Statistiques" />
        </q-tabs>

        <q-separator />

        <q-tab-panels v-model="tab" class="bg-accent" animated>
          <q-tab-panel name="transactions">
            <div class="row">
              <q-btn-dropdown
                color="primary"
                label="Trier par"
                no-caps
                rounded
              >
                <q-list>
                  <q-item class="bg-primary" clickable v-close-popup @click="onItemClick">
                    <q-item-section>
                      <q-item-label>Date</q-item-label>
                    </q-item-section>
                  </q-item>

                  <q-item class="bg-primary" clickable v-close-popup @click="onItemClick">
                    <q-item-section>
                      <q-item-label>Montant</q-item-label>
                    </q-item-section>
                  </q-item>

                  <q-item  class="bg-primary" clickable v-close-popup @click="onItemClick">
                    <q-item-section>
                      <q-item-label>Titre A-Z</q-item-label>
                    </q-item-section>
                  </q-item>
                </q-list>
              </q-btn-dropdown>
              <q-space>

              </q-space>
              <q-btn
                color="secondary"
                icon-right="add"
                label="Nouvelle transaction"
                no-caps/>
            </div>
            <div class="row">
              <q-card-section v-if="transactionList.length > 0">
                <q-item v-for="transaction in transactionList" :key="transaction.id">
                  <q-item-section avatar>
                    <q-avatar rounded color="secondary" text-color="white">
                      {{ transaction.Transaction.group.picture }}
                    </q-avatar>
                  </q-item-section>

                  <q-item-section>
                    <q-item-label class="text-h6">{{ transaction.Transaction.id}}</q-item-label>
                  </q-item-section>

                  <q-item-section>
                    <q-item-label class="text-h6">{{ transaction.Transaction.date }}</q-item-label>
                  </q-item-section>

                  <q-item-section>
                    <q-item-label class="text-h6">{{ transaction.Transaction.total_amount }}</q-item-label>
                  </q-item-section>
                  <q-item-section>
                    <q-chip class="chip-status" color="green" text-color="white">Payé</q-chip>
                  </q-item-section>
                </q-item>
              </q-card-section>
              <q-card-section v-if="transactionList.length == 0">
                <q-item-section>
                  <q-item-label class="text-h6 q-pa-lg">Rien à afficher</q-item-label>
                </q-item-section>
              </q-card-section>
            </div>
          </q-tab-panel>

          <q-tab-panel name="remboursements">
            <div class="text-h6">Alarms</div>
            Lorem ipsum dolor sit amet consectetur adipisicing elit.
          </q-tab-panel>

          <q-tab-panel name="statistiques">
            <div class="text-h6">Movies</div>
            Lorem ipsum dolor sit amet consectetur adipisicing elit.
          </q-tab-panel>
        </q-tab-panels>
      </q-card>
    </div>
  </div>
</template>

<style scoped>
.tableau{
  width: 100%;
}

.card-tab{
  width: 100%;
  border-radius: 15px;
  margin-top: 50px;
}
</style>

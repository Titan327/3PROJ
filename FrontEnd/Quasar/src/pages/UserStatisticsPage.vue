<script setup lang="ts">

import {computed, onMounted, ref} from "vue";
import {disconnectUser, getUser} from "stores/userStore";
import {useRouter} from "vue-router";
import {DefaultUser} from "src/interfaces/user.interface";
import {useQuasar} from "quasar";
import {api} from "boot/axios";
import {Chart as ChartJS, ArcElement, Tooltip, Legend, CategoryScale, LinearScale, BarElement, Title} from 'chart.js'
import { Doughnut, Bar } from 'vue-chartjs'
import DialogUpdateImage from "components/Common/DialogUpdateImage.vue";
import {UserTransactionsStatistics} from "src/interfaces/userStatistics.interface";

const router = useRouter();
let User = ref(DefaultUser());
let modifiedUser = ref(DefaultUser());
const $q = useQuasar();

let loading = ref(false);
let mounted = ref(false);
const width = ref(0);

let isPhotoHover = ref(false);
let expendGlobalStats = ref(true);
let dialogModifyPp = ref(false);
const chartCatdata= ref();
const chartCountCatdata= ref();
const chartAverageCatdata= ref();
const chartDepenssePerMonth= ref();
const chartTotalDepenssePerMonth= ref();

const stats = ref<UserTransactionsStatistics[]>([]);

const options = {
  responsive: false,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      position: 'left',
      labels: {
        font: {
          size: 14
        }
      }
    }
  }
}
const colors = [
    '#FF5733', '#FFC300', '#8d5d51', '#C70039', '#900C3F',
    '#581845', '#f59a0a', '#3498DB', '#1ABC9C', '#16A085',
    '#27AE60', '#1b898d', '#7D3C98', '#ad448c', '#D4AC0D'
]

const optionsBar = {
  responsive: false,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      display: false,
    },
    scales: {
      x: {
        ticks: {
          display: false,
        },
      },
    },
  },
}


onMounted(async () => {

  await getUserData();
  await getStats();
  await getChart();

  mounted.value=true;

  function getWidth() {
    width.value = window.innerWidth;
    getChart();
  }
  getWidth();
  window.addEventListener('resize', getWidth);
});

async function getUserData() {
  const userData = await getUser();
  if (userData != null) {
    User.value = userData;
    modifiedUser.value = userData;
  }
}

async function openDialogPP(){

       dialogModifyPp.value = true;
      $q.dialog({
        component: DialogUpdateImage,

        componentProps: {
          isOpen: dialogModifyPp,
        }
      }).onDismiss(() => {
        window.location.reload();
      })
}
async function getChart(){
  ChartJS.register(ArcElement, Tooltip, Legend);
  ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend)

  chartCatdata.value = {
    labels: Object.keys(stats.value.amountByCategories),
    datasets: [
      {
        backgroundColor: colors,
        data: Object.values(stats.value.amountByCategories)
      }
    ]
  };

  chartCountCatdata.value = {
    labels: Object.keys(stats.value.numberByCategories),
    datasets: [
      {
        backgroundColor: colors,
        data: Object.values(stats.value.numberByCategories)
      }
    ]
  };

  chartAverageCatdata.value = {
    labels: Object.keys(stats.value.averageByCategories),
    datasets: [
      {
        backgroundColor: colors,
        data: Object.values(stats.value.averageByCategories)
      }
    ]
  };

  chartDepenssePerMonth.value = {
    labels: Object.keys(stats.value.numberByMonth),
    datasets: [
      {
        label: null,
        backgroundColor: colors,
        data: Object.values(stats.value.numberByMonth)
      }
    ]
  }

  chartTotalDepenssePerMonth.value = {
    labels: Object.keys(stats.value.amountByMonth),
    datasets: [
      {
        label: null,
        backgroundColor: colors,
        data: Object.values(stats.value.amountByMonth)
      }
    ]
  }

}

async function getStats(){
  try {
    const response = await api.get(`/users/${User.value.id}/statistics`)
    stats.value = response.data
  }
  catch(e){
    console.error(e)
  }
}

</script>

<template>
  <q-page class="q-pa-md">
    <div class="div-first-last-name">
      <q-card class="transparent no-box-shadow">
        <q-card-section :horizontal="width<500? false:true">
            <div class="q-pa-md q-gutter-sm">
              <q-avatar
                @click="openDialogPP"
                @mouseenter ="isPhotoHover=true"
                @mouseleave ="isPhotoHover=false"
                size="200px"
                class="cursor-pointer"
              >
                <img :src="User.profile_picture ? User.profile_picture[2] : 'assets/defaults/user-default.webp'">
                <div class="absolute-full text-subtitle2 flex flex-center text-secondary"
                v-if="isPhotoHover">
                  Modifier
                </div>
                <q-tooltip>
                  Modifier la photo de profil
                </q-tooltip>
              </q-avatar>
            </div>
          <q-card-section>
            <q-item-label class="text-h4">{{ `${User.firstname} ${User.lastname}` }}</q-item-label>
            <q-item-label class="text-subtitle1">{{ `@${User.username}` }}</q-item-label>
          </q-card-section>
        </q-card-section>
      </q-card>

    </div>
    <div class="q-pa-md row items-start q-gutter-md">
      <q-card class="card-user-data bg-primary rounded-borders">
        <q-card-actions>
          <q-item-label class="text-h6 text-head-card q-pa-md" @click="expendGlobalStats = !expendGlobalStats">Statistiques</q-item-label>

          <q-space />

          <q-btn
            color="grey"
            round
            flat
            dense
            :icon="expendGlobalStats ? 'keyboard_arrow_up' : 'keyboard_arrow_down'"
            @click="expendGlobalStats = !expendGlobalStats"
          />
        </q-card-actions>

        <q-slide-transition>
          <div v-show="expendGlobalStats">
            <q-separator />
            <q-card-section v-if="mounted">
              <q-item-label class="text-h6">{{ stats.transactions }} Transactions au total</q-item-label>
              <q-item-label class="text-h6">Dépense moyenne {{ stats.average }}€</q-item-label>
            </q-card-section>
            <q-card-section  v-if="mounted" class="row chart-container">
              <q-card  flat dark  bordered class="card-chart">
                <div class="q-pa-md q-mx-auto">
                  <q-item-label class="text-h6">Dépenses totale par catégorie (€) :</q-item-label>
                  <Doughnut class="q-mx-auto chart" :data="chartCatdata" :options="options"/>
                </div>
              </q-card>

              <q-card  flat dark  bordered class="card-chart">
                <div class="q-pa-md q-mx-auto">
                  <q-item-label class="text-h6">Quantité de dépenses par catégorie :</q-item-label>
                  <Doughnut class="q-mx-auto chart" :data="chartCountCatdata" :options="options"/>
                </div>
              </q-card>

              <q-card  flat dark  bordered class="card-chart">
                <div class="q-pa-md q-mx-auto">
                  <q-item-label class="text-h6">Dépenses moyennes par catégorie (€)  :</q-item-label>
                  <Doughnut class="q-mx-auto chart" :data="chartAverageCatdata" :options="options"/>
                </div>
              </q-card>

              <q-card  flat dark  bordered class="card-chart">
                <div class="q-pa-md q-mx-auto">
                  <q-item-label class="text-h6">Total des dépenses par mois (€):</q-item-label>
                  <Bar class="q-mx-auto w-70 chart chart-center" :data="chartTotalDepenssePerMonth" :options="optionsBar"/>
                </div>
              </q-card>

              <q-card  flat dark  bordered class="card-chart">
                <div class="q-pa-md q-mx-auto">
                  <q-item-label class="text-h6">Nombre de dépenses par mois :</q-item-label>
                  <Bar class="q-mx-auto w-70 chart chart-center" :data="chartDepenssePerMonth" :options="optionsBar"/>
                </div>
              </q-card>
            </q-card-section>
          </div>
        </q-slide-transition>
      </q-card>
    </div>
  </q-page>
</template>

<style scoped>
.card-user-data{
  width: 100%;
}
.text-head-card{
  margin-bottom: 15px;
}
.card-chart{
  margin:20px;
  width: calc(50% - 40px);
}
.chart-center{
  margin-top: calc(50% - 150px);;
}
.chart{
  width: 70%;
}

@media screen and (max-width: 1000px) {
  .card-chart{
    margin:20px;
    width: calc(100% - 40px);
  }
  .chart{
    width: 50%;
  }
}

@media screen and (max-width: 800px) {
  .chart{
    width: 30%;
  }
}
</style>

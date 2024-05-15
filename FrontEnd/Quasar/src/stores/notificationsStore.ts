import { ref, Ref } from 'vue';
import { api } from "boot/axios";

const notifications = ref([]);
const count = ref(null);

async function getNotificationsData(): Promise<void> {
  try {
    const response = await api.get(`notifs`, {
    });
    notifications.value = response.data;
  }
  catch (error) {
    console.error("Erreur lors de la récupération des notifications", error);
  }
}
async function getNotificationCounts() {
  try {
    const response = await api.get('notifs/count');
    count.value = response.data.count;
  } catch (error) {
    console.error(error);
  }
}


async function getNotifications(){
  if (notifications.value) {
    await getNotificationsData();
  }
  return notifications.value;
}

async function getNotificationsCountData(update = false){
  if (update) {
    await getNotificationCounts();
    return count.value;
  }
  if (count.value) {
    return count.value;
  }
  else {
    await getNotificationCounts();
    return count.value;
  }
}

export {getNotifications, getNotificationsCountData};

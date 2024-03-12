import { ref, Ref } from 'vue';
import { api } from "boot/axios";
import {DefaultUser, IUser} from "src/interfaces/user.interface";

const user: Ref<IUser> = ref(DefaultUser());
function updateUser(token: string): void {
  user.value.token = token;
  localStorage.setItem('userToken', token);
}

async function getUserData(): Promise<void> {
  const token = localStorage.getItem('userToken');

  if (!token) {
    console.error("Pas de token");
    return;
  }

  try {
    const payload = token.split('.')[1];
    const decodedPayload = JSON.parse(atob(payload));

    const response = await api.get(`user/${decodedPayload.userId}`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });

    const userData = response.data;
    user.value.id = decodedPayload.userId;
    user.value.firstName = userData.firstname;
    user.value.lastName = userData.lastname;
    user.value.username = userData.username;
    user.value.email = userData.email;
    user.value.birthdate = userData.birth_date;
  } catch (error) {

    console.error("Erreur lors de la récupération des données utilisateur :", error);
  }
}

async function getUser(): Promise<IUser> {
  if (!user.value.id) {
    await getUserData();
  }
  return user.value;
}

function disconnectUser(): void {
  user.value = DefaultUser();
  localStorage.removeItem('userToken');
}

export { updateUser, getUser, disconnectUser };

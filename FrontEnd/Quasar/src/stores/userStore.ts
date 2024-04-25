import { ref, Ref } from 'vue';
import { api } from "boot/axios";
import {DefaultUser, IUser} from "src/interfaces/user.interface";

const user: Ref<IUser> = ref(DefaultUser());
function updateUser(token: string): void {
  user.value.token = token;
  sessionStorage.setItem('userToken', token);
}

async function getUserData(): Promise<void> {
  const token = sessionStorage.getItem('userToken');

  if (!token) {
    console.error("Pas de token");
    return;
  }

  try {
    const payload = token.split('.')[1];
    const decodedPayload = JSON.parse(atob(payload));

    const response = await api.get(`users/${decodedPayload.userId}`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });

    const userData = response.data;
    user.value.id = decodedPayload.userId;
    user.value.firstname = userData.firstname;
    user.value.lastname = userData.lastname;
    user.value.username = userData.username;
    user.value.email = userData.email;
    user.value.birth_date = new Date(userData.birth_date).toISOString().split('T')[0];
    user.value.profile_picture = userData.profile_picture;
  } catch (error) {

    console.error("Erreur lors de la récupération des données utilisateur :", error);
    disconnectUser();
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

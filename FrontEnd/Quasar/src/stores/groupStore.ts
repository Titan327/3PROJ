import { ref } from 'vue';
import { api } from "boot/axios";
import { DefaultGroup, Group } from 'src/interfaces/group.interface';

const group = ref(DefaultGroup());

async function getGroupData(id:number): Promise<void> {
  try {
    const response = await api.get(`groups/${id}`, {
    });

    const gettedGroup = response.data;
    group.value.id = gettedGroup.id;
    group.value.name = gettedGroup.name;
    group.value.description = gettedGroup.description;
    group.value.picture = gettedGroup.picture;
    group.value.ownerId = gettedGroup.ownerId;
    group.value.createdAt = gettedGroup.updatedAt;
    group.value.Users = gettedGroup.Users;
    group.value.activeUsersCount = gettedGroup.activeUsersCount;
  }
  catch (error) {
    console.error("Erreur lors de la récupération des données du groupe :", error);
  }
}

async function getGroup(id:number): Promise<Group> {
  if (group.value.id !== id) {
    await getGroupData(id);
  }
  return group.value;
}

function getUserGroupData(id:number){
  return group.value.Users.find(user => user.id == id);
}

export {getGroup, getUserGroupData};

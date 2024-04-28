export interface INotifications {
  allNotif: INotification[]
}

export interface INotification {
  "_id": string,
  "user_id": number,
  "message": string,
  "link":string,
  "seen": boolean,
  "date":Date
}

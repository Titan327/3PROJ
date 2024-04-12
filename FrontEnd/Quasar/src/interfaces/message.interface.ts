export interface IMessage {
  messages: [
    _id: string,
    text: string,
    timestamp: Date,
    userId: number,
    groupId: number,
  ]
}

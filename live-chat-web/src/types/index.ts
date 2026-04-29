export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

export interface LoginResponse {
  token: string;
  userId: number;
  nickname: string;
  avatar: string;
  accountType: string;
}

export interface RoomSummary {
  id: number;
  roomName: string;
  ownerUserId: number;
  coverUrl: string;
  roomStatus: string;
  onlineCount: number;
}

export interface RoomDetail extends RoomSummary {}

export interface MessageItem {
  messageId: number;
  roomId: number;
  senderId: number;
  senderName: string;
  messageType: string;
  content: string;
  status: string;
  sendTime: string;
}

export interface WsEnvelope<T> {
  type: string;
  data: T;
}

export interface WsErrorPayload {
  code: number;
  message: string;
}

export interface WsOnlineCountPayload {
  roomId: number;
  onlineCount: number;
}


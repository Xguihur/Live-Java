import { httpClient } from "@/lib/axios";
import type { ApiResponse, MessageItem } from "@/types";

export async function fetchRoomMessages(roomId: number) {
  const response = await httpClient.get<ApiResponse<MessageItem[]>>(`/api/rooms/${roomId}/messages`, {
    params: {
      pageSize: 20,
    },
  });
  return response.data.data;
}

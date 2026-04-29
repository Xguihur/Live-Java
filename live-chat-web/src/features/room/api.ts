import { httpClient } from "@/lib/axios";
import type { ApiResponse, RoomDetail, RoomSummary } from "@/types";

export async function fetchRoomList() {
  const response = await httpClient.get<ApiResponse<RoomSummary[]>>("/api/rooms");
  return response.data.data;
}

export async function fetchRoomDetail(roomId: number) {
  const response = await httpClient.get<ApiResponse<RoomDetail>>(`/api/rooms/${roomId}`);
  return response.data.data;
}


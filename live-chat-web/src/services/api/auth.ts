import { httpClient } from "@/lib/axios";
import type { ApiResponse, LoginResponse } from "@/types";

export async function login(nickname: string) {
  const response = await httpClient.post<ApiResponse<LoginResponse>>("/api/auth/login", {
    nickname,
  });
  return response.data.data;
}

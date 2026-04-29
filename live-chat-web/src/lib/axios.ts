import axios from "axios";
import { useAuthStore } from "@/stores/auth-store";
import type { ApiResponse } from "@/types";

const baseURL = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

export const httpClient = axios.create({
  baseURL,
  timeout: 10_000,
});

httpClient.interceptors.request.use((config) => {
  const token = useAuthStore.getState().token;
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

httpClient.interceptors.response.use((response) => {
  const payload = response.data as ApiResponse<unknown>;
  if (payload.code !== 0) {
    return Promise.reject(new Error(payload.message));
  }
  return response;
});


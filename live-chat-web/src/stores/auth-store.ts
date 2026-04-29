import { create } from "zustand";
import { persist } from "zustand/middleware";
import type { LoginResponse } from "@/types";

interface AuthState {
  token: string | null;
  currentUser: LoginResponse | null;
  setSession: (session: LoginResponse) => void;
  clearSession: () => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      token: null,
      currentUser: null,
      setSession: (session) =>
        set({
          token: session.token,
          currentUser: session,
        }),
      clearSession: () =>
        set({
          token: null,
          currentUser: null,
        }),
    }),
    {
      name: "live-chat-auth",
    },
  ),
);


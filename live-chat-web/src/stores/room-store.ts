import { create } from "zustand";

interface RoomState {
  currentRoomId: number | null;
  drafts: Record<number, string>;
  setCurrentRoomId: (roomId: number | null) => void;
  setDraft: (roomId: number, draft: string) => void;
}

export const useRoomStore = create<RoomState>((set) => ({
  currentRoomId: null,
  drafts: {},
  setCurrentRoomId: (roomId) => set({ currentRoomId: roomId }),
  setDraft: (roomId, draft) =>
    set((state) => ({
      drafts: {
        ...state.drafts,
        [roomId]: draft,
      },
    })),
}));


import { FormEvent } from "react";
import { useRoomStore } from "@/stores/room-store";

interface ChatInputProps {
  roomId: number;
  disabled?: boolean;
  onSend: (content: string) => void;
}

export function ChatInput({ roomId, disabled, onSend }: ChatInputProps) {
  const draft = useRoomStore((state) => state.drafts[roomId] ?? "");
  const setDraft = useRoomStore((state) => state.setDraft);

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const content = draft.trim();
    if (!content || disabled) {
      return;
    }
    onSend(content);
    setDraft(roomId, "");
  }

  return (
    <form
      onSubmit={handleSubmit}
      className="mt-4 flex flex-col gap-3 rounded-[28px] border border-white/10 bg-white/6 p-4"
    >
      <textarea
        value={draft}
        onChange={(event) => setDraft(roomId, event.target.value)}
        placeholder="输入你想在直播间里说的话..."
        className="min-h-28 rounded-2xl border border-white/10 bg-slate-950/60 px-4 py-3 text-sm text-slate-100 outline-none transition focus:border-sky-400/60"
        maxLength={300}
      />
      <div className="flex items-center justify-between">
        <span className="text-xs text-slate-400">MVP 版当前仅支持文本消息。</span>
        <button
          type="submit"
          disabled={disabled || draft.trim().length === 0}
          className="rounded-full bg-sky-500 px-5 py-2 text-sm font-medium text-white transition hover:bg-sky-400 disabled:cursor-not-allowed disabled:bg-slate-700"
        >
          发送消息
        </button>
      </div>
    </form>
  );
}


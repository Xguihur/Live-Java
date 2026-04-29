import dayjs from "dayjs";
import { useAuthStore } from "@/stores/auth-store";
import { joinClassNames } from "@/lib/utils";
import type { MessageItem as Message } from "@/types";

interface MessageItemProps {
  message: Message;
}

export function MessageItem({ message }: MessageItemProps) {
  const currentUserId = useAuthStore((state) => state.currentUser?.userId);
  const isSelf = currentUserId === message.senderId;

  return (
    <div className={joinClassNames("flex", isSelf ? "justify-end" : "justify-start")}>
      <div
        className={joinClassNames(
          "max-w-[85%] rounded-3xl px-4 py-3 shadow-lg md:max-w-[70%]",
          isSelf ? "bg-sky-500 text-white" : "bg-white/8 text-slate-100",
        )}
      >
        <div className="mb-1 flex items-center gap-2 text-xs">
          <span className={joinClassNames("font-medium", isSelf ? "text-sky-50" : "text-slate-300")}>
            {message.senderName}
          </span>
          <span className={joinClassNames(isSelf ? "text-sky-100/80" : "text-slate-400")}>
            {dayjs(message.sendTime).format("HH:mm:ss")}
          </span>
        </div>
        <div className="whitespace-pre-wrap break-words text-sm leading-6">{message.content}</div>
      </div>
    </div>
  );
}


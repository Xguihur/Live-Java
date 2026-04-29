import { useDeferredValue, useEffect, useRef } from "react";
import { MessageItem } from "@/components/chat/message-item";
import type { MessageItem as Message } from "@/types";

interface MessageListProps {
  messages: Message[];
}

export function MessageList({ messages }: MessageListProps) {
  const deferredMessages = useDeferredValue(messages);
  const containerRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    if (!containerRef.current) {
      return;
    }
    containerRef.current.scrollTop = containerRef.current.scrollHeight;
  }, [deferredMessages]);

  return (
    <div
      ref={containerRef}
      className="flex min-h-[420px] flex-1 flex-col gap-3 overflow-y-auto rounded-[28px] border border-white/10 bg-slate-950/35 p-4"
    >
      {deferredMessages.map((message) => (
        <MessageItem key={message.messageId} message={message} />
      ))}
    </div>
  );
}


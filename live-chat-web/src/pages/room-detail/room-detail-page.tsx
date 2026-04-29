import { startTransition, useEffect, useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { Link, useParams } from "react-router-dom";
import { ChatInput } from "@/components/chat/chat-input";
import { MessageList } from "@/components/chat/message-list";
import { PageShell } from "@/components/common/page-shell";
import { fetchRoomMessages } from "@/features/message/api";
import { fetchRoomDetail } from "@/features/room/api";
import { useChatSocket } from "@/hooks/use-chat-socket";
import { useRoomStore } from "@/stores/room-store";
import type { MessageItem, RoomDetail } from "@/types";

function appendUniqueMessage(messages: MessageItem[], incoming: MessageItem) {
  if (messages.some((message) => message.messageId === incoming.messageId)) {
    return messages;
  }
  return [...messages, incoming];
}

export function RoomDetailPage() {
  const params = useParams();
  const roomId = Number(params.roomId);
  const setCurrentRoomId = useRoomStore((state) => state.setCurrentRoomId);
  const [messages, setMessages] = useState<MessageItem[]>([]);
  const [onlineCount, setOnlineCount] = useState(0);
  const [socketError, setSocketError] = useState<string | null>(null);

  const roomDetailQuery = useQuery<RoomDetail, Error>({
    queryKey: ["room-detail", roomId],
    queryFn: () => fetchRoomDetail(roomId),
    enabled: Number.isFinite(roomId),
  });

  const historyQuery = useQuery<MessageItem[], Error>({
    queryKey: ["room-history", roomId],
    queryFn: () => fetchRoomMessages(roomId),
    enabled: Number.isFinite(roomId),
  });

  useEffect(() => {
    if (!Number.isFinite(roomId)) {
      return;
    }
    setCurrentRoomId(roomId);
    return () => setCurrentRoomId(null);
  }, [roomId, setCurrentRoomId]);

  useEffect(() => {
    if (roomDetailQuery.data) {
      setOnlineCount(roomDetailQuery.data.onlineCount);
    }
  }, [roomDetailQuery.data]);

  useEffect(() => {
    if (historyQuery.data) {
      setMessages(historyQuery.data);
    }
  }, [historyQuery.data]);

  const { connectionState, sendChatMessage } = useChatSocket({
    enabled: Number.isFinite(roomId),
    roomId,
    onMessage: (message) => {
      startTransition(() => {
        setMessages((current) => appendUniqueMessage(current, message));
      });
    },
    onOnlineCount: (count) => setOnlineCount(count),
    onErrorMessage: (message) => setSocketError(message),
  });

  const room = roomDetailQuery.data;

  if (!Number.isFinite(roomId)) {
    return (
      <PageShell>
        <p className="text-rose-300">无效的房间编号。</p>
      </PageShell>
    );
  }

  return (
    <PageShell>
      <div className="mb-6 flex flex-wrap items-center justify-between gap-4">
        <div>
          <Link to="/rooms" className="text-sm text-sky-200 transition hover:text-sky-100">
            ← 返回房间列表
          </Link>
          <h1 className="mt-3 text-3xl font-semibold text-white">{room?.roomName ?? "正在进入房间..."}</h1>
          <p className="mt-2 text-sm text-slate-300">
            在线人数 {onlineCount} · 连接状态 {connectionState}
          </p>
        </div>
        <div className="rounded-full border border-white/10 bg-white/8 px-4 py-2 text-sm text-slate-200">
          当前流程：HTTP 拉历史 + WebSocket 收增量
        </div>
      </div>

      {roomDetailQuery.error ? <p className="mb-4 text-rose-300">{roomDetailQuery.error.message}</p> : null}
      {historyQuery.error ? <p className="mb-4 text-rose-300">{historyQuery.error.message}</p> : null}
      {socketError ? <p className="mb-4 text-amber-300">{socketError}</p> : null}

      <div className="grid gap-6 xl:grid-cols-[minmax(0,1fr)_320px]">
        <section className="flex min-h-[700px] flex-col rounded-[32px] border border-white/10 bg-white/6 p-4 md:p-6">
          <MessageList messages={messages} />
          <ChatInput
            roomId={roomId}
            disabled={connectionState !== "open"}
            onSend={(content) => {
              setSocketError(null);
              sendChatMessage(content);
            }}
          />
        </section>

        <aside className="space-y-4 rounded-[32px] border border-white/10 bg-white/6 p-5">
          <div className="overflow-hidden rounded-[24px]">
            {room?.coverUrl ? (
              <img src={room.coverUrl} alt={room.roomName} className="h-44 w-full object-cover" />
            ) : (
              <div className="flex h-44 items-center justify-center bg-slate-900 text-slate-400">加载中...</div>
            )}
          </div>
          <div>
            <h2 className="text-xl font-semibold text-white">MVP 说明</h2>
            <ul className="mt-3 space-y-3 text-sm leading-6 text-slate-300">
              <li>登录后先拉房间详情和最近 20 条消息。</li>
              <li>WebSocket 建连成功后再接收实时广播。</li>
              <li>发送消息会经过后端限流和禁言校验。</li>
            </ul>
          </div>
        </aside>
      </div>
    </PageShell>
  );
}

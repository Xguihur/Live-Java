import { useEffect, useEffectEvent, useRef, useState } from "react";
import { useAuthStore } from "@/stores/auth-store";
import type { MessageItem, WsEnvelope, WsErrorPayload, WsOnlineCountPayload } from "@/types";

interface UseChatSocketOptions {
  enabled?: boolean;
  roomId: number;
  onMessage: (message: MessageItem) => void;
  onOnlineCount: (onlineCount: number) => void;
  onErrorMessage: (message: string) => void;
}

type ConnectionState = "idle" | "connecting" | "open" | "closed";

export function useChatSocket({
  enabled = true,
  roomId,
  onMessage,
  onOnlineCount,
  onErrorMessage,
}: UseChatSocketOptions) {
  const token = useAuthStore((state) => state.token);
  const socketRef = useRef<WebSocket | null>(null);
  const [connectionState, setConnectionState] = useState<ConnectionState>("idle");
  const emitError = useEffectEvent((message: string) => onErrorMessage(message));

  const handleMessage = useEffectEvent((event: MessageEvent<string>) => {
    const payload = JSON.parse(event.data) as WsEnvelope<unknown>;
    if (payload.type === "NEW_MESSAGE") {
      onMessage(payload.data as MessageItem);
      return;
    }
    if (payload.type === "ROOM_ONLINE_COUNT") {
      onOnlineCount((payload.data as WsOnlineCountPayload).onlineCount);
      return;
    }
    if (payload.type === "ERROR") {
      emitError((payload.data as WsErrorPayload).message);
    }
  });

  const handleClose = useEffectEvent(() => {
    setConnectionState("closed");
  });

  useEffect(() => {
    if (!token || !enabled) {
      setConnectionState("idle");
      return;
    }

    const baseUrl = import.meta.env.VITE_WS_BASE_URL ?? "ws://localhost:8091/ws/chat";
    const params = new URLSearchParams({
      token,
      roomId: String(roomId),
    });
    let disposed = false;
    let socket: WebSocket | null = null;

    // Defer the actual connection so the development StrictMode probe mount
    // can clean up before a socket is created.
    const connectTimer = window.setTimeout(() => {
      if (disposed) {
        return;
      }

      socket = new WebSocket(`${baseUrl}?${params.toString()}`);
      socketRef.current = socket;
      setConnectionState("connecting");

      const handleOpen = () => {
        if (disposed) {
          return;
        }
        setConnectionState("open");
      };

      const handleSocketMessage = (event: MessageEvent<string>) => {
        if (disposed) {
          return;
        }
        handleMessage(event);
      };

      const handleSocketClose = () => {
        if (disposed) {
          return;
        }
        handleClose();
      };

      const handleSocketError = () => {
        if (disposed) {
          return;
        }
        setConnectionState("closed");
        emitError("WebSocket 连接出现异常，请稍后重试。");
      };

      socket.addEventListener("open", handleOpen);
      socket.addEventListener("message", handleSocketMessage);
      socket.addEventListener("close", handleSocketClose);
      socket.addEventListener("error", handleSocketError);

      socket.addEventListener(
        "close",
        () => {
          socket?.removeEventListener("open", handleOpen);
          socket?.removeEventListener("message", handleSocketMessage);
          socket?.removeEventListener("close", handleSocketClose);
          socket?.removeEventListener("error", handleSocketError);
        },
        { once: true },
      );
    }, 0);

    return () => {
      disposed = true;
      window.clearTimeout(connectTimer);

      if (socket) {
        socket.close();
      }

      socketRef.current = null;
    };
  }, [enabled, token, roomId]);

  function sendChatMessage(content: string) {
    if (!socketRef.current || socketRef.current.readyState !== WebSocket.OPEN) {
      emitError("连接尚未建立，请稍后再试。");
      return;
    }

    socketRef.current.send(
      JSON.stringify({
        type: "CHAT_MESSAGE",
        traceId: crypto.randomUUID(),
        data: {
          roomId,
          content,
        },
      }),
    );
  }

  return {
    connectionState,
    sendChatMessage,
  };
}

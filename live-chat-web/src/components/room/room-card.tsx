import { Link } from "react-router-dom";
import type { RoomSummary } from "@/types";

interface RoomCardProps {
  room: RoomSummary;
}

export function RoomCard({ room }: RoomCardProps) {
  return (
    <Link
      to={`/rooms/${room.id}`}
      className="group overflow-hidden rounded-3xl border border-white/10 bg-white/6 shadow-[0_20px_60px_rgba(15,23,42,0.35)] transition hover:-translate-y-1 hover:border-sky-300/40 hover:bg-white/10"
    >
      <div className="aspect-[16/9] overflow-hidden">
        <img
          src={room.coverUrl}
          alt={room.roomName}
          className="h-full w-full object-cover transition duration-500 group-hover:scale-105"
        />
      </div>
      <div className="space-y-3 px-5 py-5">
        <div className="flex items-center justify-between">
          <span className="rounded-full bg-emerald-400/15 px-3 py-1 text-xs font-medium text-emerald-200">
            {room.roomStatus}
          </span>
          <span className="text-sm text-slate-300">{room.onlineCount} 人在线</span>
        </div>
        <h2 className="text-xl font-semibold tracking-tight text-white">{room.roomName}</h2>
        <p className="text-sm leading-6 text-slate-300">
          进入房间后先通过 HTTP 拉历史消息，再通过 WebSocket 接收实时增量。
        </p>
      </div>
    </Link>
  );
}


import { useQuery } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";
import { PageShell } from "@/components/common/page-shell";
import { RoomCard } from "@/components/room/room-card";
import { fetchRoomList } from "@/services/api/room";
import { useAuthStore } from "@/stores/auth-store";
import type { RoomSummary } from "@/types";

export function RoomListPage() {
  const navigate = useNavigate();
  const currentUser = useAuthStore((state) => state.currentUser);
  const clearSession = useAuthStore((state) => state.clearSession);

  const roomListQuery = useQuery<RoomSummary[], Error>({
    queryKey: ["room-list"],
    queryFn: fetchRoomList,
  });

  return (
    <PageShell>
      <header className="mb-10 flex flex-col gap-4 rounded-[32px] border border-white/10 bg-white/6 px-6 py-6 md:flex-row md:items-center md:justify-between">
        <div>
          <p className="text-sm uppercase tracking-[0.3em] text-sky-200/80">Live Chat MVP</p>
          <h1 className="mt-3 text-3xl font-semibold text-white">选择一个房间开始联调</h1>
          <p className="mt-2 text-sm leading-6 text-slate-300">
            当前登录身份：{currentUser?.nickname} · {currentUser?.accountType}
          </p>
        </div>
        <div className="flex items-center gap-3">
          <button
            type="button"
            onClick={() => navigate("/login")}
            className="rounded-full border border-white/10 px-4 py-2 text-sm text-slate-200 transition hover:border-white/20 hover:bg-white/8"
          >
            切换昵称
          </button>
          <button
            type="button"
            onClick={() => {
              clearSession();
              navigate("/login");
            }}
            className="rounded-full bg-white/8 px-4 py-2 text-sm text-white transition hover:bg-white/12"
          >
            退出
          </button>
        </div>
      </header>

      {roomListQuery.isLoading ? <p className="text-slate-300">正在加载房间列表...</p> : null}
      {roomListQuery.error ? <p className="text-rose-300">{roomListQuery.error.message}</p> : null}

      <section className="grid gap-6 md:grid-cols-2 xl:grid-cols-3">
        {roomListQuery.data?.map((room) => <RoomCard key={room.id} room={room} />)}
      </section>
    </PageShell>
  );
}

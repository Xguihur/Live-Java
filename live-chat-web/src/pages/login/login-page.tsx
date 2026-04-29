import { zodResolver } from "@hookform/resolvers/zod";
import { useMutation } from "@tanstack/react-query";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { useNavigate } from "react-router-dom";
import { login } from "@/services/api/auth";
import { PageShell } from "@/components/common/page-shell";
import { useAuthStore } from "@/stores/auth-store";
import type { LoginResponse } from "@/types";

const loginSchema = z.object({
  nickname: z.string().trim().min(2, "昵称至少 2 个字").max(20, "昵称不能超过 20 个字"),
});

type LoginFormValues = z.infer<typeof loginSchema>;

export function LoginPage() {
  const navigate = useNavigate();
  const setSession = useAuthStore((state) => state.setSession);
  const currentUser = useAuthStore((state) => state.currentUser);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormValues>({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      nickname: currentUser?.nickname ?? "",
    },
  });

  const mutation = useMutation<LoginResponse, Error, LoginFormValues>({
    mutationFn: async (values: LoginFormValues) => login(values.nickname),
    onSuccess: (session) => {
      setSession(session);
      navigate("/rooms");
    },
  });

  return (
    <PageShell>
      <div className="grid min-h-[calc(100vh-3rem)] items-center gap-8 lg:grid-cols-[1.1fr_0.9fr]">
        <section className="space-y-6">
          <span className="inline-flex rounded-full border border-sky-300/30 bg-sky-400/10 px-4 py-2 text-sm text-sky-200">
            React + Spring Boot + WebSocket
          </span>
          <h1 className="max-w-3xl text-4xl font-semibold leading-tight text-white md:text-6xl">
            大榕树下直播间
            <br />
            一起把实时消息系统做成一个真的项目
          </h1>
          <p className="max-w-2xl text-base leading-8 text-slate-300 md:text-lg">
            这一版是最小可用 MVP。先打通游客登录、房间列表、历史消息和 WebSocket 实时聊天，再继续往禁言、限流、跨实例广播迭代。
          </p>
        </section>

        <section className="rounded-[32px] border border-white/10 bg-white/8 p-6 shadow-[0_30px_100px_rgba(8,47,73,0.4)] backdrop-blur md:p-8">
          <div className="mb-6">
            <h2 className="text-2xl font-semibold text-white">游客进入直播间</h2>
            <p className="mt-2 text-sm leading-6 text-slate-300">先用昵称快速登录，后面再扩展正式账号体系。</p>
          </div>

          <form className="space-y-5" onSubmit={handleSubmit((values) => mutation.mutate(values))}>
            <div className="space-y-2">
              <label className="text-sm font-medium text-slate-200" htmlFor="nickname">
                昵称
              </label>
              <input
                id="nickname"
                {...register("nickname")}
                className="w-full rounded-2xl border border-white/10 bg-slate-950/60 px-4 py-3 text-slate-50 outline-none transition focus:border-sky-400/60"
                placeholder="例如：后端小白也想冲"
              />
              {errors.nickname ? <p className="text-sm text-rose-300">{errors.nickname.message}</p> : null}
            </div>

            {mutation.error ? <p className="text-sm text-rose-300">{mutation.error.message}</p> : null}

            <button
              type="submit"
              disabled={mutation.isPending}
              className="w-full rounded-full bg-sky-500 px-5 py-3 text-sm font-medium text-white transition hover:bg-sky-400 disabled:cursor-not-allowed disabled:bg-slate-700"
            >
              {mutation.isPending ? "正在进入..." : "进入房间列表"}
            </button>
          </form>
        </section>
      </div>
    </PageShell>
  );
}

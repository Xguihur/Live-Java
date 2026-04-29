import { PropsWithChildren } from "react";

export function PageShell({ children }: PropsWithChildren) {
  return (
    <div className="min-h-screen bg-[radial-gradient(circle_at_top,_rgba(14,165,233,0.22),_transparent_28%),linear-gradient(180deg,_#08111f_0%,_#020617_100%)] text-slate-100">
      <div className="mx-auto flex min-h-screen w-full max-w-7xl flex-col px-4 py-6 md:px-8">
        {children}
      </div>
    </div>
  );
}


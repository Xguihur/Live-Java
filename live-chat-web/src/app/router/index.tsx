import { createBrowserRouter, Navigate, Outlet, RouterProvider } from "react-router-dom";
import { LoginPage } from "@/pages/login/login-page";
import { RoomListPage } from "@/pages/room-list/room-list-page";
import { RoomDetailPage } from "@/pages/room-detail/room-detail-page";
import { useAuthStore } from "@/stores/auth-store";

function ProtectedLayout() {
  const token = useAuthStore((state) => state.token);

  if (!token) {
    return <Navigate to="/login" replace />;
  }

  return <Outlet />;
}

const router = createBrowserRouter([
  {
    path: "/",
    element: <Navigate to="/rooms" replace />,
  },
  {
    path: "/login",
    element: <LoginPage />,
  },
  {
    element: <ProtectedLayout />,
    children: [
      {
        path: "/rooms",
        element: <RoomListPage />,
      },
      {
        path: "/rooms/:roomId",
        element: <RoomDetailPage />,
      },
    ],
  },
]);

export function AppRouter() {
  return <RouterProvider router={router} />;
}


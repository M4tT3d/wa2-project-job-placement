import type { AuthContext } from "@/providers/auth"
import type { QueryClient } from "@tanstack/react-query"
import { ReactQueryDevtools } from "@tanstack/react-query-devtools"
import { createRootRouteWithContext, Outlet } from "@tanstack/react-router"
import { TanStackRouterDevtools } from "@tanstack/router-devtools"
import type { IMe } from "../types"

type IContext = {
  me?: IMe
  queryClient: QueryClient
  auth: AuthContext
}

export const Route = createRootRouteWithContext<IContext>()({
  component: () => (
    <>
      <Outlet />
      <ReactQueryDevtools buttonPosition="top-right" />
      <TanStackRouterDevtools />
    </>
  ),
})

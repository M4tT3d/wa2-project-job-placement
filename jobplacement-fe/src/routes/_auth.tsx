import Navbar from "@/components/molecules/Navbar"
import { useAuth } from "@/providers/auth"
import { Outlet, createFileRoute, redirect, useRouter } from "@tanstack/react-router"

export const Route = createFileRoute("/_auth")({
  beforeLoad: async ({ location, context }) => {
    if (!context.auth.isAuthenticated) {
      await new Promise((resolve) => setTimeout(resolve, 600))
    }
    if (!context.auth.isAuthenticated) {
      throw redirect({
        to: "/login",
        search: {
          redirect: location.href,
        },
      })
    }
  },
  component: AuthLayout,
})

function AuthLayout() {
  const router = useRouter()
  const navigate = Route.useNavigate()
  const auth = useAuth()

  const handleLogout = () => {
    auth.logout()
    router.invalidate().finally(() => {
      navigate({ to: "/" })
    })
  }

  return (
    <div className="relative flex">
      <Navbar onLogout={handleLogout} />
      <section className="ml-72 flex-1 overflow-y-scroll p-4">
        <Outlet />
      </section>
    </div>
  )
}

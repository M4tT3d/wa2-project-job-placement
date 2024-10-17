import { Button } from "@/components/atoms/Button"
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/atoms/Card"
import { useAuth } from "@/providers/auth"
import { createFileRoute, redirect } from "@tanstack/react-router"
import { z } from "zod"

export const Route = createFileRoute("/login/")({
  validateSearch: z.object({
    redirect: z.string().optional().catch(""),
  }),
  beforeLoad: ({ context, search }) => {
    if (context.auth.isAuthenticated) {
      throw redirect({ to: search.redirect || "/" })
    }
  },
  component: Login,
})

function Login() {
  const { user, loginUrl, logout, isAuthenticated } = useAuth()

  return (
    <div className="m-auto">
      <Card className="w-[350px]">
        <CardHeader>
          <CardTitle>Job Placement</CardTitle>
          <CardDescription>Fantastic job placement service</CardDescription>
        </CardHeader>
        <CardContent>
          {isAuthenticated ? (
            <>
              <p>User logged: {user?.fullName}</p>
              <p>XSRF token: {user?.xsrfToken}</p>
            </>
          ) : (
            <p>No one is logged</p>
          )}
        </CardContent>
        <CardFooter>
          <Button
            className="w-full"
            onClick={() => {
              if (!isAuthenticated) window.location.href = loginUrl as string
              else logout()
            }}
          >
            {!isAuthenticated ? "Login" : "Logout"}
          </Button>
        </CardFooter>
        <div></div>
      </Card>
    </div>
  )
}

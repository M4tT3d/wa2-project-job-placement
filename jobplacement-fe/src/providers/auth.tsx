import { api } from "@/lib/queries"
import type { IMe } from "@/types"
import { createContext, type PropsWithChildren, useContext, useEffect, useState } from "react"

export interface AuthContext {
  isAuthenticated: boolean
  user: Omit<IMe, "loginUrl" | "logoutUrl"> | null
  loginUrl: IMe["loginUrl"] | null
  logout: () => void
}

const AuthContext = createContext<AuthContext | null>(null)

const key = "tanstack.auth.user"

function getStoredUser() {
  return JSON.parse(localStorage.getItem(key) || "null") as IMe | null
}

function setStoredUser(user: string | null) {
  if (user) {
    localStorage.setItem(key, user)
  } else {
    localStorage.removeItem(key)
  }
}

export function AuthProvider({ children }: PropsWithChildren) {
  const [user, setUser] = useState<IMe | null>(getStoredUser())
  const [loginUrl, setLoginUrl] = useState<string | null>(user?.loginUrl ?? null)
  const [logoutUrl, setLogoutUrl] = useState<string | null>(user?.logoutUrl ?? null)
  const [isAuthenticated, setIsAuthenticated] = useState(!!user?.principal)
  const logout = () => {
    setStoredUser(null)
    window.location.href = logoutUrl as string
  }

  useEffect(() => {
    const getIamData = async () => {
      const { data } = await api.get<IMe>("me")
      setLoginUrl(data.loginUrl)
      setLogoutUrl(data.logoutUrl)
      setStoredUser(JSON.stringify(data))
      return data
    }
    getIamData().then((val) => {
      if (val.principal) {
        setIsAuthenticated(true)
        setUser(val)
      } else {
        setIsAuthenticated(false)
        setUser(null)
        setStoredUser(null)
      }
    })
  }, [])

  return (
    <AuthContext.Provider value={{ isAuthenticated, user, loginUrl, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider")
  }
  return context
}

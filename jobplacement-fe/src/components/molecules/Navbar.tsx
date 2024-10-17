import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from "@/components/atoms/Accordion"
import { Button } from "@/components/atoms/Button"
import { NAVBAR_LINKS } from "@/lib/constants"
import { cn } from "@/lib/utils"
import { useAuth } from "@/providers/auth"
import { Link } from "@tanstack/react-router"
import { Fragment } from "react/jsx-runtime"

function Navbar({ onLogout }: { onLogout: () => void }) {
  const { user } = useAuth()
  return (
      <nav className="fixed left-0 top-0 h-screen w-72 bg-primary p-4">
        <div className="flex flex-col space-y-2">
          <p className="text-primary-foreground">{user?.fullName}</p>
          <Button className="w-full font-bold" variant="secondary" onClick={onLogout}>
            Logout
          </Button>
        </div>
        <div className="mt-20 flex h-full flex-col space-y-4">
          <Link to="/">
            {({ isActive }) => (
                <Button
                    className={cn("w-full font-bold text-primary-foreground", {
                      "text-primary": isActive,
                      "font-bold": isActive,
                    })}
                    variant={isActive ? "secondary" : "ghost"}
                >
                  {isActive}
                  Home
                </Button>
            )}
          </Link>
          <Accordion type="multiple">
            {(user?.principal?.attributes.roles.includes("admin") ||
                    user?.principal?.attributes.roles.includes("operator")) &&
                Object.keys(NAVBAR_LINKS).map((key) => (
                    <Fragment key={key}>
                      <AccordionItem value={key}>
                        <AccordionTrigger className="capitalize text-primary-foreground">
                          {key}
                        </AccordionTrigger>
                        {NAVBAR_LINKS[key as keyof typeof NAVBAR_LINKS].map((link) => (
                            <AccordionContent key={link.href} className="ml-4 flex text-primary-foreground">
                              <Link
                                  to={link.href}
                                  className="h-10 w-full content-center rounded-sm capitalize"
                                  activeProps={() => ({ className: "bg-secondary text-primary font-bold" })}
                                  inactiveProps={() => ({
                                    className: "hover:bg-secondary hover:text-primary",
                                  })}
                              >
                                {link.text}
                              </Link>
                            </AccordionContent>
                        ))}
                      </AccordionItem>
                    </Fragment>
                ))}
          </Accordion>
          {(user?.principal?.attributes.roles.includes("admin") ||
                  user?.principal?.attributes.roles.includes("operator")) && (
          <Button asChild>
            <Link to="/message">Send Message</Link>
          </Button>
          )}
          {user?.principal?.attributes.roles.includes("admin") && (
              <Button
                  onClick={() => (window.location.href = "http://localhost:3000")}
                  variant="secondary"
              >
                Grafana
              </Button>
          )}
        </div>
      </nav>
  )
}

export default Navbar
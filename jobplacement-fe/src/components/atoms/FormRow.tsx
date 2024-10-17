import { cn } from "@/lib/utils"
import type { PropsWithChildren } from "react"

export default function FormRow({
  children,
  className,
}: PropsWithChildren<{ className?: string }>) {
  return (
    <div className={cn("flex flex-col space-y-3 md:flex-row md:space-x-3 md:space-y-0", className)}>
      {children}
    </div>
  )
}

import * as React from "react"

import { Label } from "@/components/atoms/Label"
import { cn } from "@/lib/utils"

export type InputProps = React.InputHTMLAttributes<HTMLInputElement>

const InnerInput = React.forwardRef<HTMLInputElement, InputProps>(
  ({ className, type, ...props }, ref) => {
    return (
      <input
        type={type}
        className={cn(
          "flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium file:text-foreground placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50",
          className,
        )}
        ref={ref}
        {...props}
      />
    )
  },
)

const Input = React.forwardRef<
  HTMLInputElement,
  InputProps & {
    label?: string
    htmlFor?: string
    error?: string
  }
>(({ label, htmlFor, error, ...props }) => {
  if (label)
    return (
      <div className="grid w-full max-w-sm items-center gap-1.5 space-y-2">
        <Label htmlFor={htmlFor}>
          {label}
          {props.required && <span className="text-red-500">*</span>}
        </Label>
        <InnerInput {...props} />
        {error && <p className="text-sm font-medium text-destructive">{error}</p>}
      </div>
    )
  return <InnerInput {...props} />
})
Input.displayName = "Input"

export { Input }

import { Label } from "@/components/atoms/Label"
import { Textarea } from "@/components/atoms/Textarea"
import type { FormFieldOptions } from "@/types"
import type { HTMLAttributes } from "react"
import { Controller, type FieldValues } from "react-hook-form"

export type ITextareaFieldProps = HTMLAttributes<HTMLTextAreaElement> & {
  label?: string
  error?: string
  placeholder?: string
  required?: boolean
  rows?: number
}

export default function TextareaFormField<T extends FieldValues = FieldValues>({
  className,
  placeholder,
  label,
  error,
  name,
  control,
  rows = 3,
  required = false,
}: FormFieldOptions<T> & ITextareaFieldProps) {
  return (
    <Controller
      name={name}
      control={control}
      render={({ field }) => {
        return (
          <div className="grid w-full max-w-sm items-center gap-1.5 space-y-2">
            <Label htmlFor={name} className="flex">
              {label}
              {required && <span className="text-red-500">*</span>}
            </Label>
            <Textarea {...field} placeholder={placeholder} rows={rows} />
            {error && <p className="text-sm font-medium text-destructive">{error}</p>}
          </div>
        )
      }}
    />
  )
}

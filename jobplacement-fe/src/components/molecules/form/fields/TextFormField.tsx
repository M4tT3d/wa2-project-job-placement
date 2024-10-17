import { Input } from "@/components/atoms/Input"
import type { FormFieldOptions } from "@/types"
import type { HTMLAttributes } from "react"
import { Controller, type FieldValues } from "react-hook-form"

export type ITextFieldProps = HTMLAttributes<HTMLInputElement> & {
  label?: string
  type?: "text" | "password" | "email" | "number" | "tel"
  placeholder?: string
  required?: boolean
  maxLength?: number
  step?: string
}

export default function TextFormField<T extends FieldValues = FieldValues>({
  className,
  placeholder,
  label,
  type = "text",
  name,
  control,
  maxLength,
  required,
  step = "1",
}: FormFieldOptions<T> & ITextFieldProps) {
  return (
    <Controller
      name={name}
      control={control}
      render={({ field, fieldState: { error } }) => {
        return (
          <Input
            {...field}
            label={label}
            type={type}
            placeholder={placeholder}
            required={required}
            htmlFor={name}
            error={error?.message}
            id={name}
            maxLength={maxLength}
            step={step}
          />
        )
      }}
    />
  )
}

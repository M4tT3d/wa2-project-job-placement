import { Input } from "@/components/atoms/Input"
import { FormFieldOptions } from "@/types"
import { Controller, FieldValues } from "react-hook-form"

type Props = {
  className?: string
  label?: string
  error?: string
  required?: boolean
}

export default function FileFormField<T extends FieldValues = FieldValues>({
  name,
  control,
  className,
  label,
  error,
  required,
}: FormFieldOptions<T> & Props) {
  return (
    <Controller
      name={name}
      control={control}
      render={({ field }) => {
        return (
          <Input
            {...field}
            label={label}
            type="file"
            error={error}
            required={required}
            id={name}
            htmlFor={name}
          />
        )
      }}
    />
  )
}

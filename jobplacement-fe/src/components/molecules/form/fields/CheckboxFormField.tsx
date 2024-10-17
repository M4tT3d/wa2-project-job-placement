import { Checkbox } from "@/components/atoms/Checkbox"
import { Label } from "@/components/atoms/Label"
import type { FormFieldOptions } from "@/types"
import { Controller, type FieldValues } from "react-hook-form"

export type ICheckboxFieldProps = {
  label?: string
  required?: boolean
  id: string
}

export default function CheckboxFormField<T extends FieldValues = FieldValues>({
  label,
  name,
  control,
  id,
  required,
}: FormFieldOptions<T> & ICheckboxFieldProps) {
  return (
    <Controller
      name={name}
      control={control}
      render={({ field }) => {
        return (
          <div className="flex space-x-2">
            <Checkbox
              {...field}
              id={id}
              required={required}
              checked={field.value?.includes(id)}
              onCheckedChange={(checked) => {
                if (checked) {
                  const t = field.value ? [...field.value, id] : [id]
                  return field.onChange(t)
                }
                return field.onChange(field.value?.filter((value: unknown) => value !== id))
              }}
            />
            <Label htmlFor={id} className="flex">
              {label}
              {/* {required && <span className="text-red-500">*</span>} */}
            </Label>
          </div>
        )
      }}
    />
  )
}

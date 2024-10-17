import { Label } from "@/components/atoms/Label"
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/atoms/Select"
import { FormFieldOptions } from "@/types"
import { Controller, FieldValues } from "react-hook-form"

type Props = {
  className?: string
  placeholder?: string
  label?: string
  required?: boolean
  data: Record<"value" | "text", string>[]
  disabled?: boolean
}

export default function SelectFormField<T extends FieldValues = FieldValues>({
  className,
  placeholder,
  data,
  name,
  control,
  required,
  label,
  defaultValue,
  disabled,
}: FormFieldOptions<T> & Props) {
  return (
    <Controller
      name={name}
      defaultValue={defaultValue}
      control={control}
      render={({ field, fieldState: { error } }) => (
        <div className="grid w-full max-w-sm items-center gap-1.5 space-y-2">
          <Label htmlFor={name} className="flex">
            {label}
            {required && <p className="text-red-500">*</p>}
          </Label>
          <Select defaultValue={field.value} onValueChange={field.onChange} disabled={disabled}>
            <SelectTrigger>
              <SelectValue placeholder={placeholder} />
            </SelectTrigger>
            <SelectContent>
              <SelectGroup>
                {data.map(({ text, value }, i) => (
                  <SelectItem value={value} key={`${value}-${i}`}>
                    {text}
                  </SelectItem>
                ))}
              </SelectGroup>
            </SelectContent>
          </Select>
          {error && <p className="text-sm font-medium text-destructive">{error.message}</p>}
        </div>
      )}
    />
  )
}

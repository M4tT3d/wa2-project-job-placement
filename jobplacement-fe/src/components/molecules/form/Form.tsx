import { Button } from "@/atoms/Button"
import { IconCheck, IconEdit, IconX, type Icon, type IconProps } from "@tabler/icons-react"
import type {
  BaseSyntheticEvent,
  ForwardRefExoticComponent,
  PropsWithChildren,
  RefAttributes,
} from "react"

type ActionProps = {
  label: string
  icon?: ForwardRefExoticComponent<Omit<IconProps, "ref"> & RefAttributes<Icon>>
  action?: () => void
}

type FormProps = {
  id: string
  title?: string
  isEditing?: boolean
  actions?: {
    submit: ActionProps
    cancel: ActionProps
  }
  onSubmit: (e: BaseSyntheticEvent) => Promise<void>
}

const addActions: FormProps["actions"] = {
  submit: {
    label: "Add",
    icon: IconCheck,
  },
  cancel: {
    label: "Cancel",
    icon: IconX,
  },
}
const editActions: FormProps["actions"] = {
  submit: {
    label: "Edit",
    icon: IconEdit,
  },
  cancel: {
    label: "Cancel",
    icon: IconX,
  },
}

export default function Form({
  id,
  children,
  title,
  actions: actionsProps,
  isEditing,
  onSubmit,
}: PropsWithChildren<FormProps>) {
  const actions = actionsProps || (isEditing ? editActions : addActions)
  return (
    <form id={id} className="flex flex-col space-y-6 pt-4" onSubmit={onSubmit}>
      {title && <h2 className="text-xl font-bold lg:text-3xl">{title}</h2>}
      <div className="flex flex-col space-y-6">{children}</div>
      <div className="flex justify-end gap-2">
        {actions && (
          <>
            <Button className="w-40 gap-1" type="submit">
              {actions.submit.icon && <actions.submit.icon className="size-5" />}
              <span>{actions.submit.label}</span>
            </Button>
            <Button
              variant="secondary"
              className="w-40 border"
              onClick={actions.cancel.action}
              type="reset"
              disabled={isEditing}
            >
              {actions.cancel.icon && <actions.cancel.icon className="size-5" />}
              <span>{actions.cancel.label}</span>
            </Button>
          </>
        )}
      </div>
    </form>
  )
}

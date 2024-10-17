import { Card, CardContent, CardHeader, CardTitle } from "@/components/atoms/Card"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from "@/components/atoms/Dialog"
import FormRow from "@/components/atoms/FormRow"
import CheckboxFormField from "@/components/molecules/form/fields/CheckboxFormField"
import SelectFormField from "@/components/molecules/form/fields/SelectFormField"
import TextareaFormField from "@/components/molecules/form/fields/TextareaFormField"
import TextFormField from "@/components/molecules/form/fields/TextFormField"
import Form from "@/components/molecules/form/Form"
import { jobOffer, type JobOfferFormData } from "@/types"
import { zodResolver } from "@hookform/resolvers/zod"
import { useRouter } from "@tanstack/react-router"
import { useState } from "react"
import { useForm } from "react-hook-form"

type Props = {
  title?: string
  id: string
  data?: JobOfferFormData
  onSubmit: (data: JobOfferFormData) => Promise<void>
  skills: Record<"text" | "value", string>[]
  customers: Record<"text" | "value", string>[]
}

export default function JobOfferForm({ title, id, data, onSubmit, skills, customers }: Props) {
  const {
    handleSubmit,
    control,
    formState: { errors },
  } = useForm<JobOfferFormData>({
    defaultValues: data ?? {
      description: "",
      comment: "",
      duration: 0,
      customerId: 0,
      skills: [],
    },
    // resolver: async (data, context, options) => {
    //   // you can debug your validation schema here
    //   console.log("formData", data)
    //   console.log("validation result", await zodResolver(jobOffer)(data, context, options))
    //   return zodResolver(jobOffer)(data, context, options)
    // },
    resolver: zodResolver(jobOffer),
  })
  const [open, setOpen] = useState(customers.length === 0)
  const router = useRouter()
  return (
    <Card className="mx-auto">
      {title && (
        <CardHeader>
          <CardTitle>{title}</CardTitle>
        </CardHeader>
      )}
      <CardContent>
        <Dialog
          open={open}
          onOpenChange={() => {
            setOpen(false)
            router.navigate({ to: "/contacts/add", replace: true })
          }}
        >
          <DialogContent>
            <DialogHeader>
              <DialogTitle>Ops! Something went wrong</DialogTitle>
              <DialogDescription>Add firt a customer before adding a joboffer</DialogDescription>
            </DialogHeader>
          </DialogContent>
        </Dialog>
        <Form id={id} isEditing={!!data} onSubmit={handleSubmit(onSubmit)}>
          <div className="flex flex-col">
            <span className="flex flex-col space-y-2">
              <span className="inline-flex">
                Skills <p className="text-red-500">*</p>
              </span>
              {errors.skills && <p className="text-red-500">{errors.skills?.message}</p>}
            </span>
            <div className="grid grid-cols-3 gap-2">
              {skills.map((skill, i) => (
                <CheckboxFormField<JobOfferFormData>
                  key={`skills-${i}`}
                  control={control}
                  name="skills"
                  label={skill.text}
                  id={skill.value}
                />
              ))}
            </div>
          </div>
          <FormRow>
            <TextFormField
              control={control}
              name="duration"
              type="number"
              required
              label="Duration"
              placeholder="Joboffer duration"
            />
            <SelectFormField
              control={control}
              name="customerId"
              data={customers}
              placeholder="Select a customer"
              label="Customer"
              required
            />
          </FormRow>
          <FormRow>
            <TextareaFormField
              control={control}
              name="description"
              placeholder="Joboffer description"
              required
              label="Description"
            />
            <TextareaFormField
              control={control}
              name="comment"
              placeholder="Comment"
              label="Comment"
            />
          </FormRow>
        </Form>
      </CardContent>
    </Card>
  )
}

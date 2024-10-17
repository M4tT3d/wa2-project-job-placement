import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from "@/components/atoms/Accordion"
import { Button } from "@/components/atoms/Button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/atoms/Card"
import FormRow from "@/components/atoms/FormRow"
import CheckboxFormField from "@/components/molecules/form/fields/CheckboxFormField"
import SelectFormField from "@/components/molecules/form/fields/SelectFormField"
import TextareaFormField from "@/components/molecules/form/fields/TextareaFormField"
import TextFormField from "@/components/molecules/form/fields/TextFormField"
import Form from "@/components/molecules/form/Form"
import { CONTACTS_CATEGORY, PROFESSIONAL_EMPLOYMENT_STATE } from "@/lib/constants"
import { contact, type ContactFormData, type ISkill } from "@/types"
import { zodResolver } from "@hookform/resolvers/zod"
import { IconX } from "@tabler/icons-react"
import { useFieldArray, useForm } from "react-hook-form"

type Props = {
  onSubmit: (data: ContactFormData) => Promise<void>
  title?: string
  data?: ContactFormData
  skills: Record<"text" | "value", string>[]
}

export default function ContactForm({ onSubmit, title, data, skills }: Props) {
  const contactData =
    data?.category === "PROFESSIONAL"
      ? {
          ...data,
          professional: {
            ...data.professional,
            skills: data.professional.skills?.map((val) => {
              const skill = val as unknown as ISkill
              return skill.id.toString()
            }),
            dailyRate: data.professional.dailyRate.toString(),
            location: data.professional.location === null ? "" : data.professional.location,
          },
        }
      : data
  const {
    handleSubmit,
    control,
    watch,
    formState: { errors },
  } = useForm<ContactFormData>({
    defaultValues: (contactData as ContactFormData) ?? {
      name: "",
      surname: "",
      comment: "",
      ssnCode: "",
      telephones: [],
      emails: [],
      addresses: [],
    },
    resolver: zodResolver(contact),
  })
  const {
    fields: emails,
    append: appendEmail,
    remove: removeEmail,
  } = useFieldArray({
    control,
    name: "emails",
  })
  const {
    fields: telephones,
    append: appendTel,
    remove: removeTel,
  } = useFieldArray({
    control,
    name: "telephones",
  })
  const {
    fields: addresses,
    append: appendAddr,
    remove: removeAddr,
  } = useFieldArray({
    control,
    name: "addresses",
  })
  const watchCategory = watch("category")

  return (
    <Card className="mx-auto">
      {title && (
        <CardHeader>
          <CardTitle>{title}</CardTitle>
        </CardHeader>
      )}
      <CardContent>
        <Form id="addContact" onSubmit={handleSubmit(onSubmit)} isEditing={!!data}>
          <CardDescription>Contact Data</CardDescription>
          <div className="space-y-3">
            <FormRow>
              <TextFormField<ContactFormData> control={control} name="name" label="Name" required />
              <TextFormField<ContactFormData>
                control={control}
                name="surname"
                label="Surname"
                required
              />
            </FormRow>
            <FormRow>
              <TextFormField<ContactFormData>
                control={control}
                name="ssnCode"
                label="SSN Code"
                className="uppercase"
                maxLength={16}
              />
              <SelectFormField<ContactFormData>
                control={control}
                name="category"
                data={CONTACTS_CATEGORY}
                placeholder="Select category"
                label="Select category"
                required
                defaultValue=""
                disabled={!!data && data.category !== "UNKNOWN"}
              />
            </FormRow>
            <Accordion type="multiple">
              <AccordionItem value="emails">
                <AccordionTrigger className="flex">Emails</AccordionTrigger>
                <AccordionContent>
                  <Button
                    onClick={() =>
                      appendEmail({
                        email: "",
                        comment: "",
                      })
                    }
                  >
                    Add Email
                  </Button>
                  {emails.map((field, i) => (
                    <FormRow key={field.id} className="mt-2 px-2">
                      <TextFormField<ContactFormData>
                        control={control}
                        name={`emails.${i}.email`}
                        label="Email"
                        required
                        type="email"
                      />
                      <TextFormField<ContactFormData>
                        control={control}
                        name={`emails.${i}.comment`}
                        label="Comment"
                      />
                      <Button onClick={() => removeEmail(i)} size="icon">
                        <IconX />
                      </Button>
                    </FormRow>
                  ))}
                </AccordionContent>
              </AccordionItem>
              <AccordionItem value="telephones">
                <AccordionTrigger className="flex">Telephones</AccordionTrigger>
                <AccordionContent>
                  <Button
                    onClick={() =>
                      appendTel({
                        telephone: "",
                        comment: "",
                      })
                    }
                  >
                    Add Telephone
                  </Button>
                  {telephones.map((field, i) => (
                    <FormRow key={field.id} className="mt-2 px-2">
                      <TextFormField<ContactFormData>
                        control={control}
                        name={`telephones.${i}.telephone`}
                        label="Telephone"
                        required
                        type="tel"
                      />
                      <TextFormField<ContactFormData>
                        control={control}
                        name={`telephones.${i}.comment`}
                        label="Comment"
                      />
                      <Button onClick={() => removeTel(i)} size="icon">
                        <IconX />
                      </Button>
                    </FormRow>
                  ))}
                </AccordionContent>
              </AccordionItem>
              <AccordionItem value="addresses">
                <AccordionTrigger className="flex">Addresses</AccordionTrigger>
                <AccordionContent>
                  <Button
                    onClick={() =>
                      appendAddr({
                        address: "",
                        comment: "",
                      })
                    }
                  >
                    Add Address
                  </Button>
                  {addresses.map((field, i) => (
                    <FormRow key={field.id} className="mt-2 px-2">
                      <TextFormField<ContactFormData>
                        control={control}
                        name={`addresses.${i}.address`}
                        label="Address"
                        required
                      />
                      <TextFormField<ContactFormData>
                        control={control}
                        name={`addresses.${i}.comment`}
                        label="Comment"
                      />
                      <Button onClick={() => removeAddr(i)} size="icon">
                        <IconX />
                      </Button>
                    </FormRow>
                  ))}
                </AccordionContent>
              </AccordionItem>
            </Accordion>
            <TextareaFormField<ContactFormData> control={control} name="comment" label="Comment" />
          </div>
          {(watchCategory === "CUSTOMER" || watchCategory === "PROFESSIONAL") && (
            <Accordion type="single" defaultValue={watchCategory}>
              <AccordionItem value={watchCategory}>
                {watchCategory === "PROFESSIONAL" && (
                  <>
                    <AccordionTrigger>Professional Data</AccordionTrigger>
                    <AccordionContent className="space-y-3 px-1">
                      <FormRow>
                        <TextFormField<ContactFormData>
                          control={control}
                          name="professional.dailyRate"
                          label="Daily Rate"
                          required
                          type="number"
                          defaultValue=""
                          step="0.1"
                        />
                        <SelectFormField<ContactFormData>
                          control={control}
                          name="professional.employmentState"
                          label="Employment State"
                          data={PROFESSIONAL_EMPLOYMENT_STATE}
                          placeholder="Select employment state"
                          required
                          defaultValue=""
                        />
                      </FormRow>
                      <div className="flex flex-col">
                        <span className="inline-flex">
                          Skills
                          <p className="ml-2 text-red-500">*</p>
                        </span>
                        {(errors as any).professional?.skills && (
                          <p className="my-2 text-red-500">
                            {(errors as any).professional?.skills.message}
                          </p>
                        )}
                        <div className="grid grid-cols-3 gap-2">
                          {skills.map((skill, i) => (
                            <CheckboxFormField<ContactFormData>
                              key={`skills-${i}`}
                              control={control}
                              name="professional.skills"
                              label={skill.text}
                              id={skill.value}
                            />
                          ))}
                        </div>
                      </div>
                      <FormRow>
                        <TextFormField<ContactFormData>
                          control={control}
                          name="professional.location"
                          label="Location"
                          defaultValue=""
                        />
                        <TextFormField<ContactFormData>
                          control={control}
                          name="professional.comment"
                          label="Comment"
                          placeholder="Comment"
                          defaultValue=""
                        />
                      </FormRow>
                    </AccordionContent>
                  </>
                )}
                {watchCategory === "CUSTOMER" && (
                  <>
                    <AccordionTrigger>Customer Data</AccordionTrigger>
                    <AccordionContent className="space-y-3 px-1">
                      <TextareaFormField<ContactFormData>
                        control={control}
                        name="customer.comment"
                        placeholder="Comment"
                        label="Comment"
                        defaultValue=""
                      />
                    </AccordionContent>
                  </>
                )}
              </AccordionItem>
            </Accordion>
          )}
        </Form>
      </CardContent>
    </Card>
  )
}

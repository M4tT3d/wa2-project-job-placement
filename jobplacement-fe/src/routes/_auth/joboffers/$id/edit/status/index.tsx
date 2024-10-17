import { Card, CardContent, CardHeader, CardTitle } from "@/components/atoms/Card"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from "@/components/atoms/Dialog"
import FormRow from "@/components/atoms/FormRow"
import SelectFormField from "@/components/molecules/form/fields/SelectFormField"
import Form from "@/components/molecules/form/Form"
import { statuses } from "@/lib/constants"
import { api, contactsQuery, jobOfferQuery } from "@/lib/queries"
import { useSuspenseQuery } from "@tanstack/react-query"
import { createFileRoute, useRouter } from "@tanstack/react-router"
import type { AxiosError } from "axios"
import { useState } from "react"
import { useForm } from "react-hook-form"

export const Route = createFileRoute("/_auth/joboffers/$id/edit/status/")({
  loader: ({ context: { queryClient }, params: { id } }) => {
    queryClient.ensureQueryData(jobOfferQuery(id))
    queryClient.ensureQueryData(contactsQuery({ category: "PROFESSIONAL", pageSize: 1000 }))
  },
  component: JobOfferEditStatus,
})

function JobOfferEditStatus() {
  const params = Route.useParams()
  const {
    data: { jobOffer, possibleStatuses },
  } = useSuspenseQuery(jobOfferQuery(params.id))
  const {
    data: { contacts },
  } = useSuspenseQuery(contactsQuery({ category: "PROFESSIONAL", pageSize: 1000 }))
  const router = useRouter()
  const [isError, setIsError] = useState(false)
  const [errorMessage, setErrorMessage] = useState("")
  const { handleSubmit, control } = useForm<{ status: string; professionalId: string }>({
    defaultValues: {
      status: jobOffer.status,
      professionalId: jobOffer.professionalId === null ? "" : jobOffer.professionalId.toString(),
    },
  })
  const pStatuses = [
    ...statuses.filter((s) => possibleStatuses.includes(s.value)),
    statuses.filter((s) => s.value === jobOffer.status)[0],
  ]
  const onSubmit = async (data: { status: string; professionalId: string }) => {
    try {
      await api.post(`/crm/api/joboffers/${params.id}/`, data)
      router.navigate({ to: `/joboffers/${params.id}`, replace: true })
    } catch (error) {
      setIsError(true)
      setErrorMessage(
        ((error as AxiosError).response?.data as any).description || "An error occurred",
      )
    }
  }
  return (
    <Card>
      <CardHeader>
        <CardTitle>Edit JobOffer Status</CardTitle>
      </CardHeader>
      <CardContent>
        <Dialog open={isError} onOpenChange={setIsError}>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>Ops! Something went wrong</DialogTitle>
              <DialogDescription>{errorMessage}</DialogDescription>
            </DialogHeader>
          </DialogContent>
        </Dialog>
        <Form id="editJobOfferStatus" onSubmit={handleSubmit(onSubmit)} isEditing>
          <FormRow>
            <SelectFormField
              control={control}
              name="status"
              data={pStatuses}
              placeholder="Status"
              required
              label="Status"
            />
            <SelectFormField
              control={control}
              name="professionalId"
              required
              label="Professional"
              placeholder="Professional"
              data={contacts.map((c) => ({
                text: `${c.name} ${c.surname}`,
                value: c.category === "PROFESSIONAL" ? c.professional?.id?.toString() || "" : "",
              }))}
            />
          </FormRow>
        </Form>
      </CardContent>
    </Card>
  )
}

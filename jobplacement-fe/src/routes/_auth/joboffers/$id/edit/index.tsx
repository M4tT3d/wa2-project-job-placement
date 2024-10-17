import JobOfferForm from "@/components/organisms/JobOfferForm"
import { api, contactsQuery, jobOfferQuery, skillsQuery } from "@/lib/queries"
import { JobOfferFormData } from "@/types"
import { useSuspenseQuery } from "@tanstack/react-query"
import { createFileRoute, useRouter } from "@tanstack/react-router"
import { Suspense } from "react"

export const Route = createFileRoute("/_auth/joboffers/$id/edit/")({
  loader: ({ context: { queryClient }, params: { id: jobOfferId } }) => {
    queryClient.ensureQueryData(jobOfferQuery(jobOfferId))
    queryClient.ensureQueryData(skillsQuery())
    queryClient.ensureQueryData(contactsQuery({ category: "CUSTOMER", pageSize: 1000 }))
  },
  component: JobOfferEdit,
})

function JobOfferEdit() {
  const {
    data: { jobOffer: data },
  } = useSuspenseQuery(jobOfferQuery(Route.useParams().id))
  const { data: skills } = useSuspenseQuery(skillsQuery())
  const {
    data: { contacts },
  } = useSuspenseQuery(contactsQuery({ category: "CUSTOMER", pageSize: 1000 }))
  const router = useRouter()
  const params = Route.useParams()

  const onSubmit = async (data: JobOfferFormData) => {
    try {
      await api.put(`/crm/api/joboffers/${params.id}`, data)
      router.navigate({ to: `/joboffers/${params.id}`, replace: true })
    } catch (error) {
      console.error(error)
    }
  }

  const jobOffer = {
    ...data,
    skills: skills.filter((s) => data.skills.includes(s.text)).map((s) => s.value),
    comment: data.comment === null ? "" : data.comment,
    duration: data.duration.toString(),
    customerId: data.customerId.toString(),
  }

  return (
    <Suspense fallback={<div>Loading...</div>}>
      <JobOfferForm
        title="Edit Job Offer"
        data={jobOffer as unknown as JobOfferFormData}
        id="editJobOffer"
        skills={skills}
        onSubmit={onSubmit}
        customers={contacts.map((c) => ({
          text: `${c.name} ${c.surname}`,
          value: c.category === "CUSTOMER" ? c.customer.id?.toString() || "" : "",
        }))}
      />
    </Suspense>
  )
}

import JobOfferForm from "@/components/organisms/JobOfferForm"
import { api, contactsQuery, skillsQuery } from "@/lib/queries"
import { JobOfferFormData } from "@/types"
import { useSuspenseQuery } from "@tanstack/react-query"
import { createFileRoute, useRouter } from "@tanstack/react-router"
import { Suspense } from "react"

export const Route = createFileRoute("/_auth/joboffers/add/")({
  loader: ({ context: { queryClient } }) => {
    queryClient.ensureQueryData(skillsQuery())
    queryClient.ensureQueryData(contactsQuery({ category: "CUSTOMER", pageSize: 1000 }))
  },
  component: JobOffersAdd,
})

function JobOffersAdd() {
  const { data: skills } = useSuspenseQuery(skillsQuery())
  const {
    data: { contacts },
  } = useSuspenseQuery(contactsQuery({ category: "CUSTOMER", pageSize: 1000 }))
  const router = useRouter()
  const onSubmit = async (data: JobOfferFormData) => {
    const { id, status, ...jobOffer } = data
    const { data: d } = await api.post<JobOfferFormData>(`crm/api/joboffers`, jobOffer)
    router.navigate({ to: `/joboffers/${d.id}`, replace: true })
  }
  return (
    <Suspense fallback={<div>Loading...</div>}>
      <JobOfferForm
        title="Add Job Offer"
        id="addJobOffer"
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

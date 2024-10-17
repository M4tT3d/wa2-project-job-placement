import ContactForm from "@/components/organisms/ContactForm"
import { api, contactQuery, skillsQuery } from "@/lib/queries"
import type { ContactFormData } from "@/types"
import { useSuspenseQuery } from "@tanstack/react-query"
import { createFileRoute, useRouter } from "@tanstack/react-router"
import { Suspense } from "react"

export const Route = createFileRoute("/_auth/contacts/$id/edit/")({
  loader: ({ context: { queryClient }, params: { id: contactId } }) => {
    const skills = queryClient.ensureQueryData(skillsQuery())
    const contact = queryClient.ensureQueryData(contactQuery(contactId))
    return { skills, contact }
  },
  component: EditContact,
})

function EditContact() {
  const { data } = useSuspenseQuery(contactQuery(Route.useParams().id))
  const { data: skills } = useSuspenseQuery(skillsQuery())
  const router = useRouter()
  const params = Route.useParams()

  const onSubmit = async (data: ContactFormData) => {
    await api.patch<ContactFormData>(`/crm/api/contacts/${data.id}`, data)
    router.navigate({ to: `/contacts/${params.id}`, replace: true })
  }
  const contact = {
    ...data,
    ssnCode: data.ssnCode === null ? "" : data.ssnCode,
  }

  return (
    <Suspense fallback={<div>Loading...</div>}>
      <ContactForm onSubmit={onSubmit} title="Edit Contact" data={contact} skills={skills} />
    </Suspense>
  )
}

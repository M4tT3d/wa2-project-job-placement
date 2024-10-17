import ContactForm from "@/components/organisms/ContactForm"
import { skillsQuery } from "@/lib/queries"
import type { ContactFormData } from "@/types"
import { useSuspenseQuery } from "@tanstack/react-query"
import { createFileRoute, useRouter } from "@tanstack/react-router"
import axios from "axios"
import { Suspense } from "react"

export const Route = createFileRoute("/_auth/contacts/add/")({
  loader: ({ context: { queryClient } }) => {
    return queryClient.ensureQueryData(skillsQuery())
  },
  component: AddContact,
})

function AddContact() {
  const { data: skills } = useSuspenseQuery(skillsQuery())
  const router = useRouter()
  const onSubmit = async (data: ContactFormData) => {
    await axios.post<ContactFormData>(`${import.meta.env.VITE_GATEWAY_URL}/crm/api/contacts`, data)
    router.navigate({ to: `/contacts`, replace: true })
  }

  return (
    <Suspense fallback={<div>Loading...</div>}>
      <ContactForm onSubmit={onSubmit} title="Add Contact" skills={skills} />
    </Suspense>
  )
}

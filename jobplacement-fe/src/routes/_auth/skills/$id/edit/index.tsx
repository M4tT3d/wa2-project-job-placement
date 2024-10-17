import SkillForm from "@/components/organisms/SkillForm"
import { api, skillQuery } from "@/lib/queries"
import type { SkillFormData } from "@/types"
import { useSuspenseQuery } from "@tanstack/react-query"
import { createFileRoute, useRouter } from "@tanstack/react-router"
import { Suspense } from "react"

export const Route = createFileRoute("/_auth/skills/$id/edit/")({
  loader: ({ context: { queryClient }, params: { id } }) => {
    queryClient.ensureQueryData(skillQuery(id))
  },
  component: EditSkill,
})

function EditSkill() {
  const { data: skill } = useSuspenseQuery(skillQuery(Route.useParams().id))
  const router = useRouter()
  const params = Route.useParams()
  const onSubmit = async (data: SkillFormData) => {
    await api.put(`/crm/api/skills/${params.id}`, data)
    router.navigate({ to: "/skills", replace: true })
  }
  return (
    <Suspense fallback={<div>Loading...</div>}>
      <SkillForm title="Edit Skill" onSubmit={onSubmit} data={{ skill }} />
    </Suspense>
  )
}

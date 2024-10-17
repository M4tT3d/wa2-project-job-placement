import SkillForm from "@/components/organisms/SkillForm"
import { api } from "@/lib/queries"
import type { SkillFormData } from "@/types"
import { createFileRoute, useRouter } from "@tanstack/react-router"

export const Route = createFileRoute("/_auth/skills/add/")({
  component: AddSkill,
})

function AddSkill() {
  const router = useRouter()
  const onSubmit = async (data: SkillFormData) => {
    await api.post("crm/api/skills", data)
    router.navigate({ to: "/skills", replace: true })
  }

  return <SkillForm title="Add Skill" onSubmit={onSubmit} />
}

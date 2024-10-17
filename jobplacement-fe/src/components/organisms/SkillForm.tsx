import { Card, CardContent, CardHeader, CardTitle } from "@/components/atoms/Card"
import Form from "@/components/molecules/form/Form"
import TextFormField from "@/components/molecules/form/fields/TextFormField"
import { skill, SkillFormData } from "@/types"
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"

type Props = {
  data?: SkillFormData
  title?: string
  onSubmit: (data: SkillFormData) => Promise<void>
}

export default function SkillForm({ data, title, onSubmit }: Props) {
  const { handleSubmit, control } = useForm<SkillFormData>({
    defaultValues: data ?? {
      skill: "",
    },
    resolver: zodResolver(skill),
  })

  return (
    <Card>
      {title && (
        <CardHeader>
          <CardTitle>{title}</CardTitle>
        </CardHeader>
      )}
      <CardContent>
        <Form id="addSkill" onSubmit={handleSubmit(onSubmit)} isEditing={!!data}>
          <TextFormField
            control={control}
            name="skill"
            label="Skill"
            placeholder="Skill"
            required
          />
        </Form>
      </CardContent>
    </Card>
  )
}

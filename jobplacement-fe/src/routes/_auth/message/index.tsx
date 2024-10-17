import {Card, CardContent, CardHeader, CardTitle} from "@/components/atoms/Card"
import TextareaFormField from "@/components/molecules/form/fields/TextareaFormField"
import TextFormField from "@/components/molecules/form/fields/TextFormField"
import Form from "@/components/molecules/form/Form"
import {api} from "@/lib/queries"
import {message} from "@/types"
import {zodResolver} from "@hookform/resolvers/zod"
import {IconSend, IconX} from "@tabler/icons-react"
import {createFileRoute, useRouter} from "@tanstack/react-router"
import {useForm} from "react-hook-form"
import {z} from "zod"

export const Route = createFileRoute("/_auth/message/")({
    component: Message,
})

type MessageFormData = z.infer<typeof message>

function Message() {
    const {handleSubmit, control} = useForm<MessageFormData>({
        defaultValues: {
            email: "",
            subject: "",
            body: "",
        },
        resolver: zodResolver(message),
    })
    const router = useRouter()
    const onSubmit = async (data: MessageFormData) => {
        await api.post("communicationmanager/api/emails", data)
        router.navigate({to: "/", replace: true})
    }

    return (
        <Card>
            <CardHeader>
                <CardTitle>Send a message</CardTitle>
            </CardHeader>
            <CardContent>
                <Form
                    id="sendMessage"
                    onSubmit={handleSubmit(onSubmit)}
                    actions={{
                        submit: {
                            label: "Send",
                            icon: IconSend,
                        },
                        cancel: {
                            label: "Cancel",
                            icon: IconX,
                        },
                    }}
                >
                    <TextFormField
                        control={control}
                        name="email"
                        label="Email"
                        placeholder="Email"
                        required
                    />
                    <TextFormField
                        control={control}
                        name="subject"
                        label="Subject"
                        placeholder="Subject"
                        required
                    />
                    <TextareaFormField
                        control={control}
                        name="body"
                        label="Body"
                        placeholder="Body"
                        rows={5}
                        className="w-full"
                        required
                    />
                </Form>
            </CardContent>
        </Card>
    )
}

import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from "@/components/atoms/Accordion"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/atoms/Avatar"
import { Button } from "@/components/atoms/Button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/atoms/Card"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/atoms/Dialog"
import {api, contactQuery, downloadDocument} from "@/lib/queries"
import type { ISkill } from "@/types"
import { useSuspenseQuery } from "@tanstack/react-query"
import { createFileRoute, Link, useRouter } from "@tanstack/react-router"
import { Suspense, useState } from "react"
import FileUpload from "@/atoms/formFields/FileUpload.tsx";

export const Route = createFileRoute("/_auth/contacts/$id/")({
  loader: ({ context: { queryClient }, params: { id: contactId } }) => {
    return queryClient.ensureQueryData(contactQuery(contactId))
  },
  component: ContactPage,
})

function ContactPage() {
  const { data: contact, refetch } = useSuspenseQuery(contactQuery(Route.useParams().id))
  const [open, setOpen] = useState(false)
  const [openAddFile, setOpenAddFile] = useState(false)
  const [openDeleteFile, setOpenDeleteFile] = useState(false)
  const [fileId, setFileId] = useState("")

  const contactId = Route.useParams().id

  const router = useRouter()
  const params = Route.useParams()

  const sendFile = async (data: File) => {
    const formData = new FormData()
    formData.append("file", data)
    formData.append("contact_id", contactId)
    await api.post("/upload-file", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    })
    setOpenAddFile(false)
    await refetch()
  }

  return (
      <Suspense fallback={<div>Loading...</div>}>
        <Card className="mx-auto">
          <CardHeader className="flex flex-row items-center space-x-4">
            <Avatar className="size-28">
              <AvatarImage src={`https://robohash.org/${contact.id}.png?set=set4`} />
              <AvatarFallback className="uppercase">{`${contact.name[0]}${contact.surname[0]}`}</AvatarFallback>
            </Avatar>
            <span className="flex grow flex-col">
            <CardTitle className="capitalize">{`${contact.name} ${contact.surname}`}</CardTitle>
            <h4 className="capitalize">{contact.category.toLowerCase()}</h4>
              {contact.comment && <CardDescription>{contact.comment}</CardDescription>}
          </span>
            <Button asChild>
              <Link to={`/contacts/${Route.useParams().id}/edit`}>Edit</Link>
            </Button>
            <Button variant="destructive" onClick={() => setOpen(true)}>
              Delete
            </Button>
          </CardHeader>
          <CardContent className="space-y-3">
            <Dialog open={open} onOpenChange={setOpen}>
              <DialogContent>
                <DialogHeader>
                  <DialogTitle>Are you sure?</DialogTitle>
                  <DialogDescription>
                    This action is irreversible. Are you sure you want to delete this contact?
                  </DialogDescription>
                </DialogHeader>
                <DialogFooter className="sm:justify-start">
                  <Button
                      variant="destructive"
                      onClick={async () => {
                        await api.delete(`/crm/api/contacts/${params.id}`)
                        router.navigate({ to: "/contacts", replace: true })
                      }}
                  >
                    Delete
                  </Button>
                </DialogFooter>
              </DialogContent>
            </Dialog>
            <CardDescription>Informations</CardDescription>
            <div className="grid grid-cols-2 gap-3">
            <span className="inline-flex text-xl font-bold">
              SSN Code: <p className="ml-2 font-normal">{contact.ssnCode ?? "Not provided"}</p>
            </span>
            </div>
            {contact.emails && contact.emails.length > 0 && (
                <>
                  <CardDescription>Emails</CardDescription>
                  <Accordion type="single" collapsible defaultValue="emails">
                    <AccordionItem value="emails">
                      <AccordionTrigger>Emails</AccordionTrigger>
                      <AccordionContent>
                        <ul className="list-inside list-disc">
                          {contact.emails.map((email, index) => (
                              <li key={index}>
                                <span className="font-bold">{email.email}</span>
                                {email.comment && <span className="ml-2">{email.comment}</span>}
                              </li>
                          ))}
                        </ul>
                      </AccordionContent>
                    </AccordionItem>
                  </Accordion>
                </>
            )}
            {contact.telephones && contact.telephones.length > 0 && (
                <>
                  <CardDescription>Telephones</CardDescription>
                  <Accordion type="single" collapsible defaultValue="telephones">
                    <AccordionItem value="telephones">
                      <AccordionTrigger>Telephones</AccordionTrigger>
                      <AccordionContent>
                        <ul className="list-inside list-disc">
                          {contact.telephones.map((telephone, index) => (
                              <li key={index}>
                                <span className="font-bold">{telephone.telephone}</span>
                                {telephone.comment && <span className="ml-2">{telephone.comment}</span>}
                              </li>
                          ))}
                        </ul>
                      </AccordionContent>
                    </AccordionItem>
                  </Accordion>
                </>
            )}
            {contact.addresses && contact.addresses.length > 0 && (
                <>
                  <CardDescription>Addresses</CardDescription>
                  <Accordion type="single" collapsible defaultValue="addresses">
                    <AccordionItem value="addresses">
                      <AccordionTrigger>Addresses</AccordionTrigger>
                      <AccordionContent>
                        <ul className="list-inside list-disc">
                          {contact.addresses.map((address, index) => (
                              <li key={index}>
                                <span className="font-bold">{address.address}</span>
                                {address.comment && <span className="ml-2">{address.comment}</span>}
                              </li>
                          ))}
                        </ul>
                      </AccordionContent>
                    </AccordionItem>
                  </Accordion>
                </>
            )}
            {contact.documents && contact.documents.length > 0 && (
                <>
                  <CardDescription>Documents</CardDescription>
                  <Accordion type="single" collapsible defaultValue="documents">
                    <AccordionItem value="documents">
                      <AccordionTrigger>Documents</AccordionTrigger>
                      <AccordionContent>
                        <table className="table-left-aligned border-collapse">
                          <tbody>
                          {contact.documents.map((document, index) => (
                              <tr key={index}>
                                <td className="border px-4 py-2">
                              <span
                                  className="document-name cursor-pointer"
                                  onClick={() => downloadDocument(document.metadataId, document.name)}
                              >
                                {document.name}
                              </span>
                                </td>
                                <td className="border px-4 py-2">{new Date(document.date).toLocaleDateString()}</td>
                                <td className="border px-4 py-2">
                                  {
                                    <Button
                                        className="mt-1"
                                        onClick={() => {
                                          setFileId(document.metadataId.toString())
                                          setOpenDeleteFile(true)
                                        }}
                                    >
                                      Delete file
                                    </Button>
                                  }
                                </td>
                              </tr>
                          ))}
                          </tbody>
                        </table>
                        <Button
                            className="mt-1"
                            onClick={() => {
                              setOpenAddFile(true)
                            }}
                        >
                          Add file
                        </Button>
                      </AccordionContent>
                    </AccordionItem>
                  </Accordion>
                </>
            )}
            {!contact.documents || contact.documents.length == 0 && (
                <>
                  <Button
                      className="mt-1"
                      onClick={() => {
                        setOpenAddFile(true)
                      }}
                  >
                    Add file
                  </Button>
                </>
            )}
            {contact.category === "CUSTOMER" && contact.customer && (
                <>
                  <CardDescription>Customer</CardDescription>
                  <span className="font-bold">{contact.customer.comment}</span>
                </>
            )}
            {contact.category === "PROFESSIONAL" && contact.professional && (
                <>
                  <div className="mt-3 grid grid-cols-2 gap-2">
                <span className="flex font-bold capitalize">
                  Employment State:
                  <p className="ml-2 font-normal capitalize">
                    {contact.professional.employmentState.toLowerCase()}
                  </p>
                </span>
                    <span className="flex font-bold capitalize">
                  Daily rate:
                  <p className="ml-2 font-normal">{contact.professional.dailyRate.toFixed(2)} €</p>
                </span>
                    <span className="flex font-bold capitalize">
                  Location:
                  <p className="ml-2 font-normal capitalize">
                    {contact.professional.location?.toLowerCase() ?? "Not provided"}
                  </p>
                </span>
                    <span className="flex font-bold capitalize">
                  Comment:
                  <p className="ml-2 font-normal capitalize">
                    {contact.professional.comment?.toLowerCase() ?? "Not provided"}
                  </p>
                </span>
                  </div>
                  {contact.professional.skills && (
                      <div className="">
                        <p className="font-bold">Skills</p>
                        <ul className="grid list-inside list-disc grid-cols-4">
                          {(contact.professional.skills as unknown as ISkill[]).map(({ id, skill }) => (
                              <li key={id} className="min-w-fit">
                                {skill}
                              </li>
                          ))}
                        </ul>
                      </div>
                  )}
                </>
            )}
          </CardContent>
        </Card>
        <Dialog open={openAddFile} onOpenChange={setOpenAddFile}>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>Add File</DialogTitle>
              <DialogDescription>Upload a file for this contact</DialogDescription>
            </DialogHeader>
            <FileUpload onFileUpload={sendFile} />
          </DialogContent>
        </Dialog>
        <Dialog open={openDeleteFile} onOpenChange={setOpenDeleteFile}>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>Are you sure?</DialogTitle>
              <DialogDescription>
                This action is irreversible. Are you sure you want to delete this file?
              </DialogDescription>
            </DialogHeader>
            <DialogFooter className="sm:justify-start">
              <Button
                  variant="destructive"
                  onClick={async () => {
                    await api.delete(`/documentstore/api/documents/${fileId}`)
                    setFileId("")
                    setOpenDeleteFile(false)
                    await refetch()
                  }}
              >
                Delete
              </Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>
      </Suspense>
  )
}
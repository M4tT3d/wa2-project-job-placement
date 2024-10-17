import { Button } from "@/components/atoms/Button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/atoms/Card"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from "@/components/atoms/Dialog"
import FileUpload from "@/components/atoms/formFields/FileUpload"
import { Input } from "@/components/atoms/Input"
import { Label } from "@/components/atoms/Label"
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/atoms/Select"
import { DataTable } from "@/components/molecules/DataTable"
import { CONTACTS_CATEGORY } from "@/lib/constants"
import { api, contactsQuery } from "@/lib/queries"
import type { ContactFormData, ContactParams, IContactCategory } from "@/types"
import { useSuspenseQuery } from "@tanstack/react-query"
import { createFileRoute, Link } from "@tanstack/react-router"
import { createColumnHelper } from "@tanstack/react-table"
import { Suspense, useCallback, useMemo, useState } from "react"

function debounce<T extends (...args: any[]) => void>(
  func: T,
  wait: number,
): (...args: Parameters<T>) => void {
  let timeout: ReturnType<typeof setTimeout>
  return (...args: Parameters<T>) => {
    clearTimeout(timeout)
    timeout = setTimeout(() => func(...args), wait)
  }
}

export const Route = createFileRoute("/_auth/contacts/")({
  loader: ({ context: { queryClient } }) => {
    return queryClient.ensureQueryData(contactsQuery())
  },
  component: Contacts,
})

function Contacts() {
  const [pagination, setPagination] = useState({ pageIndex: 0, pageSize: 10 })
  const [params, setParams] = useState<ContactParams>({
    name: "",
    surname: "",
    ssnCode: "",
    category: "" as any,
  })
  const {
    data: { contacts, rowCount },
  } = useSuspenseQuery(contactsQuery({ ...pagination, ...params }))
  const [open, setOpen] = useState(false)
  const [contactId, setContactId] = useState("")
  const columns = useMemo(() => {
    const columnHelper = createColumnHelper<ContactFormData>()
    return [
      columnHelper.accessor("name", {
        cell: (info) => <span>{info.getValue()}</span>,
      }),
      columnHelper.accessor("surname", {
        cell: (info) => <span>{info.getValue()}</span>,
      }),
      columnHelper.accessor("ssnCode", {
        cell: (info) => {
          if (info.getValue()) return <span className="uppercase">{info.getValue()}</span>
          return <span>Not specified</span>
        },
      }),
      columnHelper.accessor("comment", {
        cell: (info) => <span className="line-clamp-2">{info.getValue() ?? "Not specified"}</span>,
      }),
      columnHelper.accessor("category", {
        cell: (info) => <span className="capitalize">{info.getValue().toLowerCase()}</span>,
      }),
      columnHelper.display({
        id: "goToContactInfo",
        cell: ({ row }) => (
          <Button asChild>
            <Link to={`/contacts/${row.original.id}`}>More info</Link>
          </Button>
        ),
      }),
      columnHelper.display({
        id: "addContactFile",
        cell: ({ row }) => (
          <Button
            onClick={() => {
              setOpen(true)
              setContactId(row.original.id?.toString() || "")
            }}
            disabled={!row.original.id}
          >
            Add file
          </Button>
        ),
      }),
    ]
  }, [])
  const sendFile = async (data: File) => {
    const formData = new FormData()
    formData.append("file", data)
    formData.append("contact_id", contactId)
    await api.post("/upload-file", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    })
    setOpen(false)
  }

  const debouncedSetParams = useCallback(debounce(setParams, 300), [])

  return (
    <div className="container mx-auto space-y-4">
      <Card>
        <CardHeader>
          <CardTitle>Filters</CardTitle>
        </CardHeader>
        <CardContent>
          <form className="grid grid-cols-2 gap-2">
            <Input
              htmlFor="name"
              label="Name"
              type="text"
              onChange={({ target }) =>
                debouncedSetParams((prev) => ({
                  ...prev,
                  name: target.value,
                  ssnCode: prev?.ssnCode ?? null,
                }))
              }
            />
            <Input
              htmlFor="surname"
              label="Surname"
              type="text"
              onChange={(e) => {
                console.log(e.target)
                debouncedSetParams((prev) => ({
                  ...prev,
                  surname: e.target.value,
                  ssnCode: prev?.ssnCode ?? null,
                }))
              }}
            />
            <Input
              htmlFor="ssncode"
              label="SSN Code"
              type="text"
              onChange={({ target }) =>
                debouncedSetParams((prev) => ({
                  ...prev,
                  ssnCode: target.value,
                }))
              }
            />
            <div className="grid w-full max-w-sm items-center gap-1.5 space-y-2">
              <Label htmlFor="category">Category</Label>
              <Select
                defaultValue={params.category as string}
                onValueChange={(e) =>
                  setParams((prev) => ({
                    ...prev,
                    category: e === "ALL" ? ("" as any) : (e as IContactCategory),
                    ssnCode: prev?.ssnCode ?? ("" as any),
                  }))
                }
              >
                <SelectTrigger>
                  <SelectValue placeholder="Category" defaultValue={""} />
                </SelectTrigger>
                <SelectContent>
                  <SelectGroup>
                    {[...CONTACTS_CATEGORY, { text: "All", value: "ALL" }].map(
                      ({ text, value }, i) => (
                        <SelectItem value={value} key={`${value}-${i}`}>
                          {text}
                        </SelectItem>
                      ),
                    )}
                  </SelectGroup>
                </SelectContent>
              </Select>
            </div>
            <Button
              type="reset"
              onClick={() =>
                setParams({
                  name: "",
                  surname: "",
                  ssnCode: "",
                  category: "" as any,
                })
              }
              className="max-w-fit"
            >
              Reset filters
            </Button>
          </form>
        </CardContent>
      </Card>
      <Dialog open={open} onOpenChange={setOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Add File</DialogTitle>
            <DialogDescription>Upload a file for this contact</DialogDescription>
          </DialogHeader>
          <FileUpload onFileUpload={sendFile} />
        </DialogContent>
      </Dialog>
      <Suspense fallback={<div>Loading...</div>}>
        <DataTable
          columns={columns}
          data={contacts}
          pagination={pagination}
          setPagination={setPagination}
          rowCount={rowCount}
        />
      </Suspense>
    </div>
  )
}

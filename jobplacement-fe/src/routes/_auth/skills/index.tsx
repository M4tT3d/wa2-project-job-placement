import { Button } from "@/components/atoms/Button"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/atoms/Dialog"
import { DataTable } from "@/components/molecules/DataTable"
import { api, skillsQuery } from "@/lib/queries"
import { useSuspenseQuery } from "@tanstack/react-query"
import { createFileRoute, Link, useRouteContext } from "@tanstack/react-router"
import { createColumnHelper } from "@tanstack/react-table"
import { useMemo, useState } from "react"

export const Route = createFileRoute("/_auth/skills/")({
  loader: ({ context: { queryClient } }) => {
    queryClient.ensureQueryData(skillsQuery())
  },
  component: Skills,
})

function Skills() {
  const { queryClient } = useRouteContext({ from: "/_auth/skills/" })
  const [pagination, setPagination] = useState({ pageIndex: 0, pageSize: 10 })
  const { data } = useSuspenseQuery(skillsQuery())
  const [open, setOpen] = useState(false)
  const [skillId, setSkillId] = useState("")
  const columns = useMemo(() => {
    const columnHelper = createColumnHelper<{ text: string; value: string }>()
    return [
      columnHelper.accessor("text", {
        cell: (info) => <span>{info.getValue()}</span>,
        header: "Skill",
      }),
      columnHelper.display({
        id: "editSkill",
        cell: ({ row }) => (
          <Button asChild>
            <Link to={`/skills/${row.original.value}/edit`}>Edit</Link>
          </Button>
        ),
      }),
      columnHelper.display({
        id: "deleteSkill",
        cell: ({ row }) => (
          <Button
            variant="destructive"
            onClick={() => {
              setOpen(true)
              setSkillId(row.original.value)
            }}
          >
            Delete
          </Button>
        ),
      }),
    ]
  }, [])
  return (
    <div className="container mx-auto">
      <Dialog open={open} onOpenChange={setOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Are you sure?</DialogTitle>
            <DialogDescription>
              This action is irreversible. Are you sure you want to delete skill?
            </DialogDescription>
          </DialogHeader>
          <DialogFooter className="sm:justify-start">
            <Button
              variant="destructive"
              onClick={async () => {
                await api.delete(`/crm/api/skills/${skillId}`)
                queryClient.invalidateQueries({ queryKey: ["skills"] })
                setOpen(false)
              }}
            >
              Delete
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
      <DataTable
        data={data}
        columns={columns}
        pagination={pagination}
        setPagination={setPagination}
        disablePagination
      />
    </div>
  )
}

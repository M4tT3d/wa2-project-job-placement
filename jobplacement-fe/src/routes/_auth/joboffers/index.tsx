import { Button } from "@/components/atoms/Button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/atoms/Card"
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
import { statuses } from "@/lib/constants"
import { jobOffersQuery } from "@/lib/queries"
import type { IJobOfferStatus, JobOfferFormData } from "@/types"
import { useSuspenseQuery } from "@tanstack/react-query"
import { createFileRoute, Link } from "@tanstack/react-router"
import { createColumnHelper } from "@tanstack/react-table"
import { useMemo, useState } from "react"

export const Route = createFileRoute("/_auth/joboffers/")({
  loader: ({ context: { queryClient } }) => {
    return queryClient.ensureQueryData(jobOffersQuery({ pageIndex: 0, pageSize: 10 }))
  },
  component: JobOffers,
})

function JobOffers() {
  const [pagination, setPagination] = useState({ pageIndex: 0, pageSize: 10 })
  const [params, setParams] = useState<{ jobOffer: JobOfferFormData["status"] }>()
  const { data } = useSuspenseQuery(jobOffersQuery({ ...pagination, ...params }))
  const columns = useMemo(() => {
    const columnHelper = createColumnHelper<JobOfferFormData>()
    return [
      columnHelper.accessor("description", {
        cell: (info) => <span>{info.getValue()}</span>,
      }),
      columnHelper.accessor("duration", {
        cell: (info) => <span>{info.getValue()}</span>,
      }),
      columnHelper.accessor("skills", {
        cell: (info) => <span>{info.getValue()}</span>,
      }),
      columnHelper.display({
        id: "goToJobOfferInfo",
        cell: ({ row }) => (
          <Button asChild>
            <Link to={`/joboffers/${row.original.id}`}>More info</Link>
          </Button>
        ),
      }),
    ]
  }, [])
  return (
    <div className="container mx-auto space-y-3">
      <Card>
        <CardHeader>
          <CardTitle>Filters</CardTitle>
        </CardHeader>
        <CardContent>
          <form className="grid grid-cols-2 gap-2">
            <div className="grid w-full max-w-sm items-center gap-1.5 space-y-2">
              <Label htmlFor="status">Status</Label>
              <Select
                defaultValue=""
                onValueChange={(e) =>
                  setParams({
                    jobOffer: e === "ALL" ? undefined : (e as IJobOfferStatus),
                  })
                }
              >
                <SelectTrigger>
                  <SelectValue placeholder="Category" defaultValue={""} />
                </SelectTrigger>
                <SelectContent>
                  <SelectGroup>
                    {[...statuses, { text: "All", value: "ALL" }].map(({ text, value }, i) => (
                      <SelectItem value={value} key={`${value}-${i}`}>
                        {text}
                      </SelectItem>
                    ))}
                  </SelectGroup>
                </SelectContent>
              </Select>
            </div>
            <Button type="reset" onClick={() => setParams(undefined)} className="max-w-fit">
              Reset filters
            </Button>
          </form>
        </CardContent>
      </Card>
      <DataTable
        columns={columns}
        data={data.jobOffers}
        pagination={pagination}
        setPagination={setPagination}
        rowCount={data.rowCount}
      />
    </div>
  )
}

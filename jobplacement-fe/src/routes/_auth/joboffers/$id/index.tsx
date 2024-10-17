import { Button } from "@/components/atoms/Button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/atoms/Card"
import { jobOfferQuery } from "@/lib/queries"
import { useSuspenseQuery } from "@tanstack/react-query"
import { createFileRoute, Link } from "@tanstack/react-router"
import { Suspense } from "react"

export const Route = createFileRoute("/_auth/joboffers/$id/")({
  loader: ({ context: { queryClient }, params: { id: jobOfferId } }) => {
    return queryClient.ensureQueryData(jobOfferQuery(jobOfferId))
  },
  component: JobOffer,
})

function JobOffer() {
  const {
    data: { jobOffer },
  } = useSuspenseQuery(jobOfferQuery(Route.useParams().id))
  return (
    <Suspense fallback={<div>Loading...</div>}>
      <Card className="mx-auto">
        <CardHeader className="flex flex-row items-center space-x-4">
          <span className="flex grow flex-col">
            <CardTitle className="capitalize">{`Job Offer ${jobOffer.id}`}</CardTitle>
            <CardDescription className="capitalize">{`${jobOffer.description}`}</CardDescription>
          </span>
          <Button asChild>
            <Link to={`/joboffers/${Route.useParams().id}/edit`}>Edit</Link>
          </Button>
          <Button asChild>
            <Link to={`/joboffers/${Route.useParams().id}/edit/status`}>Edit status</Link>
          </Button>
        </CardHeader>
        <CardContent className="space-y-3">
          <CardDescription>Informations</CardDescription>
          <span className="inline-flex">
            Status:
            <p className="ml-2 font-bold capitalize">{jobOffer.status?.toLowerCase()}</p>
          </span>
          <div className="">
            <p className="font-bold">Skills</p>
            <ul className="grid list-inside list-disc grid-cols-4">
              {jobOffer.skills.map((skill, i) => (
                <li key={`${skill}-${i}`} className="min-w-fit">
                  {skill}
                </li>
              ))}
            </ul>
          </div>
        </CardContent>
      </Card>
    </Suspense>
  )
}

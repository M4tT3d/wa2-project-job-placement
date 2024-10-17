import type { IContactCategory, IJobOfferStatus, IProfessionalEmploymentState } from "@/types"

export const CONTACTS_CATEGORY: {
  text: string
  value: IContactCategory
}[] = [
  {
    text: "Customer",
    value: "CUSTOMER",
  },
  {
    text: "Professional",
    value: "PROFESSIONAL",
  },
  {
    text: "Unknown",
    value: "UNKNOWN",
  },
] as const

export const PROFESSIONAL_EMPLOYMENT_STATE: {
  text: string
  value: IProfessionalEmploymentState
}[] = [
  { value: "EMPLOYED", text: "Employed" },
  { value: "UNEMPLOYED_AVAILABLE", text: "Unemployed and available" },
  { value: "NOT_AVAILABLE", text: "Not available" },
] as const

export const NAVBAR_LINKS = {
  contacts: [
    {
      text: "Contacts",
      href: "/contacts",
    },
    {
      text: "Add Contact",
      href: "/contacts/add",
    },
  ],
  jobOffers: [
    {
      text: "Job Offers",
      href: "/joboffers",
    },
    {
      text: "Add Job Offer",
      href: "/joboffers/add",
    },
  ],
  skills: [
    {
      text: "Skills",
      href: "/skills",
    },
    {
      text: "Add Skill",
      href: "/skills/add",
    },
  ],
} as const
export const statuses: {
  text: string
  value: IJobOfferStatus
}[] = [
  {
    text: "Created",
    value: "CREATED",
  },
  {
    text: "Aborted",
    value: "ABORTED",
  },
  {
    text: "Selection phase",
    value: "SELECTION_PHASE",
  },
  {
    text: "Candidate proposal",
    value: "CANDIDATE_PROPOSAL",
  },
  {
    text: "Consolidated",
    value: "CONSOLIDATED",
  },
  {
    text: "Done",
    value: "DONE",
  },
]

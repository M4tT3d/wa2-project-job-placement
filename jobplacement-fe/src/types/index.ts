import type { Control, FieldPath, FieldValues, Path, PathValue } from "react-hook-form"
import { z } from "zod"

export type IMe = {
  fullName: string | null
  username: string | null
  loginUrl: string
  logoutUrl: string
  principal: {
    [x: string]: unknown
    attributes: {
      [x: string]: unknown
      roles: string[]
    }
  } | null
  xsrfToken: string
}
export interface FormFieldOptions<T extends FieldValues> {
  control: Control<T>
  name: FieldPath<T>
  defaultValue?: PathValue<T, Path<T>>
}

export const ContactCategory = z.enum(["CUSTOMER", "PROFESSIONAL", "UNKNOWN"], {
  message: "Invalid category",
})
export type IContactCategory = z.infer<typeof ContactCategory>
export const ProfessionalEmploymentState = z.enum(
  ["EMPLOYED", "UNEMPLOYED_AVAILABLE", "NOT_AVAILABLE"],
  {
    message: "Invalid employment state",
  },
)
export type IProfessionalEmploymentState = z.infer<typeof ProfessionalEmploymentState>

const baseContact = z.object({
  id: z.number().optional(),
  name: z.string().min(1, "Name is required"),
  surname: z.string().min(1, "Surname is required"),
  ssnCode: z
    .string()
    .min(16, "SSN code must be 16 characters")
    .max(16, "SSN code must be 16 characters")
    .regex(new RegExp("[a-zA-Z0-9]+"), "Invalid SSN code")
    .nullable()
    .or(z.literal(""))
    .transform((val) => (val === "" ? null : val)),
  comment: z
    .string()
    .transform((val) => (val === "" ? null : val))
    .nullable(),
  emails: z
    .array(
      z.object({
        email: z.string().email(),
        comment: z
          .string()
          .transform((val) => (val === "" ? null : val))
          .nullable(),
      }),
    )
    .transform((val) => (val.length === 0 ? null : val))
    .nullable(),
  telephones: z
    .array(
      z.object({
        telephone: z.string(),
        comment: z
          .string()
          .transform((val) => (val === "" ? null : val))
          .nullable(),
      }),
    )
    .transform((val) => (val.length === 0 ? null : val))
    .nullable(),
  addresses: z
    .array(
      z.object({
        address: z.string(),
        comment: z
          .string()
          .transform((val) => (val === "" ? null : val))
          .nullable(),
      }),
    )
    .transform((val) => (val.length === 0 ? null : val))
    .nullable(),
})

export const contact = z.discriminatedUnion("category", [
  baseContact.extend({
    category: z.literal("CUSTOMER"),
    customer: z.object({
      id: z.number().optional(),
      comment: z
        .string()
        .transform((val) => (val === "" ? null : val))
        .nullish(),
    }),
  }),
  baseContact.extend({
    category: z.literal("PROFESSIONAL"),
    professional: z.object({
      id: z.number().optional(),
      skills: z
        .array(
          z
            .string()
            .transform((val) => parseInt(val))
            .pipe(z.number()),
        )
        .min(1, "At least one skill is required"),
      employmentState: ProfessionalEmploymentState,
      comment: z
        .string()
        .transform((val) => (val === "" ? null : val))
        .nullish(),
      location: z
        .string()
        .transform((val) => (val === "" ? null : val))
        .nullish(),
      dailyRate: z
        .string()
        .transform((val) => parseFloat(val))
        .pipe(z.number({ message: "Daily rate must be a number" }).nonnegative()),
    }),
  }),
  baseContact.extend({
    category: z.literal("UNKNOWN"),
  }),
])

export type DocumentData = {
  metadataId: number
  name: string
  size: number
  type: string
  date: string
  documentId: number
}

export type ContactFormData = z.infer<typeof contact> & {
  documents?: DocumentData[]
}

export type ISkill = {
  id: number
  skill: string
}

export type IPagination = {
  pageSize: number
  pageIndex: number
}

export type IPaginationAndParams<T> = Partial<T> & Partial<IPagination>

const jobOfferStatuses = z.enum([
  "CREATED",
  "ABORTED",
  "DONE",
  "SELECTION_PHASE",
  "CANDIDATE_PROPOSAL",
  "CONSOLIDATED",
])
export type IJobOfferStatus = z.infer<typeof jobOfferStatuses>
export const jobOffer = z.object({
  id: z.number().optional(),
  description: z.string().min(1, "Description is required"),
  comment: z
    .string()
    .transform((val) => (val === "" ? null : val))
    .nullish(),
  duration: z
    .string()
    .transform((val) => parseFloat(val))
    .pipe(z.number().min(1.0, { message: "Duration must be at least 1" })),
  customerId: z
    .string()
    .transform((val) => parseInt(val))
    .pipe(z.number().nonnegative({ message: "Customer ID must be a positive number" })),
  skills: z
    .array(
      z
        .string()
        .transform((val) => parseInt(val))
        .pipe(z.number()),
    )
    .min(1, "At least one skill is required"),
  status: jobOfferStatuses.optional(),
})
export type JobOfferFormData = z.infer<typeof jobOffer>

export const message = z.object({
  email: z.string().email(),
  subject: z.string().min(1, "Subject is required"),
  body: z.string().min(1, "Body is required"),
})

export const skill = z.object({
  skill: z
    .string()
    .min(1, "Skill is required")
    .refine((val) => val.trim().length > 0, {
      message: "Skill is required",
    }),
})
export type SkillFormData = z.infer<typeof skill>

export const contactParams = baseContact
  .pick({
    ssnCode: true,
  })
  .extend({
    category: ContactCategory.optional(),
    name: z.string().optional(),
    surname: z.string().optional(),
  })
export type ContactParams = z.infer<typeof contactParams>

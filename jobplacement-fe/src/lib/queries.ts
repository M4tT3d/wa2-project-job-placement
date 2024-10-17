import type {
  ContactFormData,
  ContactParams,
  DocumentData,
  IPaginationAndParams,
  ISkill,
  JobOfferFormData,
} from "@/types"
import { keepPreviousData, queryOptions } from "@tanstack/react-query"
import axios from "axios"

export const api = axios.create({
  baseURL: import.meta.env.VITE_GATEWAY_URL,
  withCredentials: true,
  withXSRFToken: true,
  headers: {
    "Content-type": "application/json",
  },
})

export const skillsQuery = () =>
  queryOptions({
    queryKey: ["skills"],
    queryFn: async () => {
      const {
        data: { skills },
      } = await api.get<{ skills: ISkill[] }>("crm/api/skills")
      return skills.map((skill) => ({ text: skill.skill, value: skill.id.toString() }))
    },
    placeholderData: keepPreviousData,
  })

export const skillQuery = (id: string) =>
  queryOptions({
    queryKey: ["skills", id],
    queryFn: async () => {
      const {
        data: {
          skill: { skill },
        },
      } = await api.get<{ skill: { id: number; skill: string } }>(`crm/api/skills/${id}`)
      return skill
    },
    placeholderData: keepPreviousData,
  })

export const contactQuery = (contactId: string) =>
  queryOptions({
    queryKey: ["contacts", contactId],
    queryFn: async () => {
      const {
        data: { contact, documents },
      } = await api.get<{ contact: ContactFormData; documents: DocumentData[] }>(
        `crm/api/contacts/${contactId}`,
      )
      return { ...contact, documents }
    },
  })

export const downloadDocument = async (metadataId: number, documentName: string) => {
  try {
    const response = await axios.get(`/documentstore/api/documents/${metadataId}/data`, {
      responseType: "blob",
    })
    const url = window.URL.createObjectURL(new Blob([response.data]))
    const link = document.createElement("a")
    link.href = url
    link.setAttribute("download", documentName)
    document.body.appendChild(link)
    link.click()
    link.remove()
  } catch (error) {
    console.error("Error downloading the document:", error)
  }
}

export const contactsQuery = (
  params: IPaginationAndParams<ContactParams> = {
    pageSize: 10,
    pageIndex: 0,
  },
) =>
  queryOptions({
    queryKey: ["contacts", params],
    queryFn: async () => {
      const { pageSize, pageIndex, ...rest } = params
      const { data } = await api.get<{ contacts: ContactFormData[]; rowCount: number }>(
        "crm/api/contacts",
        {
          params: {
            limit: pageSize ?? 10,
            pageNumber: pageIndex ? pageIndex + 1 : 1,
            name: params.name === "" ? null : params.name,
            surname: params.surname === "" ? null : params.surname,
            ssnCode: params.ssnCode === "" ? null : params.ssnCode,
            category: params.category === ("" as any) ? null : params.category,
            ...rest,
          },
        },
      )
      return data
    },
    placeholderData: keepPreviousData,
  })

export const jobOffersQuery = (
  params: IPaginationAndParams<JobOfferFormData> = {
    pageSize: 10,
    pageIndex: 0,
  },
) =>
  queryOptions({
    queryKey: ["joboffers", params],
    queryFn: async () => {
      const jobOffer = (params as any).jobOffer === "" ? null : (params as any).jobOffer
      const { pageSize, pageIndex, ...rest } = params
      const { data } = await api.get<{ jobOffers: JobOfferFormData[]; rowCount: number }>(
        "crm/api/joboffers",
        {
          params: {
            limit: pageSize ?? 10,
            pageNumber: pageIndex ? pageIndex + 1 : 1,
            jobOffer,
            ...rest,
          },
        },
      )
      return data
    },
    placeholderData: keepPreviousData,
  })

export const jobOfferQuery = (jobOfferId: string) =>
  queryOptions({
    queryKey: ["joboffer", jobOfferId],
    queryFn: async () => {
      const { data } = await api.get<{
        jobOffer: Omit<JobOfferFormData, "skills"> & {
          skills: string[]
          professionalId: number | null
          value: number
        }
        possibleStatuses: string[]
      }>(`crm/api/joboffers/${jobOfferId}`)
      return data
    },
    placeholderData: keepPreviousData,
  })

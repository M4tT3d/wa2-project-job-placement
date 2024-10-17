import {ChartConfig} from "@/components/ui/chart.tsx";

export const COLORS = {
    RED: "#FF0000",
    ORANGE: "#FF8042",
    BLUE: "#0000FF",
    SKY_BLUE: "#0088FE",
    WHITE: "#FFFFF0",
    GREEN: "#006400",
    YELLOW: "#FFD700",
    PURPLE: "#800080",
}
//JOB OFFER CONFIG
export const chartConfig = {
    value: {
        label: "Occurences",
    },
} satisfies ChartConfig

export const configJobOffer = {
    created: {
        label: "Created",
    },
    aborted: {
        label: "Aborted",
    },
    selection_phase: {
        label: "Selction Phase",
    },
    candidate_proposal: {
        label: "Candidate Proposal",
    },
    consolidated: {
        label: "Consolidated",
    },
    done: {
        label: "Done",
    },
} satisfies ChartConfig

//SKILL CONFIG
export const configSkill = {
    jobOffer: {
        label: "Job Offer",
    },
    professional: {
        label: "Professional",
    }
} satisfies ChartConfig

//PROFESSIONAL CONFIG
export const configProfessional = {
    value: {
        label: "Occurences",
    }
} satisfies ChartConfig

export const configProfessionalRadial = {
    employed: {
        label: "Employed",
    },
    unemployed_available: {
        label: "Unemployed Available",
    },
    not_available: {
        label: "Not Available",
    }

} satisfies ChartConfig

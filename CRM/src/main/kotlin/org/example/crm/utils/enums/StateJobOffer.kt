package org.example.crm.utils.enums

enum class StateJobOffer {
    CREATED,
    ABORTED,
    SELECTION_PHASE,
    CANDIDATE_PROPOSAL,
    CONSOLIDATED,
    DONE;

    fun getNextState(): List<StateJobOffer> {
        return when (this) {
            CREATED -> listOf(ABORTED, SELECTION_PHASE)
            SELECTION_PHASE -> listOf(CANDIDATE_PROPOSAL, ABORTED)
            CANDIDATE_PROPOSAL -> listOf(CONSOLIDATED, SELECTION_PHASE, ABORTED)
            CONSOLIDATED -> listOf(ABORTED, DONE, SELECTION_PHASE)
            ABORTED -> listOf()
            DONE -> listOf()
        }
    }

    fun getEmploymentState(): EmploymentState? {
        return when (this) {
            CREATED -> null
            ABORTED -> EmploymentState.UNEMPLOYED_AVAILABLE
            SELECTION_PHASE -> null
            CANDIDATE_PROPOSAL -> null
            CONSOLIDATED -> EmploymentState.EMPLOYED
            DONE -> EmploymentState.UNEMPLOYED_AVAILABLE
        }
    }


}
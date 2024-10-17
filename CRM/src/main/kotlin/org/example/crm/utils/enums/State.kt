package org.example.crm.utils.enums

enum class State(val list: List<Transition>) {
    RECEIVED(listOf(Transition.READMESSAGE)),
    READ(
        listOf(
            Transition.PROCESSMESSAGE,
            Transition.COMPLETEMESSAGE,
            Transition.FAILMESSAGE,
            Transition.DISCARDMESSAGE
        )
    ),
    DISCARDED(listOf()),
    PROCESSING(listOf(Transition.COMPLETEMESSAGE, Transition.FAILMESSAGE)),
    DONE(listOf()),
    FAILED(listOf())
}

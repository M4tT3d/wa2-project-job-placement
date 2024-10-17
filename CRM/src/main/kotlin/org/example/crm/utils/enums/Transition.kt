package org.example.crm.utils.enums


enum class Transition {
    READMESSAGE,
    DISCARDMESSAGE,
    PROCESSMESSAGE,
    COMPLETEMESSAGE,
    FAILMESSAGE;


    fun getState(): State {
        return when (this) {
            READMESSAGE -> State.READ
            DISCARDMESSAGE -> State.DISCARDED
            PROCESSMESSAGE -> State.PROCESSING
            COMPLETEMESSAGE -> State.DONE
            FAILMESSAGE -> State.FAILED
        }
    }

}
package org.example.crm.dtos

//import org.example.document_store.entities.DocumentContent

data class DocumentContentDTO(
    val id: Long,
    val content: ByteArray
)

/*fun DocumentContent.toDto(): DocumentContentDTO =
    DocumentContentDTO(this.id, this.content)
*/
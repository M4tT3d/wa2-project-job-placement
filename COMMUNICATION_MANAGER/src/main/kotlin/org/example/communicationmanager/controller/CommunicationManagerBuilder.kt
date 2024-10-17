package org.example.communicationmanager.controller

import com.google.api.services.gmail.model.Message
import jakarta.mail.Session
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import org.apache.camel.EndpointInject
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.google.mail.GoogleMailEndpoint
import org.apache.camel.component.jackson.JacksonDataFormat
import org.apache.commons.codec.binary.Base64
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream
import org.example.communicationmanager.dtos.CMailDTO
import org.example.communicationmanager.dtos.CMessageDTO
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class CommunicationManagerBuilder : RouteBuilder() {
    @EndpointInject("google-mail:messages/get")
    lateinit var ep: GoogleMailEndpoint

    private val logger = LoggerFactory.getLogger(CommunicationManagerBuilder::class.java)

    val jsonDataFormat = JacksonDataFormat(CMessageDTO::class.java)

    @Value("\${senderMail}")
    lateinit var senderMail: String

    override fun configure() {
        restConfiguration()
            .host("localhost")
            .port("8080")

        /*from("google-mail-stream:0?markAsRead=true&scopes=https://mail.google.com")
            .process {
                val regex = "<([^>]+)>".toRegex()
                val id = it.getIn().getHeader("CamelGoogleMailId").toString()
                val message = ep.client.users().messages().get("me", id).execute()
                val headers = message.payload.headers
                val subject =
                    headers.find { it.name.equals("subject", true) }?.get("value")?.toString()
                val sender =
                    regex.find(
                        headers.find { it.name.equals("from", true) }?.get("value")?.toString() ?: ""
                    )?.groups?.get(1)?.value
                val date = headers.find { it.name.equals("date", true) }?.get("value")?.toString() ?: ""
                val channel = "EMAIL"
                val body = message.snippet.ifEmpty { null }
                it.getIn().body = mapOf(
                    "sender" to sender,
                    "subject" to subject,
                    "date" to date,
                    "channel" to channel,
                    "body" to body,
                )
            }
            .marshal(jsonDataFormat)
            .to("rest:post:crm/api/messages")*/


        from("direct:/api/emails")
            .process {

                val email = MimeMessage(Session.getDefaultInstance(Properties(), null))
                email.setFrom(InternetAddress(senderMail))
                email.addRecipient(
                    jakarta.mail.Message.RecipientType.TO,
                    InternetAddress(it.getIn().getBody(CMailDTO::class.java).email)
                )
                email.subject = it.getIn().getBody(CMailDTO::class.java).subject
                email.setText(it.getIn().getBody(CMailDTO::class.java).body)

                val buffer = ByteArrayOutputStream()
                email.writeTo(buffer)
                val bytes = buffer.toByteArray()
                val encodedEmail = Base64.encodeBase64URLSafeString(bytes)
                val message = Message()
                message.setRaw(encodedEmail)

                it.getIn().headers["CamelGoogleMail.content"] = message
            }
            .to("google-mail:messages/send?userId=me")
    }
}

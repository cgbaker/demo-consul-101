package com.hashicorp.demo_consul_101.countingservice

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.request.host
import io.ktor.request.uri
import kotlinx.atomicfu.atomic
import kotlinx.serialization.*
import kotlinx.serialization.json.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Serializable
data class Count(val count: Long, val hostname: String)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    var index = atomic(0L)
    val json = Json(JsonConfiguration.Stable)

    routing {
        get("/health") {
            val hello = "Hello, you've hit ${call.request.uri}\n"
            call.respondText(hello, contentType = ContentType.Text.Plain, status = HttpStatusCode.OK)
        }

        get("/") {
            val i = index.addAndGet(1)
            val count = Count(i, call.request.host())
            val json = json.stringify(Count.serializer(), count)
            call.respondText(contentType = ContentType.Application.Json, text = json, status = HttpStatusCode.OK)
        }
    }
}

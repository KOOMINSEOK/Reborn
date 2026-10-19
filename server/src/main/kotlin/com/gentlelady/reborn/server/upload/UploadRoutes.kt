package com.gentlelady.reborn.server.upload

import com.gentlelady.reborn.server.plugins.ErrorResponse
import com.gentlelady.reborn.server.plugins.SUPABASE_AUTH
import com.gentlelady.reborn.server.plugins.userId
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class CreateUploadRequest(val contentType: String)

/** [uploadUrl] 에 이미지 바이트를 PUT 한 뒤, [imageUrl] 을 글/프로필의 이미지 필드로 보낸다. */
@Serializable
data class UploadResponse(val uploadUrl: String, val imageUrl: String)

fun Route.uploadRoutes() {
    authenticate(SUPABASE_AUTH) {
        post("/uploads") {
            if (!Storage.configured) {
                return@post call.respond(HttpStatusCode.ServiceUnavailable, ErrorResponse("uploads_not_configured"))
            }
            val ext = IMAGE_EXTENSIONS[call.receive<CreateUploadRequest>().contentType]
                ?: return@post call.respond(HttpStatusCode.BadRequest, ErrorResponse("unsupported_content_type"))
            val path = "${call.userId()}/${UUID.randomUUID()}.$ext"
            // 블로킹 HTTP 호출 → IO 디스패처로 격리 (Storage 지연이 요청 처리 스레드를 잡지 않게)
            val uploadUrl = withContext(Dispatchers.IO) { Storage.signUpload(path) }
            call.respond(HttpStatusCode.Created, UploadResponse(uploadUrl = uploadUrl, imageUrl = Storage.publicUrl(path)))
        }
    }
}

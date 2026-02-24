package nr.dev.papier2

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

object Route {
    // auth
    const val BASE_AUTH = "base_auth"
    const val LOGIN = "login"
    const val SIGNUP = "signup"

    const val BASE_HOME = "base_home"
    const val HOME = "home"
    const val PROFILE = "profile"
    const val PRODUCTS = "products"
    const val PRODUCT_DETAIL = "products/{id}"
    const val CART = "cart"
}

data class HttpRequest(
    val url: String,
    val method: String = "GET",
    val body: String? = null,
    val headers: Map<String, String> = emptyMap(),
    val timeout: Int = 5000
)

data class HttpResponse(
    val code: Int,
    val body: String?,
    val headers: Map<String, List<String>> = emptyMap(),
    val errors: String? = null
)

object HttpClient {
    const val address = "https://eshop.jemaristudio.id/"
    var accessToken = ""
    suspend fun send(req: HttpRequest): HttpResponse {
        val conn = URL(req.url).openConnection() as HttpURLConnection
        return try {
            conn.requestMethod = req.method
            conn.readTimeout = req.timeout
            conn.connectTimeout = req.timeout

            req.headers.forEach { (key, value) -> conn.setRequestProperty(key, value) }
            if(req.body != null && req.method in listOf("POST", "PUT", "PATCH")) {
                conn.getOutputStream().buffered().use { it.write(req.body.toByteArray()) }
            }
            conn.connect()
            val code = conn.responseCode
            val body = if(code in 200..299) {
                conn.getInputStream().bufferedReader().use { it.readText() }
            } else {
                conn.errorStream?.bufferedReader()?.use { it.readText() }
            }
            HttpResponse(
                code = code,
                body = body,
                headers = conn.headerFields
            )
        } catch(e: Exception) {
            HttpResponse(
                code = -1,
                body = null,
                errors = e.message ?: "Network Error"
            )
        } finally {
            conn.disconnect()
        }
    }
    suspend fun register(name: String, email: String, password: String): HttpResponse {
        val body = """{"name":"$name", "email": "$email", "password": "$password"}"""
        val res = withContext(Dispatchers.IO) {
            send(HttpRequest(
                url = address + "auth/register",
                body = body,
                method = "POST"
            ))
        }
        return res
    }
    suspend fun login(email: String, password: String): String {
        val body = """{"email": "$email", "password": "$password"}"""
        val res = withContext(Dispatchers.IO) {
            send(HttpRequest(
                url = address + "auth/login",
                body = body,
                method = "POST"
            ))
        }
        if(res.code in 200..299 && res.body.isNullOrEmpty()) {
            val json = JSONObject(res.body)
            if(json.getString("status") != "success") return json.getString("message")
            accessToken = json.getJSONObject("data").getString("accessToken")
            return "ok"
        } else {
            return "not ok"
        }
    }
}
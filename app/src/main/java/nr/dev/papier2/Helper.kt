package nr.dev.papier2

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

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
    const val HISTORY = "products"
    const val CART = "cart"
}

fun idxToRoute(idx: Int): String {
    return when(idx) {
        0 -> Route.HOME
        1 -> Route.PRODUCTS
        2 -> Route.HISTORY
        3 -> Route.CART
        4 -> Route.PROFILE
        else -> Route.HOME
    }
}

data class HttpRequest(
    val url: String,
    val method: String = "GET",
    val body: String? = null,
    val headers: Map<String, String> = emptyMap(),
    val timeout: Int = 10000
)

data class HttpResponse(
    val code: Int,
    val body: String?,
    val bytes: ByteArray? = null,
    val headers: Map<String, List<String>> = emptyMap(),
    val errors: String? = null
)

data class User(
    val id: String,
    val name: String,
    val email: String
)

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val avgRating: Double,
    val variants: List<Variant> = emptyList(),
    val categories: List<Category> = emptyList()
)
data class Variant(
    val id: String,
    val productId: String,
    val name: String,
    val price: String,
    val stock: Int
)
data class Category(
    val id: String,
    val name: String,
    val description: String,
)

object HttpClient {
    const val address = "https://eshop.jemaristudio.id/"
    var accessToken = ""
    var user: User? = null
    fun send(req: HttpRequest, getByte: Boolean = false): HttpResponse {
        val conn = URL(req.url).openConnection() as HttpURLConnection
        return try {
            conn.requestMethod = req.method
            conn.readTimeout = req.timeout
            conn.connectTimeout = req.timeout

            req.headers.forEach { (key, value) -> conn.setRequestProperty(key, value) }
            if(req.body != null && req.method in listOf("POST", "PUT", "PATCH")) {
                if(conn.getRequestProperty("content-type") == null) {
                    conn.setRequestProperty("content-type", "application/json")
                }
                conn.doOutput = true
                conn.getOutputStream().buffered().use { it.write(req.body.toByteArray()) }
            }
            conn.connect()
            val code = conn.responseCode
            var bytes: ByteArray? = null
            var body: String? = null
            // ternyata inputStream nya cuma bisa dibaca sekali.
            // *saya menghabiskan 1 jam debugging cuma untuk load gambar
            if(getByte) {
                bytes = if(code in 200..299) {
                    conn.getInputStream().buffered().use { it.readBytes() }
                } else {
                    conn.errorStream?.buffered()?.use { it.readBytes() }
                }
            } else {
                body = if(code in 200..299) {
                    conn.getInputStream().bufferedReader().use { it.readText() }
                } else {
                    conn.errorStream?.bufferedReader()?.use { it.readText() }
                }
            }
            HttpResponse(
                code = code,
                body = body,
                bytes = bytes,
                headers = conn.headerFields
            )
        } catch(e: Exception) {
            e.printStackTrace()
            HttpResponse(
                code = -1,
                body = null,
                errors = e.message ?: "Network Error"
            )
        } finally {
            conn.disconnect()
        }
    }
    suspend fun loadPng(url: String): ImageBitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val res = send(HttpRequest(url), true)
                if(res.code == 200 && res.bytes != null) {
                    val bitmap = BitmapFactory.decodeByteArray(res.bytes, 0, res.bytes!!.size)
                    bitmap.asImageBitmap()
                } else {
                    println("img loading errCode: ${res.code}")
                    null
                }
            } catch (e: Exception) {
                println("img loading err msg: ${e.message}")
                null
            }
        }
    }
    suspend fun register(name: String, email: String, password: String): HttpResponse {
        val body = """{"name":"$name", "email": "$email", "password": "$password"}"""
        val res = withContext(Dispatchers.IO) {
            send(HttpRequest(
                url = address + "auth/register",
                body = body,
                method = "POST",
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
        if(res.body.isNullOrEmpty()) return "not ok"
        val json = JSONObject(res.body)
        if(res.code in 200..299) {
            if(!json.getBoolean("success")) return json.getString("message")
            accessToken = json.getJSONObject("data").getString("accessToken")
            val userd = json.getJSONObject("data").getJSONObject("user")
            user = User(
                userd.getString("id"),
                userd.getString("name"),
                userd.getString("email")
            )
            return "ok"
        } else {
            return json.getString("message") ?: "not ok"
        }
    }
    suspend fun getProducts(search: String = "", categoryId: String = "0"): List<Product> {
        if(accessToken.isEmpty()) return emptyList()
        var url = address + "products?page=1&limit=10&sort=created_at&order=DESC"
        if(search != "") {
            val encoded = URLEncoder.encode(search, StandardCharsets.UTF_8.toString())
            url += "&search=$encoded"
        }
        if(categoryId != "0") {
            url += "&categoryId=$categoryId"
        }
        val res = withContext(Dispatchers.IO) {
            send(HttpRequest(
                url = url,
                headers = mapOf("authorization" to "Bearer $accessToken")
            ))
        }
        if(res.body.isNullOrEmpty()) return emptyList()
        val products = mutableListOf<Product>()
        if(res.code == 200) {
            val json = JSONObject(res.body)
            val arr = json.getJSONArray("data")
            for(i in 0 until arr.length()) {
                val prod = arr.getJSONObject(i)
                val variants = prod.getJSONArray("variants")
                val variants2 = mutableListOf<Variant>()
                val categories = prod.getJSONArray("categories")
                val categories2 = mutableListOf<Category>()

                for(i in 0 until categories.length()) {
                    val obj = categories.getJSONObject(i)
                    categories2.add(Category(
                        id = obj.getString("id"),
                        name = obj.getString("name"),
                        description = obj.getString("description"),
                    ))
                }
                for(i in 0 until variants.length()) {
                    val obj = variants.getJSONObject(i)
                    variants2.add(Variant(
                        id = obj.getString("id"),
                        productId = obj.getString("productId"),
                        name = obj.getString("name"),
                        price = obj.getString("price"),
                        stock = obj.getInt("stock"),
                    ))
                }


                products.add(
                    Product(
                        id = prod.getString("id"),
                        name = prod.getString("name"),
                        description = prod.getString("description"),
                        imageUrl = prod.getString("imageUrl").replace("?", "/png?"),
                        avgRating = prod.getDouble("avgRating"),
                        categories = categories2,
                        variants = variants2
                    )
                )
            }
            return products
        }
        println("errCode: ${res.code}\nmsg: ${res.errors}")
        return emptyList()
    }

    suspend fun getCategories(): List<Category> {
        if(accessToken.isEmpty()) return emptyList()
        val url = address + "categories"
        val res = withContext(Dispatchers.IO) {
            send(HttpRequest(
                url = url,
                headers = mapOf("authorization" to "Bearer $accessToken")
            ))
        }
        if(res.body.isNullOrEmpty()) return emptyList()
        return if(res.code == 200) {
            val json = JSONObject(res.body)
            val categories = mutableListOf<Category>()
            val arr = json.getJSONArray("data")
            for(i in 0 until arr.length()) {
                val category = arr.getJSONObject(i)
                categories.add(Category(
                    id = category.getString("id"),
                    name = category.getString("name"),
                    description = category.getString("description"),
                ))
            }
            return categories
        } else {
            emptyList()
        }
    }
}

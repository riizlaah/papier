package nr.dev.papier2

import android.accessibilityservice.GestureDescription
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

@Composable
fun rememberImageLoader(): ImageLoader {
    return remember { ImageLoader() }
}

class ImageLoader {
    private val cache = mutableMapOf<String, ImageBitmap>()
    suspend fun loadImage(url: String): ImageBitmap? {
        cache[url]?.let { return it }
        val imgBitmap = HttpClient.loadPng(url)
        if(imgBitmap != null) cache[url] = imgBitmap
        return imgBitmap
    }
    fun hasCache(url: String): Boolean {
        return cache.containsKey(url)
    }
    fun clearCache() {
        cache.clear()
    }
}

@Composable
fun NetworkImage(
    url: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.FillHeight
) {
    var imgBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }

    val imgLoader = rememberImageLoader()
    val scope = rememberCoroutineScope()

    LaunchedEffect(url) {
        if(url.isNotBlank()) {
            if(!imgLoader.hasCache(url)) isLoading = true
            isError = false
            try {
                imgBitmap = imgLoader.loadImage(url)
                isError = imgBitmap == null
            } catch (e: Exception) {
                isError = true
            } finally {
                isLoading = false
            }
        }
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        when {
            isLoading -> {
                CircularProgressIndicator(Modifier.size(48.dp))
            }
            isError -> {
                Text("Failed to load image.")
            }
            imgBitmap != null -> {
                Image(
                    imgBitmap!!,
                    contentDescription = contentDescription,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = contentScale
                )
            }
        }
    }

}
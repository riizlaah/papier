package nr.dev.papier2

import android.accessibilityservice.GestureDescription
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
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
import androidx.compose.ui.graphics.Color
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
                val infiniteTransition = rememberInfiniteTransition(label = "loading1")
                val color by infiniteTransition.animateColor(
                    Color.White,
                    Color.LightGray,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "color1"
                )
                Box(Modifier.fillMaxSize().background(color))
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
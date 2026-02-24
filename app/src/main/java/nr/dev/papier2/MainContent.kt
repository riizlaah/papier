package nr.dev.papier2

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun BaseHome(modifier: Modifier) {
    Column(modifier.fillMaxSize()) {
        Home()
    }
}

@Composable
fun Home() {
    Text("Home")
}
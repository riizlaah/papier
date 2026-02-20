package nr.dev.papier2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicSecureTextField
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import nr.dev.papier2.ui.theme.Papier2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Papier2Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginScreen(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun LoginScreen(modifier: Modifier) {
    var email by remember { mutableStateOf("") }
    val passwordState = remember { TextFieldState() }
    var showPassword by remember { mutableStateOf(false) }
    Column(modifier.padding(32.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Image(
            painter = painterResource(R.drawable.logo_papier),
            modifier = Modifier.padding(horizontal = 36.dp),
            contentDescription = "Logo"
        )
        Spacer(Modifier.height(18.dp))
        BasicTextField(
            value = email,
            onValueChange = { email = it },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)),
            decorationBox = { innerTextF ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.MailOutline, contentDescription = "Email")
                    Box(Modifier
                        .weight(1f)
                        .padding(start = 8.dp)) {
                        innerTextF()
                    }
                }
            }
        )
        Column(horizontalAlignment = Alignment.End) {
            BasicSecureTextField(
                state = passwordState,
                textObfuscationMode = if (showPassword) {
                    TextObfuscationMode.Visible
                } else {
                    TextObfuscationMode.RevealLastTyped
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)),
                decorator = { innerTextF ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = "Lock")
                        Box(Modifier
                            .weight(1f)
                            .padding(start = 8.dp)) {
                            innerTextF()
                        }
                        Icon(
                            painterResource(
                            if (showPassword) {
                                R.drawable.visibility
                            } else {
                                R.drawable.visibility_off
                            }
                        ), contentDescription = "Toggle",
                            modifier = Modifier
                                .clickable(true, onClick = { showPassword = !showPassword })
                        )
                    }
                }
            )
            TextButton(onClick = {}) {
                Text("Forgot Password?")
            }
        }
        TextButton(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(50))
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.secondary,
                            MaterialTheme.colorScheme.primary
                        )
                    )
                )
                .padding(10.dp)
        ) {
            Text("Sign In", fontSize = 4.em, color = Color.White)
            Spacer(Modifier.width(10.dp))
            Icon(
                painterResource(R.drawable.arr_forward),
                tint = Color.White,
                contentDescription = "Arrow"
            )
        }
        Spacer(Modifier.height(18.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(Modifier.weight(1f))
            Text(
                "Or continue with",
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1.5f),
                color = Color.Gray
            )
            HorizontalDivider(Modifier.weight(1f))
        }
        Spacer(Modifier.height(18.dp))
        Row(Modifier.fillMaxWidth()) {
            val mod = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(50))
                .background(Color.White)
                .border(1.dp, Color.LightGray, RoundedCornerShape(50))
            IconButton(onClick = {}, modifier = mod) {
                Icon(
                    painterResource(R.drawable.g_logo),
                    tint = Color.Unspecified,
                    contentDescription = "Google"
                )
            }
            Spacer(Modifier.width(8.dp))
            IconButton(onClick = {}, modifier = mod) {
                Icon(
                    painterResource(R.drawable.apple_logo),
                    tint = Color.Unspecified,
                    contentDescription = "Apple",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(Modifier.height(18.dp))
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Don't have an account?")
            TextButton(onClick = {}) {
                Text("Create Account", fontSize = 4.em)
            }
        }
    }
}
package nr.dev.papier2

import android.graphics.drawable.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch


@Composable
fun IconTextField(value: String, onValueChange: (String) -> Unit, icon: ImageVector) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
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
                Icon(icon, contentDescription = "Icon", tint = Color.LightGray)
                Box(Modifier
                    .weight(1f)
                    .padding(start = 12.dp)) {
                    innerTextF()
                }
            }
        }
    )
}

@Composable
fun PasswordField(state: TextFieldState, showPassword: Boolean, onTogglePassword: () -> Unit) {
    BasicSecureTextField(
        state = state,
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
                Icon(Icons.Default.Lock, contentDescription = "Lock", tint = Color.LightGray)
                Box(Modifier
                    .weight(1f)
                    .padding(start = 12.dp)) {
                    innerTextF()
                }
                Icon(
                    painterResource(
                        if (showPassword) {
                            R.drawable.visibility
                        } else {
                            R.drawable.visibility_off
                        }
                    ), contentDescription = "Toggle", tint = Color.LightGray,
                    modifier = Modifier
                        .clickable(true, onClick = onTogglePassword)
                )
            }
        }
    )
}

@Composable
fun LoginScreen(modifier: Modifier, controller: NavHostController) {
    var email by remember { mutableStateOf("") }
    val passwordState = remember { TextFieldState() }
    var showPassword by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var errorMsg by remember { mutableStateOf("") }
    Column(modifier.padding(32.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Image(
            painter = painterResource(R.drawable.logo_papier),
            modifier = Modifier.padding(horizontal = 36.dp),
            contentDescription = "Logo"
        )
        Spacer(Modifier.height(18.dp))
        if(errorMsg.isNotEmpty()) Text(errorMsg, color = Color.Red, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        IconTextField(email, {email = it}, Icons.Default.MailOutline)
        Column(horizontalAlignment = Alignment.End) {
            PasswordField(passwordState, showPassword, {showPassword = !showPassword})
            TextButton(onClick = {}) {
                Text("Forgot Password?")
            }
        }
        TextButton(
            onClick = {
                if(email.isBlank()) {
                    errorMsg = "Email can't be empty!"
                    return@TextButton
                }
                if(passwordState.text.isBlank()) {
                    errorMsg = "Password can't be empty!"
                    return@TextButton
                }
                scope.launch {
                    loading = true
                    val stat = HttpClient.login(email, passwordState.text.toString())
                    when(stat) {
                        "ok" -> {
                            controller.navigate(Route.BASE_HOME) {
                                popUpTo(controller.graph.findStartDestination().id) {
                                    inclusive = true
                                }
                            }
                        }
                        "not ok" -> {
                            errorMsg = "Login Failed"
                        }
                        else -> errorMsg = stat
                    }
                }
            },
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
                .padding(horizontal = 6.dp)
        ) {
            if(loading) {
                CircularProgressIndicator(strokeWidth = 1.dp, color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Sign In", fontSize = 4.em, color = Color.White)
                Spacer(Modifier.width(10.dp))
                Icon(
                    painterResource(R.drawable.arr_forward),
                    tint = Color.White,
                    contentDescription = "Arrow"
                )
            }
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
            TextButton(onClick = {controller.navigate(Route.SIGNUP)}) {
                Text("Create Account", fontSize = 4.em)
            }
        }
    }
}

@Composable
fun SignUpScreen(modifier: Modifier, controller: NavHostController) {
    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    val passwordState = remember { TextFieldState() }
    var showPassword by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var loading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }
    Column(modifier.padding(32.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Image(
            painter = painterResource(R.drawable.register_img),
            modifier = Modifier.padding(horizontal = 36.dp),
            contentDescription = "Logo"
        )
        Spacer(Modifier.height(18.dp))
        if(errorMsg.isNotEmpty()) Text(errorMsg, color = Color.Red, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        IconTextField(email, {email = it}, Icons.Default.MailOutline)
        IconTextField(name, {name = it}, Icons.Default.Person)
        Column(horizontalAlignment = Alignment.End) {
            PasswordField(passwordState, showPassword, {showPassword = !showPassword})
            TextButton(onClick = {}) {
                Text("Forgot Password?")
            }
        }
        TextButton(
            onClick = {
                if(name.isBlank()) {
                    errorMsg = "Name can't be empty"
                    return@TextButton
                }
                if(!email.contains('@')) {
                    errorMsg = "Email not valid"
                    return@TextButton
                }
                if(passwordState.text.isBlank()) {
                    errorMsg = "Password can't be empty"
                    return@TextButton
                }
                scope.launch {
                    loading = true
                    val res = HttpClient.register(name, email, passwordState.text.toString())
                    if(res.code in 200..299) {
                        errorMsg = ""
                        loading = false
                        controller.navigate(Route.LOGIN)
                    } else {
                        errorMsg = "Register Failed."
                        loading = false
                    }
                }
            },
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
                .padding(horizontal = 6.dp)
        ) {
            if(loading) {
                CircularProgressIndicator(strokeWidth = 1.dp, color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Create Account", fontSize = 4.em, color = Color.White)
                Spacer(Modifier.width(10.dp))
                Icon(
                    painterResource(R.drawable.arr_forward),
                    tint = Color.White,
                    contentDescription = "Arrow"
                )
            }
        }
        Spacer(Modifier.height(18.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(Modifier.weight(1f))
            Text(
                "Or sign up with",
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
            Text("Already have an account?")
            TextButton(onClick = {controller.navigate(Route.LOGIN)}) {
                Text("Sign In", fontSize = 4.em)
            }
        }
    }
}
package nr.dev.papier2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import nr.dev.papier2.ui.theme.Papier2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        lateinit var navCtrl: NavHostController
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Papier2Theme {
                navCtrl = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val mod = Modifier.padding(innerPadding)
                    NavHost(
                        navController = navCtrl,
                        startDestination = Route.BASE_AUTH
                    ) {
                        navigation(
                            startDestination = Route.LOGIN,
                            route = Route.BASE_AUTH
                        ) {
                            composable(route = Route.LOGIN) {
                                LoginScreen(mod, navCtrl)
                            }
                            composable(route = Route.SIGNUP) {
                                SignUpScreen(mod, navCtrl)
                            }
                        }
                        composable(route = Route.BASE_HOME) {
                            BaseHome(mod.fillMaxSize(), navCtrl)
                        }
                    }

                }
            }
        }
    }
}

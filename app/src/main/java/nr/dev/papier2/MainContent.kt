@file:OptIn(ExperimentalMaterial3Api::class)

package nr.dev.papier2

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.delay


@Composable
fun BaseHome(modifier: Modifier, controller: NavHostController) {
    var selectedIdx by remember { mutableIntStateOf(0) }
    val icons = listOf(
        R.drawable.feather,
        R.drawable.search,
        R.drawable.document,
        R.drawable.cart,
        R.drawable.user
    )
    val navHost = rememberNavController()
    LaunchedEffect(Unit) {
        HttpClient.updateItemInCarts()
    }
    Box(modifier) {
        NavHost(
            navController = navHost,
            startDestination = Route.HOME
        ) {
            composable(route = Route.HOME) {
                selectedIdx = 0
                HomeScreen(navHost)
            }
            composable(
                route = Route.PRODUCTS_FULL,
                arguments = listOf(
                    navArgument("search") {
                        type = NavType.StringType
                        defaultValue = ""
                    },
                    navArgument("categoryId") {
                        type = NavType.StringType
                        defaultValue = "0"
                    }
                )
            ) { backStackEntry ->
                selectedIdx = 1
                ProductsScreen(
                    searchStr = backStackEntry.arguments?.getString("search") ?: "",
                    categoryId = backStackEntry.arguments?.getString("categoryId") ?: "0",
                    controller = controller,
                )
            }
            composable(
                route = Route.TRANSACTIONS,
            ) {
                selectedIdx = 2
                TransactionScreen(navHost)
            }
            composable(
                route = Route.PRODUCT_DETAIL_FULL,
                arguments = listOf(
                    navArgument("id") {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                selectedIdx = 3
                ProductDetailScreen(backStackEntry.arguments?.getString("id") ?: "n", navHost)
            }
            composable(route = Route.CART) {
                selectedIdx = 3
                CartScreen(navHost)
            }
            composable(route = Route.PROFILE) {
                selectedIdx = 4
                ProfileScreen(controller, navHost)
            }
        }
        if (selectedIdx <= 1) {
            PrimaryTabRow(
                selectedTabIndex = selectedIdx,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomEnd),
            ) {
                icons.forEachIndexed { idx, id ->
                    Tab(
                        selected = selectedIdx == idx,
                        onClick = {
                            selectedIdx = idx
                            navHost.navigate(idxToRoute(idx))
                        },
                        modifier = Modifier.padding(vertical = 12.dp)
                    ) {
                        if(idx == 3 && HttpClient.itemInCarts.isNotEmpty()) {
                            CartIcon(navHost)
                            return@Tab
                        }
                        Icon(painterResource(id), contentDescription = idx.toString())
                    }
                }
            }
        }
    }
}


@Composable
fun HomeScreen(controller: NavHostController) {
    var products by remember { mutableStateOf(emptyList<Product>()) }
    var promoted by remember { mutableStateOf(emptyList<Product>()) }
    var filteredProducts by remember { mutableStateOf(emptyList<Product>()) }
    var categories by remember { mutableStateOf(emptyList<Category>()) }
    var selectedCategory by remember { mutableStateOf("") }

    val pageCount = Int.MAX_VALUE
    val pagerState = rememberPagerState(initialPage = pageCount / 2) { pageCount }

    LaunchedEffect(Unit) {
        if (products.isEmpty()) {
            products = HttpClient.getProducts()
            promoted = products.slice(0..2)
            categories = HttpClient.getCategories()
            selectedCategory = categories[0].id
        }
        while (true) {
            delay(5000)
            pagerState.scrollToPage(pagerState.currentPage + 1)
        }
    }
    LaunchedEffect(selectedCategory) {
        filteredProducts = products.filter { it.categories.any { it.id == selectedCategory } }
    }

    Box(Modifier.fillMaxSize()) {
        // header
        Row(
            Modifier
                .fillMaxWidth()
                .background(Color(253, 251, 247, 225))
                .padding(16.dp)
                .offset(0.dp, 0.dp)
                .zIndex(1f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painterResource(R.drawable.bars),
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = "Menu"
            )
            Text(
                "Papier",
                color = MaterialTheme.colorScheme.primary,
                fontSize = MaterialTheme.typography.displaySmall.fontSize
            )
            Row {
                Icon(
                    painterResource(R.drawable.search),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = "Search",
                    modifier = Modifier.clickable(
                        true,
                        onClick = { controller.navigate(Route.PRODUCTS) }
                    )
                )
                Spacer(Modifier.width(8.dp))
                CartIcon(controller)
            }
        }

        // main content
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(span = { GridItemSpan(2) }) {
                Column {
                    Spacer(Modifier.height(100.dp))
                    Text(
                        "New Arrivals",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.displayMedium.fontSize
                    )
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .fillMaxWidth()
                    ) { page ->
                        if (promoted.isEmpty()) return@HorizontalPager
                        val product = promoted[page % 3]
                        Box(
                            modifier = Modifier
                                .requiredHeight(400.dp)
                                .clickable(onClick = { controller.navigate(Route.PRODUCTS + "/${product.id}") })
                        ) {
                            NetworkImage(
                                product.imageUrl,
                                contentDescription = product.name,
                            )
                            Column(
                                Modifier
                                    .align(Alignment.BottomEnd)
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(
                                                Color.Transparent,
                                                Color(0, 0, 0, 100)
                                            )
                                        )
                                    )
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(Color.Gray)
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        "PROMOTED",
                                        color = Color.White,
                                        fontWeight = FontWeight.Medium,
                                    )
                                }
                                Text(
                                    text = product.name,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = MaterialTheme.typography.titleLarge.fontSize
                                )
                                Text(
                                    text = product.description,
                                    color = Color.White,
                                    fontWeight = FontWeight.Light,
                                    fontSize = MaterialTheme.typography.bodyMedium.fontSize
                                )
                            }
                        }
                    }
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(categories) { item ->
                            if (selectedCategory == item.id) {
                                Button(
                                    onClick = {},
                                    contentPadding = PaddingValues(
                                        vertical = 6.dp,
                                        horizontal = 16.dp
                                    )
                                ) {
                                    Text(item.name)
                                }
                            } else {
                                OutlinedButton(
                                    onClick = { selectedCategory = item.id },
                                    border = BorderStroke(1.dp, Color.LightGray),
                                    contentPadding = PaddingValues(
                                        vertical = 6.dp,
                                        horizontal = 16.dp
                                    )
                                ) {
                                    Text(item.name, color = Color.Gray)
                                }
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Popular",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                        TextButton(
                            onClick = { controller.navigate(Route.PRODUCTS + "?categoryId=$selectedCategory") }
                        ) {
                            Text(text = "SEE ALL", letterSpacing = 2.sp)
                        }
                    }
                }
            }
            items(filteredProducts) { product ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .clickable(onClick = { controller.navigate(Route.PRODUCTS + "/${product.id}") })
                ) {
                    NetworkImage(
                        product.imageUrl,
                        contentDescription = product.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                    )
                    Text(
                        product.name,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Medium
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Rp${product.variants[0].price}", color = Color.Gray)
                        IconButton(
                            onClick = {},
                            modifier = Modifier
                                .clip(CircleShape)
                                .padding(4.dp),
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = Color(
                                    0xffffedd4
                                )
                            )
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add",
                                tint = Color(0xff7e2a0c)
                            )
                        }
                    }
                }
            }
            item {
                Spacer(Modifier.height(64.dp))
            }
        }
    }
}

@Composable
fun ProfileScreen(rootController: NavHostController, appController: NavHostController) {
    val user = HttpClient.user
    Box(
        Modifier
            .fillMaxSize()
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            TextButton(onClick = { appController.popBackStack() }) {
                Icon(painterResource(R.drawable.arr_back), contentDescription = "Back")
                Spacer(Modifier.width(12.dp))
                Text("My Profile")
            }
        }
        LazyColumn(Modifier.fillMaxSize().padding(32.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            item {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(Modifier.height(92.dp))
                    Box {
                        Box(Modifier.size(162.dp).clip(CircleShape).background(Color.White).align(Alignment.Center)) {
                            Box(Modifier
                                .align(Alignment.Center)
                                .padding(32.dp)
                            ) {
                                val initial = user!!.name.split(" ")
                                    .joinToString { it.first().uppercase() }
                                Text(initial, fontSize = MaterialTheme.typography.displayLarge.fontSize)
                            }
                        }
                        // NetworkImage(...)
                        Icon(
                            painterResource(R.drawable.camera),
                            tint = Color.White,
                            contentDescription = "Camera",
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .border(2.dp, Color.White, CircleShape)
                                .padding(8.dp)
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(HttpClient.user!!.name, color = MaterialTheme.colorScheme.primary, fontSize = MaterialTheme.typography.displaySmall.fontSize)
//                    Text(HttpClient.user!!.email, color = Color.Gray)
                }
            }
            item {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .border(1.dp, Color.LightGray, RoundedCornerShape(20.dp))
                        .padding(20.dp)
                ) {
                    InfoRow(
                        ImageVector.vectorResource(R.drawable.user),
                        "NAME",
                        user!!.name
                    )
                    InfoRow(
                        Icons.Default.MailOutline,
                        "EMAIL",
                        user.email
                    )
                }
            }
            item {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .border(1.dp, Color.LightGray, RoundedCornerShape(20.dp))
                        .padding(20.dp)
                ) {
                    ActionRow(
                        ImageVector.vectorResource(R.drawable.pin),
                        "Shipping Addresses"
                    )
                    ActionRow(
                        ImageVector.vectorResource(R.drawable.card),
                        "Payment Methods"
                    )
                    ActionRow(
                        ImageVector.vectorResource(R.drawable.gear),
                        "Settings"
                    )
                }
            }
            item {
                OutlinedButton(
                    onClick = {
                        rootController.navigate(Route.BASE_AUTH) {
                            popUpTo(rootController.graph.id) {
                                inclusive = true
                            }
                        }
                        HttpClient.accessToken = ""
                        HttpClient.user = null
                    },
                    border = BorderStroke(1.dp, Color(0xffffe2e2)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    contentPadding = PaddingValues(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(painterResource(R.drawable.logout), contentDescription = "Log Out")
                    Spacer(Modifier.width(8.dp))
                    Text("Log Out")
                }
            }

        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        Modifier.padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clip(CircleShape)
                .background(Color(0xfffff7ed))
                .padding(12.dp)
                .requiredSize(24.dp),
            contentDescription = label
        )
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = label,
                color = Color.Gray,
                letterSpacing = 1.sp
            )
            Text(
                text = value,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ActionRow(
    startIcon: ImageVector,
    label: String,
    onClick: () -> Unit = {},
    endIcon: ImageVector = ImageVector.vectorResource(R.drawable.top_arr_right),
) {
    Row(Modifier.fillMaxWidth().padding(vertical = 16.dp).clickable(onClick = onClick), verticalAlignment = Alignment.CenterVertically) {
        Icon(
            startIcon,
            tint = Color.Gray,
            contentDescription = label
        )
        Spacer(Modifier.width(8.dp))
        Text(label, modifier = Modifier.weight(1f))
        Icon(
            endIcon,
            tint = Color.Gray,
            contentDescription = "More"
        )
    }
}
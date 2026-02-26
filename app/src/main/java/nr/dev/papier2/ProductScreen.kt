package nr.dev.papier2

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max

@Composable
fun ProductsScreen(
    searchStr: String = "",
    categoryId: String = "0",
    controller: NavHostController
) {
    var search by remember { mutableStateOf(searchStr) }
    var categories by remember { mutableStateOf(emptyList<Category>()) }
    var products by remember { mutableStateOf(emptyList<Product>()) }
    var selectedCategory by remember { mutableStateOf(categoryId) }
    var loading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (categories.isEmpty()) {
            loading = true
            products = HttpClient.getProducts(search, categoryId)
            categories =
                listOf(Category("0", "Semua Item", "Semua Item")) + HttpClient.getCategories()
            selectedCategory = categoryId
            loading = false
        }
    }

    LaunchedEffect(selectedCategory) {
        loading = true
        if (selectedCategory == "0") {
            products = HttpClient.getProducts(search)
        } else {
            products = HttpClient.getProducts(search, selectedCategory)
        }
        loading = false
    }
    LaunchedEffect(search) {
        delay(500)
        loading = true
        products = HttpClient.getProducts(search, selectedCategory)
        loading = false
    }

    Box(
        Modifier.fillMaxSize()
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(Color(253, 251, 247, 225))
                .padding(24.dp)
                .offset(0.dp, 0.dp)
                .zIndex(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painterResource(R.drawable.arr_back),
                    tint = Color.Gray,
                    modifier = Modifier.clickable(true, onClick = { controller.navigateUp() }),
                    contentDescription = "Back"
                )
                Spacer(Modifier.width(12.dp))
                IconTextField(
                    value = search,
                    onValueChange = { search = it },
                    icon = ImageVector.vectorResource(R.drawable.search),
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(12.dp))
                if(HttpClient.itemInCarts.size > 0) {
                    BadgedBox(
                        modifier = Modifier.clickable(onClick = {
                            controller.navigate(Route.CART)
                        }),
                        badge = {
                            Badge(
                                containerColor = Color(0xfff54a00),
                                contentColor = Color.White
                            ) {
                                Text("${HttpClient.itemInCarts.size}")
                            }
                        }
                    ) {
                        Icon(
                            painterResource(R.drawable.cart),
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = "Cart"
                        )
                    }
                } else {
                    Icon(
                        painterResource(R.drawable.cart),
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = "Cart",
                        modifier = Modifier.clickable(
                            true,
                            onClick = { controller.navigate(Route.CART) })
                    )
                }
            }
            LazyRow(Modifier.fillMaxWidth()) {
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
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(span = { GridItemSpan(2) }) {
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(120.dp))
                    HorizontalDivider()
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Discover",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = MaterialTheme.typography.headlineMedium.fontSize
                        )
                        Text(
                            "${products.size} item found",
                            color = Color.Gray,
                            fontSize = MaterialTheme.typography.headlineSmall.fontSize
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    if (loading) {
                        Spacer(Modifier.height(18.dp))
                        CircularProgressIndicator(Modifier.size(32.dp))
                    }
                }
            }
            items(products) { product ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.clickable(onClick = {
                        controller.navigate(Route.PRODUCTS + "/${product.id}")
                    })
                ) {
                    NetworkImage(
                        product.imageUrl,
                        contentDescription = product.name,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.FillWidth
                    )
                    Text(
                        product.name,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        product.categories[0].name,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = MaterialTheme.typography.bodySmall.fontSize,
                        color = Color.Gray
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Rp${product.variants[0].price}",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = MaterialTheme.typography.bodySmall.fontSize
                        )
                        TextButton(
                            onClick = {},
                            modifier = Modifier
                                .clip(RoundedCornerShape(50)),
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = Color(
                                    0xffffedd4
                                )
                            )
                        ) {
                            Text(
                                "View",
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                color = Color(0xff7e2a0c)
                            )
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun ProductDetailScreen(id: String, controller: NavHostController) {
    var product by remember { mutableStateOf<Product?>(null) }
    var selectedVariant by remember { mutableStateOf<Variant?>(null) }
    var loading by remember { mutableStateOf(true) }
    var loading2 by remember { mutableStateOf(false) }
    var quantity by remember { mutableIntStateOf(1) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (product == null) {
            product = HttpClient.getProductById(id)
            selectedVariant = product!!.variants[0]
            loading = false
        }
    }

    Box(Modifier.fillMaxSize()) {
        if (loading) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
            return@Box
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .offset(0.dp, 0.dp)
                .zIndex(1f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    controller.popBackStack()
                },
                modifier = Modifier
                    .clip(CircleShape)
                    .padding(4.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color.White
                )
            ) {
                Icon(
                    painterResource(R.drawable.arr_back),
                    contentDescription = "Back",
                    tint = Color.Gray
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .clip(CircleShape)
                        .padding(4.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = Color.White
                    )
                ) {
                    Icon(
                        painterResource(R.drawable.favorite),
                        contentDescription = "Favorite",
                        tint = Color.Gray
                    )
                }
                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .clip(CircleShape)
                        .padding(4.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = Color.White
                    )
                ) {
                    Icon(
                        painterResource(R.drawable.outline_share_24),
                        contentDescription = "Share",
                        tint = Color.Gray
                    )
                }
            }
        }
        LazyColumn(Modifier.fillMaxSize()) {
            item {
                NetworkImage(
                    url = product!!.imageUrl,
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(
                                bottomEnd = 24.dp,
                                bottomStart = 24.dp
                            )
                        )
                        .fillMaxWidth()
                        .height(480.dp)
                )
            }
            item {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            product?.categories[0]?.name ?: "?",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        )
                        Text(
                            product?.name ?: "?",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium,
                            fontSize = MaterialTheme.typography.displaySmall.fontSize,
                            lineHeight = MaterialTheme.typography.displaySmall.lineHeight
                        )
                    }
                    Column {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xffffedd4))
                                .padding(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                painterResource(R.drawable.star_filled),
                                tint = Color(0xffff6900),
                                contentDescription = "Star"
                            )
                            Text(
                                text = "${product?.avgRating ?: "?"}",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize
                            )
                        }
//                        Text(
//                            product?.ratings.count ?: "?",
//                            color = MaterialTheme.colorScheme.primary,
//                            fontSize = MaterialTheme.typography.displaySmall.fontSize
//                        )
                    }
                }
            }
            item {
                Text(
                    modifier = Modifier.padding(24.dp),
                    text = "Rp${selectedVariant?.price}",
                    fontSize = MaterialTheme.typography.displaySmall.fontSize,
                    fontWeight = FontWeight.Medium
                )
            }
            item {
                Text(
                    modifier = Modifier.padding(24.dp),
                    text = product?.description ?: "?",
                    color = Color.Gray,
                )
            }
            item {
                Column(Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                ) {
                    Text("Select Variant")
                    LazyRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(product?.variants ?: emptyList()) { variant ->
                            if (selectedVariant!!.id == variant.id) {
                                Button(
                                    onClick = { selectedVariant = variant },
                                    contentPadding = PaddingValues(
                                        vertical = 6.dp,
                                        horizontal = 16.dp
                                    )
                                ) {
                                    Text(variant.name)
                                }
                            } else {
                                OutlinedButton(
                                    onClick = { selectedVariant = variant },
                                    contentPadding = PaddingValues(
                                        vertical = 6.dp,
                                        horizontal = 16.dp
                                    ),
                                ) {
                                    Text(variant.name)
                                }
                            }
                        }
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Quantity", fontSize = MaterialTheme.typography.headlineMedium.fontSize)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {quantity = max(1, quantity - 1) },
                            modifier = Modifier
                                .clip(CircleShape)
                                .padding(4.dp),
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = Color.LightGray
                            )
                        ) {
                            Icon(
                                painterResource(R.drawable.remove),
                                contentDescription = "Back",
                                tint = Color.DarkGray
                            )
                        }
                        Text(
                            "$quantity",
                            fontWeight = FontWeight.Medium,
                            fontSize = MaterialTheme.typography.headlineMedium.fontSize
                        )
                        IconButton(
                            onClick = {quantity += 1},
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
                Spacer(Modifier.height(192.dp))
            }
        }
        Row(
            Modifier
                .align(Alignment.BottomStart)
                .clip(RoundedCornerShape(topEnd = 24.dp, topStart = 24.dp))
                .fillMaxWidth()
                .background(Color.White)
                .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 64.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("TOTAL PRICE", color = Color.Gray, fontSize = MaterialTheme.typography.titleSmall.fontSize)
                Text("Rp${selectedVariant!!.price.toDouble() * quantity}", color = MaterialTheme.colorScheme.primary, fontSize = MaterialTheme.typography.headlineMedium.fontSize)
            }
            Button(
                onClick = {
                    scope.launch {
                        loading2 = true
                        HttpClient.addVariantToCart(selectedVariant!!.id, quantity)
                        loading2 = false
                        HttpClient.updateItemInCarts()
                    }
                },
                contentPadding = PaddingValues(vertical = 12.dp, horizontal = 24.dp)
            ) {
                if(loading2) {
                    Text("Wait...", fontWeight = FontWeight.Medium)
                } else {
                    Icon(
                        painterResource(R.drawable.cart),
                        contentDescription = "Cart",
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Add to Cart", fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

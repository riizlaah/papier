@file:OptIn(ExperimentalMaterial3Api::class)

package nr.dev.papier2

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
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max

@Composable
fun CartIcon(controller: NavHostController) {
    if(HttpClient.itemInCarts.isNotEmpty()) {
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

@Composable
fun CartScreen(controller: NavHostController) {
    var promoCode by remember { mutableStateOf("") }
    var usedCouponCode by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var msg by remember { mutableStateOf("") }
    var applyBtnTxt by remember { mutableStateOf("Apply") }
    val freeShippingThreshold = 50000
    val shippingFee = 5000
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        HttpClient.updateItemInCarts()
    }

    Box(Modifier.fillMaxSize()) {
        if(showDialog) {
            BasicAlertDialog(
                onDismissRequest = {showDialog = false},
            ) {
                Column(
                    Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .padding(24.dp),
                ) {
                    Text("Information", fontWeight = FontWeight.Bold, fontSize = MaterialTheme.typography.headlineLarge.fontSize)
                    Text(msg, lineHeight = MaterialTheme.typography.bodyMedium.lineHeight, fontSize = MaterialTheme.typography.bodyMedium.fontSize)
                    Spacer(Modifier.height(12.dp))
                    TextButton(onClick = {
                        showDialog = false
                        controller.navigate(Route.TRANSACTIONS)
                        scope.launch {
                            HttpClient.updateItemInCarts()
                        }
                    }, modifier = Modifier.fillMaxWidth()) {
                        Text("Ok", fontSize = MaterialTheme.typography.headlineSmall.fontSize)
                    }
                }
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .background(Color(253, 251, 247, 225))
                .padding(24.dp)
                .offset(0.dp, 0.dp)
                .zIndex(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painterResource(R.drawable.arr_back),
                    contentDescription = "Back",
                    modifier = Modifier
                        .clickable(onClick = {
                            controller.popBackStack()
                        })
                )
                Spacer(Modifier.width(12.dp))
                Text("Your Cart", fontWeight = FontWeight.Medium)
            }
            Text(
                text = "${HttpClient.itemInCarts.size} Items",
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xffffedd4))
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                fontWeight = FontWeight.Medium,
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                color = MaterialTheme.colorScheme.primary
            )
        }
        if(HttpClient.itemInCarts.isEmpty()) {
            Column(
                Modifier.align(Alignment.Center).fillMaxWidth().padding(40.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(Modifier.clip(CircleShape).background(Color(0xfffff7ed)).padding(20.dp)) {
                    Icon(painterResource(R.drawable.cart), tint = Color(0xffffd7a8), contentDescription = "Cart", modifier = Modifier.requiredSize(72.dp))
                }
                Text("Your cart is empty", fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary, fontSize = MaterialTheme.typography.displaySmall.fontSize)
                Text("Looks like you haven't added any stationery to your collection yet.")
                Button(
                    onClick = {
                        controller.navigate(Route.HOME)
                    },
                    contentPadding = PaddingValues(24.dp)
                ) {
                    Text("Start Shopping")
                }
            }
            return@Box
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxSize().padding(24.dp)) {
            item {
                Spacer(Modifier.height(64.dp))
            }
            items(HttpClient.itemInCarts) { cart ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .border(1.dp, Color.LightGray, RoundedCornerShape(24.dp))
                        .background(Color.White)
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NetworkImage(
                        cart.product.imageUrl,
                        contentDescription = cart.product.name,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Spacer(Modifier.width(8.dp))
                    Column(Modifier.weight(1f)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(cart.product.name, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(4f))
                            Icon(
                                painterResource(R.drawable.delete),
                                tint = Color.Gray,
                                contentDescription = "Delete",
                                modifier = Modifier.weight(1f).clickable(onClick = {
                                    scope.launch {
                                        HttpClient.deleteVariantFromCart(cart.id)
                                        HttpClient.itemInCarts.removeIf { it.id == cart.id }
                                    }
                                })
                            )
                        }
                        Text(cart.variant.name, color = Color.Gray, fontSize = MaterialTheme.typography.titleSmall.fontSize)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Rp${cart.variant.price}")
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(Color(0xfffafafa))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val idx = HttpClient.itemInCarts.indexOfFirst { it.id == cart.id }
                                Icon(
                                    painterResource(R.drawable.remove),
                                    tint = Color.Gray,
                                    contentDescription = "Decrease",
                                    modifier = Modifier.clickable(onClick = {
                                        HttpClient.itemInCarts[idx] = cart.copy(quantity = max(1, cart.quantity - 1))
                                        scope.launch {
                                            HttpClient.updateCartItemQuantity(cart.id, HttpClient.itemInCarts[idx].quantity)
                                        }
                                    })
                                )
                                Text("${cart.quantity}")
                                Icon(
                                    Icons.Default.Add,
                                    tint = Color.Gray,
                                    contentDescription = "Increase",
                                    modifier = Modifier
                                        .requiredSize(24.dp)
                                        .clickable(onClick = {
                                            HttpClient.itemInCarts[idx] = cart.copy(quantity = cart.quantity + 1)
                                            scope.launch {
                                                HttpClient.updateCartItemQuantity(cart.id, HttpClient.itemInCarts[idx].quantity)
                                            }
                                        })
                                )
                            }
                        }
                    }
                }
            }
            item {
                Column {
                    Text("Promo Code")
                    if(usedCouponCode == "") {
                        Row(Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                            IconTextField(
                                icon = ImageVector.vectorResource(R.drawable.tag),
                                value = promoCode,
                                onValueChange = {promoCode = it},
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(Modifier.width(16.dp))
                            Button(
                                onClick = {
                                    applyBtnTxt = "Applying..."
                                    scope.launch {
                                        if(HttpClient.validateCoupon(promoCode)) {
                                            usedCouponCode = promoCode
                                            applyBtnTxt = "Apply"
                                        } else {
                                            applyBtnTxt = "Not Valid"
                                            delay(1000)
                                            applyBtnTxt = "Apply"
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xff171717),
                                    contentColor = Color.White
                                ),
                                contentPadding = PaddingValues(12.dp)
                            ) {
                                Text(applyBtnTxt)
                            }
                        }
                    } else {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xfff0fdf4))
                                .border(1.dp, Color(0xffb9f8cf))
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painterResource(R.drawable.tag),
                                contentDescription = "Tag",
                                tint = Color(0xff00a63e)
                            )
                            Spacer(Modifier.width(8.dp))
                            Column(Modifier.weight(1f)) {
                                Text("Code $usedCouponCode applied", color = Color(0xff016630))
                                Text("You saved ?", fontWeight = FontWeight.Light)
                            }
                            Icon(
                                Icons.Default.Close,
                                tint = Color(0xff00a63e),
                                contentDescription = "Close",
                                modifier = Modifier.clickable(onClick = {usedCouponCode = ""})
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(Modifier.height(12.dp))
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Order Summary")
                    val total = HttpClient.itemInCarts.sumOf { it.variant.price.toDouble() * it.quantity }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subtotal", fontWeight = FontWeight.Light)
                        Text("Rp${total}", fontWeight = FontWeight.Medium)
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Shipping", fontWeight = FontWeight.Light)
                        Text(if(total < freeShippingThreshold) {
                            "Rp$shippingFee"
                        } else {
                            "Free"
                        }, fontWeight = FontWeight.Medium)
                    }
                    if(total < freeShippingThreshold) {
                        Text(
                            text = "Add Rp${freeShippingThreshold - total} or more for free shipping.",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xfffff7ed))
                                .padding(12.dp),
                            color = Color(0xfff54a00),
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(Modifier.height(192.dp))
                }
            }
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(Color.White)
                .padding(24.dp),
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                var total = HttpClient.itemInCarts.sumOf { it.variant.price.toDouble() * it.quantity }
                if(total < freeShippingThreshold) {
                    total += shippingFee
                }
                Text("Total", color = Color.Gray)
                Text("Rp${total}", color = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    scope.launch {
                        if(HttpClient.checkout(usedCouponCode)) {
                            msg = "Order placed successfully! Check your transactions."
                        } else {
                            msg = "Order Failed."
                        }
                        showDialog = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp)
            ) {
                Text("Checkout")
                Spacer(Modifier.width(12.dp))
                Icon(
                    painterResource(R.drawable.arr_forward),
                    contentDescription = "Arrow"
                )
            }
        }
    }
}
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import java.time.format.DateTimeFormatter

@Composable
fun TransactionScreen(controller: NavHostController) {
    val availableFilter = listOf("All", "Active", "Completed")
    var transactions by remember { mutableStateOf(emptyList<Transaction>()) }
    var filteredTransaction by remember { mutableStateOf(emptyList<Transaction>()) }
    var selectedFilter by remember { mutableStateOf(availableFilter[0]) }

    LaunchedEffect(Unit) {
        if(transactions.isEmpty()) {
            transactions = HttpClient.getTransactions()
            filteredTransaction = transactions
        }
    }

    LaunchedEffect(selectedFilter) {
        when(selectedFilter) {
            "All" -> filteredTransaction = transactions
            "Active" -> filteredTransaction = transactions.filter { it.status in listOf("processed", "pending", "delivered") }
            "Completed" -> filteredTransaction = transactions.filter { it.status == "completed" }
        }
    }

    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(Color(253, 251, 247, 225))
                .padding(24.dp)
                .offset(0.dp, 0.dp)
                .zIndex(1f),
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
                Spacer(Modifier.width(16.dp))
                Text("Transactions", fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(availableFilter) { name ->
                    if(selectedFilter == name) {
                        Button(
                            onClick = {selectedFilter = name}
                        ) {
                            Text(name.replaceFirstChar { it.uppercase() })
                        }
                    } else {
                        OutlinedButton(
                            onClick = {selectedFilter = name},
                            border = BorderStroke(1.dp, Color.LightGray),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.LightGray)
                        ) {
                            Text(name.replaceFirstChar { it.uppercase() })
                        }
                    }
                }
            }
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(20.dp), modifier = Modifier.fillMaxSize()) {
            item {
                Spacer(Modifier.height(108.dp))
            }
            items(filteredTransaction) { item ->
                Column(Modifier.padding(horizontal = 32.dp).clip(RoundedCornerShape(20.dp))) {
                    Column(Modifier.fillMaxWidth().background(Color(0xfffafafa)).padding(12.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text(
                                item.updatedAt.format(
                                    DateTimeFormatter.ofPattern("MMM 22, YYYY")
                                ),
                                color = Color.Gray
                            )
                            Text(item.status.uppercase())
                        }
                        Text("ORD-${item.id}", fontWeight = FontWeight.Medium)
                    }
                    Column(Modifier.fillMaxWidth().background(Color.White).padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        item.items.forEach { transacItem ->
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                NetworkImage(
                                    url = transacItem.imageUrl,
                                    contentDescription = transacItem.productName,
                                    modifier = Modifier.size(100.dp).clip(RoundedCornerShape(16.dp))
                                )
                                Column(Modifier.weight(1f)) {
                                    Text(transacItem.productName, fontWeight = FontWeight.Medium)
                                    Text("Qty: ${transacItem.quantity}", fontWeight = FontWeight.Light, color = Color.Gray)
                                }
                                Text("Rp${transacItem.price}", modifier = Modifier.weight(1f))
                            }
                        }
                    }
                    Column(Modifier.fillMaxWidth().background(Color.White).padding(12.dp)) {
                        HorizontalDivider()
                        Spacer(Modifier.height(12.dp))
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Amount")
                            Text("Rp${item.total}")
                        }
                    }
                    Box(Modifier.fillMaxWidth().background(Color(0xfffafafa)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                        TextButton(
                            onClick = {},
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.align(Alignment.BottomEnd)
                        ) {
                            Text("View Details")
                            Icon(painterResource(R.drawable.top_arr_right), contentDescription = "Arrow Right")
                        }
                    }
                }
            }
        }
    }
}
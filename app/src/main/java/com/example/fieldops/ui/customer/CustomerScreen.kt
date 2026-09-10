package com.example.fieldops.ui.customer

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fieldops.data.model.Customer
import com.example.fieldops.data.model.WorkOrder

@Composable
fun CustomerScreen(
    customers: List<Customer>,
    workOrders: List<WorkOrder>,
    onCustomerClick: (String) -> Unit
) {

    var searchQuery by remember {
        mutableStateOf("")
    }

    val filteredCustomers =
        remember(
            searchQuery,
            workOrders
        ) {

            val query =
                searchQuery.trim()

            if (query.isBlank()) {
                customers
            } else {
                customers.filter { customer ->

                    customer.companyName.contains(
                        query,
                        ignoreCase = true
                    ) ||
                            customer.industry.contains(
                                query,
                                ignoreCase = true
                            ) ||
                            customer.contactPerson.contains(
                                query,
                                ignoreCase = true
                            ) ||
                            customer.address.contains(
                                query,
                                ignoreCase = true
                            )
                }
            }
        }

    val totalSites =
        customers.sumOf {
            it.sites.size
        }

    val totalAssets =
        customers.sumOf {
            it.sites.sumOf { site ->
                site.assets.size
            }
        }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    Color(0xFFF5F7FB)
                )
                .padding(
                    horizontal = 18.dp,
                    vertical = 18.dp
                )
    ) {

        Text(
            text = "Pelanggan",
            color = Color(0xFF0F1F3D),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier =
                Modifier.height(4.dp)
        )

        Text(
            text =
                "Kelola perusahaan, site, dan equipment pelanggan",
            color = Color(0xFF718096),
            fontSize = 14.sp
        )

        Spacer(
            modifier =
                Modifier.height(18.dp)
        )

        Row(
            modifier =
                Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(10.dp)
        ) {

            CustomerMetricCard(
                modifier =
                    Modifier.weight(1f),
                value =
                    customers.size.toString(),
                label = "Perusahaan"
            )

            CustomerMetricCard(
                modifier =
                    Modifier.weight(1f),
                value =
                    totalSites.toString(),
                label = "Site"
            )

            CustomerMetricCard(
                modifier =
                    Modifier.weight(1f),
                value =
                    totalAssets.toString(),
                label = "Asset"
            )
        }

        Spacer(
            modifier =
                Modifier.height(16.dp)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
            },
            modifier =
                Modifier.fillMaxWidth(),
            singleLine = true,
            shape =
                RoundedCornerShape(16.dp),
            placeholder = {
                Text(
                    text =
                        "Cari perusahaan, industri, atau kontak..."
                )
            },
            leadingIcon = {
                Text(
                    text = "⌕",
                    fontSize = 23.sp,
                    color = Color(0xFF2563EB)
                )
            }
        )

        Spacer(
            modifier =
                Modifier.height(14.dp)
        )

        Text(
            text =
                "${filteredCustomers.size} perusahaan",
            color = Color(0xFF718096),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        if (filteredCustomers.isEmpty()) {

            CustomerEmptyState()

        } else {

            LazyColumn(
                modifier =
                    Modifier.fillMaxSize(),
                verticalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                items(
                    items =
                        filteredCustomers,
                    key = {
                        it.id
                    }
                ) { customer ->

                    CustomerCard(
                        customer =
                            customer,
                        workOrders =
                            workOrders,
                        onClick = {
                            onCustomerClick(
                                customer.companyName
                            )
                        }
                    )
                }
            }
        }
    }

}

@Composable
private fun CustomerMetricCard(
    modifier: Modifier,
    value: String,
    label: String
) {

    Card(
        modifier = modifier,
        shape =
            RoundedCornerShape(18.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 1.dp
            )
    ) {

        Column(
            modifier =
                Modifier.padding(
                    horizontal = 14.dp,
                    vertical = 15.dp
                )
        ) {

            Text(
                text = value,
                color =
                    Color(0xFF2563EB),
                fontSize = 22.sp,
                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.height(2.dp)
            )

            Text(
                text = label,
                color =
                    Color(0xFF718096),
                fontSize = 11.sp,
                fontWeight =
                    FontWeight.Medium
            )
        }
    }

}

@Composable
private fun CustomerCard(
    customer: Customer,
    workOrders: List<WorkOrder>,
    onClick: () -> Unit
) {

    val customerWorkOrders =
        workOrders.filter {
            it.customer.equals(
                customer.companyName,
                ignoreCase = true
            )
        }

    val activeWorkOrders =
        customerWorkOrders.count {
            it.status.name == "IN_PROGRESS" ||
                    it.status.name == "ACCEPTED"
        }

    val completedWorkOrders =
        customerWorkOrders.count {
            it.status.name == "COMPLETED"
        }

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable {
                    onClick()
                },
        shape =
            RoundedCornerShape(20.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
    ) {

        Column(
            modifier =
                Modifier.padding(17.dp)
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Surface(
                    modifier =
                        Modifier.size(50.dp),
                    shape = CircleShape,
                    color =
                        Color(0xFFEAF2FF)
                ) {

                    Box(
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Text(
                            text =
                                customer.companyName
                                    .take(2)
                                    .uppercase(),
                            color =
                                Color(0xFF2563EB),
                            fontSize = 14.sp,
                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }

                Spacer(
                    modifier =
                        Modifier.width(12.dp)
                )

                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        text =
                            customer.companyName,
                        color =
                            Color(0xFF0F1F3D),
                        fontSize = 16.sp,
                        fontWeight =
                            FontWeight.Bold
                    )

                    Spacer(
                        modifier =
                            Modifier.height(3.dp)
                    )

                    Text(
                        text =
                            customer.industry,
                        color =
                            Color(0xFF718096),
                        fontSize = 12.sp
                    )
                }

                Text(
                    text = "›",
                    color =
                        Color(0xFF2563EB),
                    fontSize = 28.sp,
                    fontWeight =
                        FontWeight.Light
                )
            }

            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )

            Surface(
                modifier =
                    Modifier.fillMaxWidth(),
                shape =
                    RoundedCornerShape(14.dp),
                color =
                    Color(0xFFF5F7FB)
            ) {

                Column(
                    modifier =
                        Modifier.padding(13.dp)
                ) {

                    CustomerInfoRow(
                        label = "Kontak",
                        value =
                            customer.contactPerson
                    )

                    Spacer(
                        modifier =
                            Modifier.height(7.dp)
                    )

                    CustomerInfoRow(
                        label = "Alamat",
                        value =
                            customer.address
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(13.dp)
            )

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                CustomerStat(
                    modifier =
                        Modifier.weight(1f),
                    value =
                        customer.sites.size.toString(),
                    label = "Site"
                )

                CustomerStat(
                    modifier =
                        Modifier.weight(1f),
                    value =
                        customer.sites.sumOf {
                            it.assets.size
                        }.toString(),
                    label = "Asset"
                )

                CustomerStat(
                    modifier =
                        Modifier.weight(1f),
                    value =
                        customerWorkOrders.size.toString(),
                    label = "WO"
                )
            }

            if (
                activeWorkOrders > 0 ||
                completedWorkOrders > 0
            ) {

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    if (activeWorkOrders > 0) {

                        CustomerStatusBadge(
                            text =
                                "$activeWorkOrders aktif",
                            background =
                                Color(0xFFEAF2FF),
                            foreground =
                                Color(0xFF2563EB)
                        )
                    }

                    if (completedWorkOrders > 0) {

                        CustomerStatusBadge(
                            text =
                                "$completedWorkOrders selesai",
                            background =
                                Color(0xFFE8F7EF),
                            foreground =
                                Color(0xFF159A67)
                        )
                    }
                }
            }
        }
    }

}

@Composable
private fun CustomerInfoRow(
    label: String,
    value: String
) {

    Row(
        modifier =
            Modifier.fillMaxWidth()
    ) {

        Text(
            text = label,
            modifier =
                Modifier.width(54.dp),
            color =
                Color(0xFF718096),
            fontSize = 11.sp,
            fontWeight =
                FontWeight.Medium
        )

        Text(
            text = value,
            modifier =
                Modifier.weight(1f),
            color =
                Color(0xFF27364F),
            fontSize = 12.sp,
            fontWeight =
                FontWeight.Medium
        )
    }

}

@Composable
private fun CustomerStat(
    modifier: Modifier,
    value: String,
    label: String
) {

    Surface(
        modifier = modifier,
        shape =
            RoundedCornerShape(12.dp),
        color =
            Color(0xFFF5F7FB)
    ) {

        Column(
            modifier =
                Modifier.padding(
                    vertical = 9.dp,
                    horizontal = 8.dp
                ),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text = value,
                color =
                    Color(0xFF0F1F3D),
                fontSize = 15.sp,
                fontWeight =
                    FontWeight.Bold
            )

            Text(
                text = label,
                color =
                    Color(0xFF718096),
                fontSize = 10.sp
            )
        }
    }

}

@Composable
private fun CustomerStatusBadge(
    text: String,
    background: Color,
    foreground: Color
) {

    Surface(
        shape =
            RoundedCornerShape(50.dp),
        color = background
    ) {

        Text(
            text = text,
            modifier =
                Modifier.padding(
                    horizontal = 10.dp,
                    vertical = 5.dp
                ),
            color = foreground,
            fontSize = 10.sp,
            fontWeight =
                FontWeight.Bold
        )
    }

}

@Composable
private fun CustomerEmptyState() {

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    top = 50.dp
                ),
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Surface(
            modifier =
                Modifier.size(64.dp),
            shape = CircleShape,
            color =
                Color(0xFFEAF2FF)
        ) {

            Box(
                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text = "⌕",
                    color =
                        Color(0xFF2563EB),
                    fontSize = 28.sp
                )
            }
        }

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        Text(
            text =
                "Pelanggan tidak ditemukan",
            color =
                Color(0xFF0F1F3D),
            fontSize = 16.sp,
            fontWeight =
                FontWeight.Bold
        )

        Spacer(
            modifier =
                Modifier.height(4.dp)
        )

        Text(
            text =
                "Coba gunakan kata kunci pencarian lain.",
            color =
                Color(0xFF718096),
            fontSize = 13.sp
        )
    }

}
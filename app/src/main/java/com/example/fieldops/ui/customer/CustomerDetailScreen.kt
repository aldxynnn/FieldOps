package com.example.fieldops.ui.customer

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fieldops.data.model.WorkOrder
import com.example.fieldops.data.model.WorkOrderStatus

@Composable
fun CustomerDetailScreen(
    customerName: String,
    workOrders: List<WorkOrder>,
    onBack: () -> Unit,
    onWorkOrderClick: (WorkOrder) -> Unit
) {
    val customerWorkOrders =
        workOrders.filter {
            it.customer == customerName
        }

    val latestWorkOrder =
        customerWorkOrders.firstOrNull()

    val activeCount =
        customerWorkOrders.count {
            it.status == WorkOrderStatus.ACCEPTED ||
                    it.status == WorkOrderStatus.IN_PROGRESS
        }

    val completedCount =
        customerWorkOrders.count {
            it.status == WorkOrderStatus.COMPLETED
        }

    val pendingCount =
        customerWorkOrders.count {
            it.status == WorkOrderStatus.PENDING
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7FAFE))
    ) {
        CustomerDetailHeader(
            customerName = customerName,
            onBack = onBack
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 18.dp,
                end = 18.dp,
                bottom = 28.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                CustomerIdentityCard(
                    customerName = customerName,
                    location =
                        latestWorkOrder?.location
                            ?: "Lokasi tidak tersedia"
                )
            }

            item {
                CustomerSummaryCard(
                    totalWorkOrders =
                        customerWorkOrders.size,
                    activeCount = activeCount,
                    completedCount = completedCount,
                    pendingCount = pendingCount
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Work Order",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10213F),
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text =
                            "${customerWorkOrders.size} pekerjaan",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF718096)
                    )
                }
            }

            if (customerWorkOrders.isEmpty()) {
                item {
                    CustomerNoWorkOrderState()
                }
            } else {
                items(
                    items = customerWorkOrders,
                    key = {
                        it.id
                    }
                ) { workOrder ->
                    CustomerWorkOrderCard(
                        workOrder = workOrder,
                        onClick = {
                            onWorkOrderClick(workOrder)
                        }
                    )
                }
            }
        }
    }

}

@Composable
private fun CustomerDetailHeader(
    customerName: String,
    onBack: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 18.dp,
                    vertical = 14.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .clickable(
                        onClick = onBack
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "‹",
                    fontSize = 34.sp,
                    color = Color(0xFF10213F)
                )
            }

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Detail Pelanggan",
                    fontSize = 12.sp,
                    color = Color(0xFF718096)
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text = customerName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF10213F),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }

}

@Composable
private fun CustomerIdentityCard(
    customerName: String,
    location: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(62.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F1FF)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text =
                        customerName
                            .firstOrNull()
                            ?.uppercase()
                            ?: "P",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1769FF)
                )
            }

            Spacer(
                modifier = Modifier.width(16.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = customerName,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF10213F),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(
                    modifier = Modifier.height(7.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⌖",
                        fontSize = 16.sp,
                        color = Color(0xFF1769FF)
                    )

                    Spacer(
                        modifier = Modifier.width(5.dp)
                    )

                    Text(
                        text = location,
                        fontSize = 13.sp,
                        color = Color(0xFF718096),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }

}

@Composable
private fun CustomerSummaryCard(
    totalWorkOrders: Int,
    activeCount: Int,
    completedCount: Int,
    pendingCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "Ringkasan Pekerjaan",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF10213F)
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {
                SummaryMetric(
                    value = totalWorkOrders.toString(),
                    label = "Total",
                    modifier = Modifier.weight(1f)
                )

                SummaryMetric(
                    value = activeCount.toString(),
                    label = "Aktif",
                    modifier = Modifier.weight(1f)
                )

                SummaryMetric(
                    value = completedCount.toString(),
                    label = "Selesai",
                    modifier = Modifier.weight(1f)
                )

                SummaryMetric(
                    value = pendingCount.toString(),
                    label = "Pending",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

}

@Composable
private fun SummaryMetric(
    value: String,
    label: String,
    modifier: Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFF4F7FB)
    ) {
        Column(
            modifier = Modifier.padding(
                vertical = 11.dp,
                horizontal = 6.dp
            ),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF10213F)
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF718096)
            )
        }
    }

}

@Composable
private fun CustomerWorkOrderCard(
    workOrder: WorkOrder,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(17.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = workOrder.id,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1769FF)
                    )

                    Spacer(
                        modifier = Modifier.height(5.dp)
                    )

                    Text(
                        text = workOrder.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10213F),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                CustomerStatusBadge(
                    status = workOrder.status
                )
            }

            Spacer(
                modifier = Modifier.height(13.dp)
            )

            Text(
                text =
                    "${workOrder.date}  •  ${workOrder.time}",
                fontSize = 12.sp,
                color = Color(0xFF718096)
            )

            Spacer(
                modifier = Modifier.height(7.dp)
            )

            Text(
                text = workOrder.location,
                fontSize = 12.sp,
                color = Color(0xFF718096),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color =
                        if (
                            workOrder.priority.equals(
                                "High",
                                ignoreCase = true
                            )
                        ) {
                            Color(0xFFFFE8E8)
                        } else {
                            Color(0xFFF1F4F8)
                        }
                ) {
                    Text(
                        text =
                            "Prioritas ${workOrder.priority}",
                        modifier = Modifier.padding(
                            horizontal = 9.dp,
                            vertical = 5.dp
                        ),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color =
                            if (
                                workOrder.priority.equals(
                                    "High",
                                    ignoreCase = true
                                )
                            ) {
                                Color(0xFFD64545)
                            } else {
                                Color(0xFF64748B)
                            }
                    )
                }

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "Lihat detail ›",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1769FF)
                )
            }
        }
    }

}

@Composable
private fun CustomerStatusBadge(
    status: WorkOrderStatus
) {
    val label =
        when (status) {
            WorkOrderStatus.PENDING ->
                "Pending"

            WorkOrderStatus.ACCEPTED ->
                "Diterima"

            WorkOrderStatus.IN_PROGRESS ->
                "Berjalan"

            WorkOrderStatus.COMPLETED ->
                "Selesai"

            WorkOrderStatus.CANCELLED ->
                "Dibatalkan"
        }

    val background =
        when (status) {
            WorkOrderStatus.PENDING ->
                Color(0xFFFFF1D8)

            WorkOrderStatus.ACCEPTED ->
                Color(0xFFE8F1FF)

            WorkOrderStatus.IN_PROGRESS ->
                Color(0xFFE7F7F1)

            WorkOrderStatus.COMPLETED ->
                Color(0xFFE6F7EC)

            WorkOrderStatus.CANCELLED ->
                Color(0xFFFFE8E8)
        }

    val foreground =
        when (status) {
            WorkOrderStatus.PENDING ->
                Color(0xFF9A6500)

            WorkOrderStatus.ACCEPTED ->
                Color(0xFF1769FF)

            WorkOrderStatus.IN_PROGRESS ->
                Color(0xFF16845D)

            WorkOrderStatus.COMPLETED ->
                Color(0xFF16845D)

            WorkOrderStatus.CANCELLED ->
                Color(0xFFD64545)
        }

    Surface(
        shape = RoundedCornerShape(50),
        color = background
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 6.dp
            ),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = foreground
        )
    }

}

@Composable
private fun CustomerNoWorkOrderState() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(
                        Color(0xFFF1F4F8)
                    ),
                contentAlignment =
                    Alignment.Center
            ) {
                Text(
                    text = "—",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF718096)
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "Belum ada Work Order",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF10213F)
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text =
                    "Belum terdapat pekerjaan yang terkait dengan pelanggan ini.",
                fontSize = 12.sp,
                color = Color(0xFF718096)
            )
        }
    }

}
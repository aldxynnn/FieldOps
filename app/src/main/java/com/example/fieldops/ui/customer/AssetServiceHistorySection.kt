package com.example.fieldops.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fieldops.data.model.AssetServiceHistoryItem
import com.example.fieldops.data.model.WorkOrderStatus

@Composable
fun AssetServiceHistorySection(
    history: List<AssetServiceHistoryItem>,
    onWorkOrderClick: (String) -> Unit
) {

    val completed =
        history.count {
            it.status == WorkOrderStatus.COMPLETED
        }

    val active =
        history.count {
            it.status == WorkOrderStatus.ACCEPTED ||
                it.status == WorkOrderStatus.IN_PROGRESS
        }

    val pending =
        history.count {
            it.status == WorkOrderStatus.PENDING
        }

    val cancelled =
        history.count {
            it.status == WorkOrderStatus.CANCELLED
        }

    Column {

        Text(
            text = "Riwayat Service",
            color = Color(0xFF0F1F3D),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.size(10.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            AssetHistoryMetric(
                modifier = Modifier.weight(1f),
                value = completed.toString(),
                label = "Selesai"
            )

            AssetHistoryMetric(
                modifier = Modifier.weight(1f),
                value = active.toString(),
                label = "Aktif"
            )

            AssetHistoryMetric(
                modifier = Modifier.weight(1f),
                value = pending.toString(),
                label = "Pending"
            )

            AssetHistoryMetric(
                modifier = Modifier.weight(1f),
                value = cancelled.toString(),
                label = "Batal"
            )
        }

        Spacer(
            modifier = Modifier.size(12.dp)
        )

        if (history.isEmpty()) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 1.dp
                )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Belum Ada Riwayat Service",
                        color = Color(0xFF0F1F3D),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.size(4.dp)
                    )

                    Text(
                        text = "Belum ada Work Order yang tercatat untuk asset ini.",
                        color = Color(0xFF718096),
                        fontSize = 12.sp
                    )
                }
            }

        } else {

            history.forEach { item ->

                AssetServiceHistoryCard(
                    item = item,
                    onClick = {
                        onWorkOrderClick(
                            item.workOrderId
                        )
                    }
                )

                Spacer(
                    modifier = Modifier.size(10.dp)
                )
            }
        }
    }

}

@Composable
private fun AssetHistoryMetric(
    modifier: Modifier,
    value: String,
    label: String
) {

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFF4F7FC)
    ) {

        Column(
            modifier = Modifier.padding(
                vertical = 10.dp,
                horizontal = 4.dp
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = value,
                color = Color(0xFF2563EB),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.size(2.dp)
            )

            Text(
                text = label,
                color = Color(0xFF718096),
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }

}

@Composable
private fun AssetServiceHistoryCard(
    item: AssetServiceHistoryItem,
    onClick: () -> Unit
) {

    val statusColor =
        when (item.status) {

            WorkOrderStatus.COMPLETED ->
                Color(0xFF159A67)

            WorkOrderStatus.IN_PROGRESS ->
                Color(0xFF2563EB)

            WorkOrderStatus.ACCEPTED ->
                Color(0xFF2563EB)

            WorkOrderStatus.PENDING ->
                Color(0xFFD98600)

            WorkOrderStatus.CANCELLED ->
                Color(0xFFD6344B)
        }

    val statusText =
        when (item.status) {

            WorkOrderStatus.COMPLETED ->
                "SELESAI"

            WorkOrderStatus.IN_PROGRESS ->
                "BERLANGSUNG"

            WorkOrderStatus.ACCEPTED ->
                "DITERIMA"

            WorkOrderStatus.PENDING ->
                "PENDING"

            WorkOrderStatus.CANCELLED ->
                "DIBATALKAN"
        }

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(13.dp),
                color = Color(0xFFEAF1FF)
            ) {

                Box(
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "WO",
                        color = Color(0xFF2563EB),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = item.workOrderId,
                    color = Color(0xFF2563EB),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.size(3.dp)
                )

                Text(
                    text = item.title,
                    color = Color(0xFF0F1F3D),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.size(3.dp)
                )

                Text(
                    text = "${item.date} • ${item.time}",
                    color = Color(0xFF718096),
                    fontSize = 11.sp
                )

                Spacer(
                    modifier = Modifier.size(2.dp)
                )

                Text(
                    text = "${item.siteName} • ${item.assetCode}",
                    color = Color(0xFF8A95A5),
                    fontSize = 10.sp
                )
            }

            Surface(
                shape = RoundedCornerShape(50.dp),
                color = statusColor.copy(
                    alpha = 0.10f
                )
            ) {

                Text(
                    text = statusText,
                    modifier = Modifier.padding(
                        horizontal = 8.dp,
                        vertical = 5.dp
                    ),
                    color = statusColor,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

}
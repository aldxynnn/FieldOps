package com.example.fieldops.ui.customer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.example.fieldops.data.model.PreventiveMaintenance
import com.example.fieldops.data.model.PreventiveMaintenanceStatus

@Composable
fun AssetPreventiveMaintenanceSection(
    maintenance: PreventiveMaintenance?,
    onScheduleClick: () -> Unit
) {

    Column {

        Text(
            text = "Preventive Maintenance",
            color = Color(0xFF0F1F3D),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.size(10.dp)
        )

        if (maintenance == null) {

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
                        .padding(18.dp)
                ) {

                    Text(
                        text = "Belum ada jadwal PM",
                        color = Color(0xFF0F1F3D),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.size(4.dp)
                    )

                    Text(
                        text = "Asset belum memiliki jadwal preventive maintenance.",
                        color = Color(0xFF718096),
                        fontSize = 12.sp
                    )
                }
            }

        } else {

            val statusColor =
                when (maintenance.status) {

                    PreventiveMaintenanceStatus.SCHEDULED ->
                        Color(0xFF2563EB)

                    PreventiveMaintenanceStatus.DUE_SOON ->
                        Color(0xFFD98600)

                    PreventiveMaintenanceStatus.OVERDUE ->
                        Color(0xFFD6344B)
                }

            val statusText =
                when (maintenance.status) {

                    PreventiveMaintenanceStatus.SCHEDULED ->
                        "TERJADWAL"

                    PreventiveMaintenanceStatus.DUE_SOON ->
                        "SEGERA JATUH TEMPO"

                    PreventiveMaintenanceStatus.OVERDUE ->
                        "TERLAMBAT"
                }

            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = onScheduleClick,
                shape = RoundedCornerShape(20.dp),
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

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Surface(
                            modifier = Modifier.size(46.dp),
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFFEAF1FF)
                        ) {

                            Row(
                                horizontalArrangement =
                                    Arrangement.Center,
                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {

                                Text(
                                    text = "PM",
                                    color = Color(0xFF2563EB),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(
                            modifier = Modifier.size(12.dp)
                        )

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = maintenance.maintenanceType,
                                color = Color(0xFF0F1F3D),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(
                                modifier = Modifier.size(3.dp)
                            )

                            Text(
                                text = maintenance.assetCode,
                                color = Color(0xFF718096),
                                fontSize = 11.sp
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
                                    horizontal = 9.dp,
                                    vertical = 6.dp
                                ),
                                color = statusColor,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.size(16.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.spacedBy(10.dp)
                    ) {

                        PMDataBlock(
                            modifier = Modifier.weight(1f),
                            title = "Service terakhir",
                            value = maintenance.lastServiceDate
                        )

                        PMDataBlock(
                            modifier = Modifier.weight(1f),
                            title = "Service berikutnya",
                            value = maintenance.nextServiceDate
                        )
                    }

                    Spacer(
                        modifier = Modifier.size(12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.spacedBy(10.dp)
                    ) {

                        PMDataBlock(
                            modifier = Modifier.weight(1f),
                            title = "Interval",
                            value = "${maintenance.intervalDays} hari"
                        )

                        PMDataBlock(
                            modifier = Modifier.weight(1f),
                            title = "Teknisi",
                            value = maintenance.technician
                        )
                    }

                    Spacer(
                        modifier = Modifier.size(14.dp)
                    )

                    Text(
                        text = "Ketuk untuk melihat detail jadwal",
                        color = Color(0xFF2563EB),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }

}

@Composable
private fun PMDataBlock(
    modifier: Modifier,
    title: String,
    value: String
) {

    Column(
        modifier = modifier
    ) {

        Text(
            text = title,
            color = Color(0xFF8A95A5),
            fontSize = 10.sp
        )

        Spacer(
            modifier = Modifier.size(3.dp)
        )

        Text(
            text = value,
            color = Color(0xFF0F1F3D),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }

}
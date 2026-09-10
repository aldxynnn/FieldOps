package com.example.fieldops.ui.customer

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
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
fun PreventiveMaintenanceDetailScreen(
    maintenance: PreventiveMaintenance,
    onBack: () -> Unit,
    onCreateWorkOrder: () -> Unit
) {

    BackHandler {
        onBack()
    }

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color(0xFFF5F7FB)
            )
            .verticalScroll(
                rememberScrollState()
            )
            .padding(
                horizontal = 18.dp,
                vertical = 18.dp
            )
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            OutlinedButton(
                onClick = onBack,
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "‹ Kembali",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(
                modifier = Modifier.weight(1f)
            )

            Surface(
                shape = RoundedCornerShape(50.dp),
                color = statusColor.copy(
                    alpha = 0.10f
                )
            ) {

                Text(
                    text = statusText,
                    modifier = Modifier.padding(
                        horizontal = 11.dp,
                        vertical = 8.dp
                    ),
                    color = statusColor,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(
            modifier = Modifier.size(20.dp)
        )

        Text(
            text = "Preventive Maintenance",
            color = Color(0xFF0F1F3D),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.size(5.dp)
        )

        Text(
            text = maintenance.maintenanceType,
            color = Color(0xFF718096),
            fontSize = 14.sp
        )

        Spacer(
            modifier = Modifier.size(18.dp)
        )

        PMDetailHeroCard(
            maintenance = maintenance,
            statusText = statusText
        )

        Spacer(
            modifier = Modifier.size(16.dp)
        )

        PMDetailInformationCard(
            maintenance = maintenance
        )

        Spacer(
            modifier = Modifier.size(16.dp)
        )

        PMExecutionCard(
            maintenance = maintenance,
            onCreateWorkOrder = onCreateWorkOrder
        )

        Spacer(
            modifier = Modifier.size(24.dp)
        )
    }

}

@Composable
private fun PMDetailHeroCard(
    maintenance: PreventiveMaintenance,
    statusText: String
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2563EB)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Surface(
                    modifier = Modifier.size(46.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White.copy(
                        alpha = 0.14f
                    )
                ) {

                    BoxCenter(
                        text = "PM"
                    )
                }

                Spacer(
                    modifier = Modifier.size(12.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = maintenance.assetCode,
                        color = Color.White.copy(
                            alpha = 0.76f
                        ),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(
                        modifier = Modifier.size(3.dp)
                    )

                    Text(
                        text = statusText,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(
                modifier = Modifier.size(16.dp)
            )

            Text(
                text = maintenance.maintenanceType,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.size(7.dp)
            )

            Text(
                text =
                    when (maintenance.status) {

                        PreventiveMaintenanceStatus.SCHEDULED ->
                            "Maintenance terjadwal dan siap diproses."

                        PreventiveMaintenanceStatus.DUE_SOON ->
                            "Maintenance segera memasuki jadwal servis."

                        PreventiveMaintenanceStatus.OVERDUE ->
                            "Maintenance sudah melewati jadwal servis."
                    },
                color = Color.White.copy(
                    alpha = 0.86f
                ),
                fontSize = 13.sp,
                lineHeight = 19.sp
            )
        }
    }

}

@Composable
private fun PMDetailInformationCard(
    maintenance: PreventiveMaintenance
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
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

            Text(
                text = "Informasi Maintenance",
                color = Color(0xFF0F1F3D),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.size(12.dp)
            )

            PMDetailRow(
                label = "Kode PM",
                value = maintenance.id
            )

            PMDetailRow(
                label = "Asset",
                value = maintenance.assetCode
            )

            PMDetailRow(
                label = "Jenis Maintenance",
                value = maintenance.maintenanceType
            )

            PMDetailRow(
                label = "Interval",
                value = "${maintenance.intervalDays} hari"
            )

            PMDetailRow(
                label = "Service terakhir",
                value = maintenance.lastServiceDate
            )

            PMDetailRow(
                label = "Service berikutnya",
                value = maintenance.nextServiceDate
            )

            PMDetailRow(
                label = "Teknisi",
                value = maintenance.technician
            )
        }
    }

}

@Composable
private fun PMDetailRow(
    label: String,
    value: String
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = 7.dp
            )
    ) {

        Text(
            text = label,
            color = Color(0xFF8A95A5),
            fontSize = 11.sp
        )

        Spacer(
            modifier = Modifier.size(3.dp)
        )

        Text(
            text = value,
            color = Color(0xFF0F1F3D),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }

}

@Composable
private fun PMExecutionCard(
    maintenance: PreventiveMaintenance,
    onCreateWorkOrder: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
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

            Text(
                text = "Tindakan",
                color = Color(0xFF0F1F3D),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.size(6.dp)
            )

            Text(
                text =
                    "Buat Work Order untuk menjalankan preventive maintenance pada asset ini.",
                color = Color(0xFF718096),
                fontSize = 12.sp,
                lineHeight = 18.sp
            )

            Spacer(
                modifier = Modifier.size(14.dp)
            )

            HorizontalDivider(
                color = Color(0xFFE7ECF3)
            )

            Spacer(
                modifier = Modifier.size(14.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(10.dp),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = RoundedCornerShape(13.dp),
                    color = Color(0xFFEAF1FF)
                ) {

                    BoxCenter(
                        text = "WO"
                    )
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Maintenance Work Order",
                        color = Color(0xFF0F1F3D),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.size(3.dp)
                    )

                    Text(
                        text =
                            "Asset ${maintenance.assetCode}",
                        color = Color(0xFF718096),
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(
                modifier = Modifier.size(16.dp)
            )

            Button(
                onClick = onCreateWorkOrder,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2563EB)
                )
            ) {

                Text(
                    text = "Buat Work Order Maintenance",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

}

@Composable
private fun BoxCenter(
    text: String
) {

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement =
            Arrangement.Center,
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Text(
            text = text,
            color = Color(0xFF2563EB),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }

}
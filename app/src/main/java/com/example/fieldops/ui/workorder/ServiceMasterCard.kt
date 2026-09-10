package com.example.fieldops.ui.workorder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.fieldops.data.model.WorkOrder
import com.example.fieldops.data.model.serviceMasterData

@Composable
fun ServiceMasterCard(
    workOrder: WorkOrder
) {

    val data =
        workOrder.serviceMasterData()
            ?: return

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 6.dp
                ),
        shape =
            RoundedCornerShape(20.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 3.dp
            )
    ) {

        Column(
            modifier =
                Modifier.padding(18.dp),
            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                Surface(
                    shape =
                        RoundedCornerShape(12.dp),
                    color =
                        Color(0xFFEAF2FF)
                ) {

                    Text(
                        text = "▣",
                        modifier =
                            Modifier.padding(
                                horizontal = 11.dp,
                                vertical = 9.dp
                            ),
                        color =
                            Color(0xFF2563EB),
                        style =
                            MaterialTheme.typography
                                .titleMedium
                    )
                }

                Column {

                    Text(
                        text = "Asset / Equipment",
                        color =
                            Color(0xFF718096),
                        style =
                            MaterialTheme.typography
                                .labelMedium
                    )

                    Spacer(
                        modifier =
                            Modifier.padding(
                                top = 2.dp
                            )
                    )

                    Text(
                        text = data.assetName,
                        color =
                            Color(0xFF0F1F3D),
                        style =
                            MaterialTheme.typography
                                .titleLarge
                    )

                    Text(
                        text = data.assetCode,
                        color =
                            Color(0xFF2563EB),
                        style =
                            MaterialTheme.typography
                                .bodyMedium
                    )
                }
            }

            ServiceMasterRow(
                label = "Customer",
                value = data.customerName
            )

            ServiceMasterRow(
                label = "Site",
                value = data.siteName
            )

            ServiceMasterRow(
                label = "Lokasi",
                value = data.siteAddress
            )

            ServiceMasterRow(
                label = "Brand",
                value = data.brand
            )

            ServiceMasterRow(
                label = "Model",
                value = data.model
            )

            ServiceMasterRow(
                label = "Serial Number",
                value = data.serialNumber
            )

            ServiceMasterRow(
                label = "Status Asset",
                value = data.assetStatus
            )
        }
    }

}

@Composable
private fun ServiceMasterRow(
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
                Modifier.width(105.dp),
            color =
                Color(0xFF8A97A8),
            style =
                MaterialTheme.typography
                    .bodySmall
        )

        Text(
            text = value,
            modifier =
                Modifier.weight(1f),
            color =
                Color(0xFF334155),
            style =
                MaterialTheme.typography
                    .bodyMedium
        )
    }

}
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

private data class CustomerSummary(
    val name: String,
    val location: String,
    val workOrderCount: Int,
    val activeCount: Int,
    val completedCount: Int,
    val latestStatus: WorkOrderStatus
)

@Composable
fun CustomerScreen(
    workOrders: List<WorkOrder>,
    onCustomerClick: (String) -> Unit = {}
) {

    var searchQuery by remember {
        mutableStateOf("")
    }

    val customers =
        remember(workOrders) {
            workOrders
                .groupBy {
                    it.customer
                }
                .map { (customerName, customerOrders) ->

                    val activeCount =
                        customerOrders.count {
                            it.status ==
                                    WorkOrderStatus.ACCEPTED ||
                                    it.status ==
                                    WorkOrderStatus.IN_PROGRESS
                        }

                    val completedCount =
                        customerOrders.count {
                            it.status ==
                                    WorkOrderStatus.COMPLETED
                        }

                    val latestOrder =
                        customerOrders.firstOrNull()

                    CustomerSummary(
                        name = customerName,
                        location =
                            latestOrder?.location
                                ?: "Lokasi tidak tersedia",
                        workOrderCount =
                            customerOrders.size,
                        activeCount =
                            activeCount,
                        completedCount =
                            completedCount,
                        latestStatus =
                            latestOrder?.status
                                ?: WorkOrderStatus.PENDING
                    )
                }
                .sortedBy {
                    it.name
                }
        }

    val filteredCustomers =
        customers.filter { customer ->

            customer.name.contains(
                searchQuery,
                ignoreCase = true
            ) ||
                    customer.location.contains(
                        searchQuery,
                        ignoreCase = true
                    )
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.surface
            )
            .padding(
                horizontal = 20.dp
            )
    ) {

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Text(
            text = "Pelanggan",
            style =
                MaterialTheme.typography.headlineMedium,
            fontWeight =
                FontWeight.Bold,
            color =
                MaterialTheme.colorScheme.onSurface
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text =
                "Daftar pelanggan dan pekerjaan terkait",
            style =
                MaterialTheme.typography.bodyMedium,
            color =
                MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
            },
            modifier =
                Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = {

                Text(
                    text = "⌕",
                    fontSize = 24.sp,
                    color =
                        MaterialTheme.colorScheme.primary
                )
            },
            placeholder = {
                Text(
                    text =
                        "Cari pelanggan atau lokasi"
                )
            },
            shape =
                RoundedCornerShape(16.dp),
            colors =
                OutlinedTextFieldDefaults.colors(
                    focusedBorderColor =
                        MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor =
                        MaterialTheme.colorScheme.outlineVariant
                )
        )

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        if (filteredCustomers.isEmpty()) {

            CustomerEmptyState()

        } else {

            Text(
                text =
                    "${filteredCustomers.size} Pelanggan",
                style =
                    MaterialTheme.typography.titleMedium,
                fontWeight =
                    FontWeight.SemiBold,
                color =
                    MaterialTheme.colorScheme.onSurface
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            LazyColumn(
                modifier =
                    Modifier.fillMaxSize(),
                verticalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                items(
                    items = filteredCustomers,
                    key = {
                        it.name
                    }
                ) { customer ->

                    CustomerCard(
                        customer = customer,
                        onClick = {
                            onCustomerClick(
                                customer.name
                            )
                        }
                    )
                }

                item {

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CustomerCard(
    customer: CustomerSummary,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick
            ),
        shape =
            RoundedCornerShape(20.dp),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            ),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    MaterialTheme.colorScheme
                        .surfaceContainerLow
            )
    ) {

        Column(
            modifier =
                Modifier.padding(18.dp)
        ) {

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            MaterialTheme.colorScheme
                                .primaryContainer
                        ),
                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text =
                            customer.name
                                .firstOrNull()
                                ?.uppercase()
                                ?: "P",
                        fontSize = 19.sp,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            MaterialTheme.colorScheme
                                .primary
                    )
                }

                Spacer(
                    modifier =
                        Modifier.width(14.dp)
                )

                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        text =
                            customer.name,
                        style =
                            MaterialTheme.typography
                                .titleMedium,
                        fontWeight =
                            FontWeight.Bold,
                        maxLines = 1,
                        overflow =
                            TextOverflow.Ellipsis
                    )

                    Spacer(
                        modifier =
                            Modifier.height(4.dp)
                    )

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Text(
                            text = "⌖",
                            fontSize = 15.sp,
                            color =
                                MaterialTheme.colorScheme
                                    .onSurfaceVariant
                        )

                        Spacer(
                            modifier =
                                Modifier.width(4.dp)
                        )

                        Text(
                            text =
                                customer.location,
                            style =
                                MaterialTheme.typography
                                    .bodySmall,
                            color =
                                MaterialTheme.colorScheme
                                    .onSurfaceVariant,
                            maxLines = 1,
                            overflow =
                                TextOverflow.Ellipsis
                        )
                    }
                }

                Text(
                    text = "›",
                    fontSize = 28.sp,
                    color =
                        MaterialTheme.colorScheme
                            .onSurfaceVariant
                )
            }

            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                CustomerMetric(
                    label = "Work Order",
                    value =
                        customer.workOrderCount
                            .toString(),
                    modifier =
                        Modifier.weight(1f)
                )

                CustomerMetric(
                    label = "Aktif",
                    value =
                        customer.activeCount
                            .toString(),
                    modifier =
                        Modifier.weight(1f)
                )

                CustomerMetric(
                    label = "Selesai",
                    value =
                        customer.completedCount
                            .toString(),
                    modifier =
                        Modifier.weight(1f)
                )
            }

            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )

            CustomerStatus(
                status =
                    customer.latestStatus
            )
        }
    }
}

@Composable
private fun CustomerMetric(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {

    Surface(
        modifier = modifier,
        shape =
            RoundedCornerShape(12.dp),
        color =
            MaterialTheme.colorScheme
                .surfaceContainer
    ) {

        Column(
            modifier =
                Modifier.padding(
                    horizontal = 10.dp,
                    vertical = 9.dp
                )
        ) {

            Text(
                text = value,
                style =
                    MaterialTheme.typography
                        .titleMedium,
                fontWeight =
                    FontWeight.Bold,
                color =
                    MaterialTheme.colorScheme
                        .onSurface
            )

            Text(
                text = label,
                style =
                    MaterialTheme.typography
                        .labelSmall,
                color =
                    MaterialTheme.colorScheme
                        .onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CustomerStatus(
    status: WorkOrderStatus
) {

    val label =
        when (status) {

            WorkOrderStatus.PENDING ->
                "Pekerjaan menunggu"

            WorkOrderStatus.ACCEPTED ->
                "Pekerjaan diterima"

            WorkOrderStatus.IN_PROGRESS ->
                "Sedang dikerjakan"

            WorkOrderStatus.COMPLETED ->
                "Pekerjaan selesai"

            WorkOrderStatus.CANCELLED ->
                "Dibatalkan"
        }

    val containerColor =
        when (status) {

            WorkOrderStatus.PENDING ->
                MaterialTheme.colorScheme
                    .secondaryContainer

            WorkOrderStatus.ACCEPTED ->
                MaterialTheme.colorScheme
                    .primaryContainer

            WorkOrderStatus.IN_PROGRESS ->
                MaterialTheme.colorScheme
                    .tertiaryContainer

            WorkOrderStatus.COMPLETED ->
                MaterialTheme.colorScheme
                    .primaryContainer

            WorkOrderStatus.CANCELLED ->
                MaterialTheme.colorScheme
                    .errorContainer
        }

    val contentColor =
        when (status) {

            WorkOrderStatus.PENDING ->
                MaterialTheme.colorScheme
                    .onSecondaryContainer

            WorkOrderStatus.ACCEPTED ->
                MaterialTheme.colorScheme
                    .onPrimaryContainer

            WorkOrderStatus.IN_PROGRESS ->
                MaterialTheme.colorScheme
                    .onTertiaryContainer

            WorkOrderStatus.COMPLETED ->
                MaterialTheme.colorScheme
                    .onPrimaryContainer

            WorkOrderStatus.CANCELLED ->
                MaterialTheme.colorScheme
                    .onErrorContainer
        }

    Surface(
        shape =
            RoundedCornerShape(50),
        color =
            containerColor
    ) {

        Row(
            modifier =
                Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 7.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Text(
                text = "●",
                fontSize = 9.sp,
                color = contentColor
            )

            Spacer(
                modifier =
                    Modifier.width(6.dp)
            )

            Text(
                text = label,
                style =
                    MaterialTheme.typography
                        .labelMedium,
                fontWeight =
                    FontWeight.SemiBold,
                color =
                    contentColor
            )
        }
    }
}

@Composable
private fun CustomerEmptyState() {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                bottom = 80.dp
            ),
        contentAlignment =
            Alignment.Center
    ) {

        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme
                            .surfaceContainer
                    ),
                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text = "⌕",
                    fontSize = 34.sp,
                    color =
                        MaterialTheme.colorScheme
                            .onSurfaceVariant
                )
            }

            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )

            Text(
                text =
                    "Pelanggan tidak ditemukan",
                style =
                    MaterialTheme.typography
                        .titleMedium,
                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )

            Text(
                text =
                    "Coba ubah kata kunci pencarian Anda.",
                style =
                    MaterialTheme.typography
                        .bodyMedium,
                color =
                    MaterialTheme.colorScheme
                        .onSurfaceVariant
            )
        }
    }
}
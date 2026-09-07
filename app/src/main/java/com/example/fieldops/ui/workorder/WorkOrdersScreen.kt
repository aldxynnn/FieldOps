package com.example.fieldops.ui.workorder

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fieldops.data.model.WorkOrder
import com.example.fieldops.data.model.WorkOrderStatus

@Composable
fun WorkOrdersScreen(
    workOrders: List<WorkOrder>,
    onWorkOrderClick: (WorkOrder) -> Unit
) {
    var searchQuery by remember {
        mutableStateOf("")
    }

    var selectedFilter by remember {
        mutableStateOf<WorkOrderStatus?>(null)
    }

    val filteredWorkOrders = remember(
        workOrders,
        searchQuery,
        selectedFilter
    ) {
        workOrders.filter { workOrder ->

            val query = searchQuery.trim()

            val matchesSearch =
                query.isEmpty() ||
                        workOrder.id.contains(
                            query,
                            ignoreCase = true
                        ) ||
                        workOrder.title.contains(
                            query,
                            ignoreCase = true
                        ) ||
                        workOrder.customer.contains(
                            query,
                            ignoreCase = true
                        ) ||
                        workOrder.location.contains(
                            query,
                            ignoreCase = true
                        )

            val matchesFilter =
                selectedFilter == null ||
                        workOrder.status == selectedFilter

            matchesSearch && matchesFilter
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            )
            .padding(horizontal = 20.dp)
    ) {

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Text(
            text = "Work Orders",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(
            modifier = Modifier.height(5.dp)
        )

        Text(
            text = "Kelola pekerjaan lapangan Anda",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 15.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            singleLine = true,
            shape = RoundedCornerShape(17.dp),
            placeholder = {
                Text(
                    text = "Cari work order...",
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            leadingIcon = {
                Text(
                    text = "⌕",
                    fontSize = 27.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    TextButton(
                        onClick = {
                            searchQuery = ""
                        },
                        modifier = Modifier.size(46.dp)
                    ) {
                        Text(
                            text = "×",
                            fontSize = 23.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(
                    rememberScrollState()
                ),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            WorkOrderFilterChip(
                label = "Semua",
                selected = selectedFilter == null,
                onClick = {
                    selectedFilter = null
                }
            )

            WorkOrderFilterChip(
                label = "Pending",
                selected =
                    selectedFilter ==
                            WorkOrderStatus.PENDING,
                onClick = {
                    selectedFilter =
                        WorkOrderStatus.PENDING
                }
            )

            WorkOrderFilterChip(
                label = "Diterima",
                selected =
                    selectedFilter ==
                            WorkOrderStatus.ACCEPTED,
                onClick = {
                    selectedFilter =
                        WorkOrderStatus.ACCEPTED
                }
            )

            WorkOrderFilterChip(
                label = "Dikerjakan",
                selected =
                    selectedFilter ==
                            WorkOrderStatus.IN_PROGRESS,
                onClick = {
                    selectedFilter =
                        WorkOrderStatus.IN_PROGRESS
                }
            )

            WorkOrderFilterChip(
                label = "Selesai",
                selected =
                    selectedFilter ==
                            WorkOrderStatus.COMPLETED,
                onClick = {
                    selectedFilter =
                        WorkOrderStatus.COMPLETED
                }
            )
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "${filteredWorkOrders.size} Work Order",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(
                modifier = Modifier.weight(1f)
            )

            if (
                searchQuery.isNotEmpty() ||
                selectedFilter != null
            ) {
                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color =
                        MaterialTheme.colorScheme
                            .primaryContainer
                ) {
                    Text(
                        text = "Filter aktif",
                        modifier = Modifier.padding(
                            horizontal = 11.dp,
                            vertical = 6.dp
                        ),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color =
                            MaterialTheme.colorScheme
                                .onPrimaryContainer
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        if (filteredWorkOrders.isEmpty()) {

            WorkOrderEmptyState(
                hasFilter =
                    searchQuery.isNotEmpty() ||
                            selectedFilter != null,
                onReset = {
                    searchQuery = ""
                    selectedFilter = null
                }
            )

        } else {

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                items(
                    items = filteredWorkOrders,
                    key = {
                        it.id
                    }
                ) { workOrder ->

                    WorkOrderCard(
                        workOrder = workOrder,
                        onClick = {
                            onWorkOrderClick(
                                workOrder
                            )
                        }
                    )
                }

                item {
                    Spacer(
                        modifier = Modifier.height(92.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun WorkOrderFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight =
                    if (selected) {
                        FontWeight.SemiBold
                    } else {
                        FontWeight.Medium
                    }
            )
        },
        shape = RoundedCornerShape(50.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor =
                MaterialTheme.colorScheme.primary,
            selectedLabelColor = Color.White,
            containerColor =
                MaterialTheme.colorScheme.surface,
            labelColor =
                MaterialTheme.colorScheme.onSurfaceVariant
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor =
                MaterialTheme.colorScheme.outline
                    .copy(alpha = 0.35f),
            selectedBorderColor =
                MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
private fun WorkOrderCard(
    workOrder: WorkOrder,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surface
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
                verticalAlignment = Alignment.Top
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = workOrder.id,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color =
                            MaterialTheme.colorScheme.primary
                    )

                    Spacer(
                        modifier = Modifier.height(5.dp)
                    )

                    Text(
                        text = workOrder.title,
                        style =
                            MaterialTheme.typography.titleLarge
                                .copy(
                                    fontSize = 17.sp,
                                    lineHeight = 22.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                        color =
                            MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(
                    modifier = Modifier.width(10.dp)
                )

                WorkOrderStatusBadge(
                    status = workOrder.status
                )
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                SmallIconBadge(
                    symbol = "●",
                    background =
                        MaterialTheme.colorScheme
                            .primaryContainer,
                    foreground =
                        MaterialTheme.colorScheme.primary
                )

                Spacer(
                    modifier = Modifier.width(9.dp)
                )

                Text(
                    text = workOrder.customer,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color =
                        MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                SmallIconBadge(
                    symbol = "⌖",
                    background =
                        MaterialTheme.colorScheme
                            .surfaceVariant,
                    foreground =
                        MaterialTheme.colorScheme
                            .onSurfaceVariant
                )

                Spacer(
                    modifier = Modifier.width(9.dp)
                )

                Text(
                    text = workOrder.location,
                    fontSize = 13.sp,
                    color =
                        MaterialTheme.colorScheme
                            .onSurfaceVariant,
                    maxLines = 1
                )
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text = workOrder.date,
                    fontSize = 12.sp,
                    color =
                        MaterialTheme.colorScheme
                            .onSurfaceVariant
                )

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "Lihat detail  ›",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color =
                        MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun WorkOrderStatusBadge(
    status: WorkOrderStatus
) {
    val background: Color
    val foreground: Color
    val label: String

    when (status) {

        WorkOrderStatus.PENDING -> {
            background = Color(0xFFFFF3DE)
            foreground = Color(0xFFC97900)
            label = "Pending"
        }

        WorkOrderStatus.ACCEPTED -> {
            background = Color(0xFFEAF2FF)
            foreground = Color(0xFF1769FF)
            label = "Diterima"
        }

        WorkOrderStatus.IN_PROGRESS -> {
            background = Color(0xFFE8F8F1)
            foreground = Color(0xFF119A63)
            label = "Dikerjakan"
        }

        WorkOrderStatus.COMPLETED -> {
            background = Color(0xFFE8F8F1)
            foreground = Color(0xFF0D8B59)
            label = "Selesai"
        }

        WorkOrderStatus.CANCELLED -> {
            background = Color(0xFFFFECEF)
            foreground = Color(0xFFD6344B)
            label = "Dibatalkan"
        }
    }

    Surface(
        shape = RoundedCornerShape(50.dp),
        color = background
    ) {

        Row(
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 6.dp
            ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(foreground)
            )

            Spacer(
                modifier = Modifier.width(6.dp)
            )

            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = foreground
            )
        }
    }
}

@Composable
private fun SmallIconBadge(
    symbol: String,
    background: Color,
    foreground: Color
) {
    Box(
        modifier = Modifier
            .size(29.dp)
            .clip(RoundedCornerShape(9.dp))
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = symbol,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = foreground
        )
    }
}

@Composable
private fun WorkOrderEmptyState(
    hasFilter: Boolean,
    onReset: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp),
        contentAlignment = Alignment.Center
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
                            .primaryContainer
                    ),
                contentAlignment =
                    Alignment.Center
            ) {
                Text(
                    text = "⌕",
                    fontSize = 32.sp,
                    color =
                        MaterialTheme.colorScheme.primary
                )
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = if (hasFilter) {
                    "Work Order tidak ditemukan"
                } else {
                    "Belum ada Work Order"
                },
                style =
                    MaterialTheme.typography.titleLarge
                        .copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        ),
                color =
                    MaterialTheme.colorScheme.onBackground
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = if (hasFilter) {
                    "Coba ubah kata kunci atau filter Anda."
                } else {
                    "Work Order baru akan muncul di sini."
                },
                fontSize = 14.sp,
                color =
                    MaterialTheme.colorScheme
                        .onSurfaceVariant
            )

            if (hasFilter) {

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                TextButton(
                    onClick = onReset
                ) {
                    Text(
                        text = "Reset pencarian",
                        fontSize = 14.sp,
                        fontWeight =
                            FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
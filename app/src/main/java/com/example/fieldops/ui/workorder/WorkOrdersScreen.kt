package com.example.fieldops.ui.workorder

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fieldops.data.model.WorkOrder
import com.example.fieldops.data.model.WorkOrderStatus

@Composable
fun WorkOrdersScreen(
    workOrders: List<WorkOrder>,
    onWorkOrderClick: (WorkOrder) -> Unit,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onRetry: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf<WorkOrderStatus?>(null) }
    var selectedPriority by remember { mutableStateOf<String?>(null) }

    val filteredWorkOrders = remember(
        workOrders,
        searchQuery,
        selectedFilter,
        selectedPriority
    ) {
        workOrders.filter { workOrder ->
            val query = searchQuery.trim()

            val matchesSearch =
                query.isEmpty() ||
                        workOrder.id.contains(query, ignoreCase = true) ||
                        workOrder.title.contains(query, ignoreCase = true) ||
                        workOrder.customer.contains(query, ignoreCase = true) ||
                        workOrder.location.contains(query, ignoreCase = true)

            val matchesStatus =
                selectedFilter == null ||
                        workOrder.status == selectedFilter

            val matchesPriority =
                selectedPriority == null ||
                        workOrder.priority.equals(
                            selectedPriority,
                            ignoreCase = true
                        ) ||
                        when (selectedPriority) {
                            "HIGH" -> workOrder.priority.equals("TINGGI", ignoreCase = true)
                            "MEDIUM" -> workOrder.priority.equals("SEDANG", ignoreCase = true)
                            "LOW" -> workOrder.priority.equals("RENDAH", ignoreCase = true)
                            else -> false
                        }

            matchesSearch && matchesStatus && matchesPriority
        }
    }

    val hasActiveFilter =
        searchQuery.isNotEmpty() ||
                selectedFilter != null ||
                selectedPriority != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(20.dp))

        Text(
            text = "Work Orders",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(5.dp))

        Text(
            text = "Kelola pekerjaan lapangan Anda",
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(18.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .semantics {
                    contentDescription = "Cari Work Order berdasarkan ID, pekerjaan, pelanggan, atau lokasi"
                },
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
                        onClick = { searchQuery = "" },
                        modifier = Modifier
                            .size(46.dp)
                            .semantics {
                                contentDescription = "Hapus pencarian"
                            }
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

        Spacer(Modifier.height(14.dp))

        Text(
            text = "Status",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            WorkOrderFilterChip("Semua", selectedFilter == null) {
                selectedFilter = null
            }
            WorkOrderFilterChip("Pending", selectedFilter == WorkOrderStatus.PENDING) {
                selectedFilter = WorkOrderStatus.PENDING
            }
            WorkOrderFilterChip("Diterima", selectedFilter == WorkOrderStatus.ACCEPTED) {
                selectedFilter = WorkOrderStatus.ACCEPTED
            }
            WorkOrderFilterChip("Dikerjakan", selectedFilter == WorkOrderStatus.IN_PROGRESS) {
                selectedFilter = WorkOrderStatus.IN_PROGRESS
            }
            WorkOrderFilterChip("Selesai", selectedFilter == WorkOrderStatus.COMPLETED) {
                selectedFilter = WorkOrderStatus.COMPLETED
            }
            WorkOrderFilterChip("Dibatalkan", selectedFilter == WorkOrderStatus.CANCELLED) {
                selectedFilter = WorkOrderStatus.CANCELLED
            }
        }

        Spacer(Modifier.height(14.dp))

        Text(
            text = "Prioritas",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            WorkOrderPriorityChip("Semua", null, selectedPriority == null) {
                selectedPriority = null
            }
            WorkOrderPriorityChip("Tinggi", "HIGH", selectedPriority == "HIGH") {
                selectedPriority = "HIGH"
            }
            WorkOrderPriorityChip("Sedang", "MEDIUM", selectedPriority == "MEDIUM") {
                selectedPriority = "MEDIUM"
            }
            WorkOrderPriorityChip("Rendah", "LOW", selectedPriority == "LOW") {
                selectedPriority = "LOW"
            }
        }

        Spacer(Modifier.height(16.dp))

        if (isLoading) {
            WorkOrderLoadingState()
        } else if (errorMessage != null) {
            WorkOrderErrorState(
                message = errorMessage,
                onRetry = onRetry
            )
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${filteredWorkOrders.size} Work Order",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    if (hasActiveFilter) {
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "Menampilkan hasil sesuai filter",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.weight(1f))

                if (hasActiveFilter) {
                    Surface(
                        modifier = Modifier.semantics {
                            contentDescription = "Filter aktif"
                        },
                        shape = RoundedCornerShape(50.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "Filter aktif",
                            modifier = Modifier.padding(
                                horizontal = 11.dp,
                                vertical = 6.dp
                            ),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            if (filteredWorkOrders.isEmpty()) {
                WorkOrderEmptyState(
                    hasFilter = hasActiveFilter,
                    onReset = {
                        searchQuery = ""
                        selectedFilter = null
                        selectedPriority = null
                    }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = filteredWorkOrders,
                        key = { it.id }
                    ) { workOrder ->
                        WorkOrderCard(
                            workOrder = workOrder,
                            onClick = { onWorkOrderClick(workOrder) }
                        )
                    }

                    item {
                        Spacer(Modifier.height(92.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkOrderLoadingState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp)
            .semantics {
                contentDescription = "Memuat data Work Order"
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                modifier = Modifier.size(42.dp),
                strokeWidth = 4.dp
            )

            Spacer(Modifier.height(18.dp))

            Text(
                text = "Memuat Work Order",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Menyiapkan data pekerjaan Anda...",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun WorkOrderErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.errorContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "!",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Data Work Order gagal dimuat",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(7.dp))

            Text(
                text = message,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onRetry,
                modifier = Modifier.semantics {
                    contentDescription = "Coba lagi memuat Work Order"
                },
                shape = RoundedCornerShape(13.dp)
            ) {
                Text(
                    text = "Coba lagi",
                    fontWeight = FontWeight.Bold
                )
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
        modifier = Modifier.semantics {
            contentDescription = "Filter status $label"
            this.selected = selected
            role = Role.Tab
        },
        label = {
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
            )
        },
        shape = RoundedCornerShape(50.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = Color.White,
            containerColor = MaterialTheme.colorScheme.surface,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
            selectedBorderColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
private fun WorkOrderPriorityChip(
    label: String,
    value: String?,
    selected: Boolean,
    onClick: () -> Unit
) {
    val activeColor =
        if (value == "HIGH") Color(0xFFD6344B)
        else MaterialTheme.colorScheme.primary

    FilterChip(
        selected = selected,
        onClick = onClick,
        modifier = Modifier.semantics {
            contentDescription = "Filter prioritas $label"
            this.selected = selected
            role = Role.Tab
        },
        label = {
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
            )
        },
        shape = RoundedCornerShape(50.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = activeColor,
            selectedLabelColor = Color.White,
            containerColor = MaterialTheme.colorScheme.surface,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
            selectedBorderColor = activeColor
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
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription =
                    "Work Order ${workOrder.id}, ${workOrder.title}, pelanggan ${workOrder.customer}, status ${statusLabel(workOrder.status)}, prioritas ${priorityLabel(workOrder.priority)}. Ketuk untuk melihat detail."
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(17.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = workOrder.id,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(Modifier.height(5.dp))

                    Text(
                        text = workOrder.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 17.sp,
                            lineHeight = 22.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(Modifier.width(10.dp))

                WorkOrderStatusBadge(workOrder.status)
            }

            Spacer(Modifier.height(14.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                SmallIconBadge(
                    symbol = "●",
                    background = MaterialTheme.colorScheme.primaryContainer,
                    foreground = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.width(9.dp))

                Text(
                    text = workOrder.customer,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                SmallIconBadge(
                    symbol = "⌖",
                    background = MaterialTheme.colorScheme.surfaceVariant,
                    foreground = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.width(9.dp))

                Text(
                    text = workOrder.location,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }

            Spacer(Modifier.height(13.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                WorkOrderPriorityBadge(workOrder.priority)

                Spacer(Modifier.weight(1f))

                Text(
                    text = workOrder.date,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(13.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 10.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Detail pekerjaan",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.weight(1f))

                    Text(
                        text = "Lihat detail  ›",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun WorkOrderPriorityBadge(priority: String) {
    val normalized = priority.trim().uppercase()

    val background: Color
    val foreground: Color
    val label: String

    when (normalized) {
        "HIGH", "TINGGI" -> {
            background = Color(0xFFFFECEF)
            foreground = Color(0xFFD6344B)
            label = "Prioritas tinggi"
        }
        "MEDIUM", "SEDANG" -> {
            background = Color(0xFFFFF3DE)
            foreground = Color(0xFFC97900)
            label = "Prioritas sedang"
        }
        "LOW", "RENDAH" -> {
            background = Color(0xFFEAF2FF)
            foreground = Color(0xFF1769FF)
            label = "Prioritas rendah"
        }
        else -> {
            background = MaterialTheme.colorScheme.surfaceVariant
            foreground = MaterialTheme.colorScheme.onSurfaceVariant
            label = priority
        }
    }

    Surface(
        modifier = Modifier.semantics {
            contentDescription = label
        },
        shape = RoundedCornerShape(50.dp),
        color = background
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 9.dp,
                vertical = 5.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(foreground)
            )

            Spacer(Modifier.width(6.dp))

            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = foreground
            )
        }
    }
}

@Composable
private fun WorkOrderStatusBadge(status: WorkOrderStatus) {
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
        modifier = Modifier.semantics {
            contentDescription = "Status $label"
        },
        shape = RoundedCornerShape(50.dp),
        color = background
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 6.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(foreground)
            )

            Spacer(Modifier.width(6.dp))

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
            .background(background)
            .semantics {
                contentDescription = "Informasi"
            },
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
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⌕",
                    fontSize = 32.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = if (hasFilter) {
                    "Work Order tidak ditemukan"
                } else {
                    "Belum ada Work Order"
                },
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = if (hasFilter) {
                    "Coba ubah kata kunci atau filter Anda."
                } else {
                    "Work Order baru akan muncul di sini."
                },
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (hasFilter) {
                Spacer(Modifier.height(14.dp))

                TextButton(
                    onClick = onReset,
                    modifier = Modifier.semantics {
                        contentDescription = "Reset semua filter Work Order"
                    }
                ) {
                    Text(
                        text = "Reset semua filter",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

private fun statusLabel(status: WorkOrderStatus): String =
    when (status) {
        WorkOrderStatus.PENDING -> "Pending"
        WorkOrderStatus.ACCEPTED -> "Diterima"
        WorkOrderStatus.IN_PROGRESS -> "Dikerjakan"
        WorkOrderStatus.COMPLETED -> "Selesai"
        WorkOrderStatus.CANCELLED -> "Dibatalkan"
    }

private fun priorityLabel(priority: String): String =
    when (priority.trim().uppercase()) {
        "HIGH", "TINGGI" -> "tinggi"
        "MEDIUM", "SEDANG" -> "sedang"
        "LOW", "RENDAH" -> "rendah"
        else -> priority
    }

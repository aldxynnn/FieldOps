package com.example.fieldops.ui.workorder

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fieldops.data.model.WorkOrder
import com.example.fieldops.data.model.WorkOrderStatus
import com.example.fieldops.ui.components.EnterpriseCard
import com.example.fieldops.ui.components.SectionHeader
import com.example.fieldops.ui.components.StatusPill
import com.example.fieldops.ui.theme.FieldOpsBackground
import com.example.fieldops.ui.theme.FieldOpsDanger
import com.example.fieldops.ui.theme.FieldOpsPrimary
import com.example.fieldops.ui.theme.FieldOpsSuccess
import com.example.fieldops.ui.theme.FieldOpsWarning

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
        workOrders.filter { wo ->
            val query = searchQuery.trim()

            val matchesSearch =
                query.isEmpty() ||
                        wo.id.contains(query, true) ||
                        wo.title.contains(query, true) ||
                        wo.customer.contains(query, true) ||
                        wo.location.contains(query, true)

            val matchesStatus =
                selectedFilter == null ||
                        wo.status == selectedFilter

            val matchesPriority =
                selectedPriority == null ||
                        wo.priority.equals(selectedPriority, true) ||
                        when (selectedPriority) {
                            "HIGH" ->
                                wo.priority.equals("TINGGI", true)

                            "MEDIUM" ->
                                wo.priority.equals("SEDANG", true)

                            "LOW" ->
                                wo.priority.equals("RENDAH", true)

                            else -> false
                        }

            matchesSearch &&
                    matchesStatus &&
                    matchesPriority
        }
    }

    val hasFilter =
        searchQuery.isNotBlank() ||
                selectedFilter != null ||
                selectedPriority != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FieldOpsBackground)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(
                horizontal = 18.dp,
                vertical = 18.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                WorkOrdersHeader()
            }

            item {
                PremiumSearchField(
                    searchQuery = searchQuery,
                    onSearchChange = {
                        searchQuery = it
                    }
                )
            }

            item {
                FilterSection(
                    title = "STATUS",
                    content = {

                        Chip(
                            label = "Semua",
                            selected = selectedFilter == null
                        ) {
                            selectedFilter = null
                        }

                        Chip(
                            label = "Pending",
                            selected = selectedFilter ==
                                    WorkOrderStatus.PENDING
                        ) {
                            selectedFilter =
                                WorkOrderStatus.PENDING
                        }

                        Chip(
                            label = "Diterima",
                            selected = selectedFilter ==
                                    WorkOrderStatus.ACCEPTED
                        ) {
                            selectedFilter =
                                WorkOrderStatus.ACCEPTED
                        }

                        Chip(
                            label = "Dikerjakan",
                            selected = selectedFilter ==
                                    WorkOrderStatus.IN_PROGRESS
                        ) {
                            selectedFilter =
                                WorkOrderStatus.IN_PROGRESS
                        }

                        Chip(
                            label = "Selesai",
                            selected = selectedFilter ==
                                    WorkOrderStatus.COMPLETED
                        ) {
                            selectedFilter =
                                WorkOrderStatus.COMPLETED
                        }

                        Chip(
                            label = "Dibatalkan",
                            selected = selectedFilter ==
                                    WorkOrderStatus.CANCELLED
                        ) {
                            selectedFilter =
                                WorkOrderStatus.CANCELLED
                        }
                    }
                )
            }

            item {
                FilterSection(
                    title = "PRIORITAS",
                    content = {

                        PriorityChip(
                            label = "Semua",
                            value = null,
                            selected = selectedPriority == null
                        ) {
                            selectedPriority = null
                        }

                        PriorityChip(
                            label = "Tinggi",
                            value = "HIGH",
                            selected = selectedPriority == "HIGH"
                        ) {
                            selectedPriority = "HIGH"
                        }

                        PriorityChip(
                            label = "Sedang",
                            value = "MEDIUM",
                            selected = selectedPriority == "MEDIUM"
                        ) {
                            selectedPriority = "MEDIUM"
                        }

                        PriorityChip(
                            label = "Rendah",
                            value = "LOW",
                            selected = selectedPriority == "LOW"
                        ) {
                            selectedPriority = "LOW"
                        }
                    }
                )
            }

            item {
                SectionHeader(
                    "${filteredWorkOrders.size} Work Order",
                    if (hasFilter) {
                        "Hasil sudah disaring sesuai pilihan Anda"
                    } else {
                        "Semua pekerjaan yang tersedia"
                    }
                )
            }

            if (isLoading) {

                item {
                    LoadingState()
                }

            } else if (errorMessage != null) {

                item {
                    ErrorState(
                        errorMessage = errorMessage,
                        onRetry = onRetry
                    )
                }

            } else if (filteredWorkOrders.isEmpty()) {

                item {
                    EmptyState(
                        hasFilter = hasFilter
                    )
                }

            } else {

                items(
                    items = filteredWorkOrders,
                    key = { it.id }
                ) { wo ->

                    WorkOrderCard(
                        wo = wo,
                        onClick = onWorkOrderClick
                    )
                }
            }

            item {
                Spacer(
                    modifier = Modifier.height(4.dp)
                )
            }
        }
    }
}

@Composable
private fun WorkOrdersHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 2.dp,
                end = 2.dp,
                top = 2.dp
            )
    ) {
        Text(
            text = "Work Orders",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            letterSpacing = (-0.5).sp
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text = "Pantau dan kelola seluruh pekerjaan lapangan.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 20.sp
        )
    }
}

@Composable
private fun PremiumSearchField(
    searchQuery: String,
    onSearchChange: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        tonalElevation = 1.dp
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription =
                        "Cari Work Order berdasarkan ID, pekerjaan, pelanggan, atau lokasi"
                },
            singleLine = true,
            shape = RoundedCornerShape(18.dp),
            placeholder = {
                Text(
                    text = "Cari ID, pekerjaan, pelanggan, lokasi...",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            leadingIcon = {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(
                            FieldOpsPrimary.copy(alpha = 0.10f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⌕",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = FieldOpsPrimary
                    )
                }
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    TextButton(
                        onClick = {
                            onSearchChange("")
                        }
                    ) {
                        Text(
                            text = "Bersihkan",
                            fontWeight = FontWeight.SemiBold,
                            color = FieldOpsPrimary
                        )
                    }
                }
            }
        )
    }
}

@Composable
private fun FilterSection(
    title: String,
    content: @Composable RowScope.() -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 0.8.sp
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(
                    rememberScrollState()
                ),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            content = content
        )
    }
}

@Composable
private fun Chip(
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
                fontWeight = if (selected) {
                    FontWeight.SemiBold
                } else {
                    FontWeight.Medium
                }
            )
        },
        shape = RoundedCornerShape(50.dp),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = MaterialTheme.colorScheme.outline
                .copy(alpha = 0.35f),
            selectedBorderColor = FieldOpsPrimary
                .copy(alpha = 0.35f)
        ),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.surface,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            selectedContainerColor = FieldOpsPrimary
                .copy(alpha = 0.12f),
            selectedLabelColor = FieldOpsPrimary
        )
    )
}

@Composable
private fun PriorityChip(
    label: String,
    value: String?,
    selected: Boolean,
    onClick: () -> Unit
) {
    val accent = when (value) {
        "HIGH" -> FieldOpsWarning
        "MEDIUM" -> FieldOpsPrimary
        "LOW" -> FieldOpsSuccess
        else -> FieldOpsPrimary
    }

    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                fontWeight = if (selected) {
                    FontWeight.SemiBold
                } else {
                    FontWeight.Medium
                }
            )
        },
        shape = RoundedCornerShape(50.dp),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = MaterialTheme.colorScheme.outline
                .copy(alpha = 0.35f),
            selectedBorderColor = accent
                .copy(alpha = 0.38f)
        ),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.surface,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            selectedContainerColor = accent
                .copy(alpha = 0.12f),
            selectedLabelColor = accent
        )
    )
}

@Composable
private fun WorkOrderCard(
    wo: WorkOrder,
    onClick: (WorkOrder) -> Unit
) {
    val priorityColor = when {
        wo.priority.equals("HIGH", true) ||
                wo.priority.equals("TINGGI", true) -> FieldOpsDanger

        wo.priority.equals("MEDIUM", true) ||
                wo.priority.equals("SEDANG", true) -> FieldOpsWarning

        wo.priority.equals("LOW", true) ||
                wo.priority.equals("RENDAH", true) -> FieldOpsSuccess

        else -> MaterialTheme.colorScheme.outline
    }

    EnterpriseCard(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline
                    .copy(alpha = 0.10f),
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(priorityColor)
                        )

                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )

                        Text(
                            text = wo.id,
                            modifier = Modifier.clickable {
                                onClick(wo)
                            },
                            color = FieldOpsPrimary,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(7.dp)
                    )

                    Text(
                        text = wo.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 21.sp
                    )
                }

                Spacer(
                    modifier = Modifier.width(10.dp)
                )

                StatusPill(
                    wo.status,
                    compact = true
                )
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
                    .copy(alpha = 0.45f)
            ) {
                Column(
                    modifier = Modifier.padding(
                        horizontal = 13.dp,
                        vertical = 11.dp
                    )
                ) {

                    Text(
                        text = wo.customer,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(
                        modifier = Modifier.height(7.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = "⌖",
                            fontSize = 15.sp,
                            color = FieldOpsPrimary,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.width(7.dp)
                        )

                        Text(
                            text = wo.location,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme
                                .onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = "◷",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme
                                .onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.width(7.dp)
                        )

                        Text(
                            text = "${wo.date} • ${wo.time}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme
                                .onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "PRIORITAS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme
                            .onSurfaceVariant,
                        letterSpacing = 0.7.sp
                    )

                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )

                    Text(
                        text = formatPriority(wo.priority),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = priorityColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                TextButton(
                    onClick = {
                        onClick(wo)
                    }
                ) {
                    Text(
                        text = "Lihat detail",
                        fontWeight = FontWeight.Bold,
                        color = FieldOpsPrimary
                    )
                }
            }
        }
    }
}

private fun formatPriority(
    priority: String
): String {
    return when {
        priority.equals("HIGH", true) ->
            "Tinggi"

        priority.equals("MEDIUM", true) ->
            "Sedang"

        priority.equals("LOW", true) ->
            "Rendah"

        priority.equals("TINGGI", true) ->
            "Tinggi"

        priority.equals("SEDANG", true) ->
            "Sedang"

        priority.equals("RENDAH", true) ->
            "Rendah"

        else ->
            priority
    }
}

@Composable
private fun LoadingState() {
    EnterpriseCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 34.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            CircularProgressIndicator(
                modifier = Modifier.size(34.dp),
                color = FieldOpsPrimary,
                strokeWidth = 3.dp
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Text(
                text = "Memuat Work Order...",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "Mohon tunggu sebentar.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme
                    .onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ErrorState(
    errorMessage: String,
    onRetry: () -> Unit
) {
    EnterpriseCard(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = FieldOpsDanger.copy(alpha = 0.18f),
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(
                            FieldOpsDanger.copy(alpha = 0.10f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "!",
                        color = FieldOpsDanger,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(
                    modifier = Modifier.width(11.dp)
                )

                Text(
                    text = "Tidak dapat memuat Work Order",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(
                modifier = Modifier.height(11.dp)
            )

            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme
                    .onSurfaceVariant,
                lineHeight = 20.sp
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            TextButton(
                onClick = onRetry
            ) {
                Text(
                    text = "Coba lagi",
                    fontWeight = FontWeight.Bold,
                    color = FieldOpsPrimary
                )
            }
        }
    }
}

@Composable
private fun EmptyState(
    hasFilter: Boolean
) {
    EnterpriseCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 22.dp,
                    vertical = 30.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        FieldOpsPrimary.copy(alpha = 0.09f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✓",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = FieldOpsPrimary
                )
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Text(
                text = if (hasFilter) {
                    "Work Order tidak ditemukan"
                } else {
                    "Tidak ada Work Order"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = if (hasFilter) {
                    "Coba ubah kata kunci atau filter Anda."
                } else {
                    "Belum ada pekerjaan yang tersedia."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme
                    .onSurfaceVariant,
                lineHeight = 20.sp
            )
        }
    }
}
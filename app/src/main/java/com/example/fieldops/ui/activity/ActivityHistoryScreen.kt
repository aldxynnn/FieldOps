package com.example.fieldops.ui.activity

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ActivityHistoryItem(
    val id: String,
    val workOrderId: String,
    val title: String,
    val description: String,
    val time: String,
    val type: ActivityType
)

enum class ActivityType {
    STATUS,
    NOTE,
    PHOTO
}

private enum class ActivityFilter {
    ALL,
    STATUS,
    NOTE,
    PHOTO
}

private val Blue = Color(0xFF2563EB)
private val BlueDark = Color(0xFF173B8F)
private val BlueSoft = Color(0xFFEAF2FF)
private val Dark = Color(0xFF0F1F3D)
private val Secondary = Color(0xFF718096)
private val Background = Color(0xFFF4F7FB)
private val SurfaceWhite = Color.White
private val Green = Color(0xFF159A67)
private val GreenSoft = Color(0xFFE8F8F1)
private val Orange = Color(0xFFB66A00)
private val OrangeSoft = Color(0xFFFFF3DE)
private val Red = Color(0xFFE94B5F)
private val RedSoft = Color(0xFFFFECEF)
private val Border = Color(0xFFE5EAF2)

@Composable
fun ActivityHistoryScreen(
    activities: List<ActivityHistoryItem>,
    onBack: () -> Unit
) {
    BackHandler {
        onBack()
    }

    var selectedFilter by remember {
        mutableStateOf(ActivityFilter.ALL)
    }

    val filteredActivities = remember(
        activities,
        selectedFilter
    ) {
        when (selectedFilter) {
            ActivityFilter.ALL -> activities

            ActivityFilter.STATUS ->
                activities.filter {
                    it.type == ActivityType.STATUS
                }

            ActivityFilter.NOTE ->
                activities.filter {
                    it.type == ActivityType.NOTE
                }

            ActivityFilter.PHOTO ->
                activities.filter {
                    it.type == ActivityType.PHOTO
                }
        }
    }

    val statusCount = activities.count {
        it.type == ActivityType.STATUS
    }

    val noteCount = activities.count {
        it.type == ActivityType.NOTE
    }

    val photoCount = activities.count {
        it.type == ActivityType.PHOTO
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {

        HistoryHeader(
            onBack = onBack
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(
                start = 16.dp,
                top = 8.dp,
                end = 16.dp,
                bottom = 28.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            item {
                HistoryOverviewCard(
                    total = activities.size,
                    statusCount = statusCount,
                    noteCount = noteCount,
                    photoCount = photoCount
                )
            }

            item {
                HistoryFilterSection(
                    selectedFilter = selectedFilter,
                    onFilterChanged = {
                        selectedFilter = it
                    }
                )
            }

            if (filteredActivities.isEmpty()) {

                item {
                    EmptyActivityState(
                        filtered = selectedFilter != ActivityFilter.ALL
                    )
                }

            } else {

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = when (selectedFilter) {
                                ActivityFilter.ALL ->
                                    "Semua Aktivitas"

                                ActivityFilter.STATUS ->
                                    "Perubahan Status"

                                ActivityFilter.NOTE ->
                                    "Catatan Pekerjaan"

                                ActivityFilter.PHOTO ->
                                    "Bukti Foto"
                            },
                            color = Dark,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(
                            modifier = Modifier.weight(1f)
                        )

                        Box(
                            modifier = Modifier
                                .background(
                                    BlueSoft,
                                    CircleShape
                                )
                                .padding(
                                    horizontal = 10.dp,
                                    vertical = 5.dp
                                )
                        ) {
                            Text(
                                text = filteredActivities.size.toString(),
                                color = Blue,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                items(
                    items = filteredActivities,
                    key = {
                        it.id
                    }
                ) { activity ->

                    ActivityTimelineItem(
                        activity = activity,
                        isLast =
                            activity.id ==
                                    filteredActivities.lastOrNull()?.id
                    )
                }
            }
        }
    }

}

@Composable
private fun HistoryHeader(
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceWhite)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(
                start = 16.dp,
                end = 16.dp,
                top = 10.dp,
                bottom = 16.dp
            )
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(BlueSoft)
                    .clickable(
                        onClick = onBack
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "‹",
                    color = Blue,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Light
                )
            }

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Riwayat Aktivitas",
                    color = Dark,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text = "Jejak aktivitas pekerjaan FieldOps",
                    color = Secondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }

}

@Composable
private fun HistoryOverviewCard(
    total: Int,
    statusCount: Int,
    noteCount: Int,
    photoCount: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(22.dp)
            ),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceWhite
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = Border,
                    shape = RoundedCornerShape(22.dp)
                )
                .padding(18.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(BlueSoft),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✓",
                        color = Blue,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Activity Overview",
                        color = Dark,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(3.dp)
                    )

                    Text(
                        text = "Pantau jejak pekerjaan secara ringkas",
                        color = Secondary,
                        fontSize = 11.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                OverviewMetric(
                    modifier = Modifier.weight(1f),
                    value = total,
                    label = "Total"
                )

                OverviewMetric(
                    modifier = Modifier.weight(1f),
                    value = statusCount,
                    label = "Status"
                )

                OverviewMetric(
                    modifier = Modifier.weight(1f),
                    value = noteCount,
                    label = "Catatan"
                )

                OverviewMetric(
                    modifier = Modifier.weight(1f),
                    value = photoCount,
                    label = "Foto"
                )
            }
        }
    }

}

@Composable
private fun OverviewMetric(
    modifier: Modifier,
    value: Int,
    label: String
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Background)
            .border(
                width = 1.dp,
                color = Border,
                shape = RoundedCornerShape(14.dp)
            )
            .padding(
                vertical = 11.dp,
                horizontal = 4.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = value.toString(),
            color = Dark,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(2.dp)
        )

        Text(
            text = label,
            color = Secondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }

}

@Composable
private fun HistoryFilterSection(
    selectedFilter: ActivityFilter,
    onFilterChanged: (ActivityFilter) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "FILTER AKTIVITAS",
                    color = Secondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.7.sp
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = "Tampilkan aktivitas berdasarkan tipe",
                    color = Dark,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (selectedFilter != ActivityFilter.ALL) {
                Text(
                    text = "Reset",
                    color = Blue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            onFilterChanged(
                                ActivityFilter.ALL
                            )
                        }
                        .padding(
                            horizontal = 9.dp,
                            vertical = 7.dp
                        )
                )
            }
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {

            HistoryFilterChip(
                modifier = Modifier.weight(1f),
                label = "Semua",
                selected =
                    selectedFilter ==
                            ActivityFilter.ALL,
                onClick = {
                    onFilterChanged(
                        ActivityFilter.ALL
                    )
                }
            )

            HistoryFilterChip(
                modifier = Modifier.weight(1f),
                label = "Status",
                selected =
                    selectedFilter ==
                            ActivityFilter.STATUS,
                onClick = {
                    onFilterChanged(
                        ActivityFilter.STATUS
                    )
                }
            )

            HistoryFilterChip(
                modifier = Modifier.weight(1f),
                label = "Catatan",
                selected =
                    selectedFilter ==
                            ActivityFilter.NOTE,
                onClick = {
                    onFilterChanged(
                        ActivityFilter.NOTE
                    )
                }
            )

            HistoryFilterChip(
                modifier = Modifier.weight(1f),
                label = "Foto",
                selected =
                    selectedFilter ==
                            ActivityFilter.PHOTO,
                onClick = {
                    onFilterChanged(
                        ActivityFilter.PHOTO
                    )
                }
            )
        }
    }

}

@Composable
private fun HistoryFilterChip(
    modifier: Modifier,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        modifier = modifier,
        selected = selected,
        onClick = onClick,
        label = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight =
                        if (selected) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Medium
                        },
                    maxLines = 1
                )
            }
        },
        shape = RoundedCornerShape(12.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = BlueSoft,
            selectedLabelColor = Blue,
            containerColor = SurfaceWhite,
            labelColor = Secondary
        ),
        border =
            FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = selected,
                borderColor = Border,
                selectedBorderColor =
                    Blue.copy(alpha = 0.35f)
            )
    )
}

@Composable
private fun ActivityTimelineItem(
    activity: ActivityHistoryItem,
    isLast: Boolean
) {
    val iconBackground = when (activity.type) {
        ActivityType.STATUS -> GreenSoft
        ActivityType.NOTE -> OrangeSoft
        ActivityType.PHOTO -> BlueSoft
    }

    val iconColor = when (activity.type) {
        ActivityType.STATUS -> Green
        ActivityType.NOTE -> Orange
        ActivityType.PHOTO -> Blue
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(iconBackground),
                contentAlignment = Alignment.Center
            ) {
                ActivityIcon(
                    type = activity.type,
                    color = iconColor
                )
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(74.dp)
                        .background(Border)
                )
            }
        }

        Spacer(
            modifier = Modifier.width(10.dp)
        )

        ActivityHistoryCard(
            modifier = Modifier.weight(1f),
            activity = activity
        )
    }

}

@Composable
private fun ActivityHistoryCard(
    modifier: Modifier = Modifier,
    activity: ActivityHistoryItem
) {
    Card(
        modifier = modifier
            .shadow(
                elevation = 1.dp,
                shape = RoundedCornerShape(18.dp)
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceWhite
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = Border,
                    shape = RoundedCornerShape(18.dp)
                )
                .padding(15.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = activity.title,
                        color = Dark,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Box(
                        modifier = Modifier
                            .background(
                                BlueSoft,
                                RoundedCornerShape(7.dp)
                            )
                            .padding(
                                horizontal = 8.dp,
                                vertical = 4.dp
                            )
                    ) {
                        Text(
                            text = activity.workOrderId,
                            color = Blue,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    text = activity.time,
                    color = Secondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
            }

            Spacer(
                modifier = Modifier.height(9.dp)
            )

            Text(
                text = activity.description,
                color = Secondary,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            ActivityTypeLabel(
                type = activity.type
            )
        }
    }

}

@Composable
private fun ActivityTypeLabel(
    type: ActivityType
) {
    val label: String
    val background: Color
    val foreground: Color

    when (type) {
        ActivityType.STATUS -> {
            label = "Perubahan status"
            background = GreenSoft
            foreground = Green
        }

        ActivityType.NOTE -> {
            label = "Catatan pekerjaan"
            background = OrangeSoft
            foreground = Orange
        }

        ActivityType.PHOTO -> {
            label = "Bukti foto"
            background = BlueSoft
            foreground = Blue
        }
    }

    Box(
        modifier = Modifier
            .background(
                background,
                RoundedCornerShape(50.dp)
            )
            .padding(
                horizontal = 9.dp,
                vertical = 5.dp
            )
    ) {
        Text(
            text = label,
            color = foreground,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }

}

@Composable
private fun ActivityIcon(
    type: ActivityType,
    color: Color
) {
    val symbol = when (type) {
        ActivityType.STATUS -> "✓"
        ActivityType.NOTE -> "✎"
        ActivityType.PHOTO -> "▣"
    }

    Text(
        text = symbol,
        fontSize = 19.sp,
        fontWeight = FontWeight.Bold,
        color = color
    )

}

@Composable
private fun EmptyActivityState(
    filtered: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(22.dp)
            ),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceWhite
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = Border,
                    shape = RoundedCornerShape(22.dp)
                )
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(BlueSoft),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "◌",
                    fontSize = 28.sp,
                    color = Blue
                )
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Text(
                text =
                    if (filtered) {
                        "Tidak ada aktivitas"
                    } else {
                        "Belum ada aktivitas"
                    },
                color = Dark,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text =
                    if (filtered) {
                        "Belum ada aktivitas untuk filter yang dipilih."
                    } else {
                        "Aktivitas pekerjaan akan muncul di sini."
                    },
                color = Secondary,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        }
    }

}
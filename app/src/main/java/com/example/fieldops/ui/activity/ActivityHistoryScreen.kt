package com.example.fieldops.ui.activity

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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

private val Blue = Color(0xFF1769FF)
private val Dark = Color(0xFF10213F)
private val Secondary = Color(0xFF718096)
private val Background = Color(0xFFF7FAFE)
private val Green = Color(0xFF13895C)
private val Orange = Color(0xFFB66A00)
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp,
                    vertical = 16.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            TextButton(
                onClick = onBack
            ) {
                Text(
                    text = "‹",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Medium,
                    color = Dark
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Riwayat Aktivitas",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Dark
                )

                Text(
                    text = "Jejak aktivitas pekerjaan",
                    fontSize = 13.sp,
                    color = Secondary
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp
                ),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 3.dp
            )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {

                Text(
                    text = "Ringkasan Aktivitas",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Dark
                )

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    ActivityMetric(
                        modifier = Modifier.weight(1f),
                        value = activities.size,
                        label = "Total",
                        valueColor = Blue
                    )

                    ActivityMetric(
                        modifier = Modifier.weight(1f),
                        value = statusCount,
                        label = "Status",
                        valueColor = Green
                    )

                    ActivityMetric(
                        modifier = Modifier.weight(1f),
                        value = noteCount,
                        label = "Catatan",
                        valueColor = Orange
                    )

                    ActivityMetric(
                        modifier = Modifier.weight(1f),
                        value = photoCount,
                        label = "Foto",
                        valueColor = Blue
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp
                )
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Aktivitas",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Dark,
                    modifier = Modifier.weight(1f)
                )

                if (selectedFilter != ActivityFilter.ALL) {
                    TextButton(
                        onClick = {
                            selectedFilter = ActivityFilter.ALL
                        }
                    ) {
                        Text(
                            text = "Reset",
                            color = Blue,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                ActivityFilterChip(
                    label = "Semua",
                    selected =
                        selectedFilter == ActivityFilter.ALL,
                    onClick = {
                        selectedFilter = ActivityFilter.ALL
                    }
                )

                ActivityFilterChip(
                    label = "Status",
                    selected =
                        selectedFilter == ActivityFilter.STATUS,
                    onClick = {
                        selectedFilter = ActivityFilter.STATUS
                    }
                )

                ActivityFilterChip(
                    label = "Catatan",
                    selected =
                        selectedFilter == ActivityFilter.NOTE,
                    onClick = {
                        selectedFilter = ActivityFilter.NOTE
                    }
                )

                ActivityFilterChip(
                    label = "Foto",
                    selected =
                        selectedFilter == ActivityFilter.PHOTO,
                    onClick = {
                        selectedFilter = ActivityFilter.PHOTO
                    }
                )
            }
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        if (filteredActivities.isEmpty()) {

            EmptyActivityState(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(20.dp),
                filtered = selectedFilter != ActivityFilter.ALL
            )

        } else {

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding =
                    androidx.compose.foundation.layout.PaddingValues(
                        start = 20.dp,
                        top = 8.dp,
                        end = 20.dp,
                        bottom = 28.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                items(
                    items = filteredActivities,
                    key = {
                        it.id
                    }
                ) { activity ->

                    ActivityHistoryCard(
                        activity = activity
                    )
                }
            }
        }
    }

}

@Composable
private fun ActivityMetric(
    modifier: Modifier,
    value: Int,
    label: String,
    valueColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8FAFD)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 12.dp,
                    horizontal = 6.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = value.toString(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = label,
                fontSize = 11.sp,
                color = Secondary
            )
        }
    }

}

@Composable
private fun ActivityFilterChip(
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
                fontSize = 12.sp,
                fontWeight = if (selected) {
                    FontWeight.SemiBold
                } else {
                    FontWeight.Normal
                }
            )
        },
        shape = RoundedCornerShape(12.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Blue.copy(
                alpha = 0.10f
            ),
            selectedLabelColor = Blue,
            containerColor = Color.White,
            labelColor = Secondary
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = Border,
            selectedBorderColor = Blue.copy(
                alpha = 0.30f
            )
        )
    )
}

@Composable
private fun ActivityHistoryCard(
    activity: ActivityHistoryItem
) {
    val iconBackground = when (activity.type) {
        ActivityType.STATUS ->
            Green.copy(alpha = 0.10f)

        ActivityType.NOTE ->
            Orange.copy(alpha = 0.10f)

        ActivityType.PHOTO ->
            Blue.copy(alpha = 0.10f)
    }

    val iconColor = when (activity.type) {
        ActivityType.STATUS ->
            Green

        ActivityType.NOTE ->
            Orange

        ActivityType.PHOTO ->
            Blue
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
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
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(
                        RoundedCornerShape(13.dp)
                    )
                    .background(iconBackground),
                contentAlignment = Alignment.Center
            ) {

                ActivityIcon(
                    type = activity.type,
                    color = iconColor
                )
            }

            Spacer(
                modifier = Modifier.size(12.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
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
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Dark
                        )

                        Spacer(
                            modifier = Modifier.height(3.dp)
                        )

                        Text(
                            text = activity.workOrderId,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Blue
                        )
                    }

                    Text(
                        text = activity.time,
                        fontSize = 11.sp,
                        color = Secondary
                    )
                }

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = activity.description,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = Secondary
                )
            }
        }
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
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = color
    )

}

@Composable
private fun EmptyActivityState(
    modifier: Modifier,
    filtered: Boolean
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "◌",
                    fontSize = 34.sp,
                    color = Secondary
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Text(
                    text = if (filtered) {
                        "Tidak ada aktivitas"
                    } else {
                        "Belum ada aktivitas"
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Dark
                )

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Text(
                    text = if (filtered) {
                        "Belum ada aktivitas untuk filter ini."
                    } else {
                        "Aktivitas pekerjaan akan muncul di sini."
                    },
                    fontSize = 13.sp,
                    color = Secondary
                )
            }
        }
    }

}
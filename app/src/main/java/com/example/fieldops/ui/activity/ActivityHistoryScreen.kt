package com.example.fieldops.ui.activity

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

private val Blue = Color(0xFF1769FF)
private val Dark = Color(0xFF10213F)
private val Secondary = Color(0xFF718096)
private val Background = Color(0xFFF7FAFE)
private val Green = Color(0xFF13895C)
private val Orange = Color(0xFFB66A00)

@Composable
fun ActivityHistoryScreen(
    activities: List<ActivityHistoryItem>,
    onBack: () -> Unit
) {

    BackHandler {
        onBack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .navigationBarsPadding()
    ) {

        ActivityHistoryTopBar(
            onBack = onBack
        )

        if (activities.isEmpty()) {

            EmptyActivityState()

        } else {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = 18.dp,
                        vertical = 16.dp
                    )
            ) {

                ActivitySummaryCard(
                    totalActivities = activities.size
                )

                Spacer(
                    modifier = Modifier.size(16.dp)
                )

                Text(
                    text = "Aktivitas Terbaru",
                    color = Dark,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.size(10.dp)
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),

                    verticalArrangement =
                        Arrangement.spacedBy(10.dp)
                ) {

                    items(
                        items = activities,
                        key = {
                            it.id
                        }
                    ) { activity ->

                        ActivityItem(
                            activity = activity
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActivityHistoryTopBar(
    onBack: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(
                horizontal = 8.dp,
                vertical = 8.dp
            ),

        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = Color.Transparent,
                    shape = CircleShape
                )
                .clickable {
                    onBack()
                },

            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "‹",
                color = Dark,
                fontSize = 38.sp,
                fontWeight = FontWeight.Light
            )
        }

        Spacer(
            modifier = Modifier.width(8.dp)
        )

        Text(
            text = "Riwayat Aktivitas",
            color = Dark,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(0xFFE8EDF5))
    )
}

@Composable
private fun ActivitySummaryCard(
    totalActivities: Int
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        Color(0xFFEAF2FF),
                        RoundedCornerShape(15.dp)
                    ),

                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "↻",
                    color = Blue,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Total Aktivitas",
                    color = Secondary,
                    fontSize = 12.sp
                )

                Spacer(
                    modifier = Modifier.size(2.dp)
                )

                Text(
                    text = totalActivities.toString(),
                    color = Dark,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "Aktivitas",
                color = Secondary,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun ActivityItem(
    activity: ActivityHistoryItem
) {

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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),

            verticalAlignment = Alignment.Top
        ) {

            ActivityIcon(
                type = activity.type
            )

            Spacer(
                modifier = Modifier.width(12.dp)
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
                            color = Dark,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.size(3.dp)
                        )

                        Text(
                            text = activity.workOrderId,
                            color = Blue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Text(
                        text = activity.time,
                        color = Secondary,
                        fontSize = 11.sp
                    )
                }

                Spacer(
                    modifier = Modifier.size(7.dp)
                )

                Text(
                    text = activity.description,
                    color = Secondary,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
            }
        }
    }
}

@Composable
private fun ActivityIcon(
    type: ActivityType
) {

    val background: Color
    val foreground: Color
    val symbol: String

    when (type) {

        ActivityType.STATUS -> {
            background = Color(0xFFEAF2FF)
            foreground = Blue
            symbol = "✓"
        }

        ActivityType.NOTE -> {
            background = Color(0xFFFFF3DE)
            foreground = Orange
            symbol = "✎"
        }

        ActivityType.PHOTO -> {
            background = Color(0xFFE8F8F1)
            foreground = Green
            symbol = "▣"
        }
    }

    Box(
        modifier = Modifier
            .size(42.dp)
            .background(
                background,
                RoundedCornerShape(12.dp)
            ),

        contentAlignment = Alignment.Center
    ) {

        Text(
            text = symbol,
            color = foreground,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun EmptyActivityState() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),

        horizontalAlignment = Alignment.CenterHorizontally,

        verticalArrangement = Arrangement.Center
    ) {

        Box(
            modifier = Modifier
                .size(72.dp)
                .background(
                    Color(0xFFEAF2FF),
                    RoundedCornerShape(22.dp)
                ),

            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "↻",
                color = Blue,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            modifier = Modifier.size(16.dp)
        )

        Text(
            text = "Belum Ada Aktivitas",
            color = Dark,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.size(6.dp)
        )

        Text(
            text = "Aktivitas Work Order akan muncul di sini.",
            color = Secondary,
            fontSize = 13.sp
        )
    }
}
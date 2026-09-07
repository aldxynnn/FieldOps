package com.example.fieldops.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Blue = Color(0xFF1769FF)
private val Background = Color(0xFFF7FAFE)
private val Dark = Color(0xFF10213F)
private val Secondary = Color(0xFF718096)
private val Green = Color(0xFF159A67)
private val Orange = Color(0xFFD98600)
private val Red = Color(0xFFD6344B)

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Background),

        contentPadding = PaddingValues(
            horizontal = 18.dp,
            vertical = 18.dp
        ),

        verticalArrangement =
            Arrangement.spacedBy(14.dp)
    ) {

        item {
            DashboardHeader()
        }

        item {
            TodaySummary()
        }

        item {
            PerformanceCard()
        }

        item {
            SectionTitle(
                title = "Jadwal Hari Ini",
                action = "3 Jadwal"
            )
        }

        item {
            ScheduleItem(
                time = "08:00 - 10:00",
                title = "AC Installation",
                customer = "PT. Maju Bersama",
                status = "In Progress"
            )
        }

        item {
            ScheduleItem(
                time = "10:30 - 12:00",
                title = "Electrical Inspection",
                customer = "CV. Sentosa Jaya",
                status = "Pending"
            )
        }

        item {
            ScheduleItem(
                time = "13:00 - 15:00",
                title = "Maintenance",
                customer = "PT. Mega Karya",
                status = "Upcoming"
            )
        }

        item {
            SectionTitle(
                title = "Work Order Terbaru",
                action = "Lihat Semua"
            )
        }

        item {
            WorkOrderPreview(
                id = "WO-001234",
                title = "AC Installation",
                customer = "PT. Maju Bersama",
                status = "In Progress",
                priority = "Tinggi"
            )
        }

        item {
            WorkOrderPreview(
                id = "WO-001233",
                title = "HVAC Maintenance",
                customer = "CV. Sentosa Jaya",
                status = "In Progress",
                priority = "Sedang"
            )
        }

        item {
            WorkOrderPreview(
                id = "WO-001232",
                title = "Electrical Inspection",
                customer = "PT. Mega Karya",
                status = "Pending",
                priority = "Tinggi"
            )
        }

        item {
            Spacer(
                modifier = Modifier.height(82.dp)
            )
        }
    }

}

@Composable
private fun DashboardHeader() {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Surface(
            modifier = Modifier.size(50.dp),
            shape = CircleShape,
            color = Color(0xFFE6F0FF)
        ) {

            Box(
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "AJ",
                    color = Blue,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(
            modifier = Modifier.width(12.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "Halo, Alex Johnson 👋",
                color = Dark,
                fontSize = 18.sp,
                lineHeight = 23.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = "Field Technician • Aktif",
                color = Secondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }

        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color.White
        ) {

            Box(
                modifier = Modifier.padding(
                    horizontal = 11.dp,
                    vertical = 9.dp
                )
            ) {

                Text(
                    text = "🔔",
                    fontSize = 18.sp
                )

                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(Red)
                        .align(Alignment.TopEnd)
                )
            }
        }
    }

}

@Composable
private fun TodaySummary() {

    Card(
        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(22.dp),

        colors = CardDefaults.cardColors(
            containerColor = Blue
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Ringkasan Hari Ini",
                        color = Color.White,
                        fontSize = 17.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(3.dp)
                    )

                    Text(
                        text = "Aktivitas pekerjaan lapangan",
                        color = Color.White.copy(alpha = 0.78f),
                        fontSize = 12.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White.copy(alpha = 0.14f)
                ) {

                    Text(
                        text = "Hari ini",
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 6.dp
                        ),
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(7.dp)
            ) {

                SummaryNumber(
                    modifier = Modifier.weight(1f),
                    number = "12",
                    label = "Total WO"
                )

                SummaryNumber(
                    modifier = Modifier.weight(1f),
                    number = "5",
                    label = "Dikerjakan"
                )

                SummaryNumber(
                    modifier = Modifier.weight(1f),
                    number = "4",
                    label = "Pending"
                )

                SummaryNumber(
                    modifier = Modifier.weight(1f),
                    number = "3",
                    label = "Selesai"
                )
            }
        }
    }

}

@Composable
private fun SummaryNumber(
    modifier: Modifier,
    number: String,
    label: String
) {

    Surface(
        modifier = modifier,

        shape = RoundedCornerShape(13.dp),

        color = Color.White.copy(
            alpha = 0.14f
        )
    ) {

        Column(
            modifier = Modifier.padding(
                horizontal = 9.dp,
                vertical = 10.dp
            )
        ) {

            Text(
                text = number,
                color = Color.White,
                fontSize = 21.sp,
                lineHeight = 25.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = label,
                color = Color.White.copy(
                    alpha = 0.86f
                ),
                fontSize = 9.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }

}

@Composable
private fun PerformanceCard() {

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
            modifier = Modifier.padding(17.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Performa Hari Ini",
                        color = Dark,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(3.dp)
                    )

                    Text(
                        text = "Progress penyelesaian pekerjaan",
                        color = Secondary,
                        fontSize = 12.sp
                    )
                }

                Text(
                    text = "75%",
                    color = Green,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(13.dp)
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                shape = RoundedCornerShape(50.dp),
                color = Color(0xFFE8EEF7)
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(Green)
                )
            }

            Spacer(
                modifier = Modifier.height(9.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "9 dari 12 pekerjaan selesai / berjalan",
                    color = Secondary,
                    fontSize = 11.sp
                )

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "Target 100%",
                    color = Blue,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }

}

@Composable
private fun SectionTitle(
    title: String,
    action: String
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = title,

            modifier = Modifier.weight(1f),

            color = Dark,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = action,
            color = Blue,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
    }

}

@Composable
private fun ScheduleItem(
    time: String,
    title: String,
    customer: String,
    status: String
) {

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
            modifier = Modifier.padding(15.dp),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFEAF2FF)
            ) {

                Box(
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "⚙",
                        color = Blue,
                        fontSize = 20.sp
                    )
                }
            }

            Spacer(
                modifier = Modifier.width(11.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    color = Dark,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text = customer,
                    color = Secondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = time,
                    color = Blue,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(
                modifier = Modifier.width(7.dp)
            )

            StatusBadge(status)
        }
    }

}

@Composable
private fun WorkOrderPreview(
    id: String,
    title: String,
    customer: String,
    status: String,
    priority: String
) {

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

        Column(
            modifier = Modifier.padding(15.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Surface(
                    modifier = Modifier.size(42.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFEAF2FF)
                ) {

                    Box(
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = "◉",
                            color = Blue,
                            fontSize = 18.sp
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.width(11.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = id,
                        color = Blue,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )

                    Text(
                        text = title,
                        color = Dark,
                        fontSize = 15.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )

                    Text(
                        text = customer,
                        color = Secondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }

                Spacer(
                    modifier = Modifier.width(7.dp)
                )

                StatusBadge(status)
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                PriorityBadge(priority)

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "Lihat detail  ›",
                    color = Blue,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }

}

@Composable
private fun PriorityBadge(
    priority: String
) {

    val isHigh =
        priority.equals(
            "Tinggi",
            ignoreCase = true
        )

    Surface(
        shape = RoundedCornerShape(50.dp),
        color = if (isHigh) {
            Color(0xFFFFECEF)
        } else {
            Color(0xFFFFF3DE)
        }
    ) {

        Row(
            modifier = Modifier.padding(
                horizontal = 9.dp,
                vertical = 5.dp
            ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(
                        if (isHigh) Red else Orange
                    )
            )

            Spacer(
                modifier = Modifier.width(6.dp)
            )

            Text(
                text = "Prioritas $priority",
                color = if (isHigh) {
                    Red
                } else {
                    Orange
                },
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }

}

@Composable
private fun StatusBadge(
    status: String
) {

    val background: Color
    val foreground: Color

    when (status) {

        "Completed" -> {
            background = Color(0xFFE6F8F0)
            foreground = Green
        }

        "In Progress" -> {
            background = Color(0xFFFFF1DA)
            foreground = Orange
        }

        "Pending" -> {
            background = Color(0xFFFFF1DA)
            foreground = Orange
        }

        else -> {
            background = Color(0xFFEAF2FF)
            foreground = Blue
        }
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = background
    ) {

        Text(
            text = status,

            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 6.dp
            ),

            color = foreground,
            fontSize = 11.sp,
            lineHeight = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
    }

}
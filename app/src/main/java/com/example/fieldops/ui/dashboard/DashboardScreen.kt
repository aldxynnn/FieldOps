package com.example.fieldops.ui.dashboard

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Blue = Color(0xFF1769FF)
private val Background = Color(0xFFF7FAFE)
private val Dark = Color(0xFF10213F)
private val Secondary = Color(0xFF718096)

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),

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
            SectionTitle(
                title = "Jadwal Hari Ini",
                action = "Lihat Semua"
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
                action = "3 Baru"
            )
        }

        item {
            WorkOrderPreview(
                id = "WO-001234",
                title = "AC Installation",
                customer = "PT. Maju Bersama",
                status = "In Progress"
            )
        }

        item {
            WorkOrderPreview(
                id = "WO-001233",
                title = "HVAC Maintenance",
                customer = "CV. Sentosa Jaya",
                status = "In Progress"
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
            modifier = Modifier.size(48.dp),
            shape = RoundedCornerShape(50),
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
            modifier = Modifier.size(12.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "Halo, Alex Johnson 👋",
                color = Dark,
                fontSize = 17.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = "Field Technician",
                color = Secondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }

        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color.White
        ) {

            Text(
                text = "🔔",

                modifier = Modifier.padding(
                    horizontal = 10.dp,
                    vertical = 8.dp
                ),

                fontSize = 18.sp
            )
        }
    }
}

@Composable
private fun TodaySummary() {

    Card(
        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(20.dp),

        colors = CardDefaults.cardColors(
            containerColor = Blue
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(17.dp)
        ) {

            Text(
                text = "Ringkasan Hari Ini",
                color = Color.White,
                fontSize = 16.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(12.dp)
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
                    label = "In Progress"
                )

                SummaryNumber(
                    modifier = Modifier.weight(1f),
                    number = "4",
                    label = "Pending"
                )

                SummaryNumber(
                    modifier = Modifier.weight(1f),
                    number = "3",
                    label = "Completed"
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

        shape = RoundedCornerShape(12.dp),

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
                modifier = Modifier.size(11.dp)
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
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text = time,
                    color = Secondary,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }

            Spacer(
                modifier = Modifier.size(7.dp)
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
                modifier = Modifier.size(11.dp)
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
                modifier = Modifier.size(7.dp)
            )

            StatusBadge(status)
        }
    }
}

@Composable
private fun StatusBadge(
    status: String
) {

    val background = when (status) {

        "Completed" ->
            Color(0xFFE6F8F0)

        "In Progress" ->
            Color(0xFFFFF1DA)

        "Pending" ->
            Color(0xFFFFF1DA)

        else ->
            Color(0xFFEAF2FF)
    }

    val foreground = when (status) {

        "Completed" ->
            Color(0xFF159A67)

        "In Progress" ->
            Color(0xFFD98600)

        "Pending" ->
            Color(0xFFD98600)

        else ->
            Blue
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
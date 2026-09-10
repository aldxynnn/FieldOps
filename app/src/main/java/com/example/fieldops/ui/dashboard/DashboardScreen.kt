package com.example.fieldops.ui.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fieldops.data.model.WorkOrder
import com.example.fieldops.data.model.WorkOrderStatus

private val Blue = Color(0xFF1769FF)
private val BlueDark = Color(0xFF0A3EAC)
private val BlueDeep = Color(0xFF082B70)
private val BlueSoft = Color(0xFFEAF2FF)

private val Background = Color(0xFFF4F7FB)
private val SurfaceWhite = Color(0xFFFFFFFF)
private val Dark = Color(0xFF0B1930)
private val DarkSecondary = Color(0xFF344054)
private val Secondary = Color(0xFF667085)
private val Muted = Color(0xFF98A2B3)
private val Border = Color(0xFFE5EAF2)

private val Green = Color(0xFF12A56F)
private val GreenSoft = Color(0xFFE8F8F1)

private val Orange = Color(0xFFDC8A00)
private val OrangeSoft = Color(0xFFFFF4DE)

private val Red = Color(0xFFD92D4A)
private val RedSoft = Color(0xFFFFEDF0)

private val Purple = Color(0xFF7357D9)
private val PurpleSoft = Color(0xFFF0ECFF)


@Composable
fun DashboardScreen(
    workOrders: List<WorkOrder>,
    modifier: Modifier = Modifier
) {

    val total =
        workOrders.size

    val pending =
        workOrders.count {
            it.status == WorkOrderStatus.PENDING
        }

    val accepted =
        workOrders.count {
            it.status == WorkOrderStatus.ACCEPTED
        }

    val inProgress =
        workOrders.count {
            it.status == WorkOrderStatus.IN_PROGRESS
        }

    val completed =
        workOrders.count {
            it.status == WorkOrderStatus.COMPLETED
        }

    val cancelled =
        workOrders.count {
            it.status == WorkOrderStatus.CANCELLED
        }

    val active =
        accepted + inProgress

    val progress =
        if (total > 0) {
            completed.toFloat() / total.toFloat()
        } else {
            0f
        }

    val completionPercentage =
        if (total > 0) {
            (completed * 100) / total
        } else {
            0
        }

    val latestWorkOrders =
        workOrders
            .takeLast(5)
            .reversed()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Background),

        contentPadding = PaddingValues(
            start = 18.dp,
            end = 18.dp,
            top = 14.dp,
            bottom = 108.dp
        ),

        verticalArrangement =
            Arrangement.spacedBy(16.dp)
    ) {

        item {
            DashboardHeader(
                activeCount = active
            )
        }

        item {
            OperationsHero(
                total = total,
                active = active,
                completed = completed,
                percentage = completionPercentage
            )
        }

        item {
            KpiGrid(
                total = total,
                active = active,
                pending = pending,
                completed = completed
            )
        }

        item {
            PerformanceCard(
                completed = completed,
                total = total,
                progress = progress,
                percentage = completionPercentage
            )
        }

        item {
            SectionHeader(
                title = "Status pekerjaan",
                subtitle = "Distribusi seluruh Work Order",
                trailing = "$total total"
            )
        }

        item {
            StatusOverviewCard(
                pending = pending,
                accepted = accepted,
                inProgress = inProgress,
                completed = completed,
                cancelled = cancelled
            )
        }

        item {
            SectionHeader(
                title = "Work Order terbaru",
                subtitle = "Aktivitas pekerjaan terakhir",
                trailing = null
            )
        }

        if (latestWorkOrders.isEmpty()) {

            item {
                EmptyWorkOrderCard()
            }

        } else {

            items(
                count = latestWorkOrders.size,
                key = {
                    latestWorkOrders[it].id
                }
            ) { index ->

                val workOrder =
                    latestWorkOrders[index]

                WorkOrderPreview(
                    workOrder = workOrder
                )
            }
        }
    }
}


@Composable
private fun DashboardHeader(
    activeCount: Int
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 4.dp,
                bottom = 2.dp
            ),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Surface(
            modifier = Modifier
                .size(50.dp)
                .shadow(
                    elevation = 5.dp,
                    shape = CircleShape,
                    clip = false
                ),
            shape = CircleShape,
            color = SurfaceWhite
        ) {

            Box(
                contentAlignment =
                    Alignment.Center
            ) {

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Blue,
                                    BlueDark
                                )
                            )
                        ),
                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text = "AJ",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.width(12.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "Selamat datang kembali",
                color = Secondary,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = "Alex 👋",
                color = Dark,
                fontSize = 20.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.4).sp
            )
        }

        Surface(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = Border,
                    shape = RoundedCornerShape(16.dp)
                ),
            shape = RoundedCornerShape(16.dp),
            color = SurfaceWhite
        ) {

            Row(
                modifier = Modifier.padding(
                    horizontal = 10.dp,
                    vertical = 8.dp
                ),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Green)
                )

                Spacer(
                    modifier = Modifier.width(6.dp)
                )

                Column {

                    Text(
                        text = "$activeCount",
                        color = Dark,
                        fontSize = 13.sp,
                        lineHeight = 15.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text = "aktif",
                        color = Secondary,
                        fontSize = 9.sp,
                        lineHeight = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}


@Composable
private fun OperationsHero(
    total: Int,
    active: Int,
    completed: Int,
    percentage: Int
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(188.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        BlueDeep,
                        BlueDark,
                        Blue
                    )
                )
            )
    ) {

        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {

            drawCircle(
                color = Color.White.copy(alpha = 0.07f),
                radius = size.width * 0.55f,
                center = Offset(
                    x = size.width * 1.02f,
                    y = size.height * 0.02f
                )
            )

            drawCircle(
                color = Color.White.copy(alpha = 0.045f),
                radius = size.width * 0.35f,
                center = Offset(
                    x = size.width * 0.90f,
                    y = size.height * 0.76f
                )
            )

            val route = Path().apply {

                moveTo(
                    size.width * 0.53f,
                    size.height * 0.92f
                )

                cubicTo(
                    size.width * 0.64f,
                    size.height * 0.64f,
                    size.width * 0.72f,
                    size.height * 0.79f,
                    size.width * 0.79f,
                    size.height * 0.48f
                )

                cubicTo(
                    size.width * 0.84f,
                    size.height * 0.28f,
                    size.width * 0.90f,
                    size.height * 0.34f,
                    size.width * 1.02f,
                    size.height * 0.15f
                )
            }

            drawPath(
                path = route,
                color = Color.White.copy(alpha = 0.15f),
                style = Stroke(
                    width = 2.2f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            val nodeOne = Offset(
                size.width * 0.53f,
                size.height * 0.92f
            )

            val nodeTwo = Offset(
                size.width * 0.79f,
                size.height * 0.48f
            )

            drawCircle(
                color = Color.White.copy(alpha = 0.22f),
                radius = 12f,
                center = nodeOne
            )

            drawCircle(
                color = Color.White.copy(alpha = 0.85f),
                radius = 4f,
                center = nodeOne
            )

            drawCircle(
                color = Color.White.copy(alpha = 0.22f),
                radius = 15f,
                center = nodeTwo
            )

            drawCircle(
                color = Color.White,
                radius = 5f,
                center = nodeTwo
            )

            drawCircle(
                color = Color.White.copy(alpha = 0.22f),
                radius = 2f,
                center = nodeTwo
            )

            val barsX = size.width * 0.67f

            drawRoundRect(
                color = Color.White.copy(alpha = 0.14f),
                topLeft = Offset(
                    barsX,
                    size.height * 0.16f
                ),
                size = Size(
                    size.width * 0.18f,
                    5f
                ),
                cornerRadius = CornerRadius(3f, 3f)
            )

            drawRoundRect(
                color = Color.White.copy(alpha = 0.08f),
                topLeft = Offset(
                    barsX,
                    size.height * 0.22f
                ),
                size = Size(
                    size.width * 0.12f,
                    4f
                ),
                cornerRadius = CornerRadius(3f, 3f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 20.dp,
                    vertical = 20.dp
                )
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "OPERATIONS OVERVIEW",
                        color = Color.White.copy(alpha = 0.62f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )

                    Spacer(
                        modifier = Modifier.height(7.dp)
                    )

                    Text(
                        text = "Ringkasan pekerjaan",
                        color = Color.White,
                        fontSize = 21.sp,
                        lineHeight = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.4).sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White.copy(alpha = 0.13f)
                ) {

                    Row(
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 7.dp
                        ),
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(
                                    Color(0xFF7FF0C3)
                                )
                        )

                        Spacer(
                            modifier = Modifier.width(6.dp)
                        )

                        Text(
                            text = "LIVE",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.Bottom
            ) {

                Column {

                    Text(
                        text = total.toString(),
                        color = Color.White,
                        fontSize = 38.sp,
                        lineHeight = 40.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-1.4).sp
                    )

                    Text(
                        text = "total Work Order",
                        color = Color.White.copy(alpha = 0.68f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(
                    modifier = Modifier.width(26.dp)
                )

                HeroMetric(
                    value = active.toString(),
                    label = "Aktif"
                )

                Spacer(
                    modifier = Modifier.width(20.dp)
                )

                HeroMetric(
                    value = completed.toString(),
                    label = "Selesai"
                )

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                Column(
                    horizontalAlignment =
                        Alignment.End
                ) {

                    Text(
                        text = "$percentage%",
                        color = Color.White,
                        fontSize = 22.sp,
                        lineHeight = 25.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text = "completion",
                        color = Color.White.copy(alpha = 0.62f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}


@Composable
private fun HeroMetric(
    value: String,
    label: String
) {

    Column {

        Text(
            text = value,
            color = Color.White,
            fontSize = 19.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = label,
            color = Color.White.copy(alpha = 0.62f),
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium
        )
    }
}


@Composable
private fun KpiGrid(
    total: Int,
    active: Int,
    pending: Int,
    completed: Int
) {

    Column(
        verticalArrangement =
            Arrangement.spacedBy(10.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(10.dp)
        ) {

            KpiCard(
                modifier = Modifier.weight(1f),
                value = total.toString(),
                label = "Total",
                description = "Work Order",
                accent = Blue,
                iconType = 0
            )

            KpiCard(
                modifier = Modifier.weight(1f),
                value = active.toString(),
                label = "Aktif",
                description = "Sedang berjalan",
                accent = Purple,
                iconType = 1
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(10.dp)
        ) {

            KpiCard(
                modifier = Modifier.weight(1f),
                value = pending.toString(),
                label = "Pending",
                description = "Menunggu",
                accent = Orange,
                iconType = 2
            )

            KpiCard(
                modifier = Modifier.weight(1f),
                value = completed.toString(),
                label = "Selesai",
                description = "Completed",
                accent = Green,
                iconType = 3
            )
        }
    }
}


@Composable
private fun KpiCard(
    modifier: Modifier,
    value: String,
    label: String,
    description: String,
    accent: Color,
    iconType: Int
) {

    Surface(
        modifier = modifier
            .height(108.dp)
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(20.dp),
                clip = false
            )
            .border(
                width = 1.dp,
                color = Border,
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        color = SurfaceWhite
    ) {

        Column(
            modifier = Modifier.padding(
                horizontal = 14.dp,
                vertical = 13.dp
            )
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Surface(
                    modifier = Modifier.size(34.dp),
                    shape = RoundedCornerShape(11.dp),
                    color = accent.copy(alpha = 0.10f)
                ) {

                    Box(
                        contentAlignment =
                            Alignment.Center
                    ) {

                        KpiIcon(
                            type = iconType,
                            color = accent,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = value,
                    color = Dark,
                    fontSize = 23.sp,
                    lineHeight = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.6).sp
                )
            }

            Spacer(
                modifier = Modifier.height(9.dp)
            )

            Text(
                text = label,
                color = Dark,
                fontSize = 12.sp,
                lineHeight = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = description,
                color = Secondary,
                fontSize = 9.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}


@Composable
private fun PerformanceCard(
    completed: Int,
    total: Int,
    progress: Float,
    percentage: Int
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(22.dp),
                clip = false
            )
            .border(
                width = 1.dp,
                color = Border,
                shape = RoundedCornerShape(22.dp)
            ),
        shape = RoundedCornerShape(22.dp),
        color = SurfaceWhite
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(13.dp),
                    color = BlueSoft
                ) {

                    Box(
                        contentAlignment =
                            Alignment.Center
                    ) {

                        PerformanceIcon(
                            modifier = Modifier.size(20.dp),
                            color = Blue
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
                        text = "Performa pekerjaan",
                        color = Dark,
                        fontSize = 16.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )

                    Text(
                        text = "Progress penyelesaian",
                        color = Secondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = "$percentage%",
                    color = Green,
                    fontSize = 20.sp,
                    lineHeight = 23.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            LinearProgressIndicator(
                progress = {
                    progress.coerceIn(
                        0f,
                        1f
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(9.dp)
                    .clip(
                        RoundedCornerShape(50.dp)
                    ),
                color = Green,
                trackColor = Color(0xFFE9EEF5)
            )

            Spacer(
                modifier = Modifier.height(11.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text = "$completed dari $total Work Order selesai",
                    color = Secondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GreenSoft
                ) {

                    Text(
                        text = if (total == 0) {
                            "Belum ada data"
                        } else {
                            "Progress"
                        },
                        modifier = Modifier.padding(
                            horizontal = 8.dp,
                            vertical = 4.dp
                        ),
                        color = Green,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}


@Composable
private fun SectionHeader(
    title: String,
    subtitle: String,
    trailing: String?
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment =
            Alignment.Bottom
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = title,
                color = Dark,
                fontSize = 17.sp,
                lineHeight = 21.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.2).sp
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = subtitle,
                color = Secondary,
                fontSize = 10.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        if (trailing != null) {

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = BlueSoft
            ) {

                Text(
                    text = trailing,
                    modifier = Modifier.padding(
                        horizontal = 9.dp,
                        vertical = 5.dp
                    ),
                    color = Blue,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


@Composable
private fun StatusOverviewCard(
    pending: Int,
    accepted: Int,
    inProgress: Int,
    completed: Int,
    cancelled: Int
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(22.dp),
                clip = false
            )
            .border(
                width = 1.dp,
                color = Border,
                shape = RoundedCornerShape(22.dp)
            ),
        shape = RoundedCornerShape(22.dp),
        color = SurfaceWhite
    ) {

        Column(
            modifier = Modifier.padding(17.dp)
        ) {

            StatusRow(
                label = "Pending",
                value = pending,
                color = Orange,
                softColor = OrangeSoft
            )

            StatusDivider()

            StatusRow(
                label = "Diterima",
                value = accepted,
                color = Blue,
                softColor = BlueSoft
            )

            StatusDivider()

            StatusRow(
                label = "Sedang dikerjakan",
                value = inProgress,
                color = Purple,
                softColor = PurpleSoft
            )

            StatusDivider()

            StatusRow(
                label = "Selesai",
                value = completed,
                color = Green,
                softColor = GreenSoft
            )

            StatusDivider()

            StatusRow(
                label = "Dibatalkan",
                value = cancelled,
                color = Red,
                softColor = RedSoft
            )
        }
    }
}


@Composable
private fun StatusDivider() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 34.dp,
                end = 2.dp
            )
            .height(1.dp)
            .background(
                Color(0xFFF0F2F6)
            )
    )
}


@Composable
private fun StatusRow(
    label: String,
    value: Int,
    color: Color,
    softColor: Color
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Surface(
            modifier = Modifier.size(28.dp),
            shape = RoundedCornerShape(9.dp),
            color = softColor
        ) {

            Box(
                contentAlignment =
                    Alignment.Center
            ) {

                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }

        Spacer(
            modifier = Modifier.width(10.dp)
        )

        Text(
            text = label,
            modifier = Modifier.weight(1f),
            color = DarkSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = value.toString(),
            color = Dark,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(
            modifier = Modifier.width(7.dp)
        )

        Box(
            modifier = Modifier
                .width(3.dp)
                .height(18.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color)
        )
    }
}


@Composable
private fun WorkOrderPreview(
    workOrder: WorkOrder
) {

    val statusText =
        statusText(
            workOrder.status
        )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(20.dp),
                clip = false
            )
            .border(
                width = 1.dp,
                color = Border,
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        color = SurfaceWhite
    ) {

        Row(
            modifier = Modifier.padding(
                horizontal = 14.dp,
                vertical = 14.dp
            ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Surface(
                modifier = Modifier.size(45.dp),
                shape = RoundedCornerShape(14.dp),
                color = BlueSoft
            ) {

                Box(
                    contentAlignment =
                        Alignment.Center
                ) {

                    WorkOrderIcon(
                        modifier = Modifier.size(21.dp),
                        color = Blue
                    )
                }
            }

            Spacer(
                modifier = Modifier.width(11.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Text(
                        text = workOrder.id,
                        color = Blue,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(
                        modifier = Modifier.width(7.dp)
                    )

                    Box(
                        modifier = Modifier
                            .size(3.dp)
                            .clip(CircleShape)
                            .background(Muted)
                    )

                    Spacer(
                        modifier = Modifier.width(7.dp)
                    )

                    Text(
                        text = statusText,
                        color = Secondary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = workOrder.title,
                    color = Dark,
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = workOrder.customer,
                    color = Secondary,
                    fontSize = 10.sp,
                    lineHeight = 14.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
            }

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Column(
                horizontalAlignment =
                    Alignment.End
            ) {

                StatusBadge(
                    status = workOrder.status
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                PriorityBadge(
                    priority = workOrder.priority
                )
            }
        }
    }
}


@Composable
private fun PriorityBadge(
    priority: String
) {

    val normalized =
        priority
            .trim()
            .uppercase()

    val isHigh =
        normalized == "HIGH" ||
                normalized == "TINGGI"

    val isLow =
        normalized == "LOW" ||
                normalized == "RENDAH"

    val badgeColor =
        when {
            isHigh -> Red
            isLow -> Green
            else -> Orange
        }

    val background =
        when {
            isHigh -> RedSoft
            isLow -> GreenSoft
            else -> OrangeSoft
        }

    Surface(
        shape = RoundedCornerShape(7.dp),
        color = background
    ) {

        Text(
            text = when {
                isHigh -> "TINGGI"
                isLow -> "RENDAH"
                else -> "SEDANG"
            },
            modifier = Modifier.padding(
                horizontal = 7.dp,
                vertical = 4.dp
            ),
            color = badgeColor,
            fontSize = 8.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.4.sp
        )
    }
}


@Composable
private fun StatusBadge(
    status: WorkOrderStatus
) {

    val background: Color
    val foreground: Color

    when (status) {

        WorkOrderStatus.COMPLETED -> {
            background = GreenSoft
            foreground = Green
        }

        WorkOrderStatus.IN_PROGRESS -> {
            background = PurpleSoft
            foreground = Purple
        }

        WorkOrderStatus.ACCEPTED -> {
            background = BlueSoft
            foreground = Blue
        }

        WorkOrderStatus.PENDING -> {
            background = OrangeSoft
            foreground = Orange
        }

        WorkOrderStatus.CANCELLED -> {
            background = RedSoft
            foreground = Red
        }
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = background
    ) {

        Text(
            text = statusText(status),
            modifier = Modifier.padding(
                horizontal = 7.dp,
                vertical = 4.dp
            ),
            color = foreground,
            fontSize = 8.sp,
            lineHeight = 11.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}


private fun statusText(
    status: WorkOrderStatus
): String {

    return when (status) {

        WorkOrderStatus.PENDING ->
            "Pending"

        WorkOrderStatus.ACCEPTED ->
            "Diterima"

        WorkOrderStatus.IN_PROGRESS ->
            "Dikerjakan"

        WorkOrderStatus.COMPLETED ->
            "Selesai"

        WorkOrderStatus.CANCELLED ->
            "Dibatalkan"
    }
}


@Composable
private fun EmptyWorkOrderCard() {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(22.dp),
                clip = false
            )
            .border(
                width = 1.dp,
                color = Border,
                shape = RoundedCornerShape(22.dp)
            ),
        shape = RoundedCornerShape(22.dp),
        color = SurfaceWhite
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 24.dp,
                    vertical = 32.dp
                ),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Surface(
                modifier = Modifier.size(58.dp),
                shape = RoundedCornerShape(18.dp),
                color = BlueSoft
            ) {

                Box(
                    contentAlignment =
                        Alignment.Center
                ) {

                    CompletedIcon(
                        modifier = Modifier.size(25.dp),
                        color = Blue
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(13.dp)
            )

            Text(
                text = "Belum ada Work Order",
                color = Dark,
                fontSize = 15.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "Work Order akan tampil di dashboard.",
                color = Secondary,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}


@Composable
private fun KpiIcon(
    type: Int,
    color: Color,
    modifier: Modifier
) {

    Canvas(
        modifier = modifier
    ) {

        val stroke = 1.8f

        when (type) {

            0 -> {

                drawRoundRect(
                    color = color,
                    topLeft = Offset(
                        size.width * 0.16f,
                        size.height * 0.20f
                    ),
                    size = Size(
                        size.width * 0.68f,
                        size.height * 0.60f
                    ),
                    cornerRadius = CornerRadius(
                        3f,
                        3f
                    ),
                    style = Stroke(
                        width = stroke
                    )
                )

                drawLine(
                    color = color,
                    start = Offset(
                        size.width * 0.31f,
                        size.height * 0.42f
                    ),
                    end = Offset(
                        size.width * 0.69f,
                        size.height * 0.42f
                    ),
                    strokeWidth = stroke,
                    cap = StrokeCap.Round
                )

                drawLine(
                    color = color,
                    start = Offset(
                        size.width * 0.31f,
                        size.height * 0.61f
                    ),
                    end = Offset(
                        size.width * 0.57f,
                        size.height * 0.61f
                    ),
                    strokeWidth = stroke,
                    cap = StrokeCap.Round
                )
            }

            1 -> {

                drawCircle(
                    color = color,
                    radius = size.minDimension * 0.30f,
                    center = center,
                    style = Stroke(
                        width = stroke
                    )
                )

                drawArc(
                    color = color,
                    startAngle = -55f,
                    sweepAngle = 85f,
                    useCenter = false,
                    topLeft = Offset(
                        size.width * 0.12f,
                        size.height * 0.12f
                    ),
                    size = Size(
                        size.width * 0.76f,
                        size.height * 0.76f
                    ),
                    style = Stroke(
                        width = stroke + 1f,
                        cap = StrokeCap.Round
                    )
                )
            }

            2 -> {

                drawRoundRect(
                    color = color,
                    topLeft = Offset(
                        size.width * 0.20f,
                        size.height * 0.18f
                    ),
                    size = Size(
                        size.width * 0.60f,
                        size.height * 0.64f
                    ),
                    cornerRadius = CornerRadius(
                        4f,
                        4f
                    ),
                    style = Stroke(
                        width = stroke
                    )
                )

                drawLine(
                    color = color,
                    start = Offset(
                        size.width * 0.50f,
                        size.height * 0.33f
                    ),
                    end = Offset(
                        size.width * 0.50f,
                        size.height * 0.57f
                    ),
                    strokeWidth = stroke,
                    cap = StrokeCap.Round
                )

                drawCircle(
                    color = color,
                    radius = 1.5f,
                    center = Offset(
                        size.width * 0.50f,
                        size.height * 0.68f
                    )
                )
            }

            else -> {

                drawCircle(
                    color = color,
                    radius = size.minDimension * 0.34f,
                    center = center,
                    style = Stroke(
                        width = stroke
                    )
                )

                drawLine(
                    color = color,
                    start = Offset(
                        size.width * 0.30f,
                        size.height * 0.50f
                    ),
                    end = Offset(
                        size.width * 0.45f,
                        size.height * 0.65f
                    ),
                    strokeWidth = stroke,
                    cap = StrokeCap.Round
                )

                drawLine(
                    color = color,
                    start = Offset(
                        size.width * 0.45f,
                        size.height * 0.65f
                    ),
                    end = Offset(
                        size.width * 0.72f,
                        size.height * 0.34f
                    ),
                    strokeWidth = stroke,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}


@Composable
private fun PerformanceIcon(
    modifier: Modifier,
    color: Color
) {

    Canvas(
        modifier = modifier
    ) {

        val stroke = 2f

        drawLine(
            color = color,
            start = Offset(
                size.width * 0.18f,
                size.height * 0.78f
            ),
            end = Offset(
                size.width * 0.18f,
                size.height * 0.48f
            ),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )

        drawLine(
            color = color,
            start = Offset(
                size.width * 0.42f,
                size.height * 0.78f
            ),
            end = Offset(
                size.width * 0.42f,
                size.height * 0.32f
            ),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )

        drawLine(
            color = color,
            start = Offset(
                size.width * 0.66f,
                size.height * 0.78f
            ),
            end = Offset(
                size.width * 0.66f,
                size.height * 0.56f
            ),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )

        drawLine(
            color = color,
            start = Offset(
                size.width * 0.90f,
                size.height * 0.78f
            ),
            end = Offset(
                size.width * 0.90f,
                size.height * 0.20f
            ),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
    }
}


@Composable
private fun WorkOrderIcon(
    modifier: Modifier,
    color: Color
) {

    Canvas(
        modifier = modifier
    ) {

        val stroke = 1.9f

        drawRoundRect(
            color = color,
            topLeft = Offset(
                size.width * 0.18f,
                size.height * 0.15f
            ),
            size = Size(
                size.width * 0.64f,
                size.height * 0.70f
            ),
            cornerRadius = CornerRadius(
                4f,
                4f
            ),
            style = Stroke(
                width = stroke
            )
        )

        drawLine(
            color = color,
            start = Offset(
                size.width * 0.32f,
                size.height * 0.36f
            ),
            end = Offset(
                size.width * 0.68f,
                size.height * 0.36f
            ),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )

        drawLine(
            color = color,
            start = Offset(
                size.width * 0.32f,
                size.height * 0.52f
            ),
            end = Offset(
                size.width * 0.60f,
                size.height * 0.52f
            ),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )

        drawLine(
            color = color,
            start = Offset(
                size.width * 0.32f,
                size.height * 0.68f
            ),
            end = Offset(
                size.width * 0.52f,
                size.height * 0.68f
            ),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
    }
}


@Composable
private fun CompletedIcon(
    modifier: Modifier,
    color: Color
) {

    Canvas(
        modifier = modifier
    ) {

        val stroke = 2.2f

        drawCircle(
            color = color,
            radius = size.minDimension * 0.37f,
            center = center,
            style = Stroke(
                width = stroke
            )
        )

        drawLine(
            color = color,
            start = Offset(
                size.width * 0.31f,
                size.height * 0.51f
            ),
            end = Offset(
                size.width * 0.45f,
                size.height * 0.65f
            ),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )

        drawLine(
            color = color,
            start = Offset(
                size.width * 0.45f,
                size.height * 0.65f
            ),
            end = Offset(
                size.width * 0.71f,
                size.height * 0.35f
            ),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
    }
}
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fieldops.data.model.WorkOrder
import com.example.fieldops.data.model.WorkOrderStatus
import com.example.fieldops.ui.components.StatusPill
import com.example.fieldops.ui.theme.FieldOpsBackground
import com.example.fieldops.ui.theme.FieldOpsSuccess
import com.example.fieldops.ui.theme.FieldOpsWarning

private val FieldBlue = Color(0xFF1677FF)
private val FieldBlueLight = Color(0xFF4B9AFF)
private val FieldBlueSoft = Color(0xFFEAF3FF)
private val FieldBluePale = Color(0xFFF4F8FF)

private val TextPrimary = Color(0xFF172033)
private val TextSecondary = Color(0xFF687386)
private val TextMuted = Color(0xFF98A2B3)

private val SurfaceWhite = Color(0xFFFFFFFF)
private val SurfaceSoft = Color(0xFFF8FAFD)
private val Border = Color(0xFFE7EBF2)

private val GreenSoft = Color(0xFFEAF8F1)
private val OrangeSoft = Color(0xFFFFF5E6)
private val PurpleSoft = Color(0xFFF2EEFF)

private val Purple = Color(0xFF7357D8)


@Composable
fun RoleDashboardScreen(
    workOrders: List<WorkOrder>,
    userName: String,
    role: String,
    isOnline: Boolean,
    onManage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val active =
        workOrders.count {
            it.status == WorkOrderStatus.ACCEPTED ||
                    it.status == WorkOrderStatus.IN_PROGRESS
        }

    val pending =
        workOrders.count {
            it.status == WorkOrderStatus.PENDING
        }

    val completed =
        workOrders.count {
            it.status == WorkOrderStatus.COMPLETED
        }

    val high =
        workOrders.count {
            it.priority.equals("HIGH", true)
        }

    val roleLabel =
        when (role.uppercase()) {
            "TECHNICIAN" -> "Teknisi Lapangan"
            "DISPATCHER" -> "Dispatcher"
            "MANAGER" -> "Manager Operasional"
            "ADMIN" -> "Administrator"
            else -> "Field Operations"
        }

    val initials =
        userName
            .trim()
            .split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") {
                it.first().uppercase()
            }
            .ifBlank {
                "FO"
            }

    val completionProgress =
        if (workOrders.isEmpty()) {
            0f
        } else {
            completed.toFloat() / workOrders.size.toFloat()
        }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(FieldOpsBackground),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 14.dp,
            bottom = 108.dp
        ),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        item {
            DashboardHeader(
                initials = initials,
                userName = userName,
                roleLabel = roleLabel,
                isOnline = isOnline
            )
        }

        item {
            OperationsOverview(
                role = role,
                active = active,
                pending = pending,
                completed = completed,
                completionProgress = completionProgress
            )
        }

        item {
            DashboardSectionHeader(
                title = "Ringkasan Operasional",
                subtitle = "Kondisi pekerjaan saat ini"
            )
        }

        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    OperationalMetricCard(
                        modifier = Modifier.weight(1f),
                        value = active.toString(),
                        label = "Pekerjaan Aktif",
                        icon = "A",
                        iconBackground = FieldBlueSoft,
                        iconColor = FieldBlue
                    )

                    OperationalMetricCard(
                        modifier = Modifier.weight(1f),
                        value = pending.toString(),
                        label = "Menunggu",
                        icon = "P",
                        iconBackground = OrangeSoft,
                        iconColor = FieldOpsWarning
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    OperationalMetricCard(
                        modifier = Modifier.weight(1f),
                        value = completed.toString(),
                        label = "Selesai",
                        icon = "✓",
                        iconBackground = GreenSoft,
                        iconColor = FieldOpsSuccess
                    )

                    OperationalMetricCard(
                        modifier = Modifier.weight(1f),
                        value = high.toString(),
                        label = "Prioritas Tinggi",
                        icon = "!",
                        iconBackground = PurpleSoft,
                        iconColor = Purple
                    )
                }
            }
        }

        if (
            role.equals("ADMIN", true) ||
            role.equals("DISPATCHER", true)
        ) {
            item {
                ManagementCard(
                    onClick = onManage
                )
            }
        }

        item {
            DashboardSectionHeader(
                title = "Work Order Terbaru",
                subtitle = "Pekerjaan yang paling relevan saat ini"
            )
        }

        if (workOrders.isEmpty()) {

            item {
                EmptyWorkOrderState(
                    role = role
                )
            }

        } else {

            items(
                workOrders.take(5),
                key = {
                    it.id
                }
            ) { workOrder ->

                WorkOrderActivityCard(
                    workOrder = workOrder
                )
            }
        }
    }
}


/*
 * ================================================================
 * DASHBOARD HEADER
 * ================================================================
 */

@Composable
private fun DashboardHeader(
    initials: String,
    userName: String,
    roleLabel: String,
    isOnline: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 2.dp,
                vertical = 2.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(52.dp)
                .shadow(
                    elevation = 7.dp,
                    shape = CircleShape
                )
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            FieldBlueLight,
                            FieldBlue
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = initials,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(
            modifier = Modifier.width(12.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "Selamat datang",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = userName.ifBlank {
                    "Pengguna"
                },
                color = TextPrimary,
                fontSize = 19.sp,
                lineHeight = 23.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(
                modifier = Modifier.height(1.dp)
            )

            Text(
                text = roleLabel,
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        OnlineIndicator(
            isOnline = isOnline
        )
    }
}


/*
 * ================================================================
 * ONLINE INDICATOR
 * ================================================================
 */

@Composable
private fun OnlineIndicator(
    isOnline: Boolean
) {
    val background =
        if (isOnline) {
            GreenSoft
        } else {
            OrangeSoft
        }

    val indicator =
        if (isOnline) {
            FieldOpsSuccess
        } else {
            FieldOpsWarning
        }

    Surface(
        shape = RoundedCornerShape(50.dp),
        color = background
    ) {

        Row(
            modifier = Modifier.padding(
                horizontal = 11.dp,
                vertical = 7.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(indicator)
            )

            Spacer(
                modifier = Modifier.width(6.dp)
            )

            Text(
                text = if (isOnline) {
                    "Online"
                } else {
                    "Offline"
                },
                color = indicator,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


/*
 * ================================================================
 * OPERATIONS OVERVIEW
 * ================================================================
 */

@Composable
private fun OperationsOverview(
    role: String,
    active: Int,
    pending: Int,
    completed: Int,
    completionProgress: Float
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(278.dp)
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(28.dp)
            )
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF5AA3FF),
                        Color(0xFF3388F5)
                    ),
                    start = Offset(
                        0f,
                        0f
                    ),
                    end = Offset(
                        900f,
                        650f
                    )
                )
            )
    ) {

        CleanHeroArtwork(
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 21.dp,
                    vertical = 20.dp
                )
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Surface(
                        shape = RoundedCornerShape(50.dp),
                        color = Color.White.copy(
                            alpha = 0.17f
                        )
                    ) {

                        Text(
                            text =
                                if (
                                    role.equals(
                                        "TECHNICIAN",
                                        true
                                    )
                                ) {
                                    "MY WORK"
                                } else {
                                    "OPERATIONS"
                                },
                            modifier = Modifier.padding(
                                horizontal = 11.dp,
                                vertical = 5.dp
                            ),
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.1.sp
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Text(
                        text =
                            if (
                                role.equals(
                                    "TECHNICIAN",
                                    true
                                )
                            ) {
                                "Pekerjaan Saya"
                            } else {
                                "Ringkasan Operasional"
                            },
                        color = Color.White,
                        fontSize = 23.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.45).sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text =
                            if (
                                role.equals(
                                    "TECHNICIAN",
                                    true
                                )
                            ) {
                                "Pantau pekerjaan yang sedang berjalan."
                            } else {
                                "Pantau kondisi operasional secara menyeluruh."
                            },
                        color = Color.White.copy(
                            alpha = 0.82f
                        ),
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(
                    modifier = Modifier.width(10.dp)
                )

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White.copy(
                        alpha = 0.15f
                    )
                ) {

                    Text(
                        text = "LIVE",
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 7.dp
                        ),
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Progress pekerjaan",
                        color = Color.White.copy(
                            alpha = 0.80f
                        ),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(
                        modifier = Modifier.height(7.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(
                                RoundedCornerShape(
                                    50.dp
                                )
                            )
                            .background(
                                Color.White.copy(
                                    alpha = 0.20f
                                )
                            )
                    ) {

                        Box(
                            modifier = Modifier
                                .fillMaxWidth(
                                    completionProgress.coerceIn(
                                        0f,
                                        1f
                                    )
                                )
                                .height(6.dp)
                                .clip(
                                    RoundedCornerShape(
                                        50.dp
                                    )
                                )
                                .background(
                                    Color.White
                                )
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.width(14.dp)
                )

                Text(
                    text = "${(completionProgress * 100).toInt()}%",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                PremiumHeroStat(
                    modifier = Modifier.weight(1f),
                    value = active.toString(),
                    label = "Aktif"
                )

                PremiumHeroStat(
                    modifier = Modifier.weight(1f),
                    value = pending.toString(),
                    label = "Pending"
                )

                PremiumHeroStat(
                    modifier = Modifier.weight(1f),
                    value = completed.toString(),
                    label = "Selesai"
                )
            }
        }
    }
}


/*
 * ================================================================
 * HERO ARTWORK
 * ================================================================
 */

@Composable
private fun CleanHeroArtwork(
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
    ) {

        drawCircle(
            color = Color.White.copy(
                alpha = 0.065f
            ),
            radius = size.width * 0.35f,
            center = Offset(
                size.width * 0.97f,
                size.height * 0.06f
            )
        )

        drawCircle(
            color = Color.White.copy(
                alpha = 0.035f
            ),
            radius = size.width * 0.22f,
            center = Offset(
                size.width * 0.90f,
                size.height * 0.15f
            )
        )

        val gridColor =
            Color.White.copy(
                alpha = 0.045f
            )

        val horizontalStep =
            size.height * 0.115f

        var y =
            size.height * 0.43f

        while (y < size.height) {

            drawLine(
                color = gridColor,
                start = Offset(
                    0f,
                    y
                ),
                end = Offset(
                    size.width,
                    y
                ),
                strokeWidth = 1f
            )

            y += horizontalStep
        }

        val verticalStep =
            size.width * 0.17f

        var x =
            -size.width * 0.20f

        while (x < size.width * 1.15f) {

            drawLine(
                color = gridColor,
                start = Offset(
                    x,
                    size.height * 0.43f
                ),
                end = Offset(
                    x + size.width * 0.10f,
                    size.height
                ),
                strokeWidth = 1f
            )

            x += verticalStep
        }

        val route =
            Path().apply {

                moveTo(
                    size.width * 0.02f,
                    size.height * 0.87f
                )

                cubicTo(
                    size.width * 0.20f,
                    size.height * 0.71f,
                    size.width * 0.34f,
                    size.height * 0.90f,
                    size.width * 0.49f,
                    size.height * 0.73f
                )

                cubicTo(
                    size.width * 0.62f,
                    size.height * 0.58f,
                    size.width * 0.72f,
                    size.height * 0.77f,
                    size.width * 0.84f,
                    size.height * 0.58f
                )

                cubicTo(
                    size.width * 0.92f,
                    size.height * 0.47f,
                    size.width * 0.98f,
                    size.height * 0.51f,
                    size.width * 1.04f,
                    size.height * 0.40f
                )
            }

        drawPath(
            path = route,
            color = Color.White.copy(
                alpha = 0.13f
            ),
            style = Stroke(
                width = 2f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        val points =
            listOf(
                Offset(
                    size.width * 0.02f,
                    size.height * 0.87f
                ),
                Offset(
                    size.width * 0.49f,
                    size.height * 0.73f
                ),
                Offset(
                    size.width * 0.84f,
                    size.height * 0.58f
                )
            )

        points.forEach { point ->

            drawCircle(
                color = Color.White.copy(
                    alpha = 0.25f
                ),
                radius = 3.5f,
                center = point
            )
        }

        drawCircle(
            color = Color.White.copy(
                alpha = 0.95f
            ),
            radius = 5f,
            center = Offset(
                size.width * 1.04f,
                size.height * 0.40f
            )
        )
    }
}


/*
 * ================================================================
 * PREMIUM HERO STAT
 * ================================================================
 */

@Composable
private fun PremiumHeroStat(
    modifier: Modifier,
    value: String,
    label: String
) {
    Surface(
        modifier = modifier
            .height(57.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(
            alpha = 0.15f
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 8.dp,
                    vertical = 8.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = value,
                color = Color.White,
                fontSize = 19.sp,
                lineHeight = 21.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = label,
                color = Color.White.copy(
                    alpha = 0.82f
                ),
                fontSize = 10.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Clip
            )
        }
    }
}


/*
 * ================================================================
 * SECTION HEADER
 * ================================================================
 */

@Composable
private fun DashboardSectionHeader(
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 2.dp
            )
    ) {

        Text(
            text = title,
            color = TextPrimary,
            fontSize = 18.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.35).sp
        )

        Spacer(
            modifier = Modifier.height(3.dp)
        )

        Text(
            text = subtitle,
            color = TextSecondary,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}


/*
 * ================================================================
 * OPERATIONAL METRIC CARD
 * ================================================================
 */

@Composable
private fun OperationalMetricCard(
    modifier: Modifier,
    value: String,
    label: String,
    icon: String,
    iconBackground: Color,
    iconColor: Color
) {
    Surface(
        modifier = modifier
            .height(112.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(21.dp)
            )
            .border(
                width = 1.dp,
                color = Border,
                shape = RoundedCornerShape(21.dp)
            ),
        shape = RoundedCornerShape(21.dp),
        color = SurfaceWhite
    ) {

        Column(
            modifier = Modifier.padding(
                horizontal = 15.dp,
                vertical = 14.dp
            )
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(
                            RoundedCornerShape(
                                12.dp
                            )
                        )
                        .background(
                            iconBackground
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = icon,
                        color = iconColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = value,
                    color = TextPrimary,
                    fontSize = 25.sp,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = label,
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


/*
 * ================================================================
 * MANAGEMENT CARD
 * ================================================================
 */

@Composable
private fun ManagementCard(
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 5.dp,
                shape = RoundedCornerShape(22.dp)
            )
            .border(
                width = 1.dp,
                color = Border,
                shape = RoundedCornerShape(22.dp)
            ),
        shape = RoundedCornerShape(22.dp),
        color = SurfaceWhite
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 14.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(
                        RoundedCornerShape(
                            14.dp
                        )
                    )
                    .background(
                        FieldBlueSoft
                    ),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "≡",
                    color = FieldBlue,
                    fontSize = 24.sp,
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
                    text = "Kelola Operasi",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text = "Work Order, pelanggan dan tim",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Surface(
                modifier = Modifier.clip(
                    RoundedCornerShape(
                        13.dp
                    )
                ),
                onClick = onClick,
                shape = RoundedCornerShape(13.dp),
                color = FieldBlue
            ) {

                Text(
                    text = "Buka",
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 10.dp
                    ),
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


/*
 * ================================================================
 * WORK ORDER ACTIVITY CARD
 * ================================================================
 */

@Composable
private fun WorkOrderActivityCard(
    workOrder: WorkOrder
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(22.dp)
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
            modifier = Modifier.padding(
                horizontal = 15.dp,
                vertical = 15.dp
            )
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(
                            RoundedCornerShape(
                                14.dp
                            )
                        )
                        .background(
                            FieldBluePale
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "WO",
                        color = FieldBlue,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(
                    modifier = Modifier.width(11.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = workOrder.id,
                        color = FieldBlue,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.35.sp
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = workOrder.title,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(
                        modifier = Modifier.height(3.dp)
                    )

                    Text(
                        text = workOrder.customer,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(
                    modifier = Modifier.width(7.dp)
                )

                StatusPill(
                    status = workOrder.status,
                    compact = true
                )
            }

            Spacer(
                modifier = Modifier.height(13.dp)
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(13.dp),
                color = SurfaceSoft
            ) {

                Row(
                    modifier = Modifier.padding(
                        horizontal = 11.dp,
                        vertical = 9.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = workOrder.date,
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "  •  ",
                        color = TextMuted,
                        fontSize = 10.sp
                    )

                    Text(
                        text = workOrder.time,
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(
                                Color(0xFFC4CBD6)
                            )
                    )

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    Text(
                        text = workOrder.location,
                        modifier = Modifier.weight(1f),
                        color = TextSecondary,
                        fontSize = 10.sp,
                        maxLines = 1,
                        fontWeight = FontWeight.Medium,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}


/*
 * ================================================================
 * EMPTY STATE
 * ================================================================
 */

@Composable
private fun EmptyWorkOrderState(
    role: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(24.dp)
            )
            .border(
                width = 1.dp,
                color = Border,
                shape = RoundedCornerShape(24.dp)
            ),
        shape = RoundedCornerShape(24.dp),
        color = SurfaceWhite
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 24.dp,
                    vertical = 32.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(62.dp)
                    .clip(CircleShape)
                    .background(
                        FieldBlueSoft
                    ),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "✓",
                    color = FieldBlue,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Text(
                text = "Belum ada Work Order",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text =
                    if (
                        role.equals(
                            "TECHNICIAN",
                            true
                        )
                    ) {
                        "Pekerjaan yang ditugaskan kepada Anda akan muncul di sini."
                    } else {
                        "Buat Work Order pertama melalui Kelola Operasi."
                    },
                modifier = Modifier.padding(
                    horizontal = 8.dp
                ),
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 17.sp
            )
        }
    }
}
package com.example.fieldops.ui.notification

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class FieldOpsNotification(
    val id: String,
    val title: String,
    val description: String,
    val time: String,
    val type: NotificationType,
    val isRead: Boolean = false
)

enum class NotificationType {
    WORK_ORDER,
    STATUS,
    SYNC,
    SYSTEM
}

@Composable
fun NotificationScreen(
    modifier: Modifier = Modifier,
    notifications: List<FieldOpsNotification>,
    onNotificationClick: (FieldOpsNotification) -> Unit,
    onMarkAllRead: () -> Unit
) {
    val unreadCount =
        notifications.count { !it.isRead }

    Scaffold(
        modifier = modifier,
        containerColor = Color(0xFFF7FAFE)
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 18.dp)
        ) {

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Notifikasi",
                        color = Color(0xFF10213F),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text =
                            if (unreadCount > 0) {
                                "$unreadCount notifikasi belum dibaca"
                            } else {
                                "Semua notifikasi sudah dibaca"
                            },
                        color = Color(0xFF718096),
                        fontSize = 14.sp
                    )
                }

                if (unreadCount > 0) {
                    TextButton(
                        onClick = onMarkAllRead
                    ) {
                        Text(
                            text = "Tandai semua",
                            color = Color(0xFF1769FF),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            if (notifications.isEmpty()) {
                NotificationEmptyState(
                    modifier = Modifier
                        .fillMaxSize()
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        items = notifications,
                        key = { it.id }
                    ) { notification ->

                        NotificationCard(
                            notification = notification,
                            onClick = {
                                onNotificationClick(
                                    notification
                                )
                            }
                        )
                    }

                    item {
                        Spacer(
                            modifier = Modifier.height(18.dp)
                        )
                    }
                }
            }
        }
    }

}

@Composable
private fun NotificationCard(
    notification: FieldOpsNotification,
    onClick: () -> Unit
) {
    val backgroundColor =
        if (notification.isRead) {
            Color.White
        } else {
            Color(0xFFEFF5FF)
        }

    val iconBackground =
        when (notification.type) {
            NotificationType.WORK_ORDER ->
                Color(0xFFE7F0FF)

            NotificationType.STATUS ->
                Color(0xFFE9F8F0)

            NotificationType.SYNC ->
                Color(0xFFFFF3DE)

            NotificationType.SYSTEM ->
                Color(0xFFF0ECFF)
        }

    val iconText =
        when (notification.type) {
            NotificationType.WORK_ORDER -> "WO"
            NotificationType.STATUS -> "✓"
            NotificationType.SYNC -> "↻"
            NotificationType.SYSTEM -> "!"
        }

    val iconColor =
        when (notification.type) {
            NotificationType.WORK_ORDER ->
                Color(0xFF1769FF)

            NotificationType.STATUS ->
                Color(0xFF159A67)

            NotificationType.SYNC ->
                Color(0xFFD98600)

            NotificationType.SYSTEM ->
                Color(0xFF7654C8)
        }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
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

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(iconBackground),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = iconText,
                    color = iconColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
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
                    Text(
                        text = notification.title,
                        modifier = Modifier.weight(1f),
                        color = Color(0xFF10213F),
                        fontSize = 15.sp,
                        fontWeight =
                            if (notification.isRead) {
                                FontWeight.SemiBold
                            } else {
                                FontWeight.Bold
                            }
                    )

                    Spacer(
                        modifier = Modifier.size(8.dp)
                    )

                    Text(
                        text = notification.time,
                        color = Color(0xFF8A96A8),
                        fontSize = 11.sp
                    )
                }

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Text(
                    text = notification.description,
                    color = Color(0xFF667085),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                if (!notification.isRead) {
                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(
                                    Color(0xFF1769FF)
                                )
                        )

                        Spacer(
                            modifier = Modifier.size(6.dp)
                        )

                        Text(
                            text = "Belum dibaca",
                            color = Color(0xFF1769FF),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

}

@Composable
private fun NotificationEmptyState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(30.dp)
        ) {

            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEAF2FF)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✓",
                    color = Color(0xFF1769FF),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Text(
                text = "Tidak ada notifikasi",
                color = Color(0xFF10213F),
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text =
                    "Aktivitas Work Order dan informasi terbaru akan muncul di sini.",
                color = Color(0xFF718096),
                fontSize = 13.sp,
                lineHeight = 19.sp
            )
        }
    }

}
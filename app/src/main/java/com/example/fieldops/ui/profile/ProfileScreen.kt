package com.example.fieldops.ui.profile

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FieldOpsProfileScreen(
    onActivityHistoryClick: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var notificationEnabled by remember { mutableStateOf(true) }
    var autoSyncEnabled by remember { mutableStateOf(true) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F8FC))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        Text(
            text = "Profil",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF172033)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Kelola akun dan preferensi aplikasi",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF7A8496)
        )

        Spacer(modifier = Modifier.height(20.dp))

        ProfileHeaderCard()

        Spacer(modifier = Modifier.height(18.dp))

        SettingsCard(
            notificationEnabled = notificationEnabled,
            onNotificationChanged = { notificationEnabled = it },
            autoSyncEnabled = autoSyncEnabled,
            onAutoSyncChanged = { autoSyncEnabled = it },
            onActivityHistoryClick = onActivityHistoryClick
        )

        Spacer(modifier = Modifier.height(18.dp))

        AppInfoCard()

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Color.White),
            horizontalArrangement = Arrangement.Center
        ) {
            TextButton(
                onClick = { showLogoutDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Keluar dari Akun",
                    color = Color(0xFFD64545),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "FieldOps",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF9AA3B2)
        )

        Text(
            text = "Versi 1.0.0",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            color = Color(0xFFB0B7C3)
        )

        Spacer(modifier = Modifier.height(20.dp))
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = {
                showLogoutDialog = false
            },
            title = {
                Text(
                    text = "Keluar dari akun?"
                )
            },
            text = {
                Text(
                    text = "Anda akan keluar dari sesi FieldOps pada perangkat ini."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }
                ) {
                    Text(
                        text = "Keluar",
                        color = Color(0xFFD64545),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                    }
                ) {
                    Text("Batal")
                }
            }
        )
    }

}

@Composable
private fun ProfileHeaderCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(Color(0xFFE8F0FF)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "FA",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF246BCE)
            )
        }

        Spacer(modifier = Modifier.size(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Field Agent",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF172033)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Teknisi Lapangan",
                fontSize = 14.sp,
                color = Color(0xFF6F7A8C)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(9.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2EAF68))
                )

                Spacer(modifier = Modifier.size(7.dp))

                Text(
                    text = "Online",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2EAF68)
                )
            }
        }
    }

}

@Composable
private fun SettingsCard(
    notificationEnabled: Boolean,
    onNotificationChanged: (Boolean) -> Unit,
    autoSyncEnabled: Boolean,
    onAutoSyncChanged: (Boolean) -> Unit,
    onActivityHistoryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .padding(horizontal = 18.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Pengaturan",
            modifier = Modifier.padding(
                start = 2.dp,
                top = 10.dp,
                bottom = 8.dp
            ),
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF172033)
        )

        SettingSwitchRow(
            title = "Notifikasi",
            description = "Terima pemberitahuan pekerjaan terbaru",
            checked = notificationEnabled,
            onCheckedChange = onNotificationChanged
        )

        HorizontalDivider(
            color = Color(0xFFE9EDF3)
        )

        SettingSwitchRow(
            title = "Sinkronisasi otomatis",
            description = "Sinkronkan data saat koneksi tersedia",
            checked = autoSyncEnabled,
            onCheckedChange = onAutoSyncChanged
        )

        HorizontalDivider(
            color = Color(0xFFE9EDF3)
        )

        TextButton(
            onClick = onActivityHistoryClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFEFF5FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "↗",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF246BCE)
                    )
                }

                Spacer(modifier = Modifier.size(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Riwayat Aktivitas",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF172033)
                    )

                    Text(
                        text = "Lihat aktivitas pekerjaan Anda",
                        fontSize = 12.sp,
                        color = Color(0xFF7A8496)
                    )
                }

                Text(
                    text = "›",
                    fontSize = 25.sp,
                    color = Color(0xFF9AA3B2)
                )
            }
        }
    }

}

@Composable
private fun SettingSwitchRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF172033)
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = description,
                fontSize = 12.sp,
                color = Color(0xFF7A8496)
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }

}

@Composable
private fun AppInfoCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .padding(18.dp)
    ) {
        Text(
            text = "Informasi Aplikasi",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF172033)
        )

        Spacer(modifier = Modifier.height(14.dp))

        InfoRow(
            label = "Nama aplikasi",
            value = "FieldOps"
        )

        Spacer(modifier = Modifier.height(12.dp))

        InfoRow(
            label = "Versi",
            value = "1.0.0"
        )

        Spacer(modifier = Modifier.height(12.dp))

        InfoRow(
            label = "Mode",
            value = "Field Service"
        )
    }

}

@Composable
private fun InfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            fontSize = 14.sp,
            color = Color(0xFF7A8496)
        )

        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF172033)
        )
    }

}
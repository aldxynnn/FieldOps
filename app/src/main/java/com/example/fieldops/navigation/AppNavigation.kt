package com.example.fieldops.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fieldops.data.local.FieldOpsDatabase
import com.example.fieldops.data.model.WorkOrder
import com.example.fieldops.data.model.WorkOrderStatus
import com.example.fieldops.data.repository.WorkOrderRepository
import com.example.fieldops.data.sync.WorkOrderSyncScheduler
import com.example.fieldops.ui.activity.ActivityHistoryItem
import com.example.fieldops.ui.activity.ActivityHistoryScreen
import com.example.fieldops.ui.activity.ActivityType
import com.example.fieldops.ui.auth.LoginScreen
import com.example.fieldops.ui.customer.CustomerDetailScreen
import com.example.fieldops.ui.customer.CustomerScreen
import com.example.fieldops.ui.dashboard.DashboardScreen
import com.example.fieldops.ui.notification.FieldOpsNotification
import com.example.fieldops.ui.notification.NotificationScreen
import com.example.fieldops.ui.notification.NotificationType
import com.example.fieldops.ui.splash.SplashScreen
import com.example.fieldops.ui.workorder.EvidencePhoto
import com.example.fieldops.ui.workorder.WorkOrderDetailScreen
import com.example.fieldops.ui.workorder.WorkOrderViewModel
import com.example.fieldops.ui.workorder.WorkOrdersScreen
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private enum class AppRoute {
    SPLASH,
    LOGIN,
    DASHBOARD,
    WORK_ORDERS,
    WORK_ORDER_DETAIL,
    CUSTOMERS,
    CUSTOMER_DETAIL,
    NOTIFICATIONS,
    PROFILE,
    ACTIVITY_HISTORY
}

@Composable
fun AppNavigation() {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        WorkOrderSyncScheduler.schedule(context)
    }

    val database = remember {
        FieldOpsDatabase.getInstance(context)
    }

    val workOrderRepository = remember(database) {
        WorkOrderRepository(database)
    }

    val workOrderViewModel: WorkOrderViewModel = viewModel(
        factory = remember(workOrderRepository) {
            WorkOrderViewModel.Factory(
                workOrderRepository
            )
        }
    )

    val workOrders by workOrderViewModel
        .workOrders
        .collectAsState()

    val pendingSyncCount by workOrderViewModel
        .pendingSyncCount
        .collectAsState()

    var route by remember {
        mutableStateOf(AppRoute.SPLASH)
    }

    var selectedWorkOrderId by remember {
        mutableStateOf<String?>(null)
    }

    var selectedCustomerName by remember {
        mutableStateOf<String?>(null)
    }

    val notesByWorkOrder = remember {
        mutableStateMapOf<String, List<String>>()
    }

    val photosByWorkOrder = remember {
        mutableStateMapOf<String, List<EvidencePhoto>>()
    }

    val activityHistory = remember {
        mutableStateOf<List<ActivityHistoryItem>>(
            emptyList()
        )
    }

    val notifications = remember {
        mutableStateOf(
            listOf(
                FieldOpsNotification(
                    id = "notification_001",
                    title = "Work Order Baru",
                    description =
                        "WO-001234 menunggu untuk diterima.",
                    time = "08:15",
                    type = NotificationType.WORK_ORDER,
                    isRead = false
                ),
                FieldOpsNotification(
                    id = "notification_002",
                    title = "Sinkronisasi Berhasil",
                    description =
                        "Semua perubahan lokal telah tersimpan.",
                    time = "08:02",
                    type = NotificationType.SYNC,
                    isRead = true
                ),
                FieldOpsNotification(
                    id = "notification_003",
                    title = "Pekerjaan Berlangsung",
                    description =
                        "WO-001235 sedang dikerjakan.",
                    time = "07:48",
                    type = NotificationType.STATUS,
                    isRead = true
                )
            )
        )
    }

    LaunchedEffect(workOrderViewModel) {
        workOrderViewModel.refreshSeedData()
    }

    LaunchedEffect(Unit) {
        delay(1800L)
        route = AppRoute.LOGIN
    }

    val selectedWorkOrder = workOrders.find {
        it.id == selectedWorkOrderId
    }

    when (route) {
        AppRoute.SPLASH -> {
            SplashScreen()
        }

        AppRoute.LOGIN -> {
            LoginScreen(
                onLoginSuccess = {
                    route = AppRoute.DASHBOARD
                }
            )
        }

        AppRoute.WORK_ORDER_DETAIL -> {
            if (selectedWorkOrder != null) {
                BackHandler {
                    route = AppRoute.WORK_ORDERS
                }

                WorkOrderDetailScreen(
                    workOrder = selectedWorkOrder,
                    notes = notesByWorkOrder[
                        selectedWorkOrder.id
                    ] ?: emptyList(),
                    photos = photosByWorkOrder[
                        selectedWorkOrder.id
                    ] ?: emptyList(),
                    onBack = {
                        route = AppRoute.WORK_ORDERS
                    },
                    onAction = {
                        val currentOrder =
                            selectedWorkOrder

                        val nextStatus =
                            when (currentOrder.status) {
                                WorkOrderStatus.PENDING ->
                                    WorkOrderStatus.ACCEPTED

                                WorkOrderStatus.ACCEPTED ->
                                    WorkOrderStatus.IN_PROGRESS

                                WorkOrderStatus.IN_PROGRESS ->
                                    WorkOrderStatus.COMPLETED

                                WorkOrderStatus.COMPLETED ->
                                    WorkOrderStatus.COMPLETED

                                WorkOrderStatus.CANCELLED ->
                                    WorkOrderStatus.CANCELLED
                            }

                        if (
                            nextStatus !=
                            currentOrder.status
                        ) {
                            val updatedOrder =
                                currentOrder.copy(
                                    status = nextStatus
                                )

                            workOrderViewModel
                                .updateWorkOrder(
                                    updatedOrder
                                )

                            addActivity(
                                activityHistory =
                                    activityHistory,
                                workOrder =
                                    currentOrder,
                                title =
                                    statusActivityTitle(
                                        nextStatus
                                    ),
                                description =
                                    statusActivityDescription(
                                        nextStatus
                                    ),
                                type =
                                    ActivityType.STATUS
                            )

                            notifications.value =
                                listOf(
                                    FieldOpsNotification(
                                        id =
                                            "notification_${System.currentTimeMillis()}",
                                        title =
                                            statusActivityTitle(
                                                nextStatus
                                            ),
                                        description =
                                            "${currentOrder.id} - ${statusActivityDescription(nextStatus)}",
                                        time =
                                            currentTime(),
                                        type =
                                            NotificationType.STATUS,
                                        isRead = false
                                    )
                                ) +
                                        notifications.value
                        }
                    },
                    onAddNote = { note ->
                        val existingNotes =
                            notesByWorkOrder[
                                selectedWorkOrder.id
                            ] ?: emptyList()

                        notesByWorkOrder[
                            selectedWorkOrder.id
                        ] = existingNotes + note

                        addActivity(
                            activityHistory =
                                activityHistory,
                            workOrder =
                                selectedWorkOrder,
                            title =
                                "Catatan Ditambahkan",
                            description =
                                "Catatan baru ditambahkan ke Work Order.",
                            type =
                                ActivityType.NOTE
                        )

                        notifications.value =
                            listOf(
                                FieldOpsNotification(
                                    id =
                                        "notification_${System.currentTimeMillis()}",
                                    title =
                                        "Catatan Ditambahkan",
                                    description =
                                        "Catatan baru ditambahkan ke ${selectedWorkOrder.id}.",
                                    time =
                                        currentTime(),
                                    type =
                                        NotificationType.SYSTEM,
                                    isRead = false
                                )
                            ) +
                                    notifications.value
                    },
                    onAddPhoto = { photo ->
                        val existingPhotos =
                            photosByWorkOrder[
                                selectedWorkOrder.id
                            ] ?: emptyList()

                        photosByWorkOrder[
                            selectedWorkOrder.id
                        ] = existingPhotos + photo

                        addActivity(
                            activityHistory =
                                activityHistory,
                            workOrder =
                                selectedWorkOrder,
                            title =
                                "Bukti Foto Ditambahkan",
                            description =
                                "Foto bukti pekerjaan berhasil ditambahkan.",
                            type =
                                ActivityType.PHOTO
                        )

                        notifications.value =
                            listOf(
                                FieldOpsNotification(
                                    id =
                                        "notification_${System.currentTimeMillis()}",
                                    title =
                                        "Bukti Foto Ditambahkan",
                                    description =
                                        "Bukti foto berhasil ditambahkan ke ${selectedWorkOrder.id}.",
                                    time =
                                        currentTime(),
                                    type =
                                        NotificationType.SYSTEM,
                                    isRead = false
                                )
                            ) +
                                    notifications.value
                    },
                    onRemovePhoto = { photoId ->
                        val existingPhotos =
                            photosByWorkOrder[
                                selectedWorkOrder.id
                            ] ?: emptyList()

                        val removed =
                            existingPhotos.any {
                                it.id == photoId
                            }

                        photosByWorkOrder[
                            selectedWorkOrder.id
                        ] =
                            existingPhotos.filter {
                                it.id != photoId
                            }

                        if (removed) {
                            addActivity(
                                activityHistory =
                                    activityHistory,
                                workOrder =
                                    selectedWorkOrder,
                                title =
                                    "Bukti Foto Dihapus",
                                description =
                                    "Bukti foto dihapus dari Work Order.",
                                type =
                                    ActivityType.PHOTO
                            )
                        }
                    }
                )
            } else {
                route = AppRoute.WORK_ORDERS
            }
        }

        AppRoute.CUSTOMER_DETAIL -> {
            val customerName =
                selectedCustomerName

            if (customerName != null) {
                BackHandler {
                    route = AppRoute.CUSTOMERS
                }

                CustomerDetailScreen(
                    customerName =
                        customerName,
                    workOrders =
                        workOrders,
                    onBack = {
                        route =
                            AppRoute.CUSTOMERS
                    },
                    onWorkOrderClick = { workOrder ->
                        selectedWorkOrderId =
                            workOrder.id

                        route =
                            AppRoute.WORK_ORDER_DETAIL
                    }
                )
            } else {
                route =
                    AppRoute.CUSTOMERS
            }
        }

        AppRoute.ACTIVITY_HISTORY -> {
            ActivityHistoryScreen(
                activities =
                    activityHistory.value,
                onBack = {
                    route =
                        AppRoute.PROFILE
                }
            )
        }

        AppRoute.DASHBOARD,
        AppRoute.WORK_ORDERS,
        AppRoute.CUSTOMERS,
        AppRoute.NOTIFICATIONS,
        AppRoute.PROFILE -> {
            BackHandler(
                enabled =
                    route != AppRoute.DASHBOARD
            ) {
                route =
                    AppRoute.DASHBOARD
            }

            MainShell(
                currentRoute =
                    route,
                workOrders =
                    workOrders,
                pendingSyncCount =
                    pendingSyncCount,
                notifications =
                    notifications.value,
                onNavigate = { destination ->
                    route =
                        destination
                },
                onActivityHistory = {
                    route =
                        AppRoute.ACTIVITY_HISTORY
                },
                onWorkOrderClick = { workOrder ->
                    selectedWorkOrderId =
                        workOrder.id

                    route =
                        AppRoute.WORK_ORDER_DETAIL
                },
                onCustomerClick = { customerName ->
                    selectedCustomerName =
                        customerName

                    route =
                        AppRoute.CUSTOMER_DETAIL
                },
                onNotificationClick = { notification ->
                    notifications.value =
                        notifications.value.map {
                            if (it.id == notification.id) {
                                it.copy(
                                    isRead = true
                                )
                            } else {
                                it
                            }
                        }
                },
                onMarkAllNotificationsRead = {
                    notifications.value =
                        notifications.value.map {
                            it.copy(
                                isRead = true
                            )
                        }
                }
            )
        }
    }

}

private fun addActivity(
    activityHistory:
    MutableState<List<ActivityHistoryItem>>,
    workOrder: WorkOrder,
    title: String,
    description: String,
    type: ActivityType
) {
    val formatter =
        SimpleDateFormat(
            "HH:mm",
            Locale.getDefault()
        )

    val activity =
        ActivityHistoryItem(
            id =
                "${System.currentTimeMillis()}_$title",
            workOrderId =
                workOrder.id,
            title =
                title,
            description =
                description,
            time =
                formatter.format(
                    Date()
                ),
            type =
                type
        )

    activityHistory.value =
        listOf(activity) +
                activityHistory.value

}

private fun currentTime(): String {
    return SimpleDateFormat(
        "HH:mm",
        Locale.getDefault()
    ).format(Date())
}

private fun statusActivityTitle(
    status: WorkOrderStatus
): String {
    return when (status) {
        WorkOrderStatus.ACCEPTED ->
            "Work Order Diterima"

        WorkOrderStatus.IN_PROGRESS ->
            "Pekerjaan Dimulai"

        WorkOrderStatus.COMPLETED ->
            "Pekerjaan Diselesaikan"

        WorkOrderStatus.PENDING ->
            "Status Pending"

        WorkOrderStatus.CANCELLED ->
            "Work Order Dibatalkan"
    }

}

private fun statusActivityDescription(
    status: WorkOrderStatus
): String {
    return when (status) {
        WorkOrderStatus.ACCEPTED ->
            "Work Order berhasil diterima oleh teknisi."

        WorkOrderStatus.IN_PROGRESS ->
            "Teknisi mulai mengerjakan Work Order."

        WorkOrderStatus.COMPLETED ->
            "Pekerjaan telah ditandai selesai."

        WorkOrderStatus.PENDING ->
            "Work Order berada pada status pending."

        WorkOrderStatus.CANCELLED ->
            "Work Order ditandai sebagai dibatalkan."
    }

}

@Composable
private fun MainShell(
    currentRoute: AppRoute,
    workOrders: List<WorkOrder>,
    pendingSyncCount: Int,
    notifications: List<FieldOpsNotification>,
    onNavigate: (AppRoute) -> Unit,
    onActivityHistory: () -> Unit,
    onWorkOrderClick: (WorkOrder) -> Unit,
    onCustomerClick: (String) -> Unit,
    onNotificationClick:
        (FieldOpsNotification) -> Unit,
    onMarkAllNotificationsRead: () -> Unit
) {
    Scaffold(
        containerColor =
            Color(0xFFF7FAFE),
        bottomBar = {
            FieldOpsBottomNavigation(
                currentRoute =
                    currentRoute,
                onNavigate =
                    onNavigate
            )
        }
    ) { innerPadding ->

        Column(
            modifier =
                Modifier.fillMaxSize()
        ) {
            SyncStatusBanner(
                pendingSyncCount =
                    pendingSyncCount
            )

            when (currentRoute) {
                AppRoute.DASHBOARD -> {
                    DashboardScreen(
                        modifier =
                            Modifier
                                .weight(1f)
                                .padding(
                                    bottom =
                                        innerPadding
                                            .calculateBottomPadding()
                                )
                    )
                }

                AppRoute.WORK_ORDERS -> {
                    Box(
                        modifier =
                            Modifier
                                .weight(1f)
                                .padding(
                                    bottom =
                                        innerPadding
                                            .calculateBottomPadding()
                                )
                    ) {
                        WorkOrdersScreen(
                            workOrders =
                                workOrders,
                            onWorkOrderClick =
                                onWorkOrderClick
                        )
                    }
                }

                AppRoute.CUSTOMERS -> {
                    Box(
                        modifier =
                            Modifier
                                .weight(1f)
                                .padding(
                                    bottom =
                                        innerPadding
                                            .calculateBottomPadding()
                                )
                    ) {
                        CustomerScreen(
                            workOrders =
                                workOrders,
                            onCustomerClick =
                                onCustomerClick
                        )
                    }
                }

                AppRoute.NOTIFICATIONS -> {
                    Box(
                        modifier =
                            Modifier
                                .weight(1f)
                                .padding(
                                    bottom =
                                        innerPadding
                                            .calculateBottomPadding()
                                )
                    ) {
                        NotificationScreen(
                            notifications =
                                notifications,
                            onNotificationClick =
                                onNotificationClick,
                            onMarkAllRead =
                                onMarkAllNotificationsRead
                        )
                    }
                }

                AppRoute.PROFILE -> {
                    ProfileScreen(
                        modifier =
                            Modifier
                                .weight(1f)
                                .padding(
                                    bottom =
                                        innerPadding
                                            .calculateBottomPadding()
                                ),
                        onActivityHistory =
                            onActivityHistory
                    )
                }

                AppRoute.SPLASH,
                AppRoute.LOGIN,
                AppRoute.WORK_ORDER_DETAIL,
                AppRoute.CUSTOMER_DETAIL,
                AppRoute.ACTIVITY_HISTORY -> {
                }
            }
        }
    }

}

@Composable
private fun SyncStatusBanner(
    pendingSyncCount: Int
) {
    val isSynced =
        pendingSyncCount == 0

    val backgroundColor =
        if (isSynced) {
            Color(0xFFE8F7EF)
        } else {
            Color(0xFFFFF4DF)
        }

    val indicatorColor =
        if (isSynced) {
            Color(0xFF159A67)
        } else {
            Color(0xFFD98600)
        }

    val title =
        if (isSynced) {
            "Tersinkron"
        } else {
            "Menunggu Sinkronisasi"
        }

    val description =
        if (isSynced) {
            "Semua perubahan tersimpan dan tersinkron."
        } else {
            "$pendingSyncCount perubahan menunggu sinkronisasi."
        }

    Surface(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 18.dp,
                    vertical = 8.dp
                ),
        shape =
            RoundedCornerShape(14.dp),
        color =
            backgroundColor
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 13.dp,
                        vertical = 10.dp
                    ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Surface(
                modifier =
                    Modifier.size(10.dp),
                shape =
                    RoundedCornerShape(50),
                color =
                    indicatorColor
            ) {
                Box(
                    modifier =
                        Modifier.fillMaxSize()
                )
            }

            Spacer(
                modifier =
                    Modifier.size(10.dp)
            )

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {
                Text(
                    text =
                        title,
                    color =
                        indicatorColor,
                    fontSize =
                        13.sp,
                    fontWeight =
                        FontWeight.Bold
                )

                Spacer(
                    modifier =
                        Modifier.height(1.dp)
                )

                Text(
                    text =
                        description,
                    color =
                        Color(0xFF5D6878),
                    fontSize =
                        11.sp,
                    lineHeight =
                        15.sp
                )
            }

            Text(
                text =
                    if (isSynced) {
                        "ONLINE"
                    } else {
                        "OFFLINE"
                    },
                color =
                    indicatorColor,
                fontSize =
                    10.sp,
                fontWeight =
                    FontWeight.Bold
            )
        }
    }

}

@Composable
private fun FieldOpsBottomNavigation(
    currentRoute: AppRoute,
    onNavigate: (AppRoute) -> Unit
) {
    NavigationBar(
        containerColor =
            Color.White,
        tonalElevation =
            5.dp
    ) {
        NavigationBarItem(
            selected =
                currentRoute ==
                        AppRoute.DASHBOARD,
            onClick = {
                onNavigate(
                    AppRoute.DASHBOARD
                )
            },
            icon = {
                Text(
                    text = "⌂",
                    fontSize = 20.sp
                )
            },
            label = {
                Text("Beranda")
            }
        )

        NavigationBarItem(
            selected =
                currentRoute ==
                        AppRoute.WORK_ORDERS,
            onClick = {
                onNavigate(
                    AppRoute.WORK_ORDERS
                )
            },
            icon = {
                Text(
                    text = "▣",
                    fontSize = 18.sp
                )
            },
            label = {
                Text("Work Order")
            }
        )

        NavigationBarItem(
            selected =
                currentRoute ==
                        AppRoute.CUSTOMERS,
            onClick = {
                onNavigate(
                    AppRoute.CUSTOMERS
                )
            },
            icon = {
                Text(
                    text = "♙",
                    fontSize = 19.sp
                )
            },
            label = {
                Text("Pelanggan")
            }
        )

        NavigationBarItem(
            selected =
                currentRoute ==
                        AppRoute.NOTIFICATIONS,
            onClick = {
                onNavigate(
                    AppRoute.NOTIFICATIONS
                )
            },
            icon = {
                Box(
                    contentAlignment =
                        Alignment.TopEnd
                ) {
                    Text(
                        text = "♧",
                        fontSize = 19.sp
                    )

                    val unreadCount =
                        notificationsUnreadCountPlaceholder()

                    if (unreadCount > 0) {
                        Surface(
                            modifier =
                                Modifier.size(7.dp),
                            shape =
                                RoundedCornerShape(50),
                            color =
                                Color(0xFFE53935)
                        ) {}
                    }
                }
            },
            label = {
                Text("Notifikasi")
            }
        )

        NavigationBarItem(
            selected =
                currentRoute ==
                        AppRoute.PROFILE,
            onClick = {
                onNavigate(
                    AppRoute.PROFILE
                )
            },
            icon = {
                Text(
                    text = "☷",
                    fontSize = 20.sp
                )
            },
            label = {
                Text("Lainnya")
            }
        )
    }

}

private fun notificationsUnreadCountPlaceholder(): Int {
    return 1
}

@Composable
private fun ProfileScreen(
    modifier: Modifier,
    onActivityHistory: () -> Unit
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(18.dp)
    ) {
        Text(
            text = "Lainnya",
            color =
                Color(0xFF10213F),
            fontSize =
                28.sp,
            fontWeight =
                FontWeight.Bold
        )

        Spacer(
            modifier =
                Modifier.height(6.dp)
        )

        Text(
            text =
                "Pengaturan dan aktivitas FieldOps",
            color =
                Color(0xFF718096),
            fontSize =
                14.sp
        )

        Spacer(
            modifier =
                Modifier.height(18.dp)
        )

        Card(
            modifier =
                Modifier.fillMaxWidth(),
            shape =
                RoundedCornerShape(20.dp),
            colors =
                CardDefaults.cardColors(
                    containerColor =
                        Color.White
                ),
            elevation =
                CardDefaults.cardElevation(
                    defaultElevation =
                        2.dp
                )
        ) {
            Column(
                modifier =
                    Modifier.padding(18.dp)
            ) {
                Text(
                    text =
                        "Aktivitas",
                    color =
                        Color(0xFF10213F),
                    fontSize =
                        17.sp,
                    fontWeight =
                        FontWeight.Bold
                )

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )

                OutlinedButton(
                    onClick =
                        onActivityHistory,
                    modifier =
                        Modifier.fillMaxWidth(),
                    shape =
                        RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text =
                            "Riwayat Aktivitas",
                        color =
                            Color(0xFF1769FF),
                        fontSize =
                            14.sp,
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        }
    }

}
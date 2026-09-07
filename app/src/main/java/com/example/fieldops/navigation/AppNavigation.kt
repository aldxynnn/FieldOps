package com.example.fieldops.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.fieldops.data.local.FieldOpsDatabase
import com.example.fieldops.data.model.WorkOrder
import com.example.fieldops.data.model.WorkOrderStatus
import com.example.fieldops.data.network.NetworkConnectivityManager
import com.example.fieldops.data.repository.WorkOrderRepository
import com.example.fieldops.data.sync.WorkOrderSyncScheduler
import com.example.fieldops.data.sync.WorkOrderSyncWorker
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

    val database = remember {
        FieldOpsDatabase.getInstance(context)
    }

    val workOrderRepository = remember(database) {
        WorkOrderRepository(database)
    }

    val networkConnectivityManager = remember {
        NetworkConnectivityManager(context)
    }

    val workOrderViewModel: WorkOrderViewModel =
        viewModel(
            factory = remember(
                workOrderRepository,
                networkConnectivityManager
            ) {
                WorkOrderViewModel.Factory(
                    repository = workOrderRepository,
                    networkConnectivityManager =
                        networkConnectivityManager
                )
            }
        )

    val workOrders by workOrderViewModel
        .workOrders
        .collectAsState()

    val pendingSyncCount by workOrderViewModel
        .pendingSyncCount
        .collectAsState()

    val isOnline by workOrderViewModel
        .isOnline
        .collectAsState()

    val activityHistory by workOrderRepository
        .observeActivityHistory()
        .collectAsState(
            initial = emptyList()
        )

    val notifications by workOrderRepository
        .observeNotifications()
        .collectAsState(
            initial = emptyList()
        )

    val coroutineScope =
        rememberCoroutineScope()

    var route by remember {
        mutableStateOf(AppRoute.SPLASH)
    }

    var selectedWorkOrderId by remember {
        mutableStateOf<String?>(null)
    }

    var selectedCustomerName by remember {
        mutableStateOf<String?>(null)
    }

    val notesByWorkOrder =
        remember {
            mutableStateMapOf<
                    String,
                    List<String>
                    >()
        }

    val photosByWorkOrder =
        remember {
            mutableStateMapOf<
                    String,
                    List<EvidencePhoto>
                    >()
        }

    LaunchedEffect(Unit) {
        WorkOrderSyncScheduler.schedule(
            context
        )
    }

    LaunchedEffect(workOrderViewModel) {
        workOrderViewModel.refreshSeedData()
    }

    LaunchedEffect(workOrderRepository) {
        workOrderRepository
            .seedDemoNotificationsIfNeeded()
    }

    LaunchedEffect(Unit) {

        delay(1800L)

        route = AppRoute.LOGIN
    }

    val selectedWorkOrder =
        workOrders.find {
            it.id == selectedWorkOrderId
        }

    when (route) {

        AppRoute.SPLASH -> {
            SplashScreen()
        }

        AppRoute.LOGIN -> {

            LoginScreen(
                onLoginSuccess = {
                    route =
                        AppRoute.DASHBOARD
                }
            )
        }

        AppRoute.WORK_ORDER_DETAIL -> {

            if (selectedWorkOrder != null) {

                BackHandler {
                    route =
                        AppRoute.WORK_ORDERS
                }

                WorkOrderDetailScreen(

                    workOrder =
                        selectedWorkOrder,

                    notes =
                        notesByWorkOrder[
                            selectedWorkOrder.id
                        ] ?: emptyList(),

                    photos =
                        photosByWorkOrder[
                            selectedWorkOrder.id
                        ] ?: emptyList(),

                    onBack = {
                        route =
                            AppRoute.WORK_ORDERS
                    },

                    onAction = {

                        val currentOrder =
                            selectedWorkOrder

                        val nextStatus =
                            when (
                                currentOrder.status
                            ) {

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
                                    status =
                                        nextStatus
                                )

                            workOrderViewModel
                                .updateWorkOrder(
                                    updatedOrder
                                )

                            val activity =
                                createActivity(
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

                            coroutineScope.launch {

                                workOrderRepository
                                    .addActivity(
                                        activity
                                    )

                                workOrderRepository
                                    .addNotification(
                                        FieldOpsNotification(
                                            id =
                                                "notification_${System.currentTimeMillis()}",
                                            title =
                                                statusActivityTitle(
                                                    nextStatus
                                                ),
                                            description =
                                                "${currentOrder.id} - ${
                                                    statusActivityDescription(
                                                        nextStatus
                                                    )
                                                }",
                                            time =
                                                currentTime(),
                                            type =
                                                NotificationType.STATUS,
                                            isRead = false
                                        )
                                    )
                            }
                        }
                    },

                    onAddNote = { note ->

                        val existingNotes =
                            notesByWorkOrder[
                                selectedWorkOrder.id
                            ] ?: emptyList()

                        notesByWorkOrder[
                            selectedWorkOrder.id
                        ] =
                            existingNotes + note

                        val activity =
                            createActivity(
                                workOrder =
                                    selectedWorkOrder,
                                title =
                                    "Catatan Ditambahkan",
                                description =
                                    "Catatan baru ditambahkan ke Work Order.",
                                type =
                                    ActivityType.NOTE
                            )

                        coroutineScope.launch {

                            workOrderRepository
                                .addActivity(
                                    activity
                                )

                            workOrderRepository
                                .addNotification(
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
                                )
                        }
                    },

                    onAddPhoto = { photo ->

                        val existingPhotos =
                            photosByWorkOrder[
                                selectedWorkOrder.id
                            ] ?: emptyList()

                        photosByWorkOrder[
                            selectedWorkOrder.id
                        ] =
                            existingPhotos + photo

                        val activity =
                            createActivity(
                                workOrder =
                                    selectedWorkOrder,
                                title =
                                    "Bukti Foto Ditambahkan",
                                description =
                                    "Foto bukti pekerjaan berhasil ditambahkan.",
                                type =
                                    ActivityType.PHOTO
                            )

                        coroutineScope.launch {

                            workOrderRepository
                                .addActivity(
                                    activity
                                )

                            workOrderRepository
                                .addNotification(
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
                                )
                        }
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

                            val activity =
                                createActivity(
                                    workOrder =
                                        selectedWorkOrder,
                                    title =
                                        "Bukti Foto Dihapus",
                                    description =
                                        "Bukti foto dihapus dari Work Order.",
                                    type =
                                        ActivityType.PHOTO
                                )

                            coroutineScope.launch {

                                workOrderRepository
                                    .addActivity(
                                        activity
                                    )

                                workOrderRepository
                                    .addNotification(
                                        FieldOpsNotification(
                                            id =
                                                "notification_${System.currentTimeMillis()}",
                                            title =
                                                "Bukti Foto Dihapus",
                                            description =
                                                "Bukti foto dihapus dari ${selectedWorkOrder.id}.",
                                            time =
                                                currentTime(),
                                            type =
                                                NotificationType.SYSTEM,
                                            isRead = false
                                        )
                                    )
                            }
                        }
                    }
                )

            } else {

                route =
                    AppRoute.WORK_ORDERS
            }
        }

        AppRoute.CUSTOMER_DETAIL -> {

            val customerName =
                selectedCustomerName

            if (
                customerName != null
            ) {

                BackHandler {
                    route =
                        AppRoute.CUSTOMERS
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

                    onWorkOrderClick = {
                            workOrder ->

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
                    activityHistory,

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
                    route !=
                            AppRoute.DASHBOARD
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

                isOnline =
                    isOnline,

                notifications =
                    notifications,

                onNavigate = {
                        destination ->

                    route =
                        destination
                },

                onActivityHistory = {

                    route =
                        AppRoute.ACTIVITY_HISTORY
                },

                onLogout = {

                    selectedWorkOrderId =
                        null

                    selectedCustomerName =
                        null

                    route =
                        AppRoute.LOGIN
                },

                onSyncNow = {

                    val request =
                        OneTimeWorkRequestBuilder<WorkOrderSyncWorker>()
                            .setConstraints(
                                Constraints.Builder()
                                    .setRequiredNetworkType(
                                        NetworkType.CONNECTED
                                    )
                                    .build()
                            )
                            .build()

                    WorkManager
                        .getInstance(context)
                        .enqueue(request)
                },

                onWorkOrderClick = {
                        workOrder ->

                    selectedWorkOrderId =
                        workOrder.id

                    route =
                        AppRoute.WORK_ORDER_DETAIL
                },

                onCustomerClick = {
                        customerName ->

                    selectedCustomerName =
                        customerName

                    route =
                        AppRoute.CUSTOMER_DETAIL
                },

                onNotificationClick = {
                        notification ->

                    coroutineScope.launch {

                        workOrderRepository
                            .markNotificationRead(
                                notification.id
                            )
                    }
                },

                onMarkAllNotificationsRead = {

                    coroutineScope.launch {

                        workOrderRepository
                            .markAllNotificationsRead()
                    }
                }
            )
        }
    }

}

private fun createActivity(
    workOrder: WorkOrder,
    title: String,
    description: String,
    type: ActivityType
): ActivityHistoryItem {

    return ActivityHistoryItem(
        id =
            "${System.currentTimeMillis()}_${workOrder.id}_$type",
        workOrderId =
            workOrder.id,
        title =
            title,
        description =
            description,
        time =
            currentTime(),
        type =
            type
    )

}

private fun currentTime(): String {

    return SimpleDateFormat(
        "HH:mm",
        Locale.getDefault()
    ).format(
        Date()
    )

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
    isOnline: Boolean,
    notifications: List<FieldOpsNotification>,
    onNavigate: (AppRoute) -> Unit,
    onActivityHistory: () -> Unit,
    onLogout: () -> Unit,
    onSyncNow: () -> Unit,
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

                unreadNotificationCount =
                    notifications.count {
                        !it.isRead
                    },

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
                    pendingSyncCount,

                isOnline =
                    isOnline,

                onSyncNow =
                    onSyncNow
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

                        workOrders =
                            workOrders,

                        isOnline =
                            isOnline,

                        onActivityHistory =
                            onActivityHistory,

                        onLogout =
                            onLogout
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
    pendingSyncCount: Int,
    isOnline: Boolean,
    onSyncNow: () -> Unit
) {

    val backgroundColor =
        when {

            !isOnline ->
                Color(0xFFFFF4DF)

            pendingSyncCount > 0 ->
                Color(0xFFFFF4DF)

            else ->
                Color(0xFFE8F7EF)
        }

    val indicatorColor =
        when {

            !isOnline ->
                Color(0xFFD98600)

            pendingSyncCount > 0 ->
                Color(0xFFD98600)

            else ->
                Color(0xFF159A67)
        }

    val title =
        when {

            !isOnline ->
                "Offline"

            pendingSyncCount > 0 ->
                "Menunggu Sinkronisasi"

            else ->
                "Tersinkron"
        }

    val description =
        when {

            !isOnline -> {

                if (
                    pendingSyncCount > 0
                ) {
                    "$pendingSyncCount perubahan tersimpan di perangkat."
                } else {
                    "Perangkat sedang tidak terhubung ke internet."
                }
            }

            pendingSyncCount > 0 ->
                "$pendingSyncCount perubahan menunggu sinkronisasi."

            else ->
                "Semua perubahan tersimpan dan tersinkron."
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
                    Modifier.width(10.dp)
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

            if (
                isOnline &&
                pendingSyncCount > 0
            ) {

                OutlinedButton(
                    onClick =
                        onSyncNow,

                    modifier =
                        Modifier.height(34.dp),

                    shape =
                        RoundedCornerShape(10.dp)
                ) {

                    Text(
                        text =
                            "Sinkronkan",

                        fontSize =
                            10.sp,

                        fontWeight =
                            FontWeight.Bold
                    )
                }

            } else {

                Text(
                    text =
                        if (isOnline) {
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

}

@Composable
private fun FieldOpsBottomNavigation(
    currentRoute: AppRoute,
    unreadNotificationCount: Int,
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

                    if (
                        unreadNotificationCount > 0
                    ) {

                        Surface(
                            modifier =
                                Modifier.size(15.dp),

                            shape =
                                RoundedCornerShape(50),

                            color =
                                Color(0xFFE53935)
                        ) {

                            Box(
                                contentAlignment =
                                    Alignment.Center
                            ) {

                                Text(
                                    text =
                                        if (
                                            unreadNotificationCount > 9
                                        ) {
                                            "9+"
                                        } else {
                                            unreadNotificationCount
                                                .toString()
                                        },

                                    color =
                                        Color.White,

                                    fontSize =
                                        7.sp,

                                    fontWeight =
                                        FontWeight.Bold
                                )
                            }
                        }
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

@Composable
private fun ProfileScreen(
    modifier: Modifier,
    workOrders: List<WorkOrder>,
    isOnline: Boolean,
    onActivityHistory: () -> Unit,
    onLogout: () -> Unit
) {

    var notificationsEnabled by remember {
        mutableStateOf(true)
    }

    var autoSyncEnabled by remember {
        mutableStateOf(true)
    }

    val completedCount =
        workOrders.count {
            it.status ==
                    WorkOrderStatus.COMPLETED
        }

    val activeCount =
        workOrders.count {
            it.status ==
                    WorkOrderStatus.IN_PROGRESS
        }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(
                    Color(0xFFF7FAFE)
                )
                .verticalScroll(
                    rememberScrollState()
                )
                .navigationBarsPadding()
                .padding(
                    horizontal = 18.dp,
                    vertical = 18.dp
                )
    ) {

        Text(
            text =
                "Profil & Pengaturan",

            color =
                Color(0xFF10213F),

            fontSize =
                28.sp,

            fontWeight =
                FontWeight.Bold
        )

        Spacer(
            modifier =
                Modifier.height(5.dp)
        )

        Text(
            text =
                "Kelola profil dan preferensi FieldOps",

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
                RoundedCornerShape(22.dp),

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

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Surface(
                        modifier =
                            Modifier.size(66.dp),

                        shape =
                            RoundedCornerShape(20.dp),

                        color =
                            Color(0xFFE8F0FF)
                    ) {

                        Box(
                            contentAlignment =
                                Alignment.Center
                        ) {

                            Text(
                                text =
                                    "AJ",

                                color =
                                    Color(0xFF1769FF),

                                fontSize =
                                    22.sp,

                                fontWeight =
                                    FontWeight.Bold
                            )
                        }
                    }

                    Spacer(
                        modifier =
                            Modifier.width(14.dp)
                    )

                    Column(
                        modifier =
                            Modifier.weight(1f)
                    ) {

                        Text(
                            text =
                                "Alex Johnson",

                            color =
                                Color(0xFF10213F),

                            fontSize =
                                19.sp,

                            fontWeight =
                                FontWeight.Bold
                        )

                        Spacer(
                            modifier =
                                Modifier.height(3.dp)
                        )

                        Text(
                            text =
                                "Field Technician",

                            color =
                                Color(0xFF718096),

                            fontSize =
                                13.sp
                        )

                        Spacer(
                            modifier =
                                Modifier.height(7.dp)
                        )

                        Surface(
                            shape =
                                RoundedCornerShape(50.dp),

                            color =
                                Color(0xFFE8F7EF)
                        ) {

                            Row(
                                modifier =
                                    Modifier.padding(
                                        horizontal = 9.dp,
                                        vertical = 5.dp
                                    ),

                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {

                                Surface(
                                    modifier =
                                        Modifier.size(7.dp),

                                    shape =
                                        RoundedCornerShape(50.dp),

                                    color =
                                        Color(0xFF159A67)
                                ) {

                                    Box(
                                        modifier =
                                            Modifier.fillMaxSize()
                                    )
                                }

                                Spacer(
                                    modifier =
                                        Modifier.width(6.dp)
                                )

                                Text(
                                    text =
                                        "Aktif",

                                    color =
                                        Color(0xFF159A67),

                                    fontSize =
                                        11.sp,

                                    fontWeight =
                                        FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(18.dp)
                )

                HorizontalDivider(
                    color =
                        Color(0xFFE9EEF5)
                )

                Spacer(
                    modifier =
                        Modifier.height(16.dp)
                )

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.spacedBy(10.dp)
                ) {

                    ProfileStat(
                        modifier =
                            Modifier.weight(1f),

                        value =
                            workOrders.size.toString(),

                        label =
                            "Total WO"
                    )

                    ProfileStat(
                        modifier =
                            Modifier.weight(1f),

                        value =
                            activeCount.toString(),

                        label =
                            "Berjalan"
                    )

                    ProfileStat(
                        modifier =
                            Modifier.weight(1f),

                        value =
                            completedCount.toString(),

                        label =
                            "Selesai"
                    )
                }
            }
        }

        Spacer(
            modifier =
                Modifier.height(16.dp)
        )

        ProfileSectionTitle(
            title =
                "Aktivitas"
        )

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        Card(
            modifier =
                Modifier.fillMaxWidth(),

            shape =
                RoundedCornerShape(18.dp),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        Color.White
                ),

            elevation =
                CardDefaults.cardElevation(
                    defaultElevation =
                        1.dp
                )
        ) {

            ProfileActionRow(
                icon =
                    "↻",

                title =
                    "Riwayat Aktivitas",

                description =
                    "Lihat seluruh aktivitas Work Order",

                onClick =
                    onActivityHistory
            )
        }

        Spacer(
            modifier =
                Modifier.height(16.dp)
        )

        ProfileSectionTitle(
            title =
                "Pengaturan"
        )

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        Card(
            modifier =
                Modifier.fillMaxWidth(),

            shape =
                RoundedCornerShape(18.dp),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        Color.White
                ),

            elevation =
                CardDefaults.cardElevation(
                    defaultElevation =
                        1.dp
                )
        ) {

            ProfileToggleRow(
                icon =
                    "♧",

                title =
                    "Notifikasi",

                description =
                    "Terima pemberitahuan Work Order",

                checked =
                    notificationsEnabled,

                onCheckedChange = {
                    notificationsEnabled =
                        it
                }
            )

            HorizontalDivider(
                modifier =
                    Modifier.padding(
                        horizontal = 18.dp
                    ),

                color =
                    Color(0xFFE9EEF5)
            )

            ProfileToggleRow(
                icon =
                    "↻",

                title =
                    "Sinkronisasi Otomatis",

                description =
                    "Sinkronkan data saat koneksi tersedia",

                checked =
                    autoSyncEnabled,

                onCheckedChange = {
                    autoSyncEnabled =
                        it
                }
            )
        }

        Spacer(
            modifier =
                Modifier.height(16.dp)
        )

        ProfileSectionTitle(
            title =
                "Status Sistem"
        )

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        Card(
            modifier =
                Modifier.fillMaxWidth(),

            shape =
                RoundedCornerShape(18.dp),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        Color.White
                ),

            elevation =
                CardDefaults.cardElevation(
                    defaultElevation =
                        1.dp
                )
        ) {

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(18.dp),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Surface(
                    modifier =
                        Modifier.size(44.dp),

                    shape =
                        RoundedCornerShape(14.dp),

                    color =
                        if (isOnline) {
                            Color(0xFFE8F7EF)
                        } else {
                            Color(0xFFFFF4DF)
                        }
                ) {

                    Box(
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Text(
                            text =
                                if (isOnline) {
                                    "✓"
                                } else {
                                    "!"
                                },

                            color =
                                if (isOnline) {
                                    Color(0xFF159A67)
                                } else {
                                    Color(0xFFD98600)
                                },

                            fontSize =
                                18.sp,

                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }

                Spacer(
                    modifier =
                        Modifier.width(12.dp)
                )

                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        text =
                            if (isOnline) {
                                "Koneksi Online"
                            } else {
                                "Mode Offline"
                            },

                        color =
                            Color(0xFF10213F),

                        fontSize =
                            15.sp,

                        fontWeight =
                            FontWeight.Bold
                    )

                    Spacer(
                        modifier =
                            Modifier.height(3.dp)
                    )

                    Text(
                        text =
                            if (isOnline) {
                                "Perangkat terhubung ke internet."
                            } else {
                                "Data tetap dapat digunakan secara lokal."
                            },

                        color =
                            Color(0xFF718096),

                        fontSize =
                            12.sp
                    )
                }

                Text(
                    text =
                        if (isOnline) {
                            "ONLINE"
                        } else {
                            "OFFLINE"
                        },

                    color =
                        if (isOnline) {
                            Color(0xFF159A67)
                        } else {
                            Color(0xFFD98600)
                        },

                    fontSize =
                        10.sp,

                    fontWeight =
                        FontWeight.Bold
                )
            }
        }

        Spacer(
            modifier =
                Modifier.height(16.dp)
        )

        ProfileSectionTitle(
            title =
                "Tentang"
        )

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        Card(
            modifier =
                Modifier.fillMaxWidth(),

            shape =
                RoundedCornerShape(18.dp),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        Color.White
                ),

            elevation =
                CardDefaults.cardElevation(
                    defaultElevation =
                        1.dp
                )
        ) {

            Column(
                modifier =
                    Modifier.padding(18.dp)
            ) {

                Text(
                    text =
                        "FieldOps",

                    color =
                        Color(0xFF1769FF),

                    fontSize =
                        18.sp,

                    fontWeight =
                        FontWeight.Bold
                )

                Spacer(
                    modifier =
                        Modifier.height(4.dp)
                )

                Text(
                    text =
                        "Field Service Management",

                    color =
                        Color(0xFF10213F),

                    fontSize =
                        14.sp,

                    fontWeight =
                        FontWeight.Medium
                )

                Spacer(
                    modifier =
                        Modifier.height(4.dp)
                )

                Text(
                    text =
                        "Platform operasional untuk membantu teknisi mengelola pekerjaan lapangan secara cepat, terstruktur, dan terdokumentasi.",

                    color =
                        Color(0xFF718096),

                    fontSize =
                        12.sp,

                    lineHeight =
                        18.sp
                )

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )

                Text(
                    text =
                        "Versi 1.0.0",

                    color =
                        Color(0xFF9AA6B6),

                    fontSize =
                        11.sp
                )
            }
        }

        Spacer(
            modifier =
                Modifier.height(16.dp)
        )

        OutlinedButton(
            onClick =
                onLogout,

            modifier =
                Modifier.fillMaxWidth(),

            shape =
                RoundedCornerShape(15.dp)
        ) {

            Text(
                text =
                    "Keluar dari Akun",

                color =
                    Color(0xFFD6344B),

                fontSize =
                    14.sp,

                fontWeight =
                    FontWeight.Bold
            )
        }

        Spacer(
            modifier =
                Modifier.height(18.dp)
        )
    }

}

@Composable
private fun ProfileStat(
    modifier: Modifier,
    value: String,
    label: String
) {

    Surface(
        modifier =
            modifier,

        shape =
            RoundedCornerShape(14.dp),

        color =
            Color(0xFFF5F8FD)
    ) {

        Column(
            modifier =
                Modifier.padding(
                    vertical = 12.dp,
                    horizontal = 8.dp
                ),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text =
                    value,

                color =
                    Color(0xFF1769FF),

                fontSize =
                    19.sp,

                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.height(2.dp)
            )

            Text(
                text =
                    label,

                color =
                    Color(0xFF718096),

                fontSize =
                    10.sp
            )
        }
    }

}

@Composable
private fun ProfileSectionTitle(
    title: String
) {

    Text(
        text =
            title,

        color =
            Color(0xFF10213F),

        fontSize =
            16.sp,

        fontWeight =
            FontWeight.Bold
    )

}

@Composable
private fun ProfileActionRow(
    icon: String,
    title: String,
    description: String,
    onClick: () -> Unit
) {

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(
                    onClick =
                        onClick
                )
                .padding(18.dp),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Surface(
            modifier =
                Modifier.size(44.dp),

            shape =
                RoundedCornerShape(14.dp),

            color =
                Color(0xFFE8F0FF)
        ) {

            Box(
                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text =
                        icon,

                    color =
                        Color(0xFF1769FF),

                    fontSize =
                        19.sp,

                    fontWeight =
                        FontWeight.Bold
                )
            }
        }

        Spacer(
            modifier =
                Modifier.width(12.dp)
        )

        Column(
            modifier =
                Modifier.weight(1f)
        ) {

            Text(
                text =
                    title,

                color =
                    Color(0xFF10213F),

                fontSize =
                    14.sp,

                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.height(3.dp)
            )

            Text(
                text =
                    description,

                color =
                    Color(0xFF718096),

                fontSize =
                    12.sp
            )
        }

        Text(
            text =
                "›",

            color =
                Color(0xFF9AA6B6),

            fontSize =
                24.sp
        )
    }

}

@Composable
private fun ProfileToggleRow(
    icon: String,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 18.dp,
                    vertical = 14.dp
                ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Surface(
            modifier =
                Modifier.size(44.dp),

            shape =
                RoundedCornerShape(14.dp),

            color =
                Color(0xFFF2F6FC)
        ) {

            Box(
                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text =
                        icon,

                    color =
                        Color(0xFF1769FF),

                    fontSize =
                        18.sp
                )
            }
        }

        Spacer(
            modifier =
                Modifier.width(12.dp)
        )

        Column(
            modifier =
                Modifier.weight(1f)
        ) {

            Text(
                text =
                    title,

                color =
                    Color(0xFF10213F),

                fontSize =
                    14.sp,

                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.height(3.dp)
            )

            Text(
                text =
                    description,

                color =
                    Color(0xFF718096),

                fontSize =
                    11.sp,

                lineHeight =
                    15.sp
            )
        }

        Switch(
            checked =
                checked,

            onCheckedChange =
                onCheckedChange
        )
    }

}
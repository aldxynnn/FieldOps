package com.example.fieldops.navigation

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.fieldops.data.local.FieldOpsDatabase
import com.example.fieldops.data.model.Customer
import com.example.fieldops.data.model.WorkOrder
import com.example.fieldops.data.model.WorkOrderStatus
import com.example.fieldops.data.network.NetworkConnectivityManager
import com.example.fieldops.data.remote.SessionManager
import com.example.fieldops.data.repository.CustomerRepository
import com.example.fieldops.data.repository.MaintenanceWorkOrderRepository
import com.example.fieldops.data.repository.WorkOrderRepository
import com.example.fieldops.data.sync.WorkOrderSyncScheduler
import com.example.fieldops.data.sync.WorkOrderSyncWorker
import com.example.fieldops.ui.activity.ActivityHistoryItem
import com.example.fieldops.ui.activity.ActivityHistoryScreen
import com.example.fieldops.ui.activity.ActivityType
import com.example.fieldops.ui.auth.LoginScreen
import com.example.fieldops.ui.customer.CustomerDetailScreen
import com.example.fieldops.ui.customer.CustomerScreen
import com.example.fieldops.ui.dashboard.RoleDashboardScreen
import com.example.fieldops.ui.management.ManagementScreen
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
    ACTIVITY_HISTORY,
    MANAGEMENT
}

@Composable
fun AppNavigation() {

    val context = LocalContext.current

    val sessionManager = remember {
        SessionManager(context)
    }

    val userName = sessionManager.name()
    val userRole = sessionManager.role().uppercase()

    val database = remember {
        FieldOpsDatabase.getInstance(context)
    }

    val workOrderRepository = remember(database) {
        WorkOrderRepository(database)
    }

    val customerRepository = remember {
        CustomerRepository.getInstance(context)
    }

    val maintenanceWorkOrderRepository =
        remember(workOrderRepository) {
            MaintenanceWorkOrderRepository(
                workOrderRepository
            )
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

    val customers by customerRepository
        .observeCustomers()
        .collectAsState(
            initial = emptyList()
        )

    LaunchedEffect(
        sessionManager.companyId()
    ) {
        customerRepository.loadFromLocal()
        customerRepository.refreshFromServer()
    }

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
        mutableStateOf(
            AppRoute.SPLASH
        )
    }

    var notificationsEnabled by remember {
        mutableStateOf(true)
    }

    var autoSyncEnabled by remember {
        mutableStateOf(true)
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

    /*
     * Sync scheduler
     */

    LaunchedEffect(autoSyncEnabled) {
        if (autoSyncEnabled) {
            WorkOrderSyncScheduler.schedule(
                context
            )
        } else {
            WorkOrderSyncScheduler.cancel(
                context
            )
        }
    }

    /*
     * Splash -> Login / Dashboard
     */

    LaunchedEffect(Unit) {
        delay(900L)

        route =
            if (sessionManager.isLoggedIn()) {
                AppRoute.DASHBOARD
            } else {
                AppRoute.LOGIN
            }
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

                                if (notificationsEnabled) {
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
                                            isRead =
                                                false
                                        )
                                    )
                                    }
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

                            if (notificationsEnabled) {
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
                                        isRead =
                                            false
                                    )
                                )
                            }
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

                            if (notificationsEnabled) {
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
                                        isRead =
                                            false
                                    )
                                )
                            }
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

                                if (notificationsEnabled) {
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
                                            isRead =
                                                false
                                        )
                                    )
                                    }
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

            if (customerName != null) {

                BackHandler {
                    route =
                        AppRoute.CUSTOMERS
                }

                CustomerDetailScreen(
                    customerName =
                        customerName,

                    customers =
                        customers,

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
                    },

                    onCreateMaintenanceWorkOrder = {
                            maintenance ->

                        coroutineScope.launch {

                            val createdWorkOrder =
                                maintenanceWorkOrderRepository
                                    .createFromMaintenance(
                                        maintenance =
                                            maintenance,
                                        existingWorkOrders =
                                            workOrders
                                    )

                            if (createdWorkOrder != null) {

                                workOrderRepository
                                    .addActivity(
                                        createActivity(
                                            workOrder =
                                                createdWorkOrder,
                                            title =
                                                "Work Order Maintenance Dibuat",
                                            description =
                                                "Work Order ${createdWorkOrder.id} dibuat dari jadwal preventive maintenance ${maintenance.id}.",
                                            type =
                                                ActivityType.STATUS
                                        )
                                    )

                                if (notificationsEnabled) {
                                    workOrderRepository
                                        .addNotification(
                                        FieldOpsNotification(
                                            id =
                                                "notification_${System.currentTimeMillis()}",
                                            title =
                                                "Work Order Maintenance Dibuat",
                                            description =
                                                "${createdWorkOrder.id} siap dikerjakan untuk ${createdWorkOrder.customer}.",
                                            time =
                                                currentTime(),
                                            type =
                                                NotificationType.WORK_ORDER,
                                            isRead =
                                                false
                                        )
                                    )
                                    }

                                selectedWorkOrderId =
                                    createdWorkOrder.id

                                route =
                                    AppRoute.WORK_ORDER_DETAIL
                            }
                        }
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
        AppRoute.PROFILE,
        AppRoute.MANAGEMENT -> {

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

                userName =
                    userName,

                userRole =
                    userRole,

                customers =
                    customers,

                pendingSyncCount =
                    pendingSyncCount,

                isOnline =
                    isOnline,

                notifications =
                    notifications,

                notificationsEnabled =
                    notificationsEnabled,

                onNotificationsEnabledChange = { enabled ->
                    notificationsEnabled = enabled
                },

                autoSyncEnabled =
                    autoSyncEnabled,

                onAutoSyncEnabledChange = { enabled ->
                    autoSyncEnabled = enabled
                },

                onNavigate = { destination ->
                    route =
                        destination
                },

                onDataChanged = {

                    WorkOrderSyncScheduler
                        .scheduleNow(
                            context
                        )

                    coroutineScope.launch {
                        customerRepository
                            .refreshFromServer()
                    }
                },

                onActivityHistory = {
                    route =
                        AppRoute.ACTIVITY_HISTORY
                },

                onLogout = {

                    sessionManager.clear()

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
    userName: String,
    userRole: String,
    customers: List<Customer>,
    pendingSyncCount: Int,
    isOnline: Boolean,
    notifications: List<FieldOpsNotification>,
    notificationsEnabled: Boolean,
    onNotificationsEnabledChange: (Boolean) -> Unit,
    autoSyncEnabled: Boolean,
    onAutoSyncEnabledChange: (Boolean) -> Unit,
    onNavigate: (AppRoute) -> Unit,
    onDataChanged: () -> Unit,
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
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    Color(0xFFF7FAFE)
                ),

        containerColor =
            Color(0xFFF7FAFE),

        bottomBar = {

            FieldOpsBottomNavigation(
                currentRoute =
                    currentRoute,

                unreadNotificationCount =
                    if (notificationsEnabled) {
                        notifications.count {
                            !it.isRead
                        }
                    } else {
                        0
                    },

                userRole =
                    userRole,

                onNavigate =
                    onNavigate
            )
        }
    ) { innerPadding ->

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(top = 0.dp)
        ) {

            when (currentRoute) {

                AppRoute.DASHBOARD -> {

                    RoleDashboardScreen(
                        workOrders =
                            workOrders,

                        userName =
                            userName,

                        role =
                            userRole,

                        isOnline =
                            isOnline,

                        onManage = {
                            onNavigate(
                                AppRoute.MANAGEMENT
                            )
                        },

                        modifier =
                            Modifier
                                .weight(1f)
                                .fillMaxWidth()
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
                                .fillMaxWidth()
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
                                .fillMaxWidth()
                                .padding(
                                    bottom =
                                        innerPadding
                                            .calculateBottomPadding()
                                )
                    ) {

                        CustomerScreen(
                            customers =
                                customers,

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
                                .fillMaxWidth()
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
                                .fillMaxWidth()
                                .padding(
                                    bottom =
                                        innerPadding
                                            .calculateBottomPadding()
                                ),

                        workOrders =
                            workOrders,

                        userName =
                            userName,

                        userRole =
                            userRole,

                        isOnline =
                            isOnline,

                        notificationsEnabled =
                            notificationsEnabled,

                        onNotificationsEnabledChange =
                            onNotificationsEnabledChange,

                        autoSyncEnabled =
                            autoSyncEnabled,

                        onAutoSyncEnabledChange =
                            onAutoSyncEnabledChange,

                        onActivityHistory =
                            onActivityHistory,

                        onLogout =
                            onLogout
                    )
                }

                AppRoute.MANAGEMENT -> {

                    ManagementScreen(
                        role =
                            userRole,

                        onChanged =
                            onDataChanged,

                        modifier =
                            Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(
                                    bottom =
                                        innerPadding
                                            .calculateBottomPadding()
                                )
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
private fun FieldOpsBottomNavigation(
    currentRoute: AppRoute,
    unreadNotificationCount: Int,
    userRole: String,
    onNavigate: (AppRoute) -> Unit
) {

    NavigationBar(
        modifier =
            Modifier.navigationBarsPadding(),

        containerColor =
            Color.White,

        tonalElevation =
            0.dp
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
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold
                )
            },

            label = {

                Text(
                    text = "Beranda",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
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
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },

            label = {

                Text(
                    text = "Work Order",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        )

        if (userRole != "TECHNICIAN") {

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
                        fontSize = 20.sp
                    )
                },

                label = {

                    Text(
                        text = "Pelanggan",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            )
        }

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
                        fontSize = 20.sp
                    )

                    if (
                        unreadNotificationCount > 0
                    ) {

                        Surface(
                            modifier =
                                Modifier.size(16.dp),

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

                Text(
                    text = "Notifikasi",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
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
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold
                )
            },

            label = {

                Text(
                    text = "Lainnya",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        )
    }
}

@Composable
private fun ProfileScreen(
    modifier: Modifier,
    workOrders: List<WorkOrder>,
    userName: String,
    userRole: String,
    isOnline: Boolean,
    notificationsEnabled: Boolean,
    onNotificationsEnabledChange: (Boolean) -> Unit,
    autoSyncEnabled: Boolean,
    onAutoSyncEnabledChange: (Boolean) -> Unit,
    onActivityHistory: () -> Unit,
    onLogout: () -> Unit
) {


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

    val initials =
        userName
            .trim()
            .split(" ")
            .filter {
                it.isNotBlank()
            }
            .take(2)
            .joinToString("") {
                it.first().uppercase()
            }
            .ifBlank {
                "FO"
            }

    val roleLabel =
        when (userRole) {

            "TECHNICIAN" ->
                "Teknisi Lapangan"

            "DISPATCHER" ->
                "Dispatcher"

            "MANAGER" ->
                "Manager Operasional"

            "ADMIN" ->
                "Administrator"

            else ->
                "Field Operations"
        }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(
                    Color(0xFFF5F8FC)
                )
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(
                    horizontal = 18.dp,
                    vertical = 18.dp
                )
    ) {

        Text(
            text = "Profil & Pengaturan",
            color = Color(0xFF10213F),
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.4).sp
        )

        Spacer(
            modifier = Modifier.height(5.dp)
        )

        Text(
            text =
                "Kelola profil dan preferensi FieldOps",
            color = Color(0xFF718096),
            fontSize = 14.sp,
            lineHeight = 20.sp
        )

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        Card(
            modifier =
                Modifier.fillMaxWidth(),

            shape =
                RoundedCornerShape(26.dp),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        Color.White
                ),

            border =
                BorderStroke(
                    1.dp,
                    Color(0xFFE5EAF2)
                ),

            elevation =
                CardDefaults.cardElevation(
                    defaultElevation = 3.dp
                )
        ) {

            Column(
                modifier =
                    Modifier.padding(20.dp)
            ) {

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Box(
                        modifier =
                            Modifier
                                .size(70.dp)
                                .background(
                                    Color(0xFFEAF2FF),
                                    RoundedCornerShape(22.dp)
                                ),

                        contentAlignment =
                            Alignment.Center
                    ) {

                        Text(
                            text = initials,
                            color =
                                Color(0xFF1769FF),
                            fontSize = 23.sp,
                            fontWeight =
                                FontWeight.Bold
                        )
                    }

                    Spacer(
                        modifier =
                            Modifier.width(15.dp)
                    )

                    Column(
                        modifier =
                            Modifier.weight(1f)
                    ) {

                        Text(
                            text =
                                userName.ifBlank {
                                    "Pengguna FieldOps"
                                },

                            color =
                                Color(0xFF10213F),

                            fontSize =
                                19.sp,

                            fontWeight =
                                FontWeight.Bold,

                            maxLines =
                                1,

                            overflow =
                                TextOverflow.Ellipsis
                        )

                        Spacer(
                            modifier =
                                Modifier.height(3.dp)
                        )

                        Text(
                            text =
                                roleLabel,

                            color =
                                Color(0xFF718096),

                            fontSize =
                                13.sp,

                            fontWeight =
                                FontWeight.Medium
                        )

                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )

                        Surface(
                            shape =
                                RoundedCornerShape(50.dp),

                            color =
                                if (isOnline) {
                                    Color(0xFFE8F7EF)
                                } else {
                                    Color(0xFFFFF4DF)
                                }
                        ) {

                            Row(
                                modifier =
                                    Modifier.padding(
                                        horizontal = 10.dp,
                                        vertical = 5.dp
                                    ),

                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {

                                Box(
                                    modifier =
                                        Modifier
                                            .size(7.dp)
                                            .background(
                                                if (isOnline) {
                                                    Color(0xFF159A67)
                                                } else {
                                                    Color(0xFFD98600)
                                                },
                                                RoundedCornerShape(50.dp)
                                            )
                                )

                                Spacer(
                                    modifier =
                                        Modifier.width(6.dp)
                                )

                                Text(
                                    text =
                                        if (isOnline) {
                                            "Terhubung"
                                        } else {
                                            "Offline"
                                        },

                                    color =
                                        if (isOnline) {
                                            Color(0xFF159A67)
                                        } else {
                                            Color(0xFFD98600)
                                        },

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
                        Modifier.height(19.dp)
                )

                HorizontalDivider(
                    color =
                        Color(0xFFE9EEF5)
                )

                Spacer(
                    modifier =
                        Modifier.height(16.dp)
                )

                Text(
                    text =
                        "Ringkasan Operasional",

                    color =
                        Color(0xFF10213F),

                    fontSize =
                        13.sp,

                    fontWeight =
                        FontWeight.Bold
                )

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.spacedBy(9.dp)
                ) {

                    ProfileStat(
                        modifier =
                            Modifier.weight(1f),

                        value =
                            workOrders.size.toString(),

                        label =
                            "Total WO",

                        accent =
                            Color(0xFF1769FF),

                        background =
                            Color(0xFFEAF2FF)
                    )

                    ProfileStat(
                        modifier =
                            Modifier.weight(1f),

                        value =
                            activeCount.toString(),

                        label =
                            "Berjalan",

                        accent =
                            Color(0xFFD98600),

                        background =
                            Color(0xFFFFF4DF)
                    )

                    ProfileStat(
                        modifier =
                            Modifier.weight(1f),

                        value =
                            completedCount.toString(),

                        label =
                            "Selesai",

                        accent =
                            Color(0xFF159A67),

                        background =
                            Color(0xFFE8F7EF)
                    )
                }
            }
        }

        Spacer(
            modifier =
                Modifier.height(22.dp)
        )

        ProfileSectionTitle(
            title =
                "Aktivitas",

            subtitle =
                "Pantau aktivitas operasional Anda"
        )

        Spacer(
            modifier =
                Modifier.height(9.dp)
        )

        Card(
            modifier =
                Modifier.fillMaxWidth(),

            shape =
                RoundedCornerShape(21.dp),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        Color.White
                ),

            border =
                BorderStroke(
                    1.dp,
                    Color(0xFFE5EAF2)
                ),

            elevation =
                CardDefaults.cardElevation(
                    defaultElevation = 2.dp
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
                Modifier.height(21.dp)
        )

        ProfileSectionTitle(
            title =
                "Pengaturan",

            subtitle =
                "Atur preferensi aplikasi FieldOps"
        )

        Spacer(
            modifier =
                Modifier.height(9.dp)
        )

        Card(
            modifier =
                Modifier.fillMaxWidth(),

            shape =
                RoundedCornerShape(21.dp),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        Color.White
                ),

            border =
                BorderStroke(
                    1.dp,
                    Color(0xFFE5EAF2)
                ),

            elevation =
                CardDefaults.cardElevation(
                    defaultElevation = 2.dp
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

                onCheckedChange =
                    onNotificationsEnabledChange
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

                onCheckedChange =
                    onAutoSyncEnabledChange
            )
        }

        Spacer(
            modifier =
                Modifier.height(21.dp)
        )

        ProfileSectionTitle(
            title =
                "Status Sistem",

            subtitle =
                "Kondisi koneksi perangkat saat ini"
        )

        Spacer(
            modifier =
                Modifier.height(9.dp)
        )

        Card(
            modifier =
                Modifier.fillMaxWidth(),

            shape =
                RoundedCornerShape(21.dp),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        Color.White
                ),

            border =
                BorderStroke(
                    1.dp,
                    Color(0xFFE5EAF2)
                ),

            elevation =
                CardDefaults.cardElevation(
                    defaultElevation = 2.dp
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

                Box(
                    modifier =
                        Modifier
                            .size(48.dp)
                            .background(
                                if (isOnline) {
                                    Color(0xFFE8F7EF)
                                } else {
                                    Color(0xFFFFF4DF)
                                },
                                RoundedCornerShape(15.dp)
                            ),

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
                            19.sp,

                        fontWeight =
                            FontWeight.Bold
                    )
                }

                Spacer(
                    modifier =
                        Modifier.width(13.dp)
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
                            12.sp,

                        lineHeight =
                            17.sp
                    )
                }

                Spacer(
                    modifier =
                        Modifier.width(8.dp)
                )

                Surface(
                    shape =
                        RoundedCornerShape(50.dp),

                    color =
                        if (isOnline) {
                            Color(0xFFE8F7EF)
                        } else {
                            Color(0xFFFFF4DF)
                        }
                ) {

                    Text(
                        modifier =
                            Modifier.padding(
                                horizontal = 9.dp,
                                vertical = 5.dp
                            ),

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
                            9.sp,

                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        }

        Spacer(
            modifier =
                Modifier.height(21.dp)
        )

        ProfileSectionTitle(
            title =
                "Tentang",

            subtitle =
                "Informasi aplikasi"
        )

        Spacer(
            modifier =
                Modifier.height(9.dp)
        )

        Card(
            modifier =
                Modifier.fillMaxWidth(),

            shape =
                RoundedCornerShape(21.dp),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        Color.White
                ),

            border =
                BorderStroke(
                    1.dp,
                    Color(0xFFE5EAF2)
                ),

            elevation =
                CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
        ) {

            Column(
                modifier =
                    Modifier.padding(19.dp)
            ) {

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Box(
                        modifier =
                            Modifier
                                .size(44.dp)
                                .background(
                                    Color(0xFFEAF2FF),
                                    RoundedCornerShape(14.dp)
                                ),

                        contentAlignment =
                            Alignment.Center
                    ) {

                        Text(
                            text =
                                "F",

                            color =
                                Color(0xFF1769FF),

                            fontSize =
                                19.sp,

                            fontWeight =
                                FontWeight.Bold
                        )
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
                                "FieldOps",

                            color =
                                Color(0xFF10213F),

                            fontSize =
                                18.sp,

                            fontWeight =
                                FontWeight.Bold
                        )

                        Spacer(
                            modifier =
                                Modifier.height(2.dp)
                        )

                        Text(
                            text =
                                "Field Service Management",

                            color =
                                Color(0xFF718096),

                            fontSize =
                                12.sp
                        )
                    }

                    Surface(
                        shape =
                            RoundedCornerShape(50.dp),

                        color =
                            Color(0xFFF2F5FA)
                    ) {

                        Text(
                            modifier =
                                Modifier.padding(
                                    horizontal = 9.dp,
                                    vertical = 5.dp
                                ),

                            text =
                                "v1.0.0",

                            color =
                                Color(0xFF718096),

                            fontSize =
                                9.sp,

                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(15.dp)
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
            }
        }

        Spacer(
            modifier =
                Modifier.height(21.dp)
        )

        OutlinedButton(
            onClick =
                onLogout,

            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(52.dp),

            shape =
                RoundedCornerShape(16.dp),

            border =
                BorderStroke(
                    1.dp,
                    Color(0xFFF1C4CB)
                ),

            colors =
                ButtonDefaults.outlinedButtonColors(
                    containerColor =
                        Color.White
                )
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
                Modifier.height(20.dp)
        )
    }
}

@Composable
private fun ProfileStat(
    modifier: Modifier,
    value: String,
    label: String,
    accent: Color,
    background: Color
) {

    Surface(
        modifier =
            modifier,

        shape =
            RoundedCornerShape(16.dp),

        color =
            background
    ) {

        Column(
            modifier =
                Modifier.padding(
                    horizontal = 7.dp,
                    vertical = 12.dp
                ),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text =
                    value,

                color =
                    accent,

                fontSize =
                    20.sp,

                fontWeight =
                    FontWeight.Bold,

                maxLines =
                    1
            )

            Spacer(
                modifier =
                    Modifier.height(3.dp)
            )

            Text(
                text =
                    label,

                color =
                    Color(0xFF718096),

                fontSize =
                    10.sp,

                fontWeight =
                    FontWeight.Medium,

                maxLines =
                    1,

                overflow =
                    TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ProfileSectionTitle(
    title: String,
    subtitle: String
) {

    Column {

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

        Spacer(
            modifier =
                Modifier.height(2.dp)
        )

        Text(
            text =
                subtitle,

            color =
                Color(0xFF8A96A8),

            fontSize =
                11.sp,

            lineHeight =
                16.sp
        )
    }
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
                    onClick = onClick
                )
                .padding(18.dp),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Box(
            modifier =
                Modifier
                    .size(48.dp)
                    .background(
                        Color(0xFFEAF2FF),
                        RoundedCornerShape(15.dp)
                    ),

            contentAlignment =
                Alignment.Center
        ) {

            Text(
                text =
                    icon,

                color =
                    Color(0xFF1769FF),

                fontSize =
                    21.sp,

                fontWeight =
                    FontWeight.Bold
            )
        }

        Spacer(
            modifier =
                Modifier.width(13.dp)
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
                    12.sp,

                lineHeight =
                    17.sp
            )
        }

        Spacer(
            modifier =
                Modifier.width(8.dp)
        )

        Box(
            modifier =
                Modifier
                    .size(30.dp)
                    .background(
                        Color(0xFFF5F7FB),
                        RoundedCornerShape(10.dp)
                    ),

            contentAlignment =
                Alignment.Center
        ) {

            Text(
                text =
                    "›",

                color =
                    Color(0xFF718096),

                fontSize =
                    21.sp,

                fontWeight =
                    FontWeight.Medium
            )
        }
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
                    vertical = 15.dp
                ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Box(
            modifier =
                Modifier
                    .size(46.dp)
                    .background(
                        if (checked) {
                            Color(0xFFEAF2FF)
                        } else {
                            Color(0xFFF2F5FA)
                        },
                        RoundedCornerShape(15.dp)
                    ),

            contentAlignment =
                Alignment.Center
        ) {

            Text(
                text =
                    icon,

                color =
                    if (checked) {
                        Color(0xFF1769FF)
                    } else {
                        Color(0xFF8A96A8)
                    },

                fontSize =
                    19.sp,

                fontWeight =
                    FontWeight.Medium
            )
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
                    16.sp
            )

            Spacer(
                modifier =
                    Modifier.height(5.dp)
            )

            Text(
                text =
                    if (checked) {
                        "Aktif"
                    } else {
                        "Dinonaktifkan"
                    },

                color =
                    if (checked) {
                        Color(0xFF159A67)
                    } else {
                        Color(0xFF8A96A8)
                    },

                fontSize =
                    10.sp,

                fontWeight =
                    FontWeight.SemiBold
            )
        }

        Spacer(
            modifier =
                Modifier.width(8.dp)
        )

        Switch(
            checked =
                checked,

            onCheckedChange =
                onCheckedChange
        )
    }
}
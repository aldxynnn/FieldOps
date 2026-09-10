package com.example.fieldops.ui.customer

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.statusBarsPadding
import com.example.fieldops.data.model.Customer
import com.example.fieldops.data.model.CustomerAsset
import com.example.fieldops.data.model.CustomerSite
import com.example.fieldops.data.model.PreventiveMaintenance
import com.example.fieldops.data.model.WorkOrder
import com.example.fieldops.data.model.WorkOrderStatus
import com.example.fieldops.data.model.buildAssetServiceHistory
import com.example.fieldops.data.model.findPreventiveMaintenance

private val CustomerBackground = Color(0xFFF5F7FB)
private val CustomerWhite = Color.White
private val CustomerDark = Color(0xFF0F1F3D)
private val CustomerText = Color(0xFF334155)
private val CustomerSecondary = Color(0xFF718096)
private val CustomerMuted = Color(0xFF9AA6B6)
private val CustomerBlue = Color(0xFF2563EB)
private val CustomerBlueSoft = Color(0xFFEAF2FF)
private val CustomerGreen = Color(0xFF159A67)
private val CustomerGreenSoft = Color(0xFFE8F7EF)
private val CustomerOrange = Color(0xFFD98600)
private val CustomerOrangeSoft = Color(0xFFFFF4DF)
private val CustomerRed = Color(0xFFD92D20)
private val CustomerRedSoft = Color(0xFFFFECEC)
private val CustomerBorder = Color(0xFFE4EAF2)

@Composable
fun CustomerDetailScreen(
    customerName: String,
    customers: List<Customer>,
    workOrders: List<WorkOrder>,
    onBack: () -> Unit,
    onWorkOrderClick: (WorkOrder) -> Unit,
    onCreateMaintenanceWorkOrder: (PreventiveMaintenance) -> Unit
) {

    val customer =
        customers.firstOrNull {
            it.companyName.equals(
                customerName,
                ignoreCase = true
            )
        }

    var selectedAsset by remember {
        mutableStateOf<CustomerAsset?>(null)
    }

    BackHandler {
        if (selectedAsset != null) {
            selectedAsset = null
        } else {
            onBack()
        }
    }

    if (customer == null) {

        CustomerNotFoundScreen(
            onBack = onBack
        )

        return
    }

    if (selectedAsset != null) {

        AssetDetailScreen(
            customer = customer,
            asset = selectedAsset!!,
            workOrders = workOrders,
            onBack = {
                selectedAsset = null
            },
            onWorkOrderClick = onWorkOrderClick,
            onCreateMaintenanceWorkOrder = onCreateMaintenanceWorkOrder
        )

        return
    }

    val customerWorkOrders =
        workOrders.filter {
            it.customer.equals(
                customer.companyName,
                ignoreCase = true
            )
        }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(CustomerBackground)
    ) {

        CustomerDetailHeader(
            customer = customer,
            onBack = onBack
        )

        LazyColumn(
            modifier =
                Modifier.fillMaxSize(),
            contentPadding =
                PaddingValues(
                    start = 18.dp,
                    end = 18.dp,
                    top = 16.dp,
                    bottom = 28.dp
                ),
            verticalArrangement =
                Arrangement.spacedBy(14.dp)
        ) {

            item {

                CustomerOverviewCard(
                    customer = customer,
                    workOrders = customerWorkOrders
                )
            }

            item {

                SectionHeader(
                    title = "Site Pelanggan"
                )
            }

            items(
                items = customer.sites,
                key = {
                    it.id
                }
            ) { site ->

                SiteCard(
                    site = site,
                    onAssetClick = {
                        selectedAsset = it
                    }
                )
            }

            item {

                SectionHeader(
                    title = "Work Order",
                    count = customerWorkOrders.size
                )
            }

            if (customerWorkOrders.isEmpty()) {

                item {

                    EmptyWorkOrderCard()
                }

            } else {

                items(
                    items = customerWorkOrders,
                    key = {
                        it.id
                    }
                ) { workOrder ->

                    CustomerWorkOrderCard(
                        workOrder = workOrder,
                        onClick = {
                            onWorkOrderClick(
                                workOrder
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CustomerDetailHeader(
    customer: Customer,
    onBack: () -> Unit
) {

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .background(CustomerBackground)
                .padding(
                    horizontal = 14.dp,
                    vertical = 10.dp
                ),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Surface(
            modifier =
                Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .clickable {
                        onBack()
                    },
            shape = CircleShape,
            color = CustomerBlueSoft
        ) {

            Box(
                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text = "‹",
                    color = CustomerBlue,
                    fontSize = 31.sp,
                    fontWeight = FontWeight.Light
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
                text = "DETAIL PELANGGAN",
                color = CustomerMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )

            Spacer(
                modifier =
                    Modifier.height(2.dp)
            )

            Text(
                text = customer.companyName,
                color = CustomerDark,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun CustomerOverviewCard(
    customer: Customer,
    workOrders: List<WorkOrder>
) {

    val active =
        workOrders.count {
            it.status ==
                    WorkOrderStatus.ACCEPTED ||
                    it.status ==
                    WorkOrderStatus.IN_PROGRESS
        }

    val completed =
        workOrders.count {
            it.status ==
                    WorkOrderStatus.COMPLETED
        }

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = CustomerBorder,
                    shape = RoundedCornerShape(22.dp)
                ),
        shape =
            RoundedCornerShape(22.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = CustomerWhite
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
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
                        Modifier.size(58.dp),
                    shape =
                        RoundedCornerShape(17.dp),
                    color =
                        CustomerBlueSoft
                ) {

                    Box(
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Text(
                            text =
                                customer.companyName
                                    .take(2)
                                    .uppercase(),
                            color = CustomerBlue,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
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
                        text = customer.industry,
                        color = CustomerSecondary,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(
                        modifier =
                            Modifier.height(3.dp)
                    )

                    Text(
                        text = customer.contactPerson,
                        color = CustomerDark,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )

            Surface(
                modifier =
                    Modifier.fillMaxWidth(),
                shape =
                    RoundedCornerShape(13.dp),
                color = CustomerBackground
            ) {

                Text(
                    text = customer.address,
                    modifier =
                        Modifier.padding(
                            horizontal = 13.dp,
                            vertical = 11.dp
                        ),
                    color = CustomerText,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(
                modifier =
                    Modifier.height(15.dp)
            )

            Text(
                text = "Ringkasan Pelanggan",
                color = CustomerDark,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.height(9.dp)
            )

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                OverviewMetric(
                    modifier =
                        Modifier.weight(1f),
                    value =
                        customer.sites.size.toString(),
                    label = "Site"
                )

                OverviewMetric(
                    modifier =
                        Modifier.weight(1f),
                    value =
                        customer.sites.sumOf {
                            it.assets.size
                        }.toString(),
                    label = "Asset"
                )

                OverviewMetric(
                    modifier =
                        Modifier.weight(1f),
                    value =
                        workOrders.size.toString(),
                    label = "Total WO"
                )

                OverviewMetric(
                    modifier =
                        Modifier.weight(1f),
                    value =
                        active.toString(),
                    label = "Aktif"
                )
            }

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            Surface(
                modifier =
                    Modifier.fillMaxWidth(),
                shape =
                    RoundedCornerShape(13.dp),
                color = CustomerBlueSoft
            ) {

                Row(
                    modifier =
                        Modifier.padding(
                            horizontal = 13.dp,
                            vertical = 10.dp
                        ),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Box(
                        modifier =
                            Modifier
                                .size(7.dp)
                                .background(
                                    CustomerBlue,
                                    CircleShape
                                )
                    )

                    Spacer(
                        modifier =
                            Modifier.width(8.dp)
                    )

                    Text(
                        text =
                            "${customer.sites.size} site terdaftar",
                        color = CustomerBlue,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier =
                            Modifier.weight(1f)
                    )

                    Text(
                        text =
                            "${customer.sites.sumOf { it.assets.size }} asset",
                        color = CustomerSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            if (completed > 0) {

                Spacer(
                    modifier =
                        Modifier.height(11.dp)
                )

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Box(
                        modifier =
                            Modifier
                                .size(7.dp)
                                .background(
                                    CustomerGreen,
                                    CircleShape
                                )
                    )

                    Spacer(
                        modifier =
                            Modifier.width(7.dp)
                    )

                    Text(
                        text =
                            "$completed Work Order telah selesai",
                        color = CustomerSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun OverviewMetric(
    modifier: Modifier,
    value: String,
    label: String
) {

    Surface(
        modifier = modifier,
        shape =
            RoundedCornerShape(13.dp),
        color =
            CustomerBackground
    ) {

        Column(
            modifier =
                Modifier.padding(
                    vertical = 10.dp,
                    horizontal = 5.dp
                ),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text = value,
                color = CustomerDark,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.height(2.dp)
            )

            Text(
                text = label,
                color = CustomerSecondary,
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    count: Int? = null
) {

    Row(
        modifier =
            Modifier.fillMaxWidth(),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Text(
            text = title,
            modifier =
                Modifier.weight(1f),
            color = CustomerDark,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        if (count != null) {

            Surface(
                shape =
                    RoundedCornerShape(50.dp),
                color = CustomerBlueSoft
            ) {

                Text(
                    text = count.toString(),
                    modifier =
                        Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 5.dp
                        ),
                    color = CustomerBlue,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SiteCard(
    site: CustomerSite,
    onAssetClick: (CustomerAsset) -> Unit
) {

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = CustomerBorder,
                    shape = RoundedCornerShape(19.dp)
                ),
        shape =
            RoundedCornerShape(19.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = CustomerWhite
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 1.dp
            )
    ) {

        Column(
            modifier =
                Modifier.padding(16.dp)
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Surface(
                    modifier =
                        Modifier.size(42.dp),
                    shape =
                        RoundedCornerShape(13.dp),
                    color =
                        CustomerBlueSoft
                ) {

                    Box(
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Text(
                            text = "⌂",
                            color = CustomerBlue,
                            fontSize = 21.sp
                        )
                    }
                }

                Spacer(
                    modifier =
                        Modifier.width(11.dp)
                )

                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        text = site.siteName,
                        color = CustomerDark,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(
                        modifier =
                            Modifier.height(3.dp)
                    )

                    Text(
                        text = site.city,
                        color = CustomerSecondary,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Surface(
                    shape =
                        RoundedCornerShape(50.dp),
                    color = CustomerGreenSoft
                ) {

                    Text(
                        text =
                            "${site.assets.size} asset",
                        modifier =
                            Modifier.padding(
                                horizontal = 9.dp,
                                vertical = 5.dp
                            ),
                        color = CustomerGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(13.dp)
            )

            Text(
                text = site.address,
                color = CustomerText,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(
                modifier =
                    Modifier.height(5.dp)
            )

            Text(
                text =
                    "PIC: ${site.contactPerson} • ${site.phone}",
                color = CustomerSecondary,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (site.assets.isNotEmpty()) {

                Spacer(
                    modifier =
                        Modifier.height(14.dp)
                )

                HorizontalDivider(
                    color = CustomerBorder
                )

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Text(
                        text = "Equipment",
                        modifier =
                            Modifier.weight(1f),
                        color = CustomerDark,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Ketuk untuk detail",
                        color = CustomerBlue,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                site.assets.forEach { asset ->

                    AssetRow(
                        asset = asset,
                        onClick = {
                            onAssetClick(asset)
                        }
                    )

                    if (
                        asset.id !=
                        site.assets.last().id
                    ) {

                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AssetRow(
    asset: CustomerAsset,
    onClick: () -> Unit
) {

    Surface(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable {
                    onClick()
                },
        shape =
            RoundedCornerShape(13.dp),
        color = CustomerBackground
    ) {

        Row(
            modifier =
                Modifier.padding(10.dp),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Surface(
                modifier =
                    Modifier.size(36.dp),
                shape =
                    RoundedCornerShape(10.dp),
                color = CustomerWhite
            ) {

                Box(
                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text = "▣",
                        color = CustomerBlue,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.width(9.dp)
            )

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(
                    text = asset.name,
                    color = CustomerText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(
                    modifier =
                        Modifier.height(2.dp)
                )

                Text(
                    text =
                        "${asset.assetCode} • ${asset.brand} ${asset.model}",
                    color = CustomerSecondary,
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Surface(
                shape =
                    RoundedCornerShape(50.dp),
                color =
                    if (
                        asset.status.equals(
                            "Aktif",
                            ignoreCase = true
                        ) ||
                        asset.status.equals(
                            "ACTIVE",
                            ignoreCase = true
                        )
                    ) {
                        CustomerGreenSoft
                    } else {
                        CustomerOrangeSoft
                    }
            ) {

                Text(
                    text = asset.status,
                    modifier =
                        Modifier.padding(
                            horizontal = 8.dp,
                            vertical = 4.dp
                        ),
                    color =
                        if (
                            asset.status.equals(
                                "Aktif",
                                ignoreCase = true
                            ) ||
                            asset.status.equals(
                                "ACTIVE",
                                ignoreCase = true
                            )
                        ) {
                            CustomerGreen
                        } else {
                            CustomerOrange
                        },
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun AssetDetailScreen(
    customer: Customer,
    asset: CustomerAsset,
    workOrders: List<WorkOrder>,
    onBack: () -> Unit,
    onWorkOrderClick: (WorkOrder) -> Unit,
    onCreateMaintenanceWorkOrder: (PreventiveMaintenance) -> Unit
) {

    var selectedMaintenance by remember {
        mutableStateOf<PreventiveMaintenance?>(null)
    }

    if (selectedMaintenance != null) {

        PreventiveMaintenanceDetailScreen(
            maintenance = selectedMaintenance!!,
            onBack = {
                selectedMaintenance = null
            },
            onCreateWorkOrder = {
                onCreateMaintenanceWorkOrder(
                    selectedMaintenance!!
                )
                selectedMaintenance = null
            }
        )

        return
    }

    val site =
        customer.sites.firstOrNull { currentSite ->
            currentSite.assets.any {
                it.id == asset.id
            }
        }

    val assetServiceHistory =
        remember(
            asset.id,
            workOrders
        ) {
            buildAssetServiceHistory(
                asset = asset,
                customer = customer,
                site = site,
                workOrders = workOrders
            )
        }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(CustomerBackground)
    ) {

        Surface(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .statusBarsPadding(),
            color = CustomerBackground,
            shadowElevation = 0.dp
        ) {

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 14.dp,
                            vertical = 10.dp
                        ),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Surface(
                    modifier =
                        Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .clickable {
                                onBack()
                            },
                    shape = CircleShape,
                    color = CustomerBlueSoft
                ) {

                    Box(
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Text(
                            text = "‹",
                            color = CustomerBlue,
                            fontSize = 31.sp,
                            fontWeight = FontWeight.Light
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
                        text = "DETAIL ASSET",
                        color = CustomerMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )

                    Spacer(
                        modifier =
                            Modifier.height(2.dp)
                    )

                    Text(
                        text = asset.name,
                        color = CustomerDark,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        LazyColumn(
            modifier =
                Modifier.fillMaxSize(),
            contentPadding =
                PaddingValues(
                    start = 18.dp,
                    end = 18.dp,
                    top = 16.dp,
                    bottom = 28.dp
                ),
            verticalArrangement =
                Arrangement.spacedBy(14.dp)
        ) {

            item {

                AssetHeroCard(
                    asset = asset
                )
            }

            item {

                AssetInformationCard(
                    asset = asset,
                    customer = customer,
                    site = site
                )
            }

            item {

                AssetPreventiveMaintenanceSection(
                    maintenance =
                        findPreventiveMaintenance(
                            asset.id
                        ),
                    onScheduleClick = {
                        selectedMaintenance =
                            findPreventiveMaintenance(
                                asset.id
                            )
                    }
                )
            }

            item {

                AssetServiceHistorySection(
                    history = assetServiceHistory,
                    onWorkOrderClick = { workOrderId ->

                        workOrders
                            .firstOrNull {
                                it.id == workOrderId
                            }
                            ?.let(
                                onWorkOrderClick
                            )
                    }
                )
            }

            if (
                assetServiceHistory.isEmpty()
            ) {

                item {

                    EmptyServiceHistoryCard()
                }
            }
        }
    }
}

@Composable
private fun AssetHeroCard(
    asset: CustomerAsset
) {

    val active =
        asset.status.equals(
            "Aktif",
            ignoreCase = true
        ) ||
                asset.status.equals(
                    "ACTIVE",
                    ignoreCase = true
                )

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = CustomerBorder,
                    shape = RoundedCornerShape(22.dp)
                ),
        shape =
            RoundedCornerShape(22.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = CustomerWhite
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
    ) {

        Column(
            modifier =
                Modifier.padding(20.dp)
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Surface(
                    modifier =
                        Modifier.size(62.dp),
                    shape =
                        RoundedCornerShape(18.dp),
                    color = CustomerBlueSoft
                ) {

                    Box(
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Text(
                            text = "▣",
                            color = CustomerBlue,
                            fontSize = 27.sp,
                            fontWeight = FontWeight.Bold
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
                        text = asset.assetCode,
                        color = CustomerSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(
                        modifier =
                            Modifier.height(4.dp)
                    )

                    Text(
                        text = asset.name,
                        color = CustomerDark,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(17.dp)
            )

            Surface(
                shape =
                    RoundedCornerShape(50.dp),
                color =
                    if (active) {
                        CustomerGreenSoft
                    } else {
                        CustomerOrangeSoft
                    }
            ) {

                Text(
                    text =
                        if (active) {
                            "Asset Aktif"
                        } else {
                            asset.status
                        },
                    modifier =
                        Modifier.padding(
                            horizontal = 11.dp,
                            vertical = 6.dp
                        ),
                    color =
                        if (active) {
                            CustomerGreen
                        } else {
                            CustomerOrange
                        },
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )

            Surface(
                modifier =
                    Modifier.fillMaxWidth(),
                shape =
                    RoundedCornerShape(13.dp),
                color = CustomerBackground
            ) {

                Column(
                    modifier =
                        Modifier.padding(12.dp)
                ) {

                    Text(
                        text =
                            "${asset.brand} ${asset.model}",
                        color = CustomerText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(
                        modifier =
                            Modifier.height(4.dp)
                    )

                    Text(
                        text =
                            "Serial Number: ${asset.serialNumber}",
                        color = CustomerSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun AssetInformationCard(
    asset: CustomerAsset,
    customer: Customer,
    site: CustomerSite?
) {

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = CustomerBorder,
                    shape = RoundedCornerShape(20.dp)
                ),
        shape =
            RoundedCornerShape(20.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = CustomerWhite
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 1.dp
            )
    ) {

        Column(
            modifier =
                Modifier.padding(18.dp)
        ) {

            Text(
                text = "Informasi Equipment",
                color = CustomerDark,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )

            AssetInfoRow(
                label = "Customer",
                value = customer.companyName
            )

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            AssetInfoRow(
                label = "Site",
                value = site?.siteName ?: "-"
            )

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            AssetInfoRow(
                label = "Lokasi",
                value = site?.address ?: "-"
            )

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            AssetInfoRow(
                label = "Asset Code",
                value = asset.assetCode
            )

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            AssetInfoRow(
                label = "Brand",
                value = asset.brand
            )

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            AssetInfoRow(
                label = "Model",
                value = asset.model
            )

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            AssetInfoRow(
                label = "Serial Number",
                value = asset.serialNumber
            )
        }
    }
}

@Composable
private fun AssetInfoRow(
    label: String,
    value: String
) {

    Row(
        modifier =
            Modifier.fillMaxWidth()
    ) {

        Text(
            text = label,
            modifier =
                Modifier.width(92.dp),
            color = CustomerMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )

        Text(
            text = value,
            modifier =
                Modifier.weight(1f),
            color = CustomerText,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun EmptyServiceHistoryCard() {

    Surface(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(18.dp),
        color = CustomerWhite
    ) {

        Column(
            modifier =
                Modifier.padding(20.dp),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text = "Belum ada riwayat service",
                color = CustomerDark,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.height(5.dp)
            )

            Text(
                text =
                    "Riwayat pekerjaan asset akan tampil di sini.",
                color = CustomerSecondary,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun CustomerWorkOrderCard(
    workOrder: WorkOrder,
    onClick: () -> Unit
) {

    val statusText =
        when (workOrder.status) {

            WorkOrderStatus.PENDING ->
                "Menunggu"

            WorkOrderStatus.ACCEPTED ->
                "Diterima"

            WorkOrderStatus.IN_PROGRESS ->
                "Dikerjakan"

            WorkOrderStatus.COMPLETED ->
                "Selesai"

            WorkOrderStatus.CANCELLED ->
                "Dibatalkan"
        }

    val statusBackground =
        when (workOrder.status) {

            WorkOrderStatus.COMPLETED ->
                CustomerGreenSoft

            WorkOrderStatus.CANCELLED ->
                CustomerRedSoft

            WorkOrderStatus.IN_PROGRESS,
            WorkOrderStatus.ACCEPTED ->
                CustomerBlueSoft

            WorkOrderStatus.PENDING ->
                CustomerOrangeSoft
        }

    val statusForeground =
        when (workOrder.status) {

            WorkOrderStatus.COMPLETED ->
                CustomerGreen

            WorkOrderStatus.CANCELLED ->
                CustomerRed

            WorkOrderStatus.IN_PROGRESS,
            WorkOrderStatus.ACCEPTED ->
                CustomerBlue

            WorkOrderStatus.PENDING ->
                CustomerOrange
        }

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = CustomerBorder,
                    shape = RoundedCornerShape(19.dp)
                )
                .clickable {
                    onClick()
                },
        shape =
            RoundedCornerShape(19.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = CustomerWhite
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 1.dp
            )
    ) {

        Column(
            modifier =
                Modifier.padding(16.dp)
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        text = workOrder.id,
                        color = CustomerBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier =
                            Modifier.height(4.dp)
                    )

                    Text(
                        text = workOrder.title,
                        color = CustomerDark,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(
                    modifier =
                        Modifier.width(8.dp)
                )

                Surface(
                    shape =
                        RoundedCornerShape(50.dp),
                    color = statusBackground
                ) {

                    Text(
                        text = statusText,
                        modifier =
                            Modifier.padding(
                                horizontal = 9.dp,
                                vertical = 5.dp
                            ),
                        color = statusForeground,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(11.dp)
            )

            Text(
                text =
                    "⌖  ${workOrder.location}",
                color = CustomerSecondary,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(
                modifier =
                    Modifier.height(5.dp)
            )

            Text(
                text =
                    "${workOrder.date} • ${workOrder.time}",
                color = CustomerSecondary,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun EmptyWorkOrderCard() {

    Surface(
        modifier =
            Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = CustomerBorder,
                    shape = RoundedCornerShape(18.dp)
                ),
        shape =
            RoundedCornerShape(18.dp),
        color = CustomerWhite
    ) {

        Column(
            modifier =
                Modifier.padding(20.dp),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Surface(
                modifier =
                    Modifier.size(44.dp),
                shape = CircleShape,
                color = CustomerBlueSoft
            ) {

                Box(
                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text = "✓",
                        color = CustomerBlue,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            Text(
                text = "Belum ada Work Order",
                color = CustomerDark,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.height(4.dp)
            )

            Text(
                text =
                    "Work Order untuk pelanggan ini akan muncul di sini.",
                color = CustomerSecondary,
                fontSize = 11.sp,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun CustomerNotFoundScreen(
    onBack: () -> Unit
) {

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(CustomerBackground)
                .statusBarsPadding()
                .padding(24.dp),
        horizontalAlignment =
            Alignment.CenterHorizontally,
        verticalArrangement =
            Arrangement.Center
    ) {

        Surface(
            modifier =
                Modifier.size(64.dp),
            shape = CircleShape,
            color = CustomerBlueSoft
        ) {

            Box(
                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text = "?",
                    color = CustomerBlue,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(
            modifier =
                Modifier.height(16.dp)
        )

        Text(
            text = "Pelanggan tidak ditemukan",
            color = CustomerDark,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        Text(
            text = "Data pelanggan tidak tersedia.",
            color = CustomerSecondary,
            fontSize = 13.sp
        )

        Spacer(
            modifier =
                Modifier.height(18.dp)
        )

        Surface(
            modifier =
                Modifier.clickable {
                    onBack()
                },
            shape =
                RoundedCornerShape(14.dp),
            color = CustomerBlue
        ) {

            Text(
                text = "Kembali",
                modifier =
                    Modifier.padding(
                        horizontal = 22.dp,
                        vertical = 11.dp
                    ),
                color = CustomerWhite,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
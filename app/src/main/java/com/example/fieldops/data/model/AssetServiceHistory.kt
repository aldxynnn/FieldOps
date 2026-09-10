package com.example.fieldops.data.model

data class AssetServiceHistoryItem(
    val workOrderId: String,
    val title: String,
    val date: String,
    val time: String,
    val status: WorkOrderStatus,
    val customerName: String,
    val siteName: String,
    val assetCode: String,
    val assetName: String,
    val brand: String,
    val model: String,
    val serialNumber: String
)

fun buildAssetServiceHistory(
    asset: CustomerAsset,
    customer: Customer,
    site: CustomerSite?,
    workOrders: List<WorkOrder>
): List<AssetServiceHistoryItem> {

    if (site == null) return emptyList()

    return workOrders
        .filter { it.assetId == asset.id }
        .map { workOrder ->
            AssetServiceHistoryItem(
                workOrderId = workOrder.id,
                title = workOrder.title,
                date = workOrder.date,
                time = workOrder.time,
                status = workOrder.status,
                customerName = customer.companyName,
                siteName = site.siteName,
                assetCode = asset.assetCode,
                assetName = asset.name,
                brand = asset.brand,
                model = asset.model,
                serialNumber = asset.serialNumber
            )
        }
}

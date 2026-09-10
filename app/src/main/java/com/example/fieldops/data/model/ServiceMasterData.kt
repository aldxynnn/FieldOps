package com.example.fieldops.data.model

data class ServiceMasterData(
    val customerId: String,
    val customerName: String,
    val siteId: String,
    val siteName: String,
    val siteAddress: String,
    val assetId: String,
    val assetCode: String,
    val assetName: String,
    val brand: String,
    val model: String,
    val serialNumber: String,
    val assetStatus: String
)

fun findServiceMasterData(
    workOrder: WorkOrder
): ServiceMasterData? = null

fun WorkOrder.serviceMasterData(): ServiceMasterData? =
    findServiceMasterData(this)

package com.example.fieldops.data.model

data class WorkOrderAssetLink(
    val workOrderId: String,
    val customerId: String,
    val siteId: String,
    val assetId: String
)

fun WorkOrder.findLinkedAsset(): CustomerAsset? = null

fun WorkOrder.findLinkedSite(): CustomerSite? = null

fun WorkOrder.findLinkedCustomer(): Customer? = null

fun WorkOrder.createAssetLink(): WorkOrderAssetLink? = null

package com.example.fieldops.data.model

data class PreventiveMaintenance(
    val id: String,
    val assetId: String,
    val assetCode: String,
    val maintenanceType: String,
    val intervalDays: Int,
    val lastServiceDate: String,
    val nextServiceDate: String,
    val technician: String,
    val status: PreventiveMaintenanceStatus
)

enum class PreventiveMaintenanceStatus {
    SCHEDULED,
    DUE_SOON,
    OVERDUE
}

fun findPreventiveMaintenance(
    assetId: String
): PreventiveMaintenance? = null

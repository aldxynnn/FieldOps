package com.example.fieldops.data.model

enum class WorkOrderStatus {
    PENDING,
    ACCEPTED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}

data class WorkOrder(
    val id: String,
    val title: String,
    val customer: String,
    val location: String,
    val date: String,
    val time: String,
    val status: WorkOrderStatus,
    val priority: String
)

val demoWorkOrders = listOf(
    WorkOrder(
        id = "WO-001234",
        title = "AC Installation",
        customer = "PT. Maju Bersama",
        location = "Jl. Sudirman No. 123, Jakarta Pusat",
        date = "12 Apr 2025",
        time = "08:00 - 10:00",
        status = WorkOrderStatus.IN_PROGRESS,
        priority = "High"
    ),
    WorkOrder(
        id = "WO-001233",
        title = "HVAC Maintenance",
        customer = "CV. Sentosa Jaya",
        location = "Jl. Gatot Subroto, Jakarta Selatan",
        date = "12 Apr 2025",
        time = "10:30 - 12:00",
        status = WorkOrderStatus.IN_PROGRESS,
        priority = "Normal"
    ),
    WorkOrder(
        id = "WO-001232",
        title = "Electrical Repair",
        customer = "PT. Mitra Karya",
        location = "Jl. Kuningan, Jakarta Selatan",
        date = "12 Apr 2025",
        time = "13:00 - 14:30",
        status = WorkOrderStatus.COMPLETED,
        priority = "Normal"
    ),
    WorkOrder(
        id = "WO-001231",
        title = "Equipment Check",
        customer = "PT. Suryo Abadi",
        location = "Jl. TB Simatupang, Jakarta Selatan",
        date = "11 Apr 2025",
        time = "09:00 - 10:00",
        status = WorkOrderStatus.ACCEPTED,
        priority = "Low"
    ),
    WorkOrder(
        id = "WO-001230",
        title = "Preventive Maintenance",
        customer = "PT. Mega Karya",
        location = "Tangerang",
        date = "13 Apr 2025",
        time = "09:00 - 11:00",
        status = WorkOrderStatus.PENDING,
        priority = "Normal"
    )
)
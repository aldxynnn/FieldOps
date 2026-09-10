package com.example.fieldops.data.model

val demoWorkOrders = listOf(
    WorkOrder("WO-001234", "AC Installation", "PT. Maju Bersama", "Jl. Sudirman No. 123, Jakarta Pusat", "12 Apr 2025", "08:00 - 10:00", WorkOrderStatus.IN_PROGRESS, "High"),
    WorkOrder("WO-001233", "HVAC Maintenance", "CV. Sentosa Jaya", "Jl. Gatot Subroto, Jakarta Selatan", "12 Apr 2025", "10:30 - 12:00", WorkOrderStatus.IN_PROGRESS, "Normal"),
    WorkOrder("WO-001232", "Electrical Repair", "PT. Mitra Karya", "Jl. Kuningan, Jakarta Selatan", "12 Apr 2025", "13:00 - 14:30", WorkOrderStatus.COMPLETED, "Normal"),
    WorkOrder("WO-001231", "Equipment Check", "PT. Suryo Abadi", "Jl. TB Simatupang, Jakarta Selatan", "11 Apr 2025", "09:00 - 10:00", WorkOrderStatus.ACCEPTED, "Low"),
    WorkOrder("WO-001230", "Preventive Maintenance", "PT. Mega Karya", "Tangerang", "13 Apr 2025", "09:00 - 11:00", WorkOrderStatus.PENDING, "Normal")
)

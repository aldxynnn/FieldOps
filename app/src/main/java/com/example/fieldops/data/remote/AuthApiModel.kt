package com.example.fieldops.data.remote
data class LoginRequest(
    val email: String,
    val password: String
)
data class LoginUser(
    val id: String,
    val email: String,
    val name: String,
    val role: String,
    val companyId: String = "",
    val active: Boolean = true
)
data class LoginResponse(
    val token: String,
    val user: LoginUser
)
data class StatusUpdateRequest(
    val status: String
)
data class NoteRequest(
    val note: String
)
data class LocationRequest(
    val latitude: Double,
    val longitude: Double
)
data class CreateUserRequest(
    val name: String,
    val email: String,
    val password: String,
    val role: String
)
data class CreateCustomerRequest(
    val companyName: String,
    val industry: String,
    val contactPerson: String,
    val phone: String,
    val email: String,
    val address: String
)
data class CreateWorkOrderRequest(
    val title: String,
    val customer: String,
    val location: String,
    val date: String,
    val time: String,
    val priority: String,
    val assetId: String? = null,
    val assignedTo: String? = null
)
data class AssignmentRequest(
    val assignedTo: String?
)
data class DashboardSummary(
    val total: Int,
    val PENDING: Int,
    val ACCEPTED: Int,
    val IN_PROGRESS: Int,
    val COMPLETED: Int,
    val CANCELLED: Int
)
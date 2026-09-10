package com.example.fieldops.data.remote

import com.example.fieldops.data.model.Customer
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface FieldOpsApi {

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    @GET("me")
    suspend fun me(): LoginUser

    @GET("dashboard/summary")
    suspend fun getDashboardSummary(): DashboardSummary

    @GET("users")
    suspend fun getUsers(): List<LoginUser>

    @POST("users")
    suspend fun createUser(@Body request: CreateUserRequest): LoginUser

    @GET("work-orders")
    suspend fun getWorkOrders(): List<WorkOrderApiModel>

    @GET("work-orders/{id}")
    suspend fun getWorkOrder(
        @Path("id") id: String
    ): WorkOrderApiModel

    @PATCH("work-orders/{id}/status")
    suspend fun updateWorkOrderStatus(
        @Path("id") id: String,
        @Body request: StatusUpdateRequest
    ): WorkOrderApiModel

    @POST("work-orders")
    suspend fun createWorkOrder(@Body request: CreateWorkOrderRequest): WorkOrderApiModel

    @PATCH("work-orders/{id}/assignment")
    suspend fun assignWorkOrder(@Path("id") id: String, @Body request: AssignmentRequest): WorkOrderApiModel

    @GET("customers")
    suspend fun getCustomers(): List<Customer>

    @POST("customers")
    suspend fun createCustomer(@Body request: CreateCustomerRequest): Customer

    @PATCH("customers/{id}")
    suspend fun updateCustomer(@Path("id") id: String, @Body request: CreateCustomerRequest): Customer

    @DELETE("customers/{id}")
    suspend fun deleteCustomer(@Path("id") id: String): Map<String, Boolean>

    @GET("customers/{id}")
    suspend fun getCustomer(
        @Path("id") id: String
    ): Customer

    @GET("activity-history")
    suspend fun getActivityHistory(): List<RemoteActivityHistoryItem>

    @GET("notifications")
    suspend fun getNotifications(): List<RemoteNotificationItem>

    @POST("work-orders/{id}/notes")
    suspend fun addNote(
        @Path("id") id: String,
        @Body request: NoteRequest
    ): RemoteNote

    @GET("work-orders/{id}/notes")
    suspend fun getNotes(
        @Path("id") id: String
    ): List<RemoteNote>

    @POST("work-orders/{id}/location")
    suspend fun saveLocation(
        @Path("id") id: String,
        @Body request: LocationRequest
    ): RemoteLocation

    @GET("work-orders/{id}/location")
    suspend fun getLocation(
        @Path("id") id: String
    ): RemoteLocation?
}

data class RemoteActivityHistoryItem(
    val id: String,
    val workOrderId: String,
    val title: String,
    val description: String,
    val time: String,
    val type: String
)

data class RemoteNotificationItem(
    val id: String,
    val title: String,
    val description: String,
    val time: String,
    val type: String,
    val isRead: Boolean
)

data class RemoteNote(
    val id: String,
    val workOrderId: String,
    val note: String,
    val createdAt: String
)

data class RemoteLocation(
    val workOrderId: String,
    val latitude: Double,
    val longitude: Double,
    val capturedAt: String
)

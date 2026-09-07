package com.example.fieldops.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

interface FieldOpsApi {

    @GET("work-orders")
    suspend fun getWorkOrders(): List<WorkOrderApiModel>

    @GET("work-orders/{id}")
    suspend fun getWorkOrder(
        @Path("id") id: String
    ): WorkOrderApiModel
}
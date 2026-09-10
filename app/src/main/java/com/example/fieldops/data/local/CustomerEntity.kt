package com.example.fieldops.data.local

import androidx.room.Entity

@Entity(
    tableName = "customers",
    primaryKeys = ["companyId", "customerId"]
)
data class CustomerEntity(
    val companyId: String,
    val customerId: String,
    val payload: String
)

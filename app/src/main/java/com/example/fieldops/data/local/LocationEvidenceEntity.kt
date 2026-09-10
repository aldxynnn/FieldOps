package com.example.fieldops.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location_evidence")
data class LocationEvidenceEntity(
    @PrimaryKey
    val workOrderId: String,
    val latitude: Double,
    val longitude: Double,
    val capturedAt: Long
)
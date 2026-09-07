package com.example.fieldops.data.remote

data class WorkOrderApiModel(
    val id: String,
    val title: String,
    val customer: String,
    val location: String,
    val date: String,
    val time: String,
    val status: String,
    val priority: String
)
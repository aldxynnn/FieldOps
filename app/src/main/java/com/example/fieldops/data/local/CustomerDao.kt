package com.example.fieldops.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface CustomerDao {

    @Query("SELECT * FROM customers WHERE companyId = :companyId ORDER BY customerId")
    suspend fun getCustomers(companyId: String): List<CustomerEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomers(customers: List<CustomerEntity>)

    @Query("DELETE FROM customers WHERE companyId = :companyId")
    suspend fun deleteCustomers(companyId: String)

    @Transaction
    suspend fun replaceCustomers(
        companyId: String,
        customers: List<CustomerEntity>
    ) {
        deleteCustomers(companyId)

        if (customers.isNotEmpty()) {
            insertCustomers(customers)
        }
    }
}

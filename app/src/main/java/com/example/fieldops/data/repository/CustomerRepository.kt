package com.example.fieldops.data.repository

import android.content.Context
import com.example.fieldops.data.local.CustomerEntity
import com.example.fieldops.data.local.FieldOpsDatabase
import com.example.fieldops.data.model.Customer
import com.example.fieldops.data.model.CustomerAsset
import com.example.fieldops.data.model.CustomerSite
import com.example.fieldops.data.remote.RetrofitClient
import com.example.fieldops.data.remote.SessionManager
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class CustomerRepository private constructor(
    context: Context
) {

    companion object {
        @Volatile
        private var INSTANCE: CustomerRepository? = null

        fun getInstance(context: Context): CustomerRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: CustomerRepository(
                    context.applicationContext
                ).also {
                    INSTANCE = it
                }
            }
    }

    private val customerDao =
        FieldOpsDatabase.getInstance(context).customerDao()

    private val sessionManager =
        SessionManager(context)

    private val gson = Gson()


    private val customersState =
        MutableStateFlow<List<Customer>>(emptyList())

    fun observeCustomers(): Flow<List<Customer>> =
        customersState

    fun observeCustomer(
        customerName: String
    ): Flow<Customer?> =
        customersState.map { customers ->
            customers.firstOrNull {
                it.companyName.equals(
                    customerName,
                    ignoreCase = true
                )
            }
        }

    fun getCustomer(
        customerName: String
    ): Customer? =
        customersState.value.firstOrNull {
            it.companyName.equals(
                customerName,
                ignoreCase = true
            )
        }

    fun getSites(
        customerName: String
    ): List<CustomerSite> =
        getCustomer(customerName)?.sites ?: emptyList()

    fun getAssets(
        customerName: String
    ): List<CustomerAsset> =
        getSites(customerName)
            .flatMap { it.assets }

    fun searchCustomers(
        query: String
    ): List<Customer> {
        if (query.isBlank()) {
            return customersState.value
        }

        return customersState.value.filter { customer ->
            customer.companyName.contains(
                query,
                ignoreCase = true
            ) || customer.industry.contains(
                query,
                ignoreCase = true
            ) || customer.contactPerson.contains(
                query,
                ignoreCase = true
            ) || customer.address.contains(
                query,
                ignoreCase = true
            )
        }
    }

    fun replaceCustomers(
        customers: List<Customer>
    ) {
        customersState.value = customers
    }

    suspend fun loadFromLocal() {
        val companyId = sessionManager.companyId()

        if (companyId.isBlank()) {
            customersState.value = emptyList()
            return
        }

        val customers =
            customerDao
                .getCustomers(companyId)
                .mapNotNull { entity ->
                    runCatching {
                        gson.fromJson<Customer>(
                            entity.payload,
                            Customer::class.java
                        )
                    }.getOrNull()
                }

        replaceCustomers(customers)
    }

    suspend fun refreshFromServer(): Result<List<Customer>> {
        if (!RetrofitClient.isConfigured) {
            return Result.failure(
                IllegalStateException(
                    "Backend API belum dikonfigurasi."
                )
            )
        }

        val companyId = sessionManager.companyId()

        if (companyId.isBlank()) {
            return Result.failure(
                IllegalStateException(
                    "Company ID sesi belum tersedia."
                )
            )
        }

        return runCatching {
            val customers =
                RetrofitClient.api.getCustomers()

            val entities =
                customers.map { customer ->
                    CustomerEntity(
                        companyId = companyId,
                        customerId = customer.id,
                        payload = gson.toJson(customer)
                    )
                }

            customerDao.replaceCustomers(
                companyId = companyId,
                customers = entities
            )

            replaceCustomers(customers)
            customers
        }
    }
}

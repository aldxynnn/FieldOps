package com.example.fieldops.data.model

data class Customer(
    val id: String,
    val companyName: String,
    val industry: String,
    val contactPerson: String,
    val phone: String,
    val email: String,
    val address: String,
    val sites: List<CustomerSite>
)

data class CustomerSite(
    val id: String,
    val customerId: String,
    val siteName: String,
    val address: String,
    val city: String,
    val contactPerson: String,
    val phone: String,
    val latitude: Double,
    val longitude: Double,
    val assets: List<CustomerAsset>
)

data class CustomerAsset(
    val id: String,
    val siteId: String,
    val assetCode: String,
    val name: String,
    val brand: String,
    val model: String,
    val serialNumber: String,
    val category: String,
    val installationDate: String,
    val status: String
)

fun Customer.findSite(
    siteId: String
): CustomerSite? {
    return sites.firstOrNull {
        it.id == siteId
    }
}

fun CustomerSite.findAsset(
    assetId: String
): CustomerAsset? {
    return assets.firstOrNull {
        it.id == assetId
    }
}

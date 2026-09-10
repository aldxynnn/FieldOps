package com.example.fieldops.ui.management
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fieldops.data.remote.CreateCustomerRequest
import com.example.fieldops.data.remote.CreateUserRequest
import com.example.fieldops.data.remote.CreateWorkOrderRequest
import com.example.fieldops.data.remote.LoginUser
import com.example.fieldops.data.remote.RetrofitClient
import kotlinx.coroutines.launch
@Composable
fun ManagementScreen(
    role: String,
    onChanged: suspend () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var section by remember { mutableStateOf("WORK_ORDER") }
    var message by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    var technicians by remember { mutableStateOf<List<LoginUser>>(emptyList()) }
    var selectedTech by remember { mutableStateOf<LoginUser?>(null) }

    var customerName by remember { mutableStateOf("") }
    var industry by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    var title by remember { mutableStateOf("") }
    var customer by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("MEDIUM") }

    var userName by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    var userPassword by remember { mutableStateOf("") }
    var userRole by remember { mutableStateOf("TECHNICIAN") }

    LaunchedEffect(role) {
        if (role == "ADMIN" || role == "DISPATCHER") {
            technicians = runCatching {
                RetrofitClient.api
                    .getUsers()
                    .filter { user ->
                        user.role == "TECHNICIAN" && user.active
                    }
            }.getOrDefault(emptyList())
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FB))
            .verticalScroll(rememberScrollState())
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Kelola Operasi",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F1F3D)
        )

        Text(
            text = if (role == "ADMIN") {
                "Kelola customer, tim, dan Work Order perusahaan."
            } else {
                "Buat customer dan Work Order untuk tim lapangan."
            },
            color = Color(0xFF718096)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = section == "WORK_ORDER",
                onClick = { section = "WORK_ORDER" },
                label = { Text("Work Order") }
            )

            FilterChip(
                selected = section == "CUSTOMER",
                onClick = { section = "CUSTOMER" },
                label = { Text("Customer") }
            )

            if (role == "ADMIN") {
                FilterChip(
                    selected = section == "USER",
                    onClick = { section = "USER" },
                    label = { Text("Tim") }
                )
            }
        }

        message?.let { text ->
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFEAF2FF),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = text,
                    modifier = Modifier.padding(13.dp),
                    color = Color(0xFF2563EB)
                )
            }
        }

        when (section) {
            "CUSTOMER" -> {
                Field(
                    label = "Nama perusahaan",
                    value = customerName,
                    onChange = { customerName = it }
                )

                Field(
                    label = "Industri",
                    value = industry,
                    onChange = { industry = it }
                )

                Field(
                    label = "Kontak",
                    value = contact,
                    onChange = { contact = it }
                )

                Field(
                    label = "Telepon",
                    value = phone,
                    onChange = { phone = it }
                )

                Field(
                    label = "Email",
                    value = email,
                    onChange = { email = it }
                )

                Field(
                    label = "Alamat",
                    value = address,
                    onChange = { address = it }
                )

                ActionButton(
                    text = "Simpan Customer"
                ) {
                    loading = true

                    scope.launch {
                        runCatching {
                            RetrofitClient.api.createCustomer(
                                CreateCustomerRequest(
                                    companyName = customerName,
                                    industry = industry,
                                    contactPerson = contact,
                                    phone = phone,
                                    email = email,
                                    address = address
                                )
                            )
                        }.onSuccess {
                            message = "Customer berhasil dibuat."

                            customerName = ""
                            industry = ""
                            contact = ""
                            phone = ""
                            email = ""
                            address = ""

                            onChanged()
                        }.onFailure {
                            message = it.message ?: "Gagal membuat customer."
                        }

                        loading = false
                    }
                }
            }

            "USER" -> {
                Field(
                    label = "Nama user",
                    value = userName,
                    onChange = { userName = it }
                )

                Field(
                    label = "Email",
                    value = userEmail,
                    onChange = { userEmail = it }
                )

                Field(
                    label = "Password minimal 8 karakter",
                    value = userPassword,
                    onChange = { userPassword = it }
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "TECHNICIAN",
                        "DISPATCHER",
                        "MANAGER"
                    ).forEach { selectedRole ->
                        FilterChip(
                            selected = userRole == selectedRole,
                            onClick = { userRole = selectedRole },
                            label = { Text(selectedRole) }
                        )
                    }
                }

                ActionButton(
                    text = "Buat User"
                ) {
                    loading = true

                    scope.launch {
                        runCatching {
                            RetrofitClient.api.createUser(
                                CreateUserRequest(
                                    name = userName,
                                    email = userEmail,
                                    password = userPassword,
                                    role = userRole
                                )
                            )
                        }.onSuccess {
                            message = "User ${it.name} berhasil dibuat."

                            userName = ""
                            userEmail = ""
                            userPassword = ""
                        }.onFailure {
                            message = it.message ?: "Gagal membuat user."
                        }

                        loading = false
                    }
                }
            }

            else -> {
                Field(
                    label = "Judul pekerjaan",
                    value = title,
                    onChange = { title = it }
                )

                Field(
                    label = "Nama customer",
                    value = customer,
                    onChange = { customer = it }
                )

                Field(
                    label = "Lokasi",
                    value = location,
                    onChange = { location = it }
                )

                Field(
                    label = "Tanggal (YYYY-MM-DD)",
                    value = date,
                    onChange = { date = it }
                )

                Field(
                    label = "Jam (HH:MM)",
                    value = time,
                    onChange = { time = it }
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "LOW",
                        "MEDIUM",
                        "HIGH"
                    ).forEach { selectedPriority ->
                        FilterChip(
                            selected = priority == selectedPriority,
                            onClick = { priority = selectedPriority },
                            label = { Text(selectedPriority) }
                        )
                    }
                }

                if (technicians.isNotEmpty()) {
                    Text(
                        text = "Teknisi",
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0F1F3D)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        technicians.forEach { tech ->
                            FilterChip(
                                selected = selectedTech?.id == tech.id,
                                onClick = { selectedTech = tech },
                                label = { Text(tech.name) }
                            )
                        }
                    }
                }

                ActionButton(
                    text = "Buat Work Order"
                ) {
                    loading = true

                    scope.launch {
                        runCatching {
                            RetrofitClient.api.createWorkOrder(
                                CreateWorkOrderRequest(
                                    title = title,
                                    customer = customer,
                                    location = location,
                                    date = date,
                                    time = time,
                                    priority = priority,
                                    assignedTo = selectedTech?.id
                                )
                            )
                        }.onSuccess {
                            message = "${it.id} berhasil dibuat."

                            title = ""
                            customer = ""
                            location = ""
                            date = ""
                            time = ""
                            selectedTech = null

                            onChanged()
                        }.onFailure {
                            message = it.message ?: "Gagal membuat Work Order."
                        }

                        loading = false
                    }
                }
            }
        }

        if (loading) {
            Text(
                text = "Memproses...",
                color = Color(0xFF718096),
                fontSize = 12.sp
            )
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )
    }
}
@Composable
private fun Field(
    label: String,
    value: String,
    onChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(14.dp)
    )
}
@Composable
private fun ActionButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(15.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF2563EB)
        )
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold
        )
    }
}
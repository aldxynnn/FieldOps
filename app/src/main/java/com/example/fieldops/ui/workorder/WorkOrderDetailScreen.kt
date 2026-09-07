package com.example.fieldops.ui.workorder

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.fieldops.data.local.FieldOpsDatabase
import com.example.fieldops.data.local.LocationEvidenceEntity
import com.example.fieldops.data.location.DeviceLocationManager
import com.example.fieldops.data.model.WorkOrder
import com.example.fieldops.data.model.WorkOrderStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

data class EvidencePhoto(
    val id: String,
    val title: String,
    val image: ImageBitmap
)

private val Blue = Color(0xFF1769FF)
private val Dark = Color(0xFF10213F)
private val Secondary = Color(0xFF718096)
private val Background = Color(0xFFF7FAFE)
private val Green = Color(0xFF13895C)
private val Red = Color(0xFFE94B5F)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkOrderDetailScreen(
    workOrder: WorkOrder,
    notes: List<String>,
    photos: List<EvidencePhoto>,
    onBack: () -> Unit,
    onAction: () -> Unit,
    onAddNote: (String) -> Unit,
    onAddPhoto: (EvidencePhoto) -> Unit,
    onRemovePhoto: (String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var noteText by remember(workOrder.id) {
        mutableStateOf("")
    }

    var showPhotoOptions by remember {
        mutableStateOf(false)
    }

    var currentLatitude by remember(workOrder.id) {
        mutableStateOf<Double?>(null)
    }

    var currentLongitude by remember(workOrder.id) {
        mutableStateOf<Double?>(null)
    }

    var locationStatus by remember(workOrder.id) {
        mutableStateOf(
            "Belum mengambil lokasi perangkat"
        )
    }

    var isGettingLocation by remember(workOrder.id) {
        mutableStateOf(false)
    }

    val locationManager = remember(context) {
        DeviceLocationManager(context)
    }

    val database = remember(context) {
        FieldOpsDatabase.getInstance(context)
    }

    val locationEvidenceDao = remember(database) {
        database.locationEvidenceDao()
    }

    LaunchedEffect(workOrder.id) {
        val savedLocation =
            withContext(Dispatchers.IO) {
                locationEvidenceDao.getLocationEvidence(
                    workOrder.id
                )
            }

        if (savedLocation != null) {

            currentLatitude =
                savedLocation.latitude

            currentLongitude =
                savedLocation.longitude

            locationStatus =
                "Lokasi GPS tersimpan"
        }
    }

    val cameraLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicturePreview()
        ) { bitmap ->

            if (bitmap != null) {
                onAddPhoto(
                    EvidencePhoto(
                        id = "camera_${System.currentTimeMillis()}",
                        title = "Foto Kamera ${photos.size + 1}",
                        image = bitmap.asImageBitmap()
                    )
                )
            }
        }

    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted) {
                cameraLauncher.launch(null)
            }
        }

    val galleryLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->

            if (uri != null) {

                coroutineScope.launch {

                    val bitmap = loadBitmapFromUri(
                        context = context,
                        uri = uri
                    )

                    if (bitmap != null) {

                        onAddPhoto(
                            EvidencePhoto(
                                id = "gallery_${System.currentTimeMillis()}",
                                title = "Foto Galeri ${photos.size + 1}",
                                image = bitmap.asImageBitmap()
                            )
                        )
                    }
                }
            }
        }

    val locationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val fineGranted =
                permissions[
                    Manifest.permission.ACCESS_FINE_LOCATION
                ] == true

            val coarseGranted =
                permissions[
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ] == true

            if (fineGranted || coarseGranted) {

                isGettingLocation = true
                locationStatus =
                    "Mengambil lokasi perangkat..."

                locationManager.getCurrentLocation(
                    onLocationReceived = { location ->

                        currentLatitude =
                            location.latitude

                        currentLongitude =
                            location.longitude

                        isGettingLocation = false

                        locationStatus =
                            if (fineGranted) {
                                "Lokasi GPS berhasil diperoleh"
                            } else {
                                "Lokasi berhasil diperoleh (perkiraan)"
                            }

                        val evidence =
                            LocationEvidenceEntity(
                                workOrderId =
                                    workOrder.id,
                                latitude =
                                    location.latitude,
                                longitude =
                                    location.longitude,
                                capturedAt =
                                    System.currentTimeMillis()
                            )

                        coroutineScope.launch(
                            Dispatchers.IO
                        ) {
                            locationEvidenceDao
                                .insertLocationEvidence(
                                    evidence
                                )
                        }
                    },
                    onError = { message ->

                        isGettingLocation = false
                        locationStatus = message

                        Toast.makeText(
                            context,
                            message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )

            } else {

                isGettingLocation = false

                locationStatus =
                    "Izin lokasi diperlukan untuk mengambil posisi."
            }
        }

    DisposableEffect(locationManager) {

        onDispose {
            locationManager.stopListening()
        }
    }

    Scaffold(
        containerColor = Background,

        topBar = {
            DetailTopBar(
                onBack = onBack
            )
        },

        bottomBar = {
            ActionButton(
                workOrder = workOrder,
                onAction = onAction
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(
                    horizontal = 18.dp,
                    vertical = 16.dp
                ),

            verticalArrangement =
                Arrangement.spacedBy(14.dp)
        ) {

            WorkOrderHeader(
                workOrder = workOrder
            )

            InformationCard(
                title = "Informasi Pekerjaan"
            ) {

                InformationRow(
                    symbol = "▣",
                    label = "Jenis Pekerjaan",
                    value = workOrder.title
                )

                InformationRow(
                    symbol = "▤",
                    label = "Tanggal",
                    value = workOrder.date
                )

                InformationRow(
                    symbol = "◷",
                    label = "Waktu",
                    value = workOrder.time
                )
            }

            InformationCard(
                title = "Pelanggan"
            ) {

                InformationRow(
                    symbol = "●",
                    label = "Nama Pelanggan",
                    value = workOrder.customer
                )
            }

            LocationCard(
                location = workOrder.location,
                currentLatitude = currentLatitude,
                currentLongitude = currentLongitude,
                locationStatus = locationStatus,
                isGettingLocation = isGettingLocation,

                onGetCurrentLocation = {

                    val hasPermission =
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) ==
                                PackageManager.PERMISSION_GRANTED ||
                                ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                ) ==
                                PackageManager.PERMISSION_GRANTED

                    if (hasPermission) {

                        if (
                            !locationManager
                                .isLocationAvailable()
                        ) {

                            locationStatus =
                                "Aktifkan GPS atau layanan lokasi perangkat."

                            Toast.makeText(
                                context,
                                "Aktifkan GPS atau layanan lokasi terlebih dahulu.",
                                Toast.LENGTH_LONG
                            ).show()

                        } else {

                            isGettingLocation = true

                            locationStatus =
                                "Mengambil lokasi perangkat..."

                            locationManager
                                .getCurrentLocation(
                                    onLocationReceived = { location ->

                                        currentLatitude =
                                            location.latitude

                                        currentLongitude =
                                            location.longitude

                                        isGettingLocation =
                                            false

                                        locationStatus =
                                            "Lokasi GPS berhasil diperoleh"

                                        val evidence =
                                            LocationEvidenceEntity(
                                                workOrderId =
                                                    workOrder.id,
                                                latitude =
                                                    location.latitude,
                                                longitude =
                                                    location.longitude,
                                                capturedAt =
                                                    System.currentTimeMillis()
                                            )

                                        coroutineScope.launch(
                                            Dispatchers.IO
                                        ) {
                                            locationEvidenceDao
                                                .insertLocationEvidence(
                                                    evidence
                                                )
                                        }
                                    },
                                    onError = { message ->

                                        isGettingLocation =
                                            false

                                        locationStatus =
                                            message

                                        Toast.makeText(
                                            context,
                                            message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                )
                        }

                    } else {

                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                },

                onOpenMaps = {

                    openLocationInMaps(
                        context = context,
                        location = workOrder.location
                    )
                }
            )

            NotesCard(
                noteText = noteText,
                notes = notes,

                onNoteTextChange = {
                    noteText = it
                },

                onSaveNote = {

                    val cleanNote =
                        noteText.trim()

                    if (cleanNote.isNotEmpty()) {

                        onAddNote(cleanNote)

                        noteText = ""
                    }
                }
            )

            PhotoEvidenceCard(
                photos = photos,

                onAddPhoto = {
                    showPhotoOptions = true
                },

                onRemovePhoto = onRemovePhoto
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )
        }
    }

    if (showPhotoOptions) {

        ModalBottomSheet(
            onDismissRequest = {
                showPhotoOptions = false
            },

            sheetState =
                rememberModalBottomSheetState(
                    skipPartiallyExpanded = true
                ),

            containerColor = Color.White
        ) {

            PhotoSourceContent(
                onCamera = {

                    showPhotoOptions = false

                    if (
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        ) ==
                        PackageManager.PERMISSION_GRANTED
                    ) {

                        cameraLauncher.launch(null)

                    } else {

                        cameraPermissionLauncher.launch(
                            Manifest.permission.CAMERA
                        )
                    }
                },

                onGallery = {

                    showPhotoOptions = false

                    galleryLauncher.launch(
                        "image/*"
                    )
                },

                onCancel = {
                    showPhotoOptions = false
                }
            )
        }
    }
}

private fun openLocationInMaps(
    context: Context,
    location: String
) {
    val cleanLocation =
        location.trim()

    if (cleanLocation.isEmpty()) {

        Toast.makeText(
            context,
            "Alamat lokasi belum tersedia.",
            Toast.LENGTH_SHORT
        ).show()

        return
    }

    val encodedLocation =
        Uri.encode(cleanLocation)

    val geoUri =
        Uri.parse(
            "geo:0,0?q=$encodedLocation"
        )

    val mapsIntent =
        Intent(
            Intent.ACTION_VIEW,
            geoUri
        ).apply {
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
            )
        }

    try {

        context.startActivity(
            mapsIntent
        )

        return

    } catch (_: ActivityNotFoundException) {

    } catch (_: Exception) {

    }

    val browserUri =
        Uri.parse(
            "https://www.google.com/maps/search/?api=1&query=$encodedLocation"
        )

    val browserIntent =
        Intent(
            Intent.ACTION_VIEW,
            browserUri
        ).apply {
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
            )
        }

    try {

        context.startActivity(
            browserIntent
        )

    } catch (_: ActivityNotFoundException) {

        Toast.makeText(
            context,
            "Tidak dapat membuka Maps pada perangkat ini.",
            Toast.LENGTH_LONG
        ).show()

    } catch (_: Exception) {

        Toast.makeText(
            context,
            "Tidak dapat membuka Maps pada perangkat ini.",
            Toast.LENGTH_LONG
        ).show()
    }
}

private suspend fun loadBitmapFromUri(
    context: Context,
    uri: Uri
): android.graphics.Bitmap? {

    return withContext(Dispatchers.IO) {

        try {

            context.contentResolver
                .openInputStream(uri)
                ?.use { inputStream ->

                    BitmapFactory.decodeStream(
                        inputStream
                    )
                }

        } catch (_: Exception) {

            null
        }
    }
}

@Composable
private fun DetailTopBar(
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(
                horizontal = 8.dp,
                vertical = 8.dp
            ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(48.dp)
                .clickable(
                    onClick = onBack
                ),

            contentAlignment =
                Alignment.Center
        ) {

            Text(
                text = "‹",
                color = Dark,
                fontSize = 38.sp,
                fontWeight = FontWeight.Light
            )
        }

        Spacer(
            modifier = Modifier.width(4.dp)
        )

        Text(
            text = "Detail Work Order",
            color = Dark,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun PhotoSourceContent(
    onCamera: () -> Unit,
    onGallery: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(
                horizontal = 22.dp,
                vertical = 8.dp
            )
    ) {

        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(42.dp)
                .height(5.dp)
                .background(
                    Color(0xFFD8E0EB),
                    RoundedCornerShape(50.dp)
                )
        )

        Spacer(
            modifier = Modifier.height(22.dp)
        )

        Text(
            text = "Tambah Bukti Foto",
            color = Dark,
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text =
                "Dokumentasikan kondisi dan hasil pekerjaan di lapangan.",
            color = Secondary,
            fontSize = 13.sp,
            lineHeight = 19.sp
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Button(
            onClick = onCamera,

            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),

            shape = RoundedCornerShape(16.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = Blue
            )
        ) {

            Text(
                text = "Ambil dari Kamera",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        OutlinedButton(
            onClick = onGallery,

            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),

            shape = RoundedCornerShape(16.dp)
        ) {

            Text(
                text = "Pilih dari Galeri",
                color = Blue,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        OutlinedButton(
            onClick = onCancel,

            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),

            shape = RoundedCornerShape(16.dp)
        ) {

            Text(
                text = "Batal",
                color = Secondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(
            modifier = Modifier.height(18.dp)
        )
    }
}

@Composable
private fun WorkOrderHeader(
    workOrder: WorkOrder
) {
    Card(
        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(22.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.Top
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = workOrder.id,
                        color = Blue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = workOrder.title,
                        color = Dark,
                        fontSize = 22.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(
                    modifier = Modifier.width(10.dp)
                )

                StatusBadge(
                    status = workOrder.status
                )
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(
                            Color(0xFFEAF2FF),
                            RoundedCornerShape(12.dp)
                        ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text = "✓",
                        color = Blue,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                Column {

                    Text(
                        text = "Prioritas",
                        color = Secondary,
                        fontSize = 12.sp
                    )

                    Text(
                        text = workOrder.priority,
                        color = Dark,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun InformationCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(20.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Text(
                text = title,
                color = Dark,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            content()
        }
    }
}

@Composable
private fun LocationCard(
    location: String,
    currentLatitude: Double?,
    currentLongitude: Double?,
    locationStatus: String,
    isGettingLocation: Boolean,
    onGetCurrentLocation: () -> Unit,
    onOpenMaps: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(20.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(
                            Color(0xFFEAF2FF),
                            RoundedCornerShape(12.dp)
                        ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text = "⌖",
                        color = Blue,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Lokasi Pekerjaan",
                        color = Dark,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(3.dp)
                    )

                    Text(
                        text = "Alamat pelanggan",
                        color = Secondary,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color(0xFFF7FAFE),
                        RoundedCornerShape(14.dp)
                    )
                    .padding(14.dp)
            ) {

                Text(
                    text = location,
                    color = Dark,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Button(
                onClick = onOpenMaps,

                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),

                shape = RoundedCornerShape(14.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = Blue
                )
            ) {

                Text(
                    text = "Buka di Maps",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            OutlinedButton(
                onClick = onGetCurrentLocation,
                enabled = !isGettingLocation,

                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),

                shape = RoundedCornerShape(14.dp)
            ) {

                Text(
                    text =
                        if (isGettingLocation) {
                            "Mengambil Lokasi..."
                        } else {
                            "Ambil Lokasi Saya"
                        },

                    color = Blue,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            LocationStatusContent(
                latitude = currentLatitude,
                longitude = currentLongitude,
                status = locationStatus
            )
        }
    }
}

@Composable
private fun LocationStatusContent(
    latitude: Double?,
    longitude: Double?,
    status: String
) {
    val hasLocation =
        latitude != null &&
                longitude != null

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (hasLocation) {
                    Color(0xFFE8F8F1)
                } else {
                    Color(0xFFF7FAFE)
                },
                RoundedCornerShape(14.dp)
            )
            .padding(14.dp)
    ) {

        Text(
            text = "Lokasi Perangkat",
            color = Dark,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = status,
            color =
                if (hasLocation) {
                    Green
                } else {
                    Secondary
                },
            fontSize = 12.sp,
            lineHeight = 18.sp
        )

        if (hasLocation) {

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = String.format(
                    Locale.US,
                    "Latitude  %.6f",
                    latitude
                ),
                color = Dark,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            Text(
                text = String.format(
                    Locale.US,
                    "Longitude %.6f",
                    longitude
                ),
                color = Dark,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun InformationRow(
    symbol: String,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),

        verticalAlignment =
            Alignment.Top
    ) {

        Box(
            modifier = Modifier
                .size(30.dp)
                .background(
                    Color(0xFFEAF2FF),
                    RoundedCornerShape(9.dp)
                ),

            contentAlignment =
                Alignment.Center
        ) {

            Text(
                text = symbol,
                color = Blue,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            modifier = Modifier.width(12.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = label,
                color = Secondary,
                fontSize = 12.sp
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = value,
                color = Dark,
                fontSize = 15.sp,
                lineHeight = 21.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun NotesCard(
    noteText: String,
    notes: List<String>,
    onNoteTextChange: (String) -> Unit,
    onSaveNote: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(20.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text = "Catatan Pekerjaan",
                    color = Dark,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,

                    modifier = Modifier.weight(1f)
                )

                if (notes.isNotEmpty()) {

                    Box(
                        modifier = Modifier
                            .background(
                                Color(0xFFEAF2FF),
                                RoundedCornerShape(50.dp)
                            )
                            .padding(
                                horizontal = 10.dp,
                                vertical = 5.dp
                            )
                    ) {

                        Text(
                            text = notes.size.toString(),
                            color = Blue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            OutlinedTextField(
                value = noteText,
                onValueChange = onNoteTextChange,

                modifier = Modifier.fillMaxWidth(),

                minLines = 4,
                maxLines = 6,

                shape = RoundedCornerShape(14.dp),

                placeholder = {
                    Text(
                        text = "Tulis catatan pekerjaan...",
                        color = Secondary,
                        fontSize = 14.sp
                    )
                }
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Button(
                onClick = onSaveNote,

                enabled =
                    noteText.trim().isNotEmpty(),

                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),

                shape = RoundedCornerShape(14.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = Blue
                )
            ) {

                Text(
                    text = "Simpan Catatan",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (notes.isNotEmpty()) {

                Spacer(
                    modifier = Modifier.height(18.dp)
                )

                Text(
                    text = "Riwayat Catatan",
                    color = Dark,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                notes.asReversed()
                    .forEachIndexed { index, note ->

                        NoteItem(
                            note = note,
                            number = notes.size - index
                        )

                        if (
                            index < notes.lastIndex
                        ) {

                            Spacer(
                                modifier = Modifier.height(8.dp)
                            )
                        }
                    }
            }
        }
    }
}

@Composable
private fun NoteItem(
    note: String,
    number: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color(0xFFF7FAFE),
                RoundedCornerShape(14.dp)
            )
            .padding(12.dp),

        verticalAlignment =
            Alignment.Top
    ) {

        Box(
            modifier = Modifier
                .size(28.dp)
                .background(
                    Color(0xFFEAF2FF),
                    RoundedCornerShape(8.dp)
                ),

            contentAlignment =
                Alignment.Center
        ) {

            Text(
                text = number.toString(),
                color = Blue,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            modifier = Modifier.width(10.dp)
        )

        Text(
            text = note,
            color = Dark,
            fontSize = 14.sp,
            lineHeight = 20.sp,

            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun PhotoEvidenceCard(
    photos: List<EvidencePhoto>,
    onAddPhoto: () -> Unit,
    onRemovePhoto: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(20.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Bukti Foto",
                        color = Dark,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(3.dp)
                    )

                    Text(
                        text =
                            "Dokumentasikan hasil pekerjaan di lapangan.",
                        color = Secondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }

                if (photos.isNotEmpty()) {

                    Box(
                        modifier = Modifier
                            .background(
                                Color(0xFFE8F8F1),
                                RoundedCornerShape(50.dp)
                            )
                            .padding(
                                horizontal = 10.dp,
                                vertical = 5.dp
                            )
                    ) {

                        Text(
                            text = "${photos.size} foto",
                            color = Green,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            if (photos.isEmpty()) {

                EmptyPhotoState(
                    onAddPhoto = onAddPhoto
                )

            } else {

                Column(
                    verticalArrangement =
                        Arrangement.spacedBy(10.dp)
                ) {

                    photos.forEachIndexed { index, photo ->

                        PhotoEvidenceItem(
                            photo = photo,
                            number = index + 1,

                            onRemove = {
                                onRemovePhoto(
                                    photo.id
                                )
                            }
                        )
                    }

                    OutlinedButton(
                        onClick = onAddPhoto,

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),

                        shape = RoundedCornerShape(14.dp)
                    ) {

                        Text(
                            text = "+ Tambah Foto",
                            color = Blue,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyPhotoState(
    onAddPhoto: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color(0xFFF7FAFE),
                RoundedCornerShape(16.dp)
            )
            .padding(20.dp),

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .size(54.dp)
                .background(
                    Color(0xFFEAF2FF),
                    RoundedCornerShape(16.dp)
                ),

            contentAlignment =
                Alignment.Center
        ) {

            Text(
                text = "▣",
                color = Blue,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Text(
            text = "Belum ada bukti foto",
            color = Dark,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text =
                "Tambahkan foto kondisi atau hasil pekerjaan.",
            color = Secondary,
            fontSize = 12.sp
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Button(
            onClick = onAddPhoto,

            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),

            shape = RoundedCornerShape(14.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = Blue
            )
        ) {

            Text(
                text = "Tambah Bukti Foto",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun PhotoEvidenceItem(
    photo: EvidencePhoto,
    number: Int,
    onRemove: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color(0xFFF7FAFE),
                RoundedCornerShape(14.dp)
            )
            .padding(10.dp)
    ) {

        Image(
            bitmap = photo.image,

            contentDescription = photo.title,

            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp),

            contentScale = ContentScale.Crop
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = photo.title,
                    color = Dark,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = "Bukti pekerjaan #$number",
                    color = Secondary,
                    fontSize = 12.sp
                )
            }

            Text(
                text = "Hapus",
                color = Red,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,

                modifier = Modifier.clickable {
                    onRemove()
                }
            )
        }
    }
}

@Composable
private fun StatusBadge(
    status: WorkOrderStatus
) {
    val label: String
    val background: Color
    val foreground: Color

    when (status) {

        WorkOrderStatus.PENDING -> {
            label = "Pending"
            background = Color(0xFFFFF3DE)
            foreground = Color(0xFFB66A00)
        }

        WorkOrderStatus.ACCEPTED -> {
            label = "Accepted"
            background = Color(0xFFEAF2FF)
            foreground = Blue
        }

        WorkOrderStatus.IN_PROGRESS -> {
            label = "In Progress"
            background = Color(0xFFE8F8F1)
            foreground = Green
        }

        WorkOrderStatus.COMPLETED -> {
            label = "Completed"
            background = Color(0xFFE8F8F1)
            foreground = Green
        }

        WorkOrderStatus.CANCELLED -> {
            label = "Cancelled"
            background = Color(0xFFFFECEF)
            foreground = Color(0xFFC52F46)
        }
    }

    Box(
        modifier = Modifier
            .background(
                background,
                RoundedCornerShape(50.dp)
            )
            .padding(
                horizontal = 11.dp,
                vertical = 7.dp
            )
    ) {

        Text(
            text = label,
            color = foreground,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ActionButton(
    workOrder: WorkOrder,
    onAction: () -> Unit
) {
    val actionText =
        when (workOrder.status) {

            WorkOrderStatus.PENDING ->
                "Terima Work Order"

            WorkOrderStatus.ACCEPTED ->
                "Mulai Pekerjaan"

            WorkOrderStatus.IN_PROGRESS ->
                "Selesaikan Pekerjaan"

            WorkOrderStatus.COMPLETED ->
                "Work Order Selesai"

            WorkOrderStatus.CANCELLED ->
                "Work Order Dibatalkan"
        }

    val enabled =
        workOrder.status != WorkOrderStatus.COMPLETED &&
                workOrder.status != WorkOrderStatus.CANCELLED

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .navigationBarsPadding()
            .padding(
                horizontal = 18.dp,
                vertical = 12.dp
            )
    ) {

        if (enabled) {

            Button(
                onClick = onAction,

                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),

                shape = RoundedCornerShape(16.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = Blue
                )
            ) {

                Text(
                    text = actionText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

        } else {

            OutlinedButton(
                onClick = {},
                enabled = false,

                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),

                shape = RoundedCornerShape(16.dp)
            ) {

                Text(
                    text = actionText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
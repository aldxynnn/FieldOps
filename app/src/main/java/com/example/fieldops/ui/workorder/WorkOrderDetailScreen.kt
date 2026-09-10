package com.example.fieldops.ui.workorder

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.fieldops.data.local.FieldOpsDatabase
import com.example.fieldops.data.local.LocationEvidenceEntity
import com.example.fieldops.data.local.WorkOrderNoteEntity
import com.example.fieldops.data.local.WorkOrderPhotoEntity
import com.example.fieldops.data.location.DeviceLocationManager
import com.example.fieldops.data.model.WorkOrder
import com.example.fieldops.data.model.WorkOrderStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.Locale
import java.util.UUID

data class EvidencePhoto(
    val id: String,
    val title: String,
    val image: ImageBitmap
)

private val Blue = Color(0xFF2563EB)
private val BlueDark = Color(0xFF173B8F)
private val BlueSoft = Color(0xFFEAF2FF)
private val Dark = Color(0xFF0F1F3D)
private val Secondary = Color(0xFF718096)
private val Background = Color(0xFFF4F7FB)
private val SurfaceWhite = Color.White
private val Green = Color(0xFF159A67)
private val GreenSoft = Color(0xFFE8F8F1)
private val Orange = Color(0xFFB66A00)
private val OrangeSoft = Color(0xFFFFF3DE)
private val Red = Color(0xFFE94B5F)
private val RedSoft = Color(0xFFFFECEF)
private val Border = Color(0xFFE5EAF2)

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
        mutableStateOf("Belum mengambil lokasi perangkat")
    }

    var isGettingLocation by remember(workOrder.id) {
        mutableStateOf(false)
    }

    var persistedPhotos by remember(workOrder.id) {
        mutableStateOf<List<EvidencePhoto>>(emptyList())
    }

    val database = remember(context) {
        FieldOpsDatabase.getInstance(context)
    }

    val locationEvidenceDao = remember(database) {
        database.locationEvidenceDao()
    }

    val noteDao = remember(database) {
        database.workOrderNoteDao()
    }

    val photoDao = remember(database) {
        database.workOrderPhotoDao()
    }

    val persistedNotesEntities by noteDao
        .observeNotes(workOrder.id)
        .collectAsState(initial = emptyList())

    val persistedNotes = remember(persistedNotesEntities) {
        persistedNotesEntities.map { it.note }
    }

    val persistedPhotoEntities by photoDao
        .observePhotos(workOrder.id)
        .collectAsState(initial = emptyList())

    val locationManager = remember(context) {
        DeviceLocationManager(context)
    }

    LaunchedEffect(
        workOrder.id,
        persistedPhotoEntities
    ) {
        val loadedPhotos = withContext(Dispatchers.IO) {
            persistedPhotoEntities.mapNotNull { entity ->
                val file = File(entity.filePath)

                if (!file.exists()) {
                    return@mapNotNull null
                }

                val bitmap = BitmapFactory.decodeFile(file.absolutePath)

                if (bitmap == null) {
                    null
                } else {
                    EvidencePhoto(
                        id = entity.id,
                        title = entity.title,
                        image = bitmap.asImageBitmap()
                    )
                }
            }
        }

        persistedPhotos = loadedPhotos
    }

    LaunchedEffect(workOrder.id) {
        val savedLocation = withContext(Dispatchers.IO) {
            locationEvidenceDao.getLocationEvidence(workOrder.id)
        }

        if (savedLocation != null) {
            currentLatitude = savedLocation.latitude
            currentLongitude = savedLocation.longitude
            locationStatus = "Lokasi GPS tersimpan"
        }
    }

    val cameraLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicturePreview()
        ) { bitmap ->
            if (bitmap != null) {
                coroutineScope.launch {
                    savePhotoToRoom(
                        context = context,
                        database = database,
                        workOrder = workOrder,
                        bitmap = bitmap,
                        title = "Foto Kamera ${persistedPhotoEntities.size + 1}",
                        onPhotoAdded = onAddPhoto
                    )
                }
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
                        savePhotoToRoom(
                            context = context,
                            database = database,
                            workOrder = workOrder,
                            bitmap = bitmap,
                            title = "Foto Galeri ${persistedPhotoEntities.size + 1}",
                            onPhotoAdded = onAddPhoto
                        )
                    } else {
                        Toast.makeText(
                            context,
                            "Foto tidak dapat dibaca.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

    val locationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val fineGranted =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true

            val coarseGranted =
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (fineGranted || coarseGranted) {
                isGettingLocation = true
                locationStatus = "Mengambil lokasi perangkat..."

                locationManager.getCurrentLocation(
                    onLocationReceived = { location ->

                        currentLatitude = location.latitude
                        currentLongitude = location.longitude
                        isGettingLocation = false

                        locationStatus =
                            if (fineGranted) {
                                "Lokasi GPS berhasil diperoleh"
                            } else {
                                "Lokasi berhasil diperoleh (perkiraan)"
                            }

                        val evidence = LocationEvidenceEntity(
                            workOrderId = workOrder.id,
                            latitude = location.latitude,
                            longitude = location.longitude,
                            capturedAt = System.currentTimeMillis()
                        )

                        coroutineScope.launch(Dispatchers.IO) {
                            locationEvidenceDao.insertLocationEvidence(evidence)
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
                workOrder = workOrder,
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
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = 16.dp,
                    vertical = 16.dp
                ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            WorkOrderHero(
                workOrder = workOrder
            )

            QuickSummaryCard(
                workOrder = workOrder
            )

            InformationCard(
                title = "Informasi Pekerjaan",
                subtitle = "Detail jadwal dan pekerjaan yang ditugaskan"
            ) {
                InformationRow(
                    symbol = "▣",
                    label = "Jenis Pekerjaan",
                    value = workOrder.title
                )

                DetailDivider()

                InformationRow(
                    symbol = "▤",
                    label = "Tanggal",
                    value = workOrder.date
                )

                DetailDivider()

                InformationRow(
                    symbol = "◷",
                    label = "Waktu",
                    value = workOrder.time
                )
            }

            CustomerCard(
                customer = workOrder.customer
            )

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
                        ) == PackageManager.PERMISSION_GRANTED ||
                                ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED

                    if (hasPermission) {

                        if (!locationManager.isLocationAvailable()) {

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

                            locationManager.getCurrentLocation(
                                onLocationReceived = { location ->

                                    currentLatitude = location.latitude
                                    currentLongitude = location.longitude
                                    isGettingLocation = false
                                    locationStatus =
                                        "Lokasi GPS berhasil diperoleh"

                                    val evidence =
                                        LocationEvidenceEntity(
                                            workOrderId = workOrder.id,
                                            latitude = location.latitude,
                                            longitude = location.longitude,
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
                notes = persistedNotes,
                onNoteTextChange = {
                    noteText = it
                },
                onSaveNote = {

                    val cleanNote = noteText.trim()

                    if (cleanNote.isNotEmpty()) {

                        coroutineScope.launch {

                            val noteEntity =
                                WorkOrderNoteEntity(
                                    id = UUID.randomUUID().toString(),
                                    workOrderId = workOrder.id,
                                    note = cleanNote,
                                    createdAt =
                                        System.currentTimeMillis()
                                )

                            noteDao.insertNote(noteEntity)

                            onAddNote(cleanNote)

                            noteText = ""
                        }
                    }
                }
            )

            PhotoEvidenceCard(
                photos = persistedPhotos,
                onAddPhoto = {
                    showPhotoOptions = true
                },
                onRemovePhoto = { photoId ->

                    coroutineScope.launch {

                        val entity =
                            photoDao.getPhoto(photoId)

                        if (entity != null) {

                            photoDao.deletePhoto(entity)

                            withContext(Dispatchers.IO) {
                                try {
                                    File(entity.filePath).delete()
                                } catch (_: Exception) {
                                }
                            }

                            onRemovePhoto(photoId)
                        }
                    }
                }
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
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            ),
            containerColor = SurfaceWhite
        ) {
            PhotoSourceContent(
                onCamera = {

                    showPhotoOptions = false

                    if (
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
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

                    galleryLauncher.launch("image/*")
                },
                onCancel = {
                    showPhotoOptions = false
                }
            )
        }
    }

}

private suspend fun savePhotoToRoom(
    context: Context,
    database: FieldOpsDatabase,
    workOrder: WorkOrder,
    bitmap: Bitmap,
    title: String,
    onPhotoAdded: (EvidencePhoto) -> Unit
) {
    withContext(Dispatchers.IO) {

        try {

            val photoId = UUID.randomUUID().toString()

            val photoDirectory =
                File(
                    context.filesDir,
                    "work_order_photos"
                )

            if (!photoDirectory.exists()) {
                photoDirectory.mkdirs()
            }

            val photoFile =
                File(
                    photoDirectory,
                    "${photoId}.jpg"
                )

            FileOutputStream(photoFile).use { outputStream ->
                bitmap.compress(
                    Bitmap.CompressFormat.JPEG,
                    90,
                    outputStream
                )
            }

            val photoEntity =
                WorkOrderPhotoEntity(
                    id = photoId,
                    workOrderId = workOrder.id,
                    title = title,
                    filePath = photoFile.absolutePath,
                    createdAt = System.currentTimeMillis()
                )

            database
                .workOrderPhotoDao()
                .insertPhoto(photoEntity)

            withContext(Dispatchers.Main) {

                onPhotoAdded(
                    EvidencePhoto(
                        id = photoId,
                        title = title,
                        image = bitmap.asImageBitmap()
                    )
                )
            }

        } catch (exception: Exception) {

            withContext(Dispatchers.Main) {

                Toast.makeText(
                    context,
                    "Foto gagal disimpan: ${
                        exception.message
                            ?: "kesalahan tidak diketahui"
                    }",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

}

private fun openLocationInMaps(
    context: Context,
    location: String
) {
    val cleanLocation = location.trim()

    if (cleanLocation.isEmpty()) {

        Toast.makeText(
            context,
            "Alamat lokasi belum tersedia.",
            Toast.LENGTH_SHORT
        ).show()

        return
    }

    val encodedLocation = Uri.encode(cleanLocation)

    val geoUri =
        Uri.parse(
            "geo:0,0?q=$encodedLocation"
        )

    val mapsIntent =
        Intent(
            Intent.ACTION_VIEW,
            geoUri
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

    try {

        context.startActivity(mapsIntent)

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
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

    try {

        context.startActivity(browserIntent)

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
): Bitmap? {

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
    workOrder: WorkOrder,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(
                horizontal = 10.dp,
                vertical = 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(BlueSoft)
                .clickable(
                    onClick = onBack
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "‹",
                color = Blue,
                fontSize = 32.sp,
                fontWeight = FontWeight.Light
            )
        }

        Spacer(
            modifier = Modifier.width(12.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Detail Work Order",
                color = Dark,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = workOrder.id,
                color = Secondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun WorkOrderHero(
    workOrder: WorkOrder
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Surface(
                        shape = RoundedCornerShape(50.dp),
                        color = BlueSoft
                    ) {
                        Text(
                            text = workOrder.id,
                            modifier = Modifier.padding(
                                horizontal = 11.dp,
                                vertical = 6.dp
                            ),
                            color = Blue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )

                    Text(
                        text = workOrder.title,
                        color = Dark,
                        fontSize = 24.sp,
                        lineHeight = 30.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = "Pekerjaan lapangan",
                        color = Secondary,
                        fontSize = 13.sp
                    )
                }

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                StatusBadge(
                    status = workOrder.status,
                    darkMode = false
                )
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                HeroMetric(
                    modifier = Modifier.weight(1f),
                    label = "Prioritas",
                    value = formatPriority(workOrder.priority)
                )

                HeroMetric(
                    modifier = Modifier.weight(1f),
                    label = "Tanggal",
                    value = workOrder.date
                )

                HeroMetric(
                    modifier = Modifier.weight(1f),
                    label = "Waktu",
                    value = workOrder.time
                )
            }
        }
    }
}

@Composable
private fun HeroMetric(
    modifier: Modifier,
    label: String,
    value: String
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = Background,
        border = BorderStroke(
            width = 1.dp,
            color = Border
        )
    ) {
        Column(
            modifier = Modifier
                .padding(
                    horizontal = 11.dp,
                    vertical = 10.dp
                )
        ) {

            Text(
                text = label,
                color = Secondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = value,
                color = Dark,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun QuickSummaryCard(
    workOrder: WorkOrder
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Border,
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceWhite
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(BlueSoft),
                contentAlignment = Alignment.Center
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

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Work Order aktif",
                    color = Dark,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = workOrder.customer,
                    color = Secondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            PriorityBadge(
                priority = workOrder.priority
            )
        }
    }

}

@Composable
private fun InformationCard(
    title: String,
    subtitle: String,
    content: @Composable () -> Unit
) {
    DetailCard {

        SectionTitle(
            title = title,
            subtitle = subtitle,
            icon = "▤"
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        content()
    }

}

@Composable
private fun CustomerCard(
    customer: String
) {
    DetailCard {

        SectionTitle(
            title = "Pelanggan",
            subtitle = "Informasi pihak yang menerima layanan",
            icon = "●"
        )

        Spacer(
            modifier = Modifier.height(15.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(BlueSoft),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = customer
                        .trim()
                        .firstOrNull()
                        ?.uppercase()
                        ?: "P",
                    color = Blue,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.width(13.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = customer,
                    color = Dark,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = "Customer / Account",
                    color = Secondary,
                    fontSize = 12.sp
                )
            }
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
    DetailCard {

        SectionTitle(
            title = "Lokasi Pekerjaan",
            subtitle = "Alamat dan verifikasi posisi teknisi",
            icon = "⌖"
        )

        Spacer(
            modifier = Modifier.height(15.dp)
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Background
        ) {

            Column(
                modifier = Modifier.padding(15.dp)
            ) {

                Text(
                    text = "ALAMAT LOKASI",
                    color = Secondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.7.sp
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = location,
                    color = Dark,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    fontWeight = FontWeight.Medium
                )
            }
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
            modifier = Modifier.height(9.dp)
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
            modifier = Modifier.height(12.dp)
        )

        LocationStatusContent(
            latitude = currentLatitude,
            longitude = currentLongitude,
            status = locationStatus
        )
    }

}

@Composable
private fun LocationStatusContent(
    latitude: Double?,
    longitude: Double?,
    status: String
) {
    val hasLocation =
        latitude != null && longitude != null

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        color = if (hasLocation) {
            GreenSoft
        } else {
            Background
        }
    ) {

        Column(
            modifier = Modifier.padding(14.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(9.dp)
                        .clip(CircleShape)
                        .background(
                            if (hasLocation) Green else Secondary
                        )
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    text = "Status lokasi perangkat",
                    color = Dark,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text = status,
                color = if (hasLocation) {
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
                    fontSize = 12.sp,
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
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
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
    DetailCard {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Catatan Pekerjaan",
                    color = Dark,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = "Catat temuan atau informasi penting di lapangan.",
                    color = Secondary,
                    fontSize = 12.sp
                )
            }

            CountBadge(
                count = notes.size
            )
        }

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        OutlinedTextField(
            value = noteText,
            onValueChange = onNoteTextChange,
            modifier = Modifier.fillMaxWidth(),
            minLines = 4,
            maxLines = 6,
            shape = RoundedCornerShape(15.dp),
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
            enabled = noteText.trim().isNotEmpty(),
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
                modifier = Modifier.height(20.dp)
            )

            Text(
                text = "RIWAYAT CATATAN",
                color = Secondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )

            Spacer(
                modifier = Modifier.height(9.dp)
            )

            notes.asReversed()
                .forEachIndexed { index, note ->

                    NoteItem(
                        note = note,
                        number = notes.size - index
                    )

                    if (index < notes.lastIndex) {
                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )
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
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        color = Background
    ) {

        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {

            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(BlueSoft),
                contentAlignment = Alignment.Center
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

}

@Composable
private fun PhotoEvidenceCard(
    photos: List<EvidencePhoto>,
    onAddPhoto: () -> Unit,
    onRemovePhoto: (String) -> Unit
) {
    DetailCard {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
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
                    text = "Dokumentasikan kondisi dan hasil pekerjaan.",
                    color = Secondary,
                    fontSize = 12.sp
                )
            }

            if (photos.isNotEmpty()) {
                CountBadge(
                    count = photos.size
                )
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
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                photos.forEachIndexed { index, photo ->

                    PhotoEvidenceItem(
                        photo = photo,
                        number = index + 1,
                        onRemove = {
                            onRemovePhoto(photo.id)
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

@Composable
private fun EmptyPhotoState(
    onAddPhoto: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(17.dp),
        color = Background
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(RoundedCornerShape(17.dp))
                    .background(BlueSoft),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "▣",
                    color = Blue,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
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
                text = "Tambahkan foto kondisi atau hasil pekerjaan.",
                color = Secondary,
                fontSize = 12.sp
            )

            Spacer(
                modifier = Modifier.height(14.dp)
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

}

@Composable
private fun PhotoEvidenceItem(
    photo: EvidencePhoto,
    number: Int,
    onRemove: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(17.dp),
        color = Background
    ) {

        Column(
            modifier = Modifier.padding(10.dp)
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(13.dp))
            ) {

                Image(
                    bitmap = photo.image,
                    contentDescription = photo.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Surface(
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.TopStart),
                    shape = RoundedCornerShape(50.dp),
                    color = Color.Black.copy(alpha = 0.62f)
                ) {

                    Text(
                        text = "BUKTI #$number",
                        modifier = Modifier.padding(
                            horizontal = 9.dp,
                            vertical = 5.dp
                        ),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = photo.title,
                        color = Dark,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(
                        modifier = Modifier.height(3.dp)
                    )

                    Text(
                        text = "Dokumentasi pekerjaan lapangan",
                        color = Secondary,
                        fontSize = 11.sp
                    )
                }

                TextButton(
                    onClick = onRemove
                ) {

                    Text(
                        text = "Hapus",
                        color = Red,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

}

@Composable
private fun DetailCard(
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Border,
                shape = RoundedCornerShape(21.dp)
            ),
        shape = RoundedCornerShape(21.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceWhite
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            content()
        }
    }

}

@Composable
private fun SectionTitle(
    title: String,
    subtitle: String,
    icon: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(BlueSoft),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = icon,
                color = Blue,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            modifier = Modifier.width(11.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = title,
                color = Dark,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = subtitle,
                color = Secondary,
                fontSize = 11.sp,
                lineHeight = 16.sp
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
        verticalAlignment = Alignment.Top
    ) {

        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(Background),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = symbol,
                color = Blue,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            modifier = Modifier.width(11.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = label,
                color = Secondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            Text(
                text = value,
                color = Dark,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }

}

@Composable
private fun DetailDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Border)
    )
}

@Composable
private fun CountBadge(
    count: Int
) {
    Surface(
        shape = RoundedCornerShape(50.dp),
        color = BlueSoft
    ) {

        Text(
            text = count.toString(),
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 5.dp
            ),
            color = Blue,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }

}

@Composable
private fun PriorityBadge(
    priority: String
) {
    val color =
        when {
            priority.equals("HIGH", true) ||
                    priority.equals("TINGGI", true) -> Red

            priority.equals("MEDIUM", true) ||
                    priority.equals("SEDANG", true) -> Orange

            priority.equals("LOW", true) ||
                    priority.equals("RENDAH", true) -> Green

            else -> Secondary
        }

    val background =
        when (color) {
            Red -> RedSoft
            Orange -> OrangeSoft
            Green -> GreenSoft
            else -> Background
        }

    Surface(
        shape = RoundedCornerShape(50.dp),
        color = background
    ) {

        Text(
            text = formatPriority(priority),
            modifier = Modifier.padding(
                horizontal = 9.dp,
                vertical = 6.dp
            ),
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }

}

@Composable
private fun StatusBadge(
    status: WorkOrderStatus,
    darkMode: Boolean = false
) {
    val label: String
    val background: Color
    val foreground: Color

    when (status) {

        WorkOrderStatus.PENDING -> {
            label = "Pending"
            background =
                if (darkMode) {
                    Color.White.copy(alpha = 0.13f)
                } else {
                    OrangeSoft
                }
            foreground =
                if (darkMode) {
                    Color.White
                } else {
                    Orange
                }
        }

        WorkOrderStatus.ACCEPTED -> {
            label = "Accepted"
            background =
                if (darkMode) {
                    Color.White.copy(alpha = 0.13f)
                } else {
                    BlueSoft
                }
            foreground =
                if (darkMode) {
                    Color.White
                } else {
                    Blue
                }
        }

        WorkOrderStatus.IN_PROGRESS -> {
            label = "In Progress"
            background =
                if (darkMode) {
                    Color.White.copy(alpha = 0.13f)
                } else {
                    GreenSoft
                }
            foreground =
                if (darkMode) {
                    Color.White
                } else {
                    Green
                }
        }

        WorkOrderStatus.COMPLETED -> {
            label = "Completed"
            background =
                if (darkMode) {
                    Color.White.copy(alpha = 0.13f)
                } else {
                    GreenSoft
                }
            foreground =
                if (darkMode) {
                    Color.White
                } else {
                    Green
                }
        }

        WorkOrderStatus.CANCELLED -> {
            label = "Cancelled"
            background =
                if (darkMode) {
                    Color.White.copy(alpha = 0.13f)
                } else {
                    RedSoft
                }
            foreground =
                if (darkMode) {
                    Color.White
                } else {
                    Red
                }
        }
    }

    Surface(
        shape = RoundedCornerShape(50.dp),
        color = background
    ) {

        Row(
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 7.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(foreground)
            )

            Spacer(
                modifier = Modifier.width(6.dp)
            )

            Text(
                text = label,
                color = foreground,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

}

private fun formatPriority(
    priority: String
): String {
    return when {
        priority.equals("HIGH", true) -> "Tinggi"
        priority.equals("MEDIUM", true) -> "Sedang"
        priority.equals("LOW", true) -> "Rendah"
        priority.equals("TINGGI", true) -> "Tinggi"
        priority.equals("SEDANG", true) -> "Sedang"
        priority.equals("RENDAH", true) -> "Rendah"
        else -> priority
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
            text = "Dokumentasikan kondisi dan hasil pekerjaan di lapangan.",
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

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SurfaceWhite,
        shadowElevation = 8.dp
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(
                    horizontal = 16.dp,
                    vertical = 11.dp
                )
        ) {

            if (enabled) {

                Button(
                    onClick = onAction,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(17.dp),
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
                        .height(56.dp),
                    shape = RoundedCornerShape(17.dp)
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

}
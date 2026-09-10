package com.example.fieldops.ui.splash

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fieldops.ui.components.FieldOpsLogo

private val DarkBlueText = Color(0xFF0F1E4A)
private val SubtitleText = Color(0xFF64748B)
private val PrimaryBlue = Color(0xFF1B6CFA)

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        SplashCityArtwork(
            modifier = Modifier.fillMaxSize()
        )

        // Konten utama diposisikan pas di tengah layar
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 40.dp), // Beri margin sedikit agar seimbang dengan indicator di bawah
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            /*
             * LOGO & TEKS BRAND (DIPERBESAR)
             */
            FieldOpsLogo(
                logoSize = 160.dp,
                textSize = 48.sp,
                vertical = true,
                lightText = false
            )

            Spacer(modifier = Modifier.height(32.dp))

            /*
             * TEKS SLOGAN & SUBTITLE (DIPERBESAR)
             */
            Text(
                text = "SMARTER FIELD OPERATIONS",
                color = DarkBlueText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.4.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "for a better service",
                color = SubtitleText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 0.4.sp
            )
        }

        CircularProgressIndicator(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp)
                .height(28.dp),
            color = PrimaryBlue,
            strokeWidth = 2.dp
        )
    }
}

@Composable
private fun SplashCityArtwork(
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
    ) {

        val width = size.width
        val height = size.height

        /*
         * =========================================================
         * ELEMEN HIASAN ATAS
         * =========================================================
         */

        // 1. Soft Gradient Background Aura
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFE0EDFF).copy(alpha = 0.8f),
                    Color(0xFFF0F6FF).copy(alpha = 0.4f),
                    Color.Transparent
                ),
                center = Offset(width * 0.88f, height * 0.10f),
                radius = width * 0.65f
            )
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFE8F1FF).copy(alpha = 0.6f),
                    Color.Transparent
                ),
                center = Offset(width * 0.10f, height * 0.08f),
                radius = width * 0.45f
            )
        )

        // 2. Wave Dynamic Layer 1
        val topWavePath1 = Path().apply {
            moveTo(width * 0.25f, 0f)
            cubicTo(
                width * 0.48f, height * 0.12f,
                width * 0.75f, height * 0.04f,
                width, height * 0.14f
            )
            lineTo(width, 0f)
            close()
        }
        drawPath(
            path = topWavePath1,
            color = Color(0xFFEFF5FF)
        )

        // 3. Wave Dynamic Layer 2
        val topWavePath2 = Path().apply {
            moveTo(width * 0.50f, 0f)
            cubicTo(
                width * 0.68f, height * 0.08f,
                width * 0.84f, height * 0.03f,
                width, height * 0.09f
            )
            lineTo(width, 0f)
            close()
        }
        drawPath(
            path = topWavePath2,
            color = Color(0xFFDCEBFF)
        )

        // 4. Siluet Awan
        val cloudPath = Path().apply {
            moveTo(-width * 0.05f, height * 0.08f)
            cubicTo(
                width * 0.05f, height * 0.04f,
                width * 0.15f, height * 0.05f,
                width * 0.22f, height * 0.09f
            )
            cubicTo(
                width * 0.28f, height * 0.07f,
                width * 0.38f, height * 0.10f,
                width * 0.40f, height * 0.15f
            )
            lineTo(-width * 0.05f, height * 0.15f)
            close()
        }
        drawPath(
            path = cloudPath,
            color = Color(0xFFF3F8FF).copy(alpha = 0.7f)
        )

        // 5. Pola Grid Dots
        val startX = width * 0.78f
        val startY = height * 0.04f
        val cols = 4
        val rows = 3
        val spacing = 18f

        for (c in 0 until cols) {
            for (r in 0 until rows) {
                drawCircle(
                    color = PrimaryBlue.copy(alpha = 0.12f),
                    radius = 2.5f,
                    center = Offset(startX + c * spacing, startY + r * spacing)
                )
            }
        }

        // 6. Ring & Accent
        drawCircle(
            color = PrimaryBlue.copy(alpha = 0.08f),
            radius = width * 0.12f,
            center = Offset(width * 0.82f, height * 0.12f)
        )
        drawCircle(
            color = PrimaryBlue.copy(alpha = 0.15f),
            radius = 4f,
            center = Offset(width * 0.22f, height * 0.18f)
        )

        /*
         * =========================================================
         * ARTWORK KOTA BAGIAN BAWAH (KETINGGIAN DITURUNKAN AGAR TIDAK NABRAK LOGO DI TENGAH)
         * =========================================================
         */

        drawCircle(
            color = Color(0xFFF0F5FF),
            radius = width * 0.50f,
            center = Offset(
                width * 0.50f,
                height * 0.94f
            )
        )

        val distantBuildings = listOf(
            Triple(0.00f, 0.18f, 0.04f),
            Triple(0.10f, 0.11f, 0.06f),
            Triple(0.21f, 0.16f, 0.05f),
            Triple(0.31f, 0.08f, 0.06f),
            Triple(0.43f, 0.14f, 0.04f),
            Triple(0.55f, 0.10f, 0.05f),
            Triple(0.67f, 0.16f, 0.04f),
            Triple(0.78f, 0.09f, 0.06f),
            Triple(0.90f, 0.13f, 0.05f)
        )

        distantBuildings.forEach { (x, buildingWidth, buildingHeight) ->
            drawRect(
                color = Color(0xFFD6E4FF),
                topLeft = Offset(
                    width * x,
                    height * (0.94f - buildingHeight)
                ),
                size = Size(
                    width * buildingWidth,
                    height * buildingHeight
                )
            )
        }

        val buildings = listOf(
            Triple(0.02f, 0.14f, 0.05f),
            Triple(0.14f, 0.12f, 0.08f),
            Triple(0.27f, 0.15f, 0.04f),
            Triple(0.43f, 0.12f, 0.09f),
            Triple(0.56f, 0.15f, 0.06f),
            Triple(0.72f, 0.13f, 0.10f),
            Triple(0.87f, 0.14f, 0.07f)
        )

        buildings.forEachIndexed { index, (x, buildingWidth, buildingHeight) ->

            val buildingColor = when (index % 3) {
                0 -> Color(0xFFB0D0FF)
                1 -> Color(0xFFC2DCFF)
                else -> Color(0xFFA3C8FF)
            }

            drawRect(
                color = buildingColor,
                topLeft = Offset(
                    width * x,
                    height * (0.94f - buildingHeight)
                ),
                size = Size(
                    width * buildingWidth,
                    height * buildingHeight
                )
            )

            val windowRows = 2
            val windowColumns = 2

            for (row in 0 until windowRows) {
                for (column in 0 until windowColumns) {

                    val wx = x + buildingWidth * 0.23f + column * buildingWidth * 0.32f
                    val wy = 0.94f - buildingHeight + buildingHeight * 0.20f + row * buildingHeight * 0.30f

                    if (wy < 0.93f) {
                        drawRect(
                            color = Color.White.copy(alpha = 0.85f),
                            topLeft = Offset(
                                width * wx,
                                height * wy
                            ),
                            size = Size(
                                width * buildingWidth * 0.10f,
                                height * buildingHeight * 0.12f
                            )
                        )
                    }
                }
            }
        }

        val towerX = width * 0.715f
        val towerTop = height * 0.82f
        val towerWidth = width * 0.08f
        val towerBottom = height * 0.94f

        drawRect(
            color = Color(0xFF96C0FE),
            topLeft = Offset(towerX, towerTop),
            size = Size(towerWidth, towerBottom - towerTop)
        )

        drawLine(
            color = Color(0xFF8BB7FA),
            start = Offset(towerX + towerWidth * 0.5f, towerTop),
            end = Offset(towerX + towerWidth * 0.5f, towerTop - height * 0.02f),
            strokeWidth = 2f
        )

        val treeBase = height * 0.95f

        for (i in 0..25) {
            val treeX = width * (i / 25f) + ((i % 3) * width * 0.006f)
            val treeHeight = height * (0.012f + (i % 5) * 0.004f)

            drawCircle(
                color = Color(0xFF5B82B3),
                radius = width * (0.020f + (i % 4) * 0.003f),
                center = Offset(treeX, treeBase - treeHeight)
            )
        }

        val groundPath = Path().apply {
            moveTo(0f, height * 0.95f)
            cubicTo(
                width * 0.20f, height * 0.93f,
                width * 0.40f, height * 0.95f,
                width * 0.58f, height * 0.94f
            )
            cubicTo(
                width * 0.76f, height * 0.93f,
                width * 0.88f, height * 0.95f,
                width, height * 0.93f
            )
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }

        drawPath(path = groundPath, color = Color(0xFF385E8E))

        drawLine(
            color = PrimaryBlue.copy(alpha = 0.3f),
            start = Offset(width * 0.02f, height * 0.95f),
            end = Offset(width * 0.98f, height * 0.93f),
            strokeWidth = 1.5f,
            cap = StrokeCap.Round
        )
    }
}
package com.example.fieldops.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fieldops.R

private val DarkBlueText = Color(0xFF0F1E4A)

@Composable
fun FieldOpsLogo(
    modifier: Modifier = Modifier,
    logoSize: Dp = 72.dp, // Default dinaikkan dari 56.dp/64.dp ke 72.dp
    textSize: TextUnit = 28.sp,
    showText: Boolean = true,
    vertical: Boolean = false,
    lightText: Boolean = false
) {
    val textColor = if (lightText) Color.White else DarkBlueText

    val logoText = buildAnnotatedString {
        withStyle(style = SpanStyle(color = textColor, fontWeight = FontWeight.Bold)) {
            append("Field")
        }
        withStyle(style = SpanStyle(color = textColor, fontWeight = FontWeight.Black)) {
            append("Ops")
        }
    }

    if (vertical) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.fieldops_logo),
                contentDescription = "FieldOps Logo",
                // Menggunakan height + fillHeight agar lebar gambar menyesuaikan otomatis
                modifier = Modifier.height(logoSize),
                contentScale = ContentScale.FillHeight
            )
            if (showText) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = logoText,
                    fontSize = textSize,
                    fontFamily = FontFamily.SansSerif,
                    letterSpacing = (-0.5).sp
                )
            }
        }
    } else {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.fieldops_logo),
                contentDescription = "FieldOps Logo",
                modifier = Modifier.height(logoSize),
                contentScale = ContentScale.FillHeight
            )
            if (showText) {
                Text(
                    text = logoText,
                    fontSize = textSize,
                    fontFamily = FontFamily.SansSerif,
                    letterSpacing = (-0.5).sp
                )
            }
        }
    }
}
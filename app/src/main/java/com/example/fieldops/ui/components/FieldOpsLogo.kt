package com.example.fieldops.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fieldops.R

@Composable
fun FieldOpsLogo(
    modifier: Modifier = Modifier,
    logoSize: Dp = 64.dp,
    textSize: TextUnit = 28.sp,
    showText: Boolean = true,
    vertical: Boolean = false,
    lightText: Boolean = false
) {

    if (vertical) {

        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Image(
                painter = painterResource(
                    id = R.drawable.fieldops_logo
                ),
                contentDescription = "FieldOps",
                modifier = Modifier
                    .width(logoSize)
                    .height(logoSize)
            )

            if (showText) {

                Text(
                    text = "FieldOps",
                    color = if (lightText) {
                        Color.White
                    } else {
                        Color(0xFF102A63)
                    },
                    fontSize = textSize,
                    fontWeight = FontWeight.Bold
                )
            }
        }

    } else {

        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Image(
                painter = painterResource(
                    id = R.drawable.fieldops_logo
                ),
                contentDescription = "FieldOps",
                modifier = Modifier
                    .width(logoSize)
                    .height(logoSize)
            )

            if (showText) {

                Text(
                    text = "FieldOps",
                    color = if (lightText) {
                        Color.White
                    } else {
                        Color(0xFF102A63)
                    },
                    fontSize = textSize,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
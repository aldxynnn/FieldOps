package com.example.fieldops.ui.auth

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fieldops.data.remote.LoginRequest
import com.example.fieldops.data.remote.RetrofitClient
import com.example.fieldops.data.local.FieldOpsDatabase
import com.example.fieldops.data.remote.SessionManager
import com.example.fieldops.data.repository.CustomerRepository
import com.example.fieldops.data.repository.WorkOrderRepository
import com.example.fieldops.data.sync.WorkOrderSyncScheduler
import com.example.fieldops.ui.components.FieldOpsLogo
import kotlinx.coroutines.launch

private val FieldOpsBlue = Color(0xFF1769FF)
private val FieldOpsBlueDark = Color(0xFF0D4FC4)
private val FieldOpsBlueSoft = Color(0xFFEAF2FF)

private val Background = Color(0xFFF5F8FC)
private val White = Color(0xFFFFFFFF)

private val TextPrimary = Color(0xFF082C68)
private val TextSecondary = Color(0xFF667085)
private val TextMuted = Color(0xFF98A2B3)

private val InputBackground = Color(0xFFFAFCFF)
private val InputBorder = Color(0xFFDDE5F0)

private val ErrorRed = Color(0xFFD92D20)
private val ErrorBackground = Color(0xFFFFF1F0)

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {

        LoginBackgroundArtwork(
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(
                modifier = Modifier.height(36.dp)
            )

            /*
             * BRAND LOGO (Diperbesar)
             */
            FieldOpsLogo(
                logoSize = 80.dp,
                textSize = 34.sp,
                vertical = false
            )

            Spacer(
                modifier = Modifier.height(44.dp)
            )

            /*
             * WELCOME HEADING (Diperbesar)
             */
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text = "Selamat Datang",
                    color = TextPrimary,
                    fontSize = 32.sp,
                    lineHeight = 38.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.7).sp
                )

                Text(
                    text = "Kembali",
                    color = TextPrimary,
                    fontSize = 32.sp,
                    lineHeight = 38.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.7).sp
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Text(
                    text = "Masuk ke akun Anda untuk melanjutkan",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Normal
                )
            }

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            /*
             * EMAIL INPUT FIELD
             */
            PremiumInputField(
                value = email,
                onValueChange = {
                    email = it
                    errorMessage = null
                },
                placeholder = "nama@perusahaan.com",
                enabled = !isLoading,
                leadingIcon = {
                    EmailFieldIcon(
                        modifier = Modifier.size(20.dp),
                        color = FieldOpsBlue
                    )
                }
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            /*
             * PASSWORD INPUT FIELD
             */
            PremiumInputField(
                value = password,
                onValueChange = {
                    password = it
                    errorMessage = null
                },
                placeholder = "Password",
                enabled = !isLoading,
                visualTransformation = if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                leadingIcon = {
                    LockFieldIcon(
                        modifier = Modifier.size(20.dp),
                        color = FieldOpsBlue
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            passwordVisible = !passwordVisible
                        },
                        enabled = !isLoading,
                        modifier = Modifier.size(44.dp)
                    ) {
                        PasswordVisibilityIcon(
                            visible = passwordVisible,
                            modifier = Modifier.size(22.dp),
                            color = TextSecondary
                        )
                    }
                }
            )

            /*
             * ADMINISTRATOR INFORMATION
             */
            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Lupa password atau belum punya akun?",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )

                Spacer(
                    modifier = Modifier.width(4.dp)
                )

                Text(
                    text = "Hubungi administrator.",
                    color = FieldOpsBlue,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            /*
             * ERROR MESSAGE
             */
            errorMessage?.let { message ->

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                SurfaceError(
                    message = message
                )
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            /*
             * PRIMARY ACTION BUTTON
             */
            Button(
                onClick = {
                    if (isLoading) return@Button

                    isLoading = true
                    errorMessage = null

                    scope.launch {
                        val result = runCatching {
                            require(RetrofitClient.isConfigured) {
                                "Backend API belum dikonfigurasi."
                            }

                            RetrofitClient.api.login(
                                LoginRequest(
                                    email = email.trim(),
                                    password = password
                                )
                            )
                        }

                        result.onSuccess { response ->
                            SessionManager(context).saveSession(
                                token = response.token,
                                email = response.user.email,
                                name = response.user.name,
                                role = response.user.role,
                                companyId = response.user.companyId
                            )

                            CustomerRepository.getInstance(context)
                                .refreshFromServer()

                            WorkOrderRepository(
                                FieldOpsDatabase.getInstance(context)
                            ).refreshFromServer().getOrThrow()

                            WorkOrderSyncScheduler.scheduleNow(context)

                            onLoginSuccess()
                        }.onFailure { exception ->
                            errorMessage = exception.message
                                ?.takeIf { it.isNotBlank() }
                                ?: "Login gagal. Periksa koneksi backend dan akun Anda."
                        }

                        isLoading = false
                    }
                },
                enabled = email.isNotBlank() &&
                        password.isNotBlank() &&
                        !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FieldOpsBlue,
                    disabledContainerColor = Color(0xFFD7E0EC),
                    disabledContentColor = White
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 3.dp,
                    pressedElevation = 1.dp,
                    disabledElevation = 0.dp
                )
            ) {

                if (isLoading) {

                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = White,
                        strokeWidth = 2.2.dp
                    )

                    Spacer(
                        modifier = Modifier.width(10.dp)
                    )

                    Text(
                        text = "Memproses",
                        color = White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                } else {

                    Text(
                        text = "Masuk",
                        color = White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(30.dp)
            )

            /*
             * FIELDOPS SIGNATURE
             */
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(FieldOpsBlue)
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    text = "FIELDOPS",
                    color = TextSecondary.copy(alpha = 0.75f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }

            Spacer(
                modifier = Modifier.height(25.dp)
            )
        }
    }
}

@Composable
private fun PremiumInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    enabled: Boolean,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    leadingIcon: @Composable () -> Unit,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp),
        enabled = enabled,
        singleLine = true,
        visualTransformation = visualTransformation,
        shape = RoundedCornerShape(12.dp),
        placeholder = {
            Text(
                text = placeholder,
                color = TextMuted,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal
            )
        },
        leadingIcon = {
            Box(
                modifier = Modifier
                    .padding(start = 4.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(FieldOpsBlueSoft),
                contentAlignment = Alignment.Center
            ) {
                leadingIcon()
            }
        },
        trailingIcon = trailingIcon,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = InputBackground,
            unfocusedContainerColor = InputBackground,
            disabledContainerColor = InputBackground,

            focusedBorderColor = FieldOpsBlue,
            unfocusedBorderColor = InputBorder,
            disabledBorderColor = InputBorder,

            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            disabledTextColor = TextSecondary,

            focusedPlaceholderColor = TextMuted,
            unfocusedPlaceholderColor = TextMuted
        )
    )
}

@Composable
private fun SurfaceError(
    message: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(11.dp))
            .background(ErrorBackground)
            .border(
                width = 1.dp,
                color = ErrorRed.copy(alpha = 0.08f),
                shape = RoundedCornerShape(11.dp)
            )
            .padding(
                horizontal = 12.dp,
                vertical = 10.dp
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(White),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "!",
                    color = ErrorRed,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(
                modifier = Modifier.width(10.dp)
            )

            Text(
                text = message,
                modifier = Modifier.weight(1f),
                color = ErrorRed,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun LoginBackgroundArtwork(
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
    ) {
        drawCircle(
            color = Color(0xFF7BAEFF).copy(alpha = 0.105f),
            radius = size.width * 0.52f,
            center = Offset(
                size.width * 1.04f,
                size.height * 0.055f
            )
        )

        drawCircle(
            color = Color(0xFF5D9CFF).copy(alpha = 0.065f),
            radius = size.width * 0.38f,
            center = Offset(
                size.width * 1.00f,
                size.height * 0.91f
            )
        )

        val lineColor = Color(0xFF1769FF).copy(alpha = 0.028f)

        for (i in -8..13) {
            val x = i * size.width * 0.12f

            drawLine(
                color = lineColor,
                start = Offset(
                    x,
                    size.height * 0.68f
                ),
                end = Offset(
                    x + size.width * 0.28f,
                    size.height
                ),
                strokeWidth = 1f
            )
        }

        val route = Path().apply {
            moveTo(
                size.width * 0.64f,
                size.height * 0.90f
            )
            cubicTo(
                size.width * 0.73f,
                size.height * 0.83f,
                size.width * 0.79f,
                size.height * 0.94f,
                size.width * 0.88f,
                size.height * 0.87f
            )
            cubicTo(
                size.width * 0.94f,
                size.height * 0.83f,
                size.width * 0.97f,
                size.height * 0.85f,
                size.width,
                size.height * 0.81f
            )
        }

        drawPath(
            path = route,
            color = FieldOpsBlue.copy(alpha = 0.075f),
            style = Stroke(
                width = 2f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        drawCircle(
            color = FieldOpsBlue.copy(alpha = 0.09f),
            radius = 4.5f,
            center = Offset(
                size.width * 0.88f,
                size.height * 0.87f
            )
        )
    }
}

@Composable
private fun EmailFieldIcon(
    modifier: Modifier = Modifier,
    color: Color
) {
    Canvas(modifier = modifier) {
        val stroke = 1.65f

        drawRoundRect(
            color = color,
            topLeft = Offset(
                size.width * 0.08f,
                size.height * 0.20f
            ),
            size = Size(
                width = size.width * 0.84f,
                height = size.height * 0.60f
            ),
            cornerRadius = CornerRadius(
                size.width * 0.10f,
                size.width * 0.10f
            ),
            style = Stroke(width = stroke)
        )

        val path = Path().apply {
            moveTo(
                size.width * 0.11f,
                size.height * 0.30f
            )
            lineTo(
                size.width * 0.50f,
                size.height * 0.56f
            )
            lineTo(
                size.width * 0.89f,
                size.height * 0.30f
            )
        }

        drawPath(
            path = path,
            color = color,
            style = Stroke(
                width = stroke,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}

@Composable
private fun LockFieldIcon(
    modifier: Modifier = Modifier,
    color: Color
) {
    Canvas(modifier = modifier) {
        val stroke = 1.75f

        drawRoundRect(
            color = color,
            topLeft = Offset(
                size.width * 0.19f,
                size.height * 0.43f
            ),
            size = Size(
                width = size.width * 0.62f,
                height = size.height * 0.40f
            ),
            cornerRadius = CornerRadius(
                size.width * 0.08f,
                size.width * 0.08f
            ),
            style = Stroke(width = stroke)
        )

        drawArc(
            color = color,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(
                x = size.width * 0.29f,
                y = size.height * 0.15f
            ),
            size = Size(
                width = size.width * 0.42f,
                height = size.height * 0.48f
            ),
            style = Stroke(
                width = stroke,
                cap = StrokeCap.Round
            )
        )

        drawCircle(
            color = color,
            radius = size.minDimension * 0.055f,
            center = Offset(
                x = size.width * 0.50f,
                y = size.height * 0.61f
            )
        )
    }
}

@Composable
private fun PasswordVisibilityIcon(
    visible: Boolean,
    modifier: Modifier = Modifier,
    color: Color
) {
    Canvas(modifier = modifier) {
        val stroke = 1.55f

        val eyePath = Path().apply {
            moveTo(
                size.width * 0.09f,
                size.height * 0.50f
            )
            cubicTo(
                size.width * 0.28f,
                size.height * 0.22f,
                size.width * 0.72f,
                size.height * 0.22f,
                size.width * 0.91f,
                size.height * 0.50f
            )
            cubicTo(
                size.width * 0.72f,
                size.height * 0.78f,
                size.width * 0.28f,
                size.height * 0.78f,
                size.width * 0.09f,
                size.height * 0.50f
            )
        }

        drawPath(
            path = eyePath,
            color = color,
            style = Stroke(
                width = stroke,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        drawCircle(
            color = color,
            radius = size.minDimension * 0.14f,
            center = center,
            style = Stroke(width = stroke)
        )

        if (!visible) {
            drawLine(
                color = color,
                start = Offset(
                    size.width * 0.17f,
                    size.height * 0.18f
                ),
                end = Offset(
                    size.width * 0.83f,
                    size.height * 0.82f
                ),
                strokeWidth = stroke,
                cap = StrokeCap.Round
            )
        }
    }
}
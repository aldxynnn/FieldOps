package com.example.fieldops.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fieldops.data.model.WorkOrderStatus
import com.example.fieldops.ui.theme.FieldOpsBackground
import com.example.fieldops.ui.theme.FieldOpsDanger
import com.example.fieldops.ui.theme.FieldOpsDangerSoft
import com.example.fieldops.ui.theme.FieldOpsPrimary
import com.example.fieldops.ui.theme.FieldOpsPrimarySoft
import com.example.fieldops.ui.theme.FieldOpsPurple
import com.example.fieldops.ui.theme.FieldOpsPurpleSoft
import com.example.fieldops.ui.theme.FieldOpsSuccess
import com.example.fieldops.ui.theme.FieldOpsSuccessSoft
import com.example.fieldops.ui.theme.FieldOpsWarning
import com.example.fieldops.ui.theme.FieldOpsWarningSoft

@Composable
fun EnterpriseCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        content = { content() }
    )
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    action: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            if (subtitle != null) {
                Spacer(Modifier.height(3.dp))
                Text(subtitle, style = MaterialTheme.typography.bodySmall)
            }
        }
        action?.invoke()
    }
}

@Composable
fun StatusPill(status: WorkOrderStatus, compact: Boolean = false) {
    val (label, fg, bg) = when (status) {
        WorkOrderStatus.PENDING -> Triple("PENDING", FieldOpsWarning, FieldOpsWarningSoft)
        WorkOrderStatus.ACCEPTED -> Triple("DITERIMA", FieldOpsPrimary, FieldOpsPrimarySoft)
        WorkOrderStatus.IN_PROGRESS -> Triple("DIKERJAKAN", FieldOpsPurple, FieldOpsPurpleSoft)
        WorkOrderStatus.COMPLETED -> Triple("SELESAI", FieldOpsSuccess, FieldOpsSuccessSoft)
        WorkOrderStatus.CANCELLED -> Triple("DIBATALKAN", FieldOpsDanger, FieldOpsDangerSoft)
    }
    Surface(shape = RoundedCornerShape(50), color = bg) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = if (compact) 9.dp else 11.dp, vertical = 5.dp),
            color = fg,
            fontSize = if (compact) 10.sp else 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.3.sp
        )
    }
}

@Composable
fun Avatar(
    initials: String,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 48.dp
) {
    Surface(
        modifier = modifier.size(size),
        shape = CircleShape,
        color = FieldOpsPrimarySoft
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                initials.take(2).uppercase(),
                color = FieldOpsPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = (size.value / 2.7f).sp
            )
        }
    }
}

@Composable
fun MetricCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    accent: Color = FieldOpsPrimary
) {
    EnterpriseCard(modifier) {
        Column(Modifier.padding(16.dp)) {
            Surface(shape = RoundedCornerShape(10.dp), color = accent.copy(alpha = 0.10f)) {
                Text(value, modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp), color = accent, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(10.dp))
            Text(label, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
fun AccentHero(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {}
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = FieldOpsPrimary)
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(title, color = Color.White, style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(5.dp))
            Text(subtitle, color = Color.White.copy(alpha = 0.82f), style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(18.dp))
            content()
        }
    }
}

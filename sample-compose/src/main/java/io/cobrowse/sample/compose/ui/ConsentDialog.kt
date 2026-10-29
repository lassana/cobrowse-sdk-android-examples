package io.cobrowse.sample.compose.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.cobrowse.sample.core.R
import io.cobrowse.sample.data.ConsentRequest

/**
 * The agent's request waiting for the user's consent, if any.
 */
object ConsentPrompt {
    var request by mutableStateOf<ConsentRequest?>(null)
}

/**
 * Asks the user to consent to the agent's request, if there is one.
 */
@Composable
fun ConsentDialog() {
    val request = ConsentPrompt.request ?: return
    val session by rememberCobrowseSession()

    LaunchedEffect(request, session) {
        val current = session
        if (current == null || !request.isPending(current)) {
            ConsentPrompt.request = null
        }
    }

    fun answer(allowed: Boolean) {
        session?.let {
            if (allowed) request.allow(it) else request.deny(it)
        }
        ConsentPrompt.request = null
    }

    val dark = isSystemInDarkTheme()
    val textColor = if (dark) Color.White else MaterialTheme.colorScheme.onSurface

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (dark) Color(0xFF1C1C1E) else Color(0xFFF6F6F7)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .padding(24.dp)
                .widthIn(max = 330.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(request.heading),
                    color = textColor,
                    fontSize = 18.sp,
                    lineHeight = 21.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(request.body),
                    color = textColor,
                    fontSize = 16.sp,
                    lineHeight = 21.sp,
                    modifier = Modifier.padding(top = 7.dp)
                )
                Text(
                    text = stringResource(request.terms),
                    color = if (dark) Color(0xFFA5A5C0) else Color(0xFF474765),
                    fontSize = 16.sp,
                    lineHeight = 21.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            HorizontalDivider(color = if (dark) Color(0xFF38383A) else Color(0xFFEEEFF1))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                TextButton(
                    onClick = { answer(allowed = false) },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if (dark) Color(0xFFFF6B6B) else Color(0xFFEF4343)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                ) {
                    Text(stringResource(R.string.consent_deny), fontSize = 16.sp)
                }
                Button(
                    onClick = { answer(allowed = true) },
                    shape = RoundedCornerShape(9.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                ) {
                    Text(stringResource(R.string.consent_allow), fontSize = 16.sp)
                }
            }
        }
    }
}

package io.cobrowse.sample.compose.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// The buttons look like the Material Components buttons of the View-based sample

private fun buttonTextStyle(fontSize: TextUnit) = TextStyle(
    fontSize = fontSize,
    fontWeight = FontWeight.Medium,
    letterSpacing = 1.25.sp
)

/**
 * A filled button, 48dp tall including the 6dp insets above and below it.
 */
@Composable
fun SampleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .padding(vertical = 6.dp)
            .height(36.dp),
        enabled = enabled,
        shape = RoundedCornerShape(4.dp),
        colors = colors,
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp, pressedElevation = 8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        ProvideTextStyle(buttonTextStyle(14.sp)) {
            content()
        }
    }
}

/**
 * A text button, 48dp tall.
 */
@Composable
fun SampleTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 14.sp
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(text, style = buttonTextStyle(fontSize))
    }
}

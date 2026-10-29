package dev.legendtuner.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.legendtuner.app.AppState
import dev.legendtuner.app.ui.theme.Amber
import dev.legendtuner.app.ui.theme.Dim
import dev.legendtuner.app.ui.theme.Ink
import dev.legendtuner.app.ui.theme.Lime
import dev.legendtuner.app.ui.theme.Line
import dev.legendtuner.app.ui.theme.Muted
import dev.legendtuner.app.ui.theme.Red
import dev.legendtuner.app.ui.theme.Surface

@Composable
fun RawXmlScreen(
    state: AppState,
    onTextChange: (String) -> Unit,
    onApply: () -> Unit,
    onSave: () -> Unit,
    onReload: () -> Unit,
    onOpenAccess: () -> Unit
) {
    Column(Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
        PageHeader(
            crumb = "Legend Tuner / Raw XML",
            title = "Raw XML",
            subtitle = "The complete preferences registry — unknown keys preserved",
            mode = state.accessMode,
            onSettings = onOpenAccess,
            actions = {
                GhostButton("Reload", onReload)
                LegendButton("Apply to draft", onApply, enabled = !state.saving)
                LegendButton("Save", onSave, enabled = !state.saving)
            }
        )
        Spacer(Modifier.height(14.dp))
        if (state.rawError != null) {
            Surface(
                color = Red.copy(alpha = 0.08f),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, Red.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "XML error: ${state.rawError}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFFFB4AE),
                    modifier = Modifier.padding(11.dp)
                )
            }
            Spacer(Modifier.height(10.dp))
        }
        Row(
            Modifier.fillMaxWidth().weight(1f),
            verticalAlignment = androidx.compose.ui.Alignment.Top
        ) {
            Surface(
                color = Surface,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Line),
                modifier = Modifier.fillMaxSize()
            ) {
                Column {
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
                    ) {
                        Text(
                            "preferences · UTF-8",
                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 7.sp),
                            color = Dim
                        )
                        Text(
                            "${state.rawXml.length} chars",
                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 7.sp),
                            color = Dim
                        )
                    }
                    Box(
                        Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(Color(0xFF0A0C09))
                            .padding(horizontal = 13.dp, vertical = 8.dp)
                    ) {
                        BasicTextField(
                            value = state.rawXml,
                            onValueChange = onTextChange,
                            modifier = Modifier.fillMaxSize(),
                            textStyle = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                lineHeight = 17.sp,
                                color = Ink
                            ),
                            cursorBrush = SolidColor(Lime)
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            "Edits are parsed and validated before they replace the draft. Save still writes to the game file; use Backups if you need to roll back.",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.5.sp, letterSpacing = 0.2.sp),
            color = Dim,
            modifier = Modifier.padding(bottom = 12.dp)
        )
    }
}

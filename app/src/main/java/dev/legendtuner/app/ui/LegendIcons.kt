package dev.legendtuner.app.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

/** Small local icon set so the APK does not carry the whole material-icons-extended pack. */
object LegendIcons {
    val Dashboard: ImageVector = vector(
        "Dashboard",
        "M3,13h8V3H3v10zM3,21h8v-6H3v6zM13,21h8V11h-8v10zM13,3v6h8V3h-8z"
    )
    val Tune: ImageVector = vector(
        "Tune",
        "M3,17v2h6v-2H3zM3,5v2h10V5H3zM13,21v-2h8v-2h-8v-2h-2v6h2zM7,9v2H3v2h4v2h2V9H7zM21,13v-2H11v2h10zM15,9h2V7h4V5h-4V3h-2v6z"
    )
    val Backups: ImageVector = vector(
        "Backups",
        "M19,12v7H5v-7H3v7c0,1.1,0.9,2,2,2h14c1.1,0,2-0.9,2-2v-7h-2zM13,12V4h-2v8H8l4,4,4-4h-3z"
    )
    val Code: ImageVector = vector(
        "Code",
        "M9.4,16.6L4.8,12l4.6,-4.6L8,6l-6,6,6,6,1.4,-1.4zM14.6,16.6l4.6,-4.6,-4.6,-4.6L16,6l6,6,-6,6,-1.4,-1.4z"
    )
    val Settings: ImageVector = vector(
        "Settings",
        "M19.43,12.98c0.04,-0.32,0.07,-0.65,0.07,-0.98s-0.03,-0.66,-0.07,-0.98l2.11,-1.65c0.19,-0.15,0.24,-0.42,0.12,-0.64l-2,-3.46c-0.12,-0.22,-0.39,-0.3,-0.61,-0.22l-2.49,1c-0.52,-0.4,-1.08,-0.73,-1.69,-0.98l-0.38,-2.65C14.46,2.18,14.25,2,14,2h-4c-0.25,0,-0.46,0.18,-0.49,0.42l-0.38,2.65c-0.61,0.25,-1.17,0.59,-1.69,0.98l-2.49,-1c-0.22,-0.08,-0.49,0,-0.61,0.22l-2,3.46c-0.13,0.22,-0.07,0.49,0.12,0.64l2.11,1.65c-0.04,0.32,-0.07,0.65,-0.07,0.98s0.03,0.66,0.07,0.98l-2.11,1.65c-0.19,0.15,-0.24,0.42,-0.12,0.64l2,3.46c0.12,0.22,0.39,0.3,0.61,0.22l2.49,-1c0.52,0.4,1.08,0.73,1.69,0.98l0.38,2.65c0.03,0.24,0.24,0.42,0.49,0.42h4c0.25,0,0.46,-0.18,0.49,-0.42l0.38,-2.65c0.61,-0.25,1.17,-0.59,1.69,-0.98l2.49,1c0.22,0.08,0.49,0,0.61,-0.22l2,-3.46c0.12,-0.22,0.07,-0.49,-0.12,-0.64l-2.11,-1.65zM12,15.5c-1.93,0,-3.5,-1.57,-3.5,-3.5s1.57,-3.5,3.5,-3.5,3.5,1.57,3.5,3.5,-1.57,3.5,-3.5,3.5z"
    )
    val Warning: ImageVector = vector(
        "Warning",
        "M1,21h22L12,2,1,21zM13,18h-2v-2h2v2zM13,14h-2v-4h2v4z"
    )
    val MoreVert: ImageVector = vector(
        "MoreVert",
        "M12,8c1.1,0,2,-0.9,2,-2s-0.9,-2,-2,-2,-2,0.9,-2,2,0.9,2,2,2zM12,10c-1.1,0,-2,0.9,-2,2s0.9,2,2,2,2,-0.9,2,-2,-0.9,-2,-2,-2zM12,16c-1.1,0,-2,0.9,-2,2s0.9,2,2,2,2,-0.9,2,-2,-0.9,-2,-2,-2z"
    )
    val Delete: ImageVector = vector(
        "Delete",
        "M6,19c0,1.1,0.9,2,2,2h8c1.1,0,2,-0.9,2,-2V7H6v12zM19,4h-3.5l-1,-1h-5l-1,1H5v2h14V4z"
    )
    val Download: ImageVector = vector(
        "Download",
        "M19,9h-4V3H9v6H5l7,7,7,-7zM5,18v2h14v-2H5z"
    )
    val Restore: ImageVector = vector(
        "Restore",
        "M13,3c-4.97,0,-9,4.03,-9,9H1l3.89,3.89,0.07,0.14L9,12H6c0,-3.87,3.13,-7,7,-7s7,3.13,7,7,-3.13,7,-7,7c-1.93,0,-3.68,-0.78,-4.94,-2.06l-1.42,1.42C8.27,19.99,10.51,21,13,21c4.97,0,9,-4.03,9,-9s-4.03,-9,-9,-9zM12,8v5l4.25,2.52,0.77,-1.28,-3.52,-2.09V8H12z"
    )

    private fun vector(name: String, pathData: String): ImageVector {
        val nodes = PathParser().parsePathString(pathData).toNodes()
        return ImageVector.Builder(
            name = name,
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).addPath(
            pathData = nodes,
            fill = SolidColor(Color.Black)
        ).build()
    }
}

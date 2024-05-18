package sample_test_app.com.ui.component.Chat

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class InvertedTriangleEdgeShape(val offset: Int) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val invertedTrianglePath = Path().apply {
            moveTo(y = 0f, x = size.height-offset)
            lineTo(y = 0f, x = size.height+15)
            lineTo(y = 0f + offset + 15, x = size.height+15)
        }
        return Outline.Generic(path = invertedTrianglePath)
    }
}

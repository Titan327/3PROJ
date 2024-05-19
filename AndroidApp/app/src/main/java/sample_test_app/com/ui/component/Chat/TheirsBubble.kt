package sample_test_app.com.ui.component.Chat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TheirsBubble(message: String,userId: String,userName: String){
    Column (
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Text(style = TextStyle(fontSize = 10.sp, color = Color.White), text=userName)
        Row (
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Column(
                modifier = Modifier
                    .background(
                        color = Color(0xFF4DD0E1),
                        shape = InvertedTriangleEdgeShape(20)
                    )
                    .size(10.dp)
            ){
            }
            Column(
                modifier = Modifier
                    .background(
                        color = Color(0xFF4DD0E1),
                        shape = RoundedCornerShape(0.dp, 4.dp, 4.dp, 4.dp)
                    )
                    .padding(8.dp)
                    .widthIn(min = 50.dp)
            ) {
                Text(message)
            }
        }


    }

}


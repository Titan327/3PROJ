import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import sample_test_app.com.R


@Composable
fun SplashScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp, bottom = 10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logomid),
            contentDescription = null,
            modifier = Modifier
                .padding(bottom = 20.dp)
                .height(500.dp)
                .width(500.dp)
        )

        Button(
            onClick = { navController.navigate("register") },
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(top = 10.dp)
                .height(70.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF5490A1)
            )
        ) {
            Text(
                text = "Inscription",
                fontSize = 18.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = { navController.navigate("login") },
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(top = 38.dp, bottom = 30.dp)
                .height(70.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF5490A1)
            )
        ) {
            Text(
                text = "Connexion",
                fontSize = 18.sp,
                color = Color.White
            )
        }
    }
}

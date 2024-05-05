package sample_test_app.com.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import sample_test_app.com.LocalUser
import sample_test_app.com.R


const val KEY_ROUTE = "androidx.navigation.compose.KEY_ROUTE"
@Composable
fun MainScreen(navController: NavController, content: @Composable () -> Unit) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.arguments?.getString(KEY_ROUTE)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.logomid),
            contentDescription = "Logo",
            modifier = Modifier.align(Alignment.TopCenter)
        )

        if (LocalUser.current.profile_picture[0].isNotBlank() && LocalUser.current.profile_picture[0] != "null") {
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data(data = LocalUser.current.profile_picture[0]).apply(block = fun ImageRequest.Builder.() {
                            transformations(CircleCropTransformation())
                        }).build()
                ),
                contentDescription = "User Profile Picture",
                modifier = Modifier
                    .size(80.dp)
                    .padding(top = 16.dp)
                    .align(Alignment.TopEnd)
                    .clickable {
                        navController.navigate("ProfilScreen")
                    }
            )
        } else {
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current).data(data = R.drawable.userdefault)
                        .apply(block = fun ImageRequest.Builder.() {
                            transformations(CircleCropTransformation())
                        }).build()
                ),
                contentDescription = "User Profile Picture",
                modifier = Modifier
                    .size(80.dp)
                    .padding(top = 16.dp)
                    .align(Alignment.TopEnd)
                    .clickable {
                        navController.navigate("ProfilScreen")
                    }
            )
        }
    }

    content()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Image(
                painter = painterResource(id = R.drawable.groupslogo),
                contentDescription = "Settings Icon",
                modifier = Modifier
                    .size(40.dp)
                    .clickable { /* Ajoutez votre action de clic ici */ }
            )

            Image(
                painter = painterResource(id = R.drawable.homepage),
                contentDescription = "Home Icon",
                modifier = Modifier
                    .size(40.dp)
                    .clickable { /* Ajoutez votre action de clic ici */ }
            )

            Image(
                painter = painterResource(id = R.drawable.notificationbellhome),
                contentDescription = "Notification Icon",
                modifier = Modifier
                    .size(40.dp)
                    .clickable { /* Ajoutez votre action de clic ici */ }
            )
        }
    }
}
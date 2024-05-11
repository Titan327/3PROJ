package sample_test_app.com.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import sample_test_app.com.LocalUser
import sample_test_app.com.R


@Composable
fun MainScreen(navController: NavController, groupPicture : String? = null, content: @Composable () -> Unit) {
    val currentRoute = navController.currentDestination?.route
    val scrollState = rememberScrollState()

    Column{
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .weight(1f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (groupPicture != null) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                ImageRequest.Builder(LocalContext.current)
                                    .data(data = groupPicture)
                                    .apply(block = fun ImageRequest.Builder.() {
                                        transformations(CircleCropTransformation())
                                    }).build()
                            ),
                            contentDescription = "Group Picture",
                            modifier = Modifier
                                .fillMaxWidth()
                                .size(180.dp)
                                .padding(top = 16.dp, bottom = 16.dp)
                        )
                    }
                    else {
                        Image(
                            painter = painterResource(id = R.drawable.logomid),
                            contentDescription = "Group Picture",
                            modifier = Modifier
                                .fillMaxWidth()
                                .size(200.dp)
                        )
                    }

                    if (LocalUser.current.profile_picture?.get(0)
                            ?.isNotBlank() == true && LocalUser.current.profile_picture!![0] != "null"
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                ImageRequest.Builder(LocalContext.current)
                                    .data(data = LocalUser.current.profile_picture!![0])
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
                    } else {
                        Image(
                            painter = rememberAsyncImagePainter(
                                ImageRequest.Builder(LocalContext.current)
                                    .data(data = R.drawable.userdefault)
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
                                    navController.navigate("profilScreen")
                                }
                        )
                    }
                }
            }


            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                content()
            }
        }

        Row (
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            horizontalArrangement = Arrangement.SpaceAround
        )  {
            Image(
                painter = if (currentRoute == "groupList") {
                    painterResource(id = R.drawable.groupslogofull)
                } else {
                    painterResource(id = R.drawable.groupslogo)
                },
                contentDescription = "Settings Icon",
                modifier = Modifier
                    .size(40.dp)
                    .clickable { navController.navigate("groupList") }
            )

            Image(
                painter = if (currentRoute == "home") {
                    painterResource(id = R.drawable.homelogofull)
                } else {
                    painterResource(id = R.drawable.homepage)
                },
                contentDescription = "Home Icon",
                modifier = Modifier
                    .size(40.dp)
                    .clickable { navController.navigate("home") }
            )

            Image(
                painter = if (currentRoute == "notifications") {
                    painterResource(id = R.drawable.notificationlogofull)
                } else {
                    painterResource(id = R.drawable.notificationlogo)
                },
                contentDescription = "Notification Icon",
                modifier = Modifier
                    .size(40.dp)
                    .clickable { navController.navigate("notifications") }
            )
        }
    }
}
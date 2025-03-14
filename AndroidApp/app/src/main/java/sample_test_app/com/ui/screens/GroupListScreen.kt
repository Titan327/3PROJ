package sample_test_app.com.ui.screens
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sample_test_app.com.LocalJwtToken
import sample_test_app.com.LocalUser
import sample_test_app.com.R
import sample_test_app.com.http.Repository.GroupRepository
import sample_test_app.com.models.Group

@Composable
fun GroupListScreen(httpClient: HttpClient, navController: NavController) {
    val jwtToken = LocalJwtToken.current
    val userId = LocalUser.current.id.toString()
    val groups = remember { mutableStateOf(emptyList<Group>()) }
    val groupRepository = GroupRepository(httpClient)
    val showDialog = remember { mutableStateOf(false) }
    val groupName = remember { mutableStateOf("") }
    val groupDescription = remember { mutableStateOf("") }


    LaunchedEffect(key1 = userId) {
        CoroutineScope(Dispatchers.Main).launch {
            groups.value = groupRepository.getUserGroups(userId, jwtToken, false)
        }
    }

    Column {
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ){
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ){
                Row (
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Mes groupes :",
                        modifier = Modifier.height(64.dp),
                        color = Color.White,
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline
                        )
                    )

                    IconButton(onClick = { showDialog.value = true }) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_cross),
                            contentDescription = "Create Group",
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                if (showDialog.value) {
                    AlertDialog(
                        onDismissRequest = { showDialog.value = false },
                        title = { Text("Création d'un Groupe") },
                        text = {
                            Column {
                                TextField(
                                    value = groupName.value,
                                    onValueChange = { groupName.value = it },
                                    label = { Text("Nom du Groupe") },
                                    colors = TextFieldDefaults.textFieldColors(
                                        focusedIndicatorColor = Color(0xFFFFA31A),
                                        unfocusedIndicatorColor = Color(0xFFFFA31A)
                                    )
                                )

                                TextField(
                                    value = groupDescription.value,
                                    onValueChange = { groupDescription.value = it },
                                    label = { Text("Description du Groupe") },
                                    colors = TextFieldDefaults.textFieldColors(
                                        focusedIndicatorColor = Color(0xFFFFA31A),
                                        unfocusedIndicatorColor = Color(0xFFFFA31A)
                                    )
                                )
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        groupRepository.createGroup(jwtToken, groupName.value, groupDescription.value)
                                        showDialog.value = false
                                        groups.value = groupRepository.getUserGroups(userId, jwtToken, false)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Color(0xFFFFA31A)
                                )
                            ) {
                                Text("Création du Groupe")
                            }
                        },
                        dismissButton = {
                            Button(
                                onClick = { showDialog.value = false },
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Color(0xFFFFA31A)
                                )
                            ) {
                                Text("Anuler")
                            }
                        }
                    )
                }

                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                ) {

                    if (groups.value.isEmpty()) {
                        Text(
                            text = "",
                            color = Color.White,
                            style = TextStyle(
                                fontSize = 18.sp
                            )
                        )
                    } else {
                        for (group in groups.value) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(shape = RoundedCornerShape(16.dp))
                                    .background(color = Color(0xFF808080))
                                    .padding(8.dp)
                                    .fillMaxWidth()
                                    .clickable {
                                        navController.navigate("group/${group.id}")
                                    }
                            ) {
                                if (group.picture?.isNotEmpty() == true) {
                                    Image(
                                        painter = rememberAsyncImagePainter(
                                            ImageRequest.Builder(LocalContext.current)
                                                .data(data = group.picture[0])
                                                .apply(block = fun ImageRequest.Builder.() {
                                                    transformations(CircleCropTransformation())
                                                }).build()
                                        ),

                                        contentDescription = "Group Picture",
                                        modifier = Modifier
                                            .size(80.dp)
                                    )
                                } else {
                                    Image(
                                        painter = rememberAsyncImagePainter(
                                            ImageRequest.Builder(LocalContext.current)
                                                .data(data = R.drawable.groupslogofull)
                                                .apply(block = fun ImageRequest.Builder.() {
                                                    transformations(CircleCropTransformation())
                                                }).build()
                                        ),
                                        contentDescription = "Logo",
                                        modifier = Modifier
                                            .size(80.dp)
                                            .padding(top = 16.dp)
                                    )
                                }
                                Text(
                                    text = group.name ?: "",
                                    color = Color.White,
                                    style = TextStyle(
                                        fontSize = 22.sp
                                    ),
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(24.dp))

                        }
                    }
                }
            }
        }
    }
}


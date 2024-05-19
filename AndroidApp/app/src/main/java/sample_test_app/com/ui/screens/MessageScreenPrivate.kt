import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sample_test_app.com.LocalJwtToken
import sample_test_app.com.http.Repository.MessageRepository
import sample_test_app.com.http.Repository.OauthGoogleRepositry
import sample_test_app.com.ui.component.Chat.MyBubble
import sample_test_app.com.ui.component.Chat.TheirsBubble
import sample_test_app.com.ui.component.Chat.TriangleEdgeShape
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import sample_test_app.com.LocalUser
import sample_test_app.com.http.Repository.GroupRepository
import sample_test_app.com.models.Group
import sample_test_app.com.models.Message
import sample_test_app.com.models.User


fun fetchUser(httpClient: HttpClient, userId: String, jwtToken: String): User? {
    return runBlocking {
        val userResponse: HttpResponse = withContext(Dispatchers.IO) {
            httpClient.get("https://3proj-back.tristan-tourbier.com/api/users/$userId") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $jwtToken")
                println("Received token: $jwtToken")
            }
        }

        if (userResponse.status == HttpStatusCode.OK) {
            val userResponseBody = userResponse.bodyAsText()
            Json.decodeFromString<User>(userResponseBody)
        } else {
            null
        }
    }
}


@Composable
fun MessageScreenPrivate(navController: NavHostController, httpClient: HttpClient, groupId: String?) {
    val messageRepository = remember { MessageRepository(httpClient) }
    val groupRepository = remember { GroupRepository(httpClient) }
    val jwtToken = LocalJwtToken.current
    val userId = LocalUser.current.id.toString()
    var messages by remember { mutableStateOf(listOf(listOf<String>())) }
    var messText by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val listState = rememberLazyListState()
    var messCount = 0

    var groupId = groupId!!.replace("-", "/")
    val tmparray = groupId.split("/").map { it.toString() }.toTypedArray()

    groupId = tmparray[0]+"/"+tmparray[1]+"/"+tmparray[2]
    val groupIdSocket = tmparray[0]

    Log.i("gpi",groupId)

    val user: User? = fetchUser(httpClient, userId, jwtToken)

    var otherId:String

    if (tmparray[0]==userId){
        otherId = tmparray[0]
    }else{
        otherId = tmparray[1]
    }

    val otherUsername = tmparray[2]


    val socket = remember {
        SocketIoHandler.setSocket()
        val socketInstance = SocketIoHandler.getSocket()
        socketInstance.connect()
        socketInstance
    }

    DisposableEffect(socket) {
        socket.on("chat-private-$groupIdSocket") { args ->
            if (args.isNotEmpty()) {
                val message = args[0]?.toString().orEmpty()
                val group = args[1]?.toString().orEmpty()
                val user = args[2]?.toString().orEmpty()
                val username = args[3]?.toString().orEmpty()

                Log.i("Socket", "message: $message, group: $group, user: $user, username: $username")
                CoroutineScope(Dispatchers.Main).launch {
                    messages = messages + listOf(listOf(message, user,username))
                    listState.scrollToItem(messages.size - 1)
                    messCount += 1
                    Log.i("Socket", "messCount: $messCount")
                }
            }
        }

        onDispose {
            socket.disconnect()
        }
    }


    fun isValidString(str: String?): Boolean {
        return !str.isNullOrEmpty() && str.trim().isNotEmpty()
    }


    fun onClick(text: String) {
        try {
            if (isValidString(text)){
                CoroutineScope(Dispatchers.Main).launch {
                    val isSent = groupId?.let { messageRepository.sendMessagePrivate(jwtToken, it, text) }
                    if (isSent == 200) {
                        socket.emit("private message", text, groupIdSocket, otherId, user!!.username,user.id)
                        messText = ""
                    } else {
                        Log.e("Socket", "Failed to send message: $isSent")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("Socket", "Error: $e")
        }
    }

    DisposableEffect(Unit) {
        focusRequester.requestFocus()
        onDispose {}
    }


    suspend fun LoadPreviousMess(){
        Log.i("Socket","on est laa")
        if (groupId != null) {
            val savePosition = messCount
            Log.i("pos",savePosition.toString())
            val limit = "50"

            val page = messCount / limit.toInt() + 1

            val offset = messCount % limit.toInt()


            val AllMess = messageRepository.getmessagePrivate(groupId,limit, page.toString(),jwtToken)
            var mess: Array<Message>? = null
            if (AllMess != null){
                mess = AllMess.drop(offset).toTypedArray()
            }

            if (mess != null) {
                Log.i("Socket","nb mess: "+mess.count())

                //messages = listOf(listOf("testttttttt-------", "1"))

                mess.forEach{
                    messages = listOf(listOf(it.text, it.userId.toString(),otherUsername)) + messages
                    messCount += 1
                }
                if (messCount == limit.toInt()){
                    listState.scrollToItem(messages.size - 1)
                }else{
                    listState.scrollToItem(savePosition)
                }

            }

        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LaunchedEffect(listState) {
            snapshotFlow { listState.firstVisibleItemIndex }
                .collect { index ->
                    if (index == 0) {
                        // Appelez votre fonction ici
                        LoadPreviousMess()
                    }
                }
        }
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Bottom
        ) {
            items(messages) { message ->
                if (message.isNotEmpty()) {
                    if (message[1] == userId) {
                        MyBubble(message[0])
                    } else {

                        TheirsBubble(message[0], message[1], message[2])


                    }
                }
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = messText,
                onValueChange = { messText = it },
                placeholder = { Text("Enter your message...") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(
                    onSend = { onClick(messText) }
                ),
                modifier = Modifier
                    .weight(1f)
                    .background(color = Color.White)
                    .height(55.dp)
                    .focusRequester(focusRequester)
            )
            Button(
                modifier = Modifier.height(55.dp),
                onClick = { onClick(messText) }
            ) {
                Text("Send")
            }
        }
    }
}
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

@Composable
fun MessageScreen(navController: NavHostController, httpClient: HttpClient,) {
    val messageRepository = MessageRepository(httpClient)
    val jwtToken = LocalJwtToken.current
    var messages by remember { mutableStateOf(listOf<String>()) }
    var messText by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }


    val socket = remember {
        SocketIoHandler.setSocket()
        val socketInstance = SocketIoHandler.getSocket()
        socketInstance.connect()
        socketInstance
    }

    DisposableEffect(socket) {
        socket.on("chat-group-5") { args ->
            if (args.isNotEmpty()) {
                val message = args[0].toString()
                Log.i("Socket", message)
                CoroutineScope(Dispatchers.Main).launch {
                    messages = messages + message
                }
            }
        }

        onDispose {
            socket.disconnect()
        }
    }

    fun onClick(text: String) {
        try {
            CoroutineScope(Dispatchers.Main).launch {
                val isSent = messageRepository.sendMessage(jwtToken, "5", text)
                if (isSent == 200) {
                    socket.emit("chat message", text, "5")
                    messText = ""
                }else{
                    Log.e("Socket",isSent.toString())
                }
            }
        } catch (e: Exception) {
            Log.e("Socket", e.toString())
        }
    }


    DisposableEffect(Unit) {
        focusRequester.requestFocus()
        onDispose {}
    }

    Column (
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Bottom
    ){
        messages.forEach { message ->
            TheirsBubble(message)
            MyBubble(message)
        }

        Row (

        ){
            TextField(
                value = messText,
                onValueChange = { messText = it },
                placeholder = { Text("Enter your message...") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(
                    onSend = { onClick(text = messText) }
                ),
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .background(
                        color = Color.White
                    )
                    .height(55.dp)

            )
            Button(
                modifier = Modifier
                    .height(55.dp),
                onClick = { onClick(messText) }
            ) {
                Text("Send")
            }
        }

    }
}

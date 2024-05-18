package sample_test_app.com.ui.component

import androidx.compose.foundation.layout.*
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Button
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.RadioButton
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Text
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.TextField
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.util.InternalAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.json.JSONObject
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonColors


@Serializable
data class PaymentInfo(
    val type: String,
    val name: String,
    val surname: String,
    val bank_name: String,
    val bank_number: String,
    val box_code: String,
    val account_number: String,
    val RIB_key: String,
    val IBAN: String
)

@OptIn(InternalAPI::class)
@Composable
fun PaymentForm(httpClient: HttpClient, jwtToken: String) {
    val typeState = remember { mutableStateOf("") }
    val nameState = remember { mutableStateOf("") }
    val surnameState = remember { mutableStateOf("") }
    val bankNameState = remember { mutableStateOf("") }
    val bankNumberState = remember { mutableStateOf("") }
    val boxCodeState = remember { mutableStateOf("") }
    val accountNumberState = remember { mutableStateOf("") }
    val ribKeyState = remember { mutableStateOf("") }
    val ibanState = remember { mutableStateOf("") }
    val paypalUsernameState = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row {
            RadioButton(
                selected = typeState.value == "RIB",
                onClick = { typeState.value = "RIB" },
                colors = RadioButtonColors(
                    selectedColor = Color.White,
                    unselectedColor = Color.White,
                    disabledSelectedColor = Color.Gray,
                    disabledUnselectedColor = Color.Gray
                )


            )
            Text("RIB", color = Color.White)


            RadioButton(
                selected = typeState.value == "Paypal",
                onClick = { typeState.value = "Paypal" },
                colors = RadioButtonColors(
                    selectedColor = Color.White,
                    unselectedColor = Color.White,
                    disabledSelectedColor = Color.Gray,
                    disabledUnselectedColor = Color.Gray
                )

            )
            Text("Paypal", color = Color.White)
        }

        if (typeState.value == "RIB") {
            TextField(
                value = nameState.value,
                onValueChange = { nameState.value = it },
                label = { Text("Nom") },
                modifier = Modifier.padding(bottom = 16.dp),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.Black,
                    cursorColor = Color.Black,
                    backgroundColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )
            TextField(
                value = surnameState.value,
                onValueChange = { surnameState.value = it },
                label = { Text("Prénom") },
                modifier = Modifier.padding(bottom = 16.dp),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.Black,
                    cursorColor = Color.Black,
                    backgroundColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )
            TextField(
                value = bankNameState.value,
                onValueChange = { bankNameState.value = it },
                label = { Text("Banque") },
                modifier = Modifier.padding(bottom = 16.dp),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.Black,
                    cursorColor = Color.Black,
                    backgroundColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )
            TextField(
                value = bankNumberState.value,
                onValueChange = { bankNumberState.value = it },
                label = { Text("Code banque") },
                modifier = Modifier.padding(bottom = 16.dp),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.Black,
                    cursorColor = Color.Black,
                    backgroundColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )
            TextField(
                value = boxCodeState.value,
                onValueChange = { boxCodeState.value = it },
                label = { Text("Code guiche") },
                modifier = Modifier.padding(bottom = 16.dp),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.Black,
                    cursorColor = Color.Black,
                    backgroundColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )
            TextField(
                value = accountNumberState.value,
                onValueChange = { accountNumberState.value = it },
                label = { Text("Numéros de compte") },
                modifier = Modifier.padding(bottom = 16.dp),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.Black,
                    cursorColor = Color.Black,
                    backgroundColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )
            TextField(
                value = ribKeyState.value,
                onValueChange = { ribKeyState.value = it },
                label = { Text("Clé RIB") },
                modifier = Modifier.padding(bottom = 16.dp),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.Black,
                    cursorColor = Color.Black,
                    backgroundColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )
            TextField(
                value = ibanState.value,
                onValueChange = { ibanState.value = it },
                label = { Text("IBAN") },
                modifier = Modifier.padding(bottom = 16.dp),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.Black,
                    cursorColor = Color.Black,
                    backgroundColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )



        }
        if (typeState.value == "Paypal") {
        TextField(
            value = paypalUsernameState.value,
            onValueChange = { paypalUsernameState.value = it },
            label = { Text("Pseudo Paypal") },
            modifier = Modifier.padding(bottom = 16.dp),
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color.Black,
                cursorColor = Color.Black,
                backgroundColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )
    }

        Button(onClick = {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val response: HttpResponse = httpClient.post("https://3proj-back.tristan-tourbier.com/api/users/paymentMethode") {
                        header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                        header("Authorization", "Bearer $jwtToken")
                        body = JSONObject().apply {
                            put("type", typeState.value)
                            if (typeState.value == "RIB") {
                                put("name", nameState.value)
                                put("surname", surnameState.value)
                                put("bank_name", bankNameState.value)
                                put("bank_number", bankNumberState.value)
                                put("box_code", boxCodeState.value)
                                put("account_number", accountNumberState.value)
                                put("RIB_key", ribKeyState.value)
                                put("IBAN", ibanState.value)
                            } else if (typeState.value == "Paypal") {
                                put("paypal_username", paypalUsernameState.value)
                            }
                            println("Sending POST request with body: $this")
                        }.toString()
                    }
                    if (response.status == HttpStatusCode.OK) {
                        println("Payment info post request succeeded.")
                    } else {
                        println("Payment info post request failed. Error code: ${response.status.value}")
                    }
                } catch (e: Exception) {
                    println("An error occurred while sending the request: ${e.message}")
                }
            }
        }) {
            Text("Submit")
        }
    }
}
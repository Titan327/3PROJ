package sample_test_app.com.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sample_test_app.com.LocalUser
import sample_test_app.com.http.Repository.GroupRepository
import sample_test_app.com.http.Repository.ProfilRepository
import sample_test_app.com.models.User
@Composable
fun ProfilScreen() {
    val user = LocalUser.current

    Column {
        Text(text = "First Name: ${user.firstname}")
        Text(text = "Last Name: ${user.lastname}")
        Text(text = "Username: ${user.username}")
        Text(text = "Email: ${user.email}")
        Text(text = "Birth Date: ${user.birth_date}")
    }
}
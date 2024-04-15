package sample_test_app.com.ui.screens

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sample_test_app.com.database.AppDatabase
import sample_test_app.com.database.recipe.Recipe

@Composable
fun TaskScreen(taskId: Int) {
    val application = LocalContext.current.applicationContext
    val database = AppDatabase.getInstance(application)
    val taskDao = database.taskDao()

    val recipe = remember { mutableStateOf<Recipe?>(null) }

    fun getTask() {
        CoroutineScope(Dispatchers.Main).launch {
            recipe.value = taskDao.getTask(taskId)
        }
    }

    getTask()

    recipe.value?.let {
        Text(
            text = it.title,
            fontSize = MaterialTheme.typography.h3.fontSize
        )
    }
}
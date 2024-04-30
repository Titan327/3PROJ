package sample_test_app.com.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sample_test_app.com.R
import sample_test_app.com.database.AppDatabase
import sample_test_app.com.database.recipe.Recipe

@Composable
fun TaskListScreen(navController: NavHostController) {
    val database = AppDatabase.getInstance(LocalContext.current.applicationContext)
    val taskDao = database.taskDao()

    val tasks = remember { mutableStateOf(listOf<Recipe>()) }

    val counter = remember { mutableStateOf(1) }

    fun getAllTasks() {
        CoroutineScope(Dispatchers.Main).launch {
            val tasksList = taskDao.getAllTasks()
            tasks.value = tasksList
            counter.value = tasksList.size + 1
        }
    }

    fun onAddTask() {
        CoroutineScope(Dispatchers.IO).launch {
            taskDao.insert(Recipe(title = "Task ${counter.value++}"))
            getAllTasks()
        }
    }

    fun onNavigateToTask(taskId: Int) {
        navController.navigate("task/$taskId")
    }

    getAllTasks()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        tasks.value.forEach { task ->
            TaskCard(task, onTaskClicked = { onNavigateToTask(it) })
        }

        Box(modifier = Modifier.fillMaxSize()) {
            ExtendedFloatingActionButton(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomEnd),
                icon = { Icon(painter = painterResource(id = R.drawable.baseline_add_24), contentDescription = null) },
                text = { Text(text = "Add task") },
                onClick = { onAddTask() }
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TaskCard(recipe: Recipe, onTaskClicked: (taskId: Int) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { },
        elevation = 4.dp,
        onClick = { onTaskClicked(recipe.id) }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            AsyncImage(
                modifier = Modifier.size(96.dp),
                model = "https://dummyimage.com/128x128/3562d4/ffffff.png&text=${recipe.id}",
                contentDescription = null,
            )
            Row(modifier = Modifier.padding(8.dp).weight(1f)) {
                Text(text = recipe.title, style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_chevron_right_24),
                        contentDescription = null
                    )
                }
            }
        }
    }
}
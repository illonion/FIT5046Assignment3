import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.todolist.MainSignup
import com.example.todolist.Analytics
import com.example.todolist.FriendsList
import com.example.todolist.Home
import com.example.todolist.MainLogin
import com.example.todolist.NavBarItem
import com.example.todolist.ToDoList
import com.example.todolist.Routes
import com.example.todolist.ToDoListItemViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BottomNavigationBar(toDoListViewModel: ToDoListItemViewModel) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            val navBackStackEntry by
            navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            if (currentDestination?.route !in listOf(Routes.MainLogin.value, Routes.MainSignup.value)) {
                BottomNavigation (backgroundColor= Color.LightGray ){
                    NavBarItem().NavBarItems().forEach { navItem ->
                        BottomNavigationItem(
                            icon = { Icon(navItem.icon, contentDescription = null) },
                            label = { Text(navItem.label) },
                            selected = currentDestination?.hierarchy?.any {
                                it.route == navItem.route
                            } == true,
                            onClick = {
                                navController.navigate(navItem.route) {
                                    // Commented this code out to make sure the logout button works
                                    // popUpTo(navController.graph.findStartDestination().id) {
                                    //     saveState = true
                                    // }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ){ paddingValues ->
        NavHost(
            navController,
            startDestination = Routes.MainLogin.value,
            Modifier.padding(paddingValues)
        ) {
            composable(Routes.Analytics.value) {
                Analytics(navController)
            }
            composable(Routes.CreateToDoListItem.value) {
                CreateToDoListItem(navController, toDoListViewModel)
            }
            composable(Routes.Home.value) {
                Home(navController)
            }
            composable(Routes.MainLogin.value) {
                MainLogin(navController)
            }
            composable(Routes.MainSignup.value) {
                MainSignup(navController)
            }
            composable(Routes.FriendsList.value) {
                FriendsList(navController)
            }
            composable(Routes.ToDoList.value) {
                ToDoList(navController, toDoListViewModel)
            }
        }
    }
}
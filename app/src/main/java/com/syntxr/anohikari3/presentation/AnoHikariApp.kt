package com.syntxr.anohikari3.presentation

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.generated.destinations.AdzanScreenDestination
import com.ramcosta.composedestinations.generated.destinations.HomeScreenDestination
import com.ramcosta.composedestinations.generated.destinations.OnBoardingScreenDestination
import com.ramcosta.composedestinations.generated.destinations.QiblaScreenDestination
import com.ramcosta.composedestinations.generated.destinations.ReadScreenDestination
import com.ramcosta.composedestinations.generated.destinations.SettingsScreenDestination
import com.ramcosta.composedestinations.manualcomposablecalls.composable
import com.ramcosta.composedestinations.utils.rememberDestinationsNavigator
import com.syntxr.anohikari3.presentation.adzan.AdzanScreen
import com.syntxr.anohikari3.presentation.component.AppDrawer
import com.syntxr.anohikari3.presentation.home.HomeScreen
import com.syntxr.anohikari3.presentation.qibla.QiblaScreen
import com.syntxr.anohikari3.presentation.read.ReadScreen
import com.syntxr.anohikari3.presentation.settings.SettingsScreen
import com.syntxr.anohikari3.utils.AppGlobalState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AnoHikariApp(
    navController: NavHostController = rememberNavController(),
    scope: CoroutineScope = rememberCoroutineScope()
) {
    val navigator = navController.rememberDestinationsNavigator()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentState = navBackStackEntry?.destination?.route ?: HomeScreenDestination.route
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val sharedViewModel : AnoHikariSharedViewModel = viewModel(LocalActivity.current as ComponentActivity)

    ModalNavigationDrawer(
        drawerContent = {
            if (!AppGlobalState.isOnBoarding){
                AppDrawer(
                    navigator = navigator,
                    navController = navController,
                    closeDrawer = { scope.launch { drawerState.close() } }
                )
            }
        },
        drawerState = drawerState,
        gesturesEnabled = currentState == HomeScreenDestination.route
    ) {
        DestinationsNavHost(
            navGraph = NavGraphs.root,
            navController = navController
        ){
            composable(HomeScreenDestination){

                if (AppGlobalState.isOnBoarding){
                    destinationsNavigator.navigate(OnBoardingScreenDestination){
                        popUpTo(OnBoardingScreenDestination){
                            inclusive = true
                            saveState = true
                        }
                        restoreState = false
                        launchSingleTop = true
                    }
                }

                HomeScreen(
                    openDrawer = { scope.launch { drawerState.open() } },
                    navigator = destinationsNavigator,
                    sharedViewModel = sharedViewModel
                )
            }
            composable(ReadScreenDestination){
                ReadScreen(
                    navigator = destinationsNavigator,
                    sharedViewModel = sharedViewModel
                )
            }
            composable(SettingsScreenDestination){
                SettingsScreen(
                    navigator = destinationsNavigator
                )
            }
            composable(AdzanScreenDestination){
                AdzanScreen(navigator = destinationsNavigator)
            }
            composable(QiblaScreenDestination){
                QiblaScreen(navigator = destinationsNavigator)
            }
        }
    }
}
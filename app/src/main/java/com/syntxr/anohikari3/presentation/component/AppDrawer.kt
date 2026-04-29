package com.syntxr.anohikari3.presentation.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Timelapse
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.generated.destinations.AdzanScreenDestination
import com.ramcosta.composedestinations.generated.destinations.HomeScreenDestination
import com.ramcosta.composedestinations.generated.destinations.QiblaScreenDestination
import com.ramcosta.composedestinations.generated.destinations.SettingsScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec
import com.ramcosta.composedestinations.utils.currentDestinationAsState
import com.ramcosta.composedestinations.utils.startDestination
import com.syntxr.anohikari3.R
import com.syntxr.anohikari3.ui.theme.novaMonoFontFamily
import com.syntxr.anohikari3.ui.theme.ubuntuMonoFontFamily

@Composable
fun AppDrawer(
    navigator: DestinationsNavigator,
    navController: NavController,
    closeDrawer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentDestination =
        navController.currentDestinationAsState().value ?: NavGraphs.root.startDestination

    val drawerDestination = listOf(
        AppDrawerDestination(HomeScreenDestination, Icons.Rounded.Home, "Home"),
        AppDrawerDestination(SettingsScreenDestination, Icons.Rounded.Settings, "Settings"),
        AppDrawerDestination(AdzanScreenDestination, Icons.Rounded.Timelapse, "Shalat Time"),
        AppDrawerDestination(QiblaScreenDestination, Icons.Rounded.Explore, "Qibla")
    )

    ModalDrawerSheet(
        modifier = modifier,
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        DrawerHeader(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        )
        drawerDestination.forEach { destination ->
            NavigationDrawerItem(
                label = { Text(text = destination.label) },
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.label
                    )
                },
                selected = currentDestination == destination.directions,
                onClick = {
                    navigator.navigate(destination.directions) {
                        launchSingleTop = true
                    }; closeDrawer()
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
    }
}

data class AppDrawerDestination(
    val directions: DirectionDestinationSpec,
    val icon: ImageVector,
    val label: String,
)

@Composable
fun DrawerHeader(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            painter = painterResource(id = R.drawable.icon_quran_compose),
            contentDescription = "Drawer Header Icon",
            modifier = Modifier.size(48.dp),
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "Quran",
            fontWeight = FontWeight.Bold,
            fontFamily = novaMonoFontFamily,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "By Syntxr",
            fontWeight = FontWeight.SemiBold,
            fontFamily = ubuntuMonoFontFamily,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
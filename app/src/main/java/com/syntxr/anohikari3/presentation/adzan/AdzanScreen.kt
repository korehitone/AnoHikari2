package com.syntxr.anohikari3.presentation.adzan

import android.Manifest
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.syntxr.anohikari3.R
import com.syntxr.anohikari3.presentation.adzan.component.AdzanCard
import com.syntxr.anohikari3.presentation.adzan.component.AdzanLocationCard
import com.syntxr.anohikari3.utils.AppGlobalState

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Destination<RootGraph>
@Composable
fun AdzanScreen(
    navigator: DestinationsNavigator,
    viewModel: AdzanViewModel = hiltViewModel(),
) {
    AppGlobalState.drawerGesture = false
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val locationPermission = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    )

    LaunchedEffect(key1 = true){
        if (locationPermission.allPermissionsGranted){
            viewModel.getLocation(context)
        }else{
            locationPermission.launchMultiplePermissionRequest()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text(
                    text = stringResource(id = R.string.txt_adzan_title),
                    fontWeight = FontWeight.Medium
                )
            }, navigationIcon = {
                IconButton(onClick = { navigator.navigateUp() }) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBackIosNew, contentDescription = "Back"
                    )
                }
            })
        }) { paddingValue ->

        Column(
            modifier = Modifier
                .padding(paddingValue)
                .fillMaxSize()
        ) {

            if (viewModel.locality.isNotEmpty() && viewModel.currentLocation.isNotEmpty()){
                AdzanLocationCard(locality = viewModel.locality, currentLocation = viewModel.currentLocation)
            }

            Spacer(modifier = Modifier.height(16.dp))


            viewModel.uiEvent.collectAsState().let { event ->
                when (val currentState = event.value) {
                    is AdzanUiEvent.ShowErrorMessage -> {
                        Toast.makeText(context, currentState.message, Toast.LENGTH_SHORT).show()
                        AdzanLocationCard(locality = stringResource(id = R.string.txt_unknown_location), currentLocation = stringResource(
                            id = R.string.txt_unknown_advance_location
                        ))

                        Spacer(modifier = Modifier.height(16.dp))

                        val shalatTime = state.adzans
//                        Log.d("FREEEBIRD", "AdzanScreen: $shalatTime")
                        if (shalatTime != null) {
                            Column(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                AdzanCard(
                                    shalatName = "Shubuh",
                                    shalatTime = shalatTime.fajr
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                AdzanCard(
                                    shalatName = "Dhuhur",
                                    shalatTime = shalatTime.dhuhr
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                AdzanCard(shalatName = "Ashar", shalatTime = shalatTime.asr)
                                Spacer(modifier = Modifier.height(8.dp))

                                AdzanCard(
                                    shalatName = "Maghrib",
                                    shalatTime = shalatTime.maghrib
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                AdzanCard(shalatName = "Isha", shalatTime = shalatTime.isha)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        } else {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = stringResource(id = R.string.txt_err_else),
                                    style = MaterialTheme.typography.titleMedium,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    AdzanUiEvent.Idle -> {
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Column(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.txt_msg_location_to_usr),
                                    style = MaterialTheme.typography.titleMedium,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                CircularProgressIndicator()
                            }
                        }
                    }

                    AdzanUiEvent.GetData -> {
                        when (isLoading) {
                            true -> {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                            false -> {
                                val shalatTime = state.adzans
                                if (shalatTime != null) {
                                    Column(
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        AdzanCard(
                                            shalatName = "Shubuh",
                                            shalatTime = shalatTime.fajr
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))

                                        AdzanCard(
                                            shalatName = "Dhuhur",
                                            shalatTime = shalatTime.dhuhr
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))

                                        AdzanCard(shalatName = "Ashar", shalatTime = shalatTime.asr)
                                        Spacer(modifier = Modifier.height(8.dp))

                                        AdzanCard(
                                            shalatName = "Maghrib",
                                            shalatTime = shalatTime.maghrib
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))

                                        AdzanCard(shalatName = "Isha", shalatTime = shalatTime.isha)
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                }
                                else {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = stringResource(id = R.string.txt_adzan_empty),
                                            style = MaterialTheme.typography.titleMedium,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }

                    }
                }
            }

        }
    }
}
package com.syntxr.anohikari3.presentation.adzan

import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syntxr.anohikari3.data.kotpref.UserPreferences
import com.syntxr.anohikari3.domain.model.Adzan
import com.syntxr.anohikari3.domain.usecase.AppUseCase
import com.syntxr.anohikari3.service.location.LocationClient
import com.syntxr.anohikari3.service.location.LocationClientTracker
import com.syntxr.anohikari3.utils.AppGlobalState
import com.syntxr.anohikari3.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AdzanViewModel @Inject constructor(
    appUseCase: AppUseCase,
    private val locationClient: LocationClient,
) : ViewModel() {

    private val adzanUseCase = appUseCase.adzanUseCase

    private val _state = MutableStateFlow(AdzanState())
    val state = _state.asStateFlow()

    private val _uiEvent = MutableStateFlow<AdzanUiEvent>(AdzanUiEvent.Idle)
    val uiEvent = _uiEvent.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private var _locality = mutableStateOf("")
    val locality = _locality.value

    private var _currentLocation = mutableStateOf("")
    val currentLocation = _currentLocation.value

    private var adzanJob: Job? = null

    private val errorLocation =
        if (AppGlobalState.currentLanguage == UserPreferences.Language.ID.tag)
            "Error While Getting Location"
        else
            "Error Ketika Mendapat Lokasi"

    private val errorGPS = if (AppGlobalState.currentLanguage == UserPreferences.Language.ID.tag)
        "Error No GPS"
    else
        "Error Tidak Ada GPS"

    private val errorPermission =
        if (AppGlobalState.currentLanguage == UserPreferences.Language.ID.tag)
            "Error Missing Permission"
        else
            "Error Tidak Ada Izin"


    fun getLocation(context: Context) {
        viewModelScope.launch {
            _uiEvent.emit(AdzanUiEvent.Idle)
            locationClient.requestLocationUpdate()
                .onEach { tracker ->
                    when (tracker) {
                        is LocationClientTracker.Error -> {
                            _uiEvent.emit(AdzanUiEvent.ShowErrorMessage(errorLocation))
                            onGetLocalCache()
                        }

                        is LocationClientTracker.MissingPermission -> {
                            _uiEvent.emit(AdzanUiEvent.ShowErrorMessage(errorPermission))
                            onGetLocalCache()
                        }

                        is LocationClientTracker.NoGps -> {
                            _uiEvent.emit(AdzanUiEvent.ShowErrorMessage(errorGPS))
                            onGetLocalCache()
                        }

                        is LocationClientTracker.Success -> {
                            val latitude = tracker.location?.latitude
                            val longitude = tracker.location?.longitude
                            if (latitude == null || longitude == null) {
                                _uiEvent.emit(AdzanUiEvent.ShowErrorMessage(errorLocation))
                                onGetLocalCache()
                                return@onEach
                            }
                            val address = Geocoder(
                                context,
                                Locale.getDefault()
                            ).getFromLocation(
                                latitude,
                                longitude,
                                1,
                            )
                            if (!address.isNullOrEmpty()) {
                                _locality.value = address.first().locality
                                _currentLocation.value =
                                    "${address.first().locality}, ${address.first().subLocality}, ${address.first().subAdminArea}"

                            }


                            _isLoading.emit(true)
                            onGetRemoteAdzan(latitude, longitude)
                            cancel()
                        }
                    }
                }.launchIn(this)
        }
    }

    private fun onGetRemoteAdzan(latitude: Double, longitude: Double) {
        adzanJob?.cancel()
        adzanJob = viewModelScope.launch {
            delay(500L)
            adzanUseCase.getAdzans(latitude, longitude)
                .onEach { resource ->
                    when (resource) {
                        is Resource.Error -> {
                            _isLoading.emit(false)
                            _state.value = _state.value.copy(adzans = resource.data)
//                            if (resource.data != null) {
//                                _uiEvent.emit(AdzanUiEvent.GetData)
//                            }
                            _uiEvent.emit(AdzanUiEvent.ShowErrorMessage(resource.message ?: ""))
                        }

                        is Resource.Success -> {
                            _isLoading.emit(false)
                            _state.value = _state.value.copy(adzans = resource.data)
                            _uiEvent.emit(AdzanUiEvent.GetData)
                        }
                    }
                }.launchIn(this)
        }
    }


    private fun onGetLocalCache() {
        adzanJob?.cancel()
        adzanJob = viewModelScope.launch {
            delay(500L)
            adzanUseCase.getCachedAdzan()
                .onEach { resource ->
                    when (resource) {
                        is Resource.Error -> {
                            _isLoading.emit(false)
                            _state.value = _state.value.copy(
                                adzans = resource.data,
                            )
//                            _uiEvent.emit(AdzanUiEvent.GetData)
                            _uiEvent.emit(AdzanUiEvent.ShowErrorMessage(resource.message ?: ""))
                        }

                        is Resource.Success -> {
                            _isLoading.emit(false)
                            _state.value = _state.value.copy(
                                adzans = resource.data,
                            )
                            _uiEvent.emit(AdzanUiEvent.GetData)
                        }

                    }
                }.launchIn(this)
        }
    }


}

//data class CurrentLocation(
//    val longitude: Double,
//    val latitude: Double,
//)

data class AdzanState(
    val adzans: Adzan? = null,
)

sealed class AdzanUiEvent {
    data object Idle : AdzanUiEvent()
    data object GetData : AdzanUiEvent()
    data class ShowErrorMessage(val message: String) : AdzanUiEvent()
}


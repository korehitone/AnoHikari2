package com.syntxr.anohikari3.presentation.read

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ramcosta.composedestinations.generated.navArgs
import com.syntxr.anohikari3.R
import com.syntxr.anohikari3.data.kotpref.UserPreferences
import com.syntxr.anohikari3.data.source.local.bookmark.entity.Bookmark
import com.syntxr.anohikari3.data.source.local.qoran.entity.Qoran
import com.syntxr.anohikari3.domain.usecase.AppUseCase
import com.syntxr.anohikari3.utils.AppGlobalActions
import com.syntxr.anohikari3.utils.AppGlobalState
import com.syntxr.anohikari3.utils.Converters
import com.syntxr.anohikari3.utils.intToUrlThreeDigits
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import snow.player.PlayMode
import snow.player.PlayerClient
import snow.player.audio.MusicItem
import snow.player.playlist.Playlist
import javax.inject.Inject

@HiltViewModel
class ReadViewModel @Inject constructor(
    useCase: AppUseCase,
    savedStateHandle: SavedStateHandle,
    private val playerClient: PlayerClient,
) : ViewModel() {

    private val qoranUseCase = useCase.qoranUseCase
    private val bookmarkUseCase = useCase.bookmarkUseCase

    private val soraNumber = savedStateHandle.navArgs<ReadScreenNavArgs>().soraNumber ?: 1
    private val jozzNumber = savedStateHandle.navArgs<ReadScreenNavArgs>().jozzNumber ?: 1
    val indexType = savedStateHandle.navArgs<ReadScreenNavArgs>().indexType
    val scrollPosition = savedStateHandle.navArgs<ReadScreenNavArgs>().scrollPosition ?: 0

    private val bookmarks = mutableListOf<Int>()
    private val checkBookmark = MutableLiveData<List<Int>>()

    private val _isAudioPlay = MutableStateFlow(playerClient.isPlaying)
    val isAudioPlay = _isAudioPlay.asStateFlow()

    private val _currentAyaPlayId = MutableStateFlow(0)
    val currentAyaPlayId = _currentAyaPlayId.asStateFlow()

    private val _currentAyaPlayName = MutableStateFlow("")
    val currentPlayAyaName = _currentAyaPlayName.asStateFlow()

    private val _uiEvent = MutableStateFlow<ReadUiEvent?>(null)
    val uiEvent = _uiEvent.asStateFlow()

    val playMode = mutableStateOf(PlayMode.SINGLE_ONCE)
    val playType = mutableStateOf(PlayType.NONE)

    private val _state = mutableStateOf(ReadAyaState())
    val state: State<ReadAyaState> = _state

    private val errorPlay = if (AppGlobalState.currentLanguage == UserPreferences.Language.ID.tag)
        "Turn on your network first!"
    else
        "Nyalakan internet anda terlebih dahulu!"

    private var getAyaJob: Job? = null

    init {
        getAyaQoran(indexType)
    }

    private fun getAyaQoran(indexType: Int) {
        getAyaJob?.cancel()
        when (indexType) {
            AYA_BY_SORA -> {
                getAyaJob = qoranUseCase.getSoraAya(soraNumber)
                    .onEach { ayas ->
                        val firstAya = ayas.firstOrNull()
                        _state.value = _state.value.copy(
                            ayas = ayas,
                            title = firstAya?.soraEn ?: ""
                        )
                    }.launchIn(viewModelScope)
            }

            AYA_BY_JOZZ -> {
                getAyaJob = qoranUseCase.getJozzAya(jozzNumber)
                    .onEach { ayas ->
                        val firstAya = ayas.firstOrNull()
                        _state.value = _state.value.copy(
                            ayas = ayas,
                            title = firstAya?.jozz?.let { "Juz $it" } ?: ""
                        )
                    }.launchIn(viewModelScope)
            }

            AYA_BY_JOZZ_SORA -> {
                getAyaJob = qoranUseCase.getSoraAya(soraNumber)
                    .onEach { ayas ->
                        val firstAya = ayas.firstOrNull()
                        _state.value = _state.value.copy(
                            ayas = ayas,
                            title = firstAya?.soraEn ?: ""
                        )
                    }.launchIn(viewModelScope)
            }
        }
    }

    fun onEvent(event: ReadEvent) {
        when (event) {
            is ReadEvent.CopyAyaContent -> {
                AppGlobalActions.copyAction(
                    event.context, event.ayaNo, event.soraEn, event.ayaText, event.translation
                )
                viewModelScope.launch { _uiEvent.emit(ReadUiEvent.AyaCopied(R.string.txt_aya_copied)) }
            }

            is ReadEvent.DeleteBookmark -> {
                viewModelScope.launch {
                    bookmarks.remove(event.bookmark.id)
                    bookmarkUseCase.deleteBookmark(event.bookmark)
                    _uiEvent.emit(ReadUiEvent.BookmarkDeleted(R.string.txt_bookmark_deleted))
                }
            }

            is ReadEvent.InsertBookmark -> {
                viewModelScope.launch {
                    event.bookmark.id?.let { bookmarks.add(it) }
                    bookmarkUseCase.insertBookmark(event.bookmark)
                    _uiEvent.emit(ReadUiEvent.BookmarkAdded(R.string.txt_bookmark_added))
                }
            }

            is ReadEvent.PlayAyaAudio -> {
                getNetwork(event.context).let {
                    when(it){
                        true -> {
                            playerClient.stop()
                            val musicItem = Converters.createMusicItem(
                                title = "${event.soraEn} - ${event.ayaNo}",
                                ayaNo = intToUrlThreeDigits(event.ayaNo),
                                soraNo = intToUrlThreeDigits(event.soraNo)
                            )
                            val playlist = Playlist.Builder().append(musicItem).build()
                            playerClient.connect {
                                playerClient.setPlaylist(playlist, true)
                                playerClient.playMode = PlayMode.SINGLE_ONCE
                                playMode.value = PlayMode.SINGLE_ONCE
                                _currentAyaPlayId.value = event.id
                                _currentAyaPlayName.value = "${event.soraEn} - ${event.ayaNo}"
                                playType.value = PlayType.SINGLE
                            }
                            if (playerClient.isError){
                                viewModelScope.launch { _uiEvent.emit(ReadUiEvent.PlayerError(playerClient.errorMessage)) }
                            } else {}
                        }
                        false -> {
                            viewModelScope.launch {
                                _uiEvent.emit(ReadUiEvent.PlayerError(
                                    errorPlay
                                ))
                            }
                        }
                    }
                }
            }

            is ReadEvent.ShareAyaContent -> {
                AppGlobalActions.shareAction(
                    event.context, event.ayaNo, event.soraEn, event.ayaText, event.translation
                )
                viewModelScope.launch { _uiEvent.emit(ReadUiEvent.AyaShared(R.string.txt_aya_shared)) }
            }

            is ReadEvent.PlayAllAudio -> {
                getNetwork(event.context).let {
                    when(it){
                        true -> {
                            playerClient.stop()
                            val musicItems = mutableListOf<MusicItem>()
                            event.ayas.forEach { qoran ->
                                val musicItem = Converters.createMusicItem(
                                    title = "${qoran.soraEn} - ${qoran.ayaNo}",
                                    ayaNo = intToUrlThreeDigits(qoran.ayaNo ?: return@forEach),
                                    soraNo = intToUrlThreeDigits(qoran.soraNo ?: return@forEach)
                                )
                                musicItems.add(musicItem)
                            }
                            val playlist = Playlist.Builder().appendAll(musicItems).build()
                            playerClient.connect {
                                playerClient.setPlaylist(playlist, true)
                                playerClient.playMode = PlayMode.PLAYLIST_LOOP
                                playMode.value = PlayMode.PLAYLIST_LOOP
                                playType.value = PlayType.ALL
                                playerClient.addOnPlayingMusicItemChangeListener { _, position, _ ->
                                    _currentAyaPlayId.value = event.ayas[position].id
                                    _currentAyaPlayName.value =
                                        "${event.ayas[position].soraEn} - ${event.ayas[position].ayaNo}"
                                }
                            }
                            if (playerClient.isError){
                                viewModelScope.launch { _uiEvent.emit(ReadUiEvent.PlayerError(playerClient.errorMessage)) }
                            } else {}
                        }
                        false -> {
                            viewModelScope.launch {
                                _uiEvent.emit(ReadUiEvent.PlayerError(
                                    errorPlay
                                ))
                            }
                        }
                    }
                }
            }
        }
    }

    fun onPlayEvent(event: PlayEvent) {
        when (event) {
            PlayEvent.PlayModeChange -> {
                when (playerClient.playMode) {
                    PlayMode.SINGLE_ONCE -> {
                        playerClient.playMode = PlayMode.PLAYLIST_LOOP
                        playMode.value = PlayMode.PLAYLIST_LOOP
                    }

                    PlayMode.PLAYLIST_LOOP -> {
                        playerClient.playMode = PlayMode.SINGLE_ONCE
                        playMode.value = PlayMode.SINGLE_ONCE
                    }

                    else -> {}
                }
            }

            PlayEvent.PlayPause -> {
                playerClient.playPause(); _isAudioPlay.value = !_isAudioPlay.value
            }

            PlayEvent.Next -> playerClient.skipToNext()
            PlayEvent.Previous -> playerClient.skipToPrevious()
            PlayEvent.Stop -> {
                playerClient.stop(); playType.value = PlayType.NONE; playerClient.shutdown()
            }
        }
    }


    fun findBookmark() {
        viewModelScope.launch {
            val datas = bookmarkUseCase.getBookmark(true).stateIn(viewModelScope).value
            datas.forEach {
                it.id?.let { it1 -> bookmarks.add(it1) }
            }
            checkBookmark.value = bookmarks
        }
    }

    fun isBookmark() = checkBookmark.value

    private fun getNetwork(context: Context) : Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                return true
            }
        }
        return false
    }

}

data class ReadAyaState(
    val ayas: List<Qoran>? = emptyList(),
    val title: String? = "",
)

sealed class ReadEvent {
    data class CopyAyaContent(
        val context: Context,
        val ayaNo: Int,
        val soraEn: String,
        val ayaText: String,
        val translation: String,
    ) : ReadEvent()

    data class ShareAyaContent(
        val context: Context,
        val ayaNo: Int,
        val soraEn: String,
        val ayaText: String,
        val translation: String,
    ) : ReadEvent()

    data class PlayAyaAudio(
        val id: Int,
        val soraEn: String,
        val ayaNo: Int,
        val soraNo: Int,
        val context: Context
    ) : ReadEvent()

    data class PlayAllAudio(
        val ayas: List<Qoran>,
        val context: Context
    ) : ReadEvent()

    data class InsertBookmark(val bookmark: Bookmark) : ReadEvent()
    data class DeleteBookmark(val bookmark: Bookmark) : ReadEvent()
}

sealed class ReadUiEvent {
    data class BookmarkAdded(val message: Int) : ReadUiEvent()
    data class BookmarkDeleted(val message: Int) : ReadUiEvent()
    data class AyaCopied(val message: Int) : ReadUiEvent()
    data class AyaShared(val message: Int) : ReadUiEvent()
    data class PlayerError(val message: String) : ReadUiEvent()
}

sealed class PlayEvent {
    data object PlayPause : PlayEvent()
    data object PlayModeChange : PlayEvent()
    data object Previous : PlayEvent()
    data object Next : PlayEvent()
    data object Stop : PlayEvent()

}

enum class PlayType {
    NONE,
    SINGLE,
    ALL
}
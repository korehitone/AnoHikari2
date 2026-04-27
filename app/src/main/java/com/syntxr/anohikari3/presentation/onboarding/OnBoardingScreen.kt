package com.syntxr.anohikari3.presentation.onboarding

import android.app.LocaleManager
import android.os.Build
import android.os.LocaleList
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.syntxr.anohikari3.R
import com.syntxr.anohikari3.data.kotpref.UserPreferences
import com.syntxr.anohikari3.data.kotpref.UserPreferences.currentLanguage
import com.syntxr.anohikari3.data.kotpref.UserPreferences.currentQori
import com.syntxr.anohikari3.service.player.MyPlayerService
import com.syntxr.anohikari3.utils.AppGlobalState
import com.syntxr.anohikari3.utils.Converters
import com.syntxr.anohikari3.utils.intToUrlThreeDigits
import kotlinx.coroutines.launch
import snow.player.PlayMode
import snow.player.PlayerClient
import snow.player.audio.MusicItem
import snow.player.playlist.Playlist

@OptIn(ExperimentalFoundationApi::class)
@Destination<RootGraph>
@Composable
fun OnBoardingScreen(
    navigator: DestinationsNavigator,
) {

    val pagerState = rememberPagerState(pageCount = { 5 })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .height(128.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.weight(1F),
                ) {
                    if (pagerState.currentPage > 0) {
                        TextButton(
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            }
                        ) {
                            Text(
                                text = stringResource(R.string.txt_previous_boarding),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier.weight(2F),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    (0..4).forEach { i ->
                        Icon(
                            imageVector = Icons.Default.Circle,
                            contentDescription = "Step $i",
                            tint = if (pagerState.currentPage == i) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                    }
                }
                TextButton(
                    modifier = Modifier.weight(1F),
                    onClick = {
                        coroutineScope.launch {
                            if (pagerState.currentPage < 4) {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                return@launch
                            }
                            if (pagerState.currentPage == 4) {
                                navigator.navigateUp()
                                UserPreferences.isOnBoarding = false
                                AppGlobalState.isOnBoarding = false
                            }
                        }
                    }
                ) {
                    Text(
                        text = if (pagerState.currentPage < 4) stringResource(R.string.txt_next_boarding) else stringResource(
                            R.string.txt_finished_boarding,
                        ),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    ) { innerPading ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPading)
        ) {
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = false,
                modifier = Modifier.fillMaxSize()
            ) {
                when (it) {
                    0 -> OnWelcome()
                    1 -> OnLanguage()
                    2 -> OnQari()
                    3 -> OnTheme()
                    4 -> OnFinish()
                }
            }
        }
    }
}

@Composable
fun OnWelcome(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.icon_quran_compose),
            contentDescription = "Qoran App Logo",
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(id = R.string.txt_welcome_boarding),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = stringResource(R.string.txt_next_start_boarding),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun OnLanguage(
    modifier: Modifier = Modifier,
) {
    var isDialogShow by remember {
        mutableStateOf(false)
    }

    var selectedLanguage by remember {
        mutableStateOf(currentLanguage)
    }

    val context = LocalContext.current

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Box {
            TextButton(
                onClick = {
                    isDialogShow = true
                }
            ) {
                Icon(imageVector = Icons.Default.Language, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = selectedLanguage.language
                )
                Spacer(modifier = Modifier.width(24.dp))
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select Language"
                )
            }

            DropdownMenu(
                expanded = isDialogShow,
                onDismissRequest = { isDialogShow = false },
                offset = DpOffset(32.dp, 0.dp)
            ) {
                DropdownMenuItem(
                    text = { Text(text = "Indonesia") },
                    onClick = {
                        isDialogShow = false
                        currentLanguage = UserPreferences.Language.ID
                        selectedLanguage = UserPreferences.Language.ID
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            context.getSystemService(LocaleManager::class.java).applicationLocales =
                                LocaleList.forLanguageTags(selectedLanguage.tag)

                        } else {
                            AppCompatDelegate.setApplicationLocales(
                                LocaleListCompat.forLanguageTags(selectedLanguage.tag)
                            )
                        }
                    }
                )
                DropdownMenuItem(
                    text = { Text(text = "English") },
                    onClick = {
                        isDialogShow = false
                        currentLanguage = UserPreferences.Language.EN
                        selectedLanguage = UserPreferences.Language.EN
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            context.getSystemService(LocaleManager::class.java).applicationLocales =
                                LocaleList.forLanguageTags(selectedLanguage.tag)

                        } else {
                            AppCompatDelegate.setApplicationLocales(
                                LocaleListCompat.forLanguageTags(selectedLanguage.tag)
                            )
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.txt_language),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = stringResource(R.string.txt_language_boarding),
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
fun OnQari(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val playerClient = PlayerClient.newInstance(context, MyPlayerService::class.java)

    var isDialogShow by remember {
        mutableStateOf(false)
    }

    var selectedQari by remember {
        mutableStateOf(currentQori)
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Button(
            onClick = {
                playerClient.stop()
                val musicItems = mutableListOf<MusicItem>()
                val musicItem = Converters.createMusicItem(
                    title = "Audio Test",
                    ayaNo = intToUrlThreeDigits(1),
                    soraNo = intToUrlThreeDigits(1)
                )
                musicItems.add(musicItem)
                val playlist = Playlist.Builder().appendAll(musicItems).build()
                playerClient.connect {
                    playerClient.setPlaylist(playlist, true)
                    playerClient.playMode = PlayMode.SINGLE_ONCE
                }
                if (playerClient.isError) {
                    Toast.makeText(context, playerClient.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        ) {
            Text(text = if (AppGlobalState.currentLanguage == UserPreferences.Language.ID.tag) "Coba Audio" else "Try Audio")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box {
            TextButton(
                onClick = {
                    isDialogShow = true
                }
            ) {
                Icon(imageVector = Icons.Default.Language, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = selectedQari.qoriName
                )
                Spacer(modifier = Modifier.width(24.dp))
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select Qari"
                )
            }

            DropdownMenu(
                expanded = isDialogShow,
                onDismissRequest = { isDialogShow = false },
                offset = DpOffset(32.dp, 0.dp)
            ) {
                DropdownMenuItem(
                    text = { Text(text = UserPreferences.Qori.IBRAHIM_AKHDAR.qoriName) },
                    onClick = {
                        isDialogShow = false
                        currentQori = UserPreferences.Qori.IBRAHIM_AKHDAR
                        selectedQari = UserPreferences.Qori.IBRAHIM_AKHDAR
                    }
                )
                DropdownMenuItem(
                    text = { Text(text = UserPreferences.Qori.HUDHAIFY.qoriName) },
                    onClick = {
                        isDialogShow = false
                        currentQori = UserPreferences.Qori.HUDHAIFY
                        selectedQari = UserPreferences.Qori.HUDHAIFY
                    }
                )
                DropdownMenuItem(
                    text = { Text(text = UserPreferences.Qori.HANI_RIFAI.qoriName) },
                    onClick = {
                        isDialogShow = false
                        currentQori = UserPreferences.Qori.HANI_RIFAI
                        selectedQari = UserPreferences.Qori.HANI_RIFAI
                    }
                )
                DropdownMenuItem(
                    text = { Text(text = UserPreferences.Qori.ALAFASY.qoriName) },
                    onClick = {
                        isDialogShow = false
                        currentQori = UserPreferences.Qori.ALAFASY
                        selectedQari = UserPreferences.Qori.ALAFASY
                    }
                )
                DropdownMenuItem(
                    text = { Text(text = UserPreferences.Qori.ABD_SUDAIS.qoriName) },
                    onClick = {
                        isDialogShow = false
                        currentQori = UserPreferences.Qori.ABD_SUDAIS
                        selectedQari = UserPreferences.Qori.ABD_SUDAIS
                    }
                )
            }
        }



        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.txt_qari),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = stringResource(R.string.txt_qori_boarding),
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
fun OnTheme(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = if (AppGlobalState.isDarkTheme) Icons.Rounded.DarkMode else Icons.Rounded.LightMode, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked = AppGlobalState.isDarkTheme,
                onCheckedChange = {
                    AppGlobalState.isDarkTheme = it
                    UserPreferences.isDarkTheme = it
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = if (AppGlobalState.isDarkTheme) R.string.txt_dark_theme else R.string.txt_light_theme),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = stringResource(R.string.txt_theme_boarding),
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
fun OnFinish(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.txt_finish_boarding),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = stringResource(R.string.txt_continue_boarding),
            style = MaterialTheme.typography.titleLarge
        )
    }
}
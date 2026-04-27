package com.syntxr.anohikari3.presentation.settings

import android.app.LocaleManager
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.ColorLens
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.syntxr.anohikari3.R
import com.syntxr.anohikari3.data.kotpref.UserPreferences
import com.syntxr.anohikari3.data.kotpref.UserPreferences.Language
import com.syntxr.anohikari3.data.kotpref.UserPreferences.Qori
import com.syntxr.anohikari3.data.kotpref.UserPreferences.currentLanguage
import com.syntxr.anohikari3.data.kotpref.UserPreferences.currentQori
import com.syntxr.anohikari3.presentation.settings.component.ActionItem
import com.syntxr.anohikari3.presentation.settings.component.ClickableCardSettings
import com.syntxr.anohikari3.presentation.settings.component.SettingsAlertDialog
import com.syntxr.anohikari3.presentation.settings.component.SwitchableCardSettings
import com.syntxr.anohikari3.utils.AppGlobalState

@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>
@Composable
fun SettingsScreen(
    navigator: DestinationsNavigator,
) {
    AppGlobalState.drawerGesture = false
    val context = LocalContext.current
    var isLanguageDialogShow by remember {
        mutableStateOf(false)
    }

    var isQariDialogShow by remember {
        mutableStateOf(false)
    }

    var selectedLanguage by remember {
        mutableStateOf(currentLanguage)
    }

    var selectedQari by remember {
        mutableStateOf(currentQori)
    }

    val scrollState = rememberScrollState()

    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = {
            Text(
                text = stringResource(id = R.string.txt_settings_title), fontWeight = FontWeight.Medium
            )
        }, navigationIcon = {
            IconButton(
                onClick = { navigator.navigateUp() }
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBackIosNew, contentDescription = "Back"
                )
            }
        })
    }) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {

            SwitchableCardSettings(label = if (AppGlobalState.isDarkTheme) stringResource(id = R.string.txt_dark_theme) else stringResource(
                id = R.string.txt_light_theme
            ),
                description = stringResource(id = R.string.txt_change_theme),
                icon = if (AppGlobalState.isDarkTheme) Icons.Rounded.DarkMode else Icons.Rounded.LightMode,
                isSwitched = AppGlobalState.isDarkTheme,
                onSwitch = { isChecked ->
                    AppGlobalState.isDarkTheme = isChecked
                    UserPreferences.isDarkTheme = isChecked
                })

            SwitchableCardSettings(
                label = stringResource(id = R.string.txt_tajweed),
                description = stringResource(id = R.string.txt_change_tajweed),
                icon = Icons.Rounded.ColorLens,
                isSwitched = AppGlobalState.isTajweed,
                onSwitch = { isChecked ->
                    AppGlobalState.isTajweed = isChecked
                    UserPreferences.isTajweed = isChecked
                }
            )

            ClickableCardSettings(label = stringResource(id = R.string.txt_language),
                description = stringResource(id = R.string.txt_change_language),
                icon = Icons.Rounded.Translate,
                currentValue = selectedLanguage.language,
                onClick = { isLanguageDialogShow = true })

            ClickableCardSettings(label = stringResource(id = R.string.txt_qari),
                description = stringResource(id = R.string.txt_change_qari),
                icon = Icons.Rounded.Person,
                currentValue = selectedQari.qoriName,
                onClick = { isQariDialogShow = true })

            if (isLanguageDialogShow) {
                val options = listOf(
                    OptionLanguage(
                        text = Language.ID.language, language = Language.ID
                    ),
                    OptionLanguage(
                        text = Language.EN.language, language = Language.EN
                    ),
                )

                SettingsAlertDialog(
                    icon = Icons.Rounded.Translate,
                    title = stringResource(id = R.string.txt_language),
                    currentSelected = currentLanguage.language,
                    content = {
                        LazyColumn(modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(), content = {
                            items(options) { option ->
                                ActionItem(
                                    text = option.text,
                                    onClick = {
                                        isLanguageDialogShow = false
                                        currentLanguage = option.language
                                        selectedLanguage = option.language
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                            context.getSystemService(LocaleManager::class.java).applicationLocales =
                                                LocaleList.forLanguageTags(option.language.tag)

                                        } else {
                                            AppCompatDelegate.setApplicationLocales(
                                                LocaleListCompat.forLanguageTags(option.language.tag)
                                            )
                                        }
                                    },
                                    buttonColors = if (currentLanguage.language == option.text) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                )
                            }
                        })
                    },
                    onDismissClick = { isLanguageDialogShow = false },
                    onConfirmClick = { isLanguageDialogShow = false },
                    dismissButtonText = "Cancel"
                )
            }

            if (isQariDialogShow) {
                val options = listOf(
                    OptionQori(
                        text = Qori.ABD_SUDAIS.qoriName, qori = Qori.ABD_SUDAIS
                    ),
                    OptionQori(
                        text = Qori.ALAFASY.qoriName, qori = Qori.ALAFASY
                    ),
                    OptionQori(
                        text = Qori.HANI_RIFAI.qoriName, qori = Qori.HANI_RIFAI
                    ),
                    OptionQori(
                        text = Qori.HUDHAIFY.qoriName, qori = Qori.HUDHAIFY
                    ),
                    OptionQori(
                        text = Qori.IBRAHIM_AKHDAR.qoriName, qori = Qori.IBRAHIM_AKHDAR
                    ),
                )

                SettingsAlertDialog(
                    icon = Icons.Rounded.Person,
                    title = stringResource(id = R.string.txt_qari),
                    currentSelected = selectedQari.qoriName,
                    content = {
                        LazyColumn(modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(), content = {
                            items(options) { option ->
                                ActionItem(
                                    text = option.text,
                                    onClick = {
                                        isQariDialogShow = false
                                        currentQori = option.qori
                                        selectedQari = option.qori
                                    },
                                    buttonColors = if (currentLanguage.language == option.text) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                )
                            }
                        })
                    },
                    onDismissClick = { isQariDialogShow = false },
                    onConfirmClick = { isQariDialogShow = false },
                    dismissButtonText = "Cancel"
                )
            }
        }
    }
}

data class OptionLanguage(
    val text: String,
    val language: Language,
)

data class OptionQori(
    val text: String,
    val qori: Qori,
)
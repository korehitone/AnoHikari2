package com.syntxr.anohikari3.presentation.search

import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.ReadScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.syntxr.anohikari3.R
import com.syntxr.anohikari3.data.kotpref.UserPreferences
import com.syntxr.anohikari3.presentation.read.AYA_BY_SORA
import com.syntxr.anohikari3.presentation.read.ReadScreenNavArgs
import com.syntxr.anohikari3.presentation.search.component.SearchItem
import com.syntxr.anohikari3.presentation.search.component.SearchSoraItem
import com.syntxr.anohikari3.utils.AppGlobalState
import com.syntxr.anohikari3.utils.reverseAyatNumber

@Destination<RootGraph>
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navigator: DestinationsNavigator,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    AppGlobalState.drawerGesture = false
    val uiState by viewModel.uiState.collectAsState()
    val ayaMatched by viewModel.ayaMatched.collectAsState()
    val soraMatched by viewModel.soraMatched.collectAsState()

    var queryState by remember {
        mutableStateOf("")
    }

    var activeState by remember {
        mutableStateOf(false)
    }

    var isSurahShow by remember {
        mutableStateOf(false)
    }

    val soraItems =
        if (isSurahShow) soraMatched.distinctBy { it.soraEn } else soraMatched.distinctBy { it.soraEn }
            .take(5)
    val snackBarHostState = SnackbarHostState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text(
                    text = stringResource(id = R.string.txt_search_title),
                    fontWeight = FontWeight.Medium
                )
            }, navigationIcon = {
                IconButton(onClick = { navigator.navigateUp() }) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBackIosNew, contentDescription = "Back"
                    )
                }
            })
        },
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            SearchBar(
                query = queryState,
                onQueryChange = { query ->
                    queryState = query
                },
                onSearch = { query ->
                    activeState = true
                    viewModel.onEvent(SearchEvent.OnSearch(query))
                },
                active = activeState,
                onActiveChange = { active ->
                    activeState = active
                },
                placeholder = {
                    Text(text = stringResource(id = R.string.txt_search_aya_idle))
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = "search lead icon"
                    )
                },
                trailingIcon = {
                    if (activeState) {
                        IconButton(onClick = {
                            if (queryState.isNotEmpty()) {
                                queryState = ""
                            } else {
                                activeState = false
                                viewModel.onEvent(SearchEvent.Clear)
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "btn_close"
                            )
                        }
                    }
                },
            ) {
                when (val state = uiState) {
                    is SearchUiState.Error -> {
                        LaunchedEffect(key1 = true) {
                            snackBarHostState.showSnackbar(state.message)
                        }
                    }

                    SearchUiState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    SearchUiState.Success -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {

                            items(soraItems) { qoran ->
                                SearchSoraItem(
                                    soraEn =
                                    AnnotatedString.Builder().run {
                                        val sorasEn = qoran.soraEn ?: ""

                                        val start = sorasEn.indexOf(queryState, ignoreCase = true)
                                        val end = start + queryState.length

                                        if (sorasEn.contains(queryState, true)){
                                            addStyle(SpanStyle(background = MaterialTheme.colorScheme.primaryContainer), start, end)
                                        }
                                        append(sorasEn)

                                        toAnnotatedString()
                                    },
                                    soraAr = AnnotatedString.Builder().run {
                                        val sorasAr = qoran.soraAr ?: ""

                                        val start = sorasAr.indexOf(queryState, ignoreCase = true)
                                        val end = start + queryState.length

                                        if (sorasAr.contains(queryState, true)){
                                            addStyle(SpanStyle(background = MaterialTheme.colorScheme.primaryContainer), start, end)
                                        }
                                        append(sorasAr)


                                        toAnnotatedString()
                                    },
                                    modifier = Modifier.clickable {
                                        navigator.navigate(
                                            ReadScreenDestination(
                                                ReadScreenNavArgs(
                                                    soraNumber = qoran.soraNo,
                                                    jozzNumber = qoran.jozz,
                                                    indexType = AYA_BY_SORA,
                                                    scrollPosition = null
                                                )
                                            )
                                        )
                                    }
                                )
                            }

                            item {
                                TextButton(onClick = {
                                    isSurahShow = !isSurahShow
                                }) {
                                    Text(
                                        text = stringResource(id = if (isSurahShow) R.string.txt_surah_less_search else R.string.txt_surah_more_search )
                                    )
                                }
                            }

                            items(ayaMatched) { qoran ->
                                SearchItem(
                                    ayaText = AnnotatedString.Builder().run {
                                        val ayas = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) qoran.ayaText ?: "" else qoran.ayaText?.reverseAyatNumber() ?: ""

                                        if (queryState.length == 1){
                                            for (word in ayas.split("")){
                                                val trim = word.trim(' ')
                                                val matched = trim.contains(queryState, false)
                                                if (matched){
                                                    pushStyle(SpanStyle(background = MaterialTheme.colorScheme.primaryContainer))
                                                }
                                                append(word)
                                                if (matched){
                                                    pop()
                                                }
                                            }
                                        }else{
                                            for (word in ayas.split(" ")){
                                                if (word.contains(queryState, true)){
                                                    pushStyle(SpanStyle(background = MaterialTheme.colorScheme.primaryContainer))
                                                }
                                                append("$word ")
                                                if (word.contains(queryState, true)){
                                                    pop()
                                                }
                                            }
                                        }


                                        toAnnotatedString()
                                    },
                                    translation =
                                    AnnotatedString.Builder().run {
                                        val translations =
                                            if (AppGlobalState.currentLanguage == UserPreferences.Language.ID.tag)
                                                qoran.translationId ?: ""
                                            else
                                                    qoran.translationEn ?: ""

                                        if (queryState.length == 1){
                                            for (word in translations.split("")){
                                                val trim = word.trim(' ')
                                                val matched = trim.contains(queryState, false)
                                                if (matched){
                                                    pushStyle(SpanStyle(background = MaterialTheme.colorScheme.primaryContainer))
                                                }
                                                append(word)
                                                if (matched){
                                                    pop()
                                                }
                                            }
                                        }else{
                                            for (word in translations.split(" ")){
                                                if (word.contains(queryState, true)){
                                                    pushStyle(SpanStyle(background = MaterialTheme.colorScheme.primaryContainer))
                                                }
                                                append("$word ")
                                                if (word.contains(queryState, true)){
                                                    pop()
                                                }
                                            }
                                        }


                                        toAnnotatedString()
                                    },
                                    modifier = Modifier.clickable {
                                        navigator.navigate(
                                            ReadScreenDestination(
                                                ReadScreenNavArgs(
                                                    soraNumber = qoran.soraNo,
                                                    jozzNumber = qoran.jozz,
                                                    indexType = AYA_BY_SORA,
                                                    scrollPosition = qoran.ayaNo?.minus(1)
                                                )
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }

                    null -> {}
                }

            }
        }
    }
}
package com.syntxr.anohikari3.presentation.qibla

import android.app.Activity
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.viewinterop.AndroidView
import com.just.agentweb.AgentWeb
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.syntxr.anohikari3.BuildConfig
import com.syntxr.anohikari3.R
import com.syntxr.anohikari3.data.kotpref.UserPreferences
import com.syntxr.anohikari3.utils.AppGlobalState

@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>
@Composable
fun QiblaScreen(
    navigator: DestinationsNavigator,
) {
    AppGlobalState.drawerGesture = false
    val activity = LocalActivity.current as Activity
    val url = if (AppGlobalState.currentLanguage == UserPreferences.Language.ID.tag) BuildConfig.QIBLA_URL_ID else BuildConfig.QIBLA_URL_EN
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text(
                    text = stringResource(id = R.string.txt_qibla_title),
                    fontWeight = FontWeight.Medium
                )
            }, navigationIcon = {
                IconButton(onClick = { navigator.navigateUp() }) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBackIosNew, contentDescription = "Back"
                    )
                }
            })
        }) { paddingValues ->
        Column(
            Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            AndroidView(
                factory = { context ->
                    LinearLayout(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                        )
                        AgentWeb.with(activity)
                            .setAgentWebParent(this, this.layoutParams)
                            .useDefaultIndicator()
                            .createAgentWeb()
                            .ready()
                            .go(url)
                    }
                }
            )
        }
    }
}
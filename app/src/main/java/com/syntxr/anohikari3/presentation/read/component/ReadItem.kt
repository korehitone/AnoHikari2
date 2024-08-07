package com.syntxr.anohikari3.presentation.read.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.CopyAll
import androidx.compose.material.icons.rounded.IosShare
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.syntxr.anohikari3.R
import com.syntxr.anohikari3.ui.theme.uthmanHafsFontFamily
import com.syntxr.anohikari3.utils.AppGlobalState
import com.syntxr.anohikari3.utils.Converters
import com.syntxr.anohikari3.utils.toAnnotatedString

@Composable
fun AyaSoraCard(
    soraArName: String,
    soraEnName: String,
    soraIdName: String,
    soraDescend: String,
    ayas: Int,
    modifier: Modifier = Modifier,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Column(
            modifier = modifier.fillMaxWidth()
        ) {

            Spacer(modifier = Modifier.heightIn(24.dp))

            Text(
                text = soraArName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Spacer(modifier = Modifier.heightIn(24.dp))

            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = soraEnName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = soraIdName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = modifier.align(Alignment.Start)
                    )
                }

                Column {
                    Text(
                        text = soraDescend,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = modifier.align(Alignment.End)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(id = R.string.txt_total_aya, ayas),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = modifier.align(Alignment.End)
                    )
                }
            }

        }
    }
}

//


@Composable
fun AyaReadItem(
    id: Int,
    ayaText: String,
    translation: String,
    soraNo: Int,
    ayaNo: Int,
    bookmarks: List<Int>?,
    currentPlayAya: Int,
    onInsertBookmarkClick: () -> Unit,
    onDeleteBookmarkClick: () -> Unit,
    onShareClick: () -> Unit,
    onCopyClick: () -> Unit,
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier,
    footnotes: String,
    onTranslateClick : (String) -> Unit = {}
) {

    var bookmarkState by remember {
        mutableStateOf(false)
    }
    bookmarkState = when (bookmarks?.contains(id)) {
        true -> true
        false -> false
        null -> false
    }


    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Column(
            modifier = modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            if (AppGlobalState.isTajweed){
                val annotatedAya = Converters.applyTajweed(LocalContext.current, ayaText).toAnnotatedString(MaterialTheme.colorScheme.onSurface)
                Text(
                    text = annotatedAya,
                    modifier = modifier.align(Alignment.End),
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = uthmanHafsFontFamily,
                )
            }else {
                Text(
                    text = ayaText,
                    modifier = modifier.align(Alignment.End),
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Medium,
                    fontFamily = uthmanHafsFontFamily,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            SpannableText(
                text = translation,
                onClick = {
                    onTranslateClick(footnotes)
                },
                modifier = modifier.align(Alignment.Start),
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = modifier
                    .padding(8.dp)
                    .align(Alignment.End),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {

                IconToggleButton(
                    checked = bookmarkState,
                    onCheckedChange = {
                        bookmarkState = if (bookmarkState) {
                            onDeleteBookmarkClick()
                            false
                        } else {
                            onInsertBookmarkClick()
                            true
                        }
                    },
                ) {
                    Icon(
                        imageVector = when (bookmarkState) {
                            true -> {
                                Icons.Rounded.Bookmark
                            }

                            false -> {
                                Icons.Rounded.BookmarkBorder
                            }
                        },
                        contentDescription = "btn_bookmark"
                    )
                }

                IconButton(onClick = onCopyClick) {
                    Icon(
                        imageVector = Icons.Rounded.CopyAll,
                        contentDescription = "btn_copy"
                    )
                }

                IconButton(onClick = onShareClick) {
                    Icon(
                        imageVector = Icons.Rounded.IosShare,
                        contentDescription = "btn_share"
                    )
                }

                IconButton(
                    onClick = onPlayClick
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = "Play Btn"
                    )
                }

                Text(
                    text = "$ayaNo-$soraNo", fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider(
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
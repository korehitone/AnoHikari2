package com.syntxr.anohikari3.presentation.home.bookmark.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.syntxr.anohikari3.R
import com.syntxr.anohikari3.ui.theme.montserratFontFamily
import com.syntxr.anohikari3.ui.theme.novaMonoFontFamily
import com.syntxr.anohikari3.ui.theme.ubuntuMonoFontFamily
import com.syntxr.anohikari3.ui.theme.uthmanHafsFontFamily
import com.syntxr.anohikari3.utils.Converters

@Composable
fun BookmarkItem(
    no: Int,
    soraEn: String,
    ayaNo: Int,
    ayaText: String,
    date: Long,
    modifier: Modifier = Modifier,
) {

    OutlinedCard(
        modifier = modifier,
        colors = CardDefaults
            .cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
        border = BorderStroke(0.4.dp, MaterialTheme.colorScheme.onSurface),
    ) {
        Row(
            modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(56.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$no",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontFamily = novaMonoFontFamily,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "$soraEn - $ayaNo",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        fontFamily = novaMonoFontFamily,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    
                    Text(
                        text = Converters.convertMillisToActualDate(date),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        fontFamily = ubuntuMonoFontFamily,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = ayaText,
                        modifier =Modifier.align(Alignment.End),
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        fontFamily = uthmanHafsFontFamily,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.secondary,
                    )

            }

        }
    }
}


@Composable
fun DeleteItemAction(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
        content = {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(id = R.string.txt_delete),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Normal,
                    fontFamily = novaMonoFontFamily
                )
            }
        }
    )
}
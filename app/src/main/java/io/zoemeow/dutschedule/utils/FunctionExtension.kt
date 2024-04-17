package io.zoemeow.dutschedule.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.distinctUntilChanged
import java.math.BigInteger
import java.security.MessageDigest
import java.text.Normalizer

fun Context.openLink(
    url: String,
    customTab: Boolean = true
) {
    when (customTab) {
        false -> {
            this.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }

        true -> {
            val builder = CustomTabsIntent.Builder()
            val defaultColors = CustomTabColorSchemeParams.Builder().build()
            builder.setDefaultColorSchemeParams(defaultColors)

            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(this, Uri.parse(url))
        }
    }
}

@Composable
fun Modifier.endOfListReached(
    lazyListState: LazyListState,
    buffer: Int = 1,
    onReached: () -> Unit
): Modifier {
    val shouldLoadMore = remember {
        derivedStateOf {
            try {
                val layoutInfo = lazyListState.layoutInfo
                val totalItemsNumber = layoutInfo.totalItemsCount
                val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.last().index + 1

                lastVisibleItemIndex > (totalItemsNumber - buffer)
            } catch (ex: Exception) {
                false
            }
        }
    }
    LaunchedEffect(shouldLoadMore) {
        snapshotFlow { shouldLoadMore.value }
            .distinctUntilChanged()
            .collect {
                if (shouldLoadMore.value)
                    onReached()
            }
    }
    return this
}

@Composable
fun RowScope.TableCell(
    modifier: Modifier = Modifier,
    text: String,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentAlign: Alignment = Alignment.Center,
    textAlign: TextAlign = TextAlign.Start,
    weight: Float
) {
    Surface(
        modifier = modifier.weight(weight),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.inverseSurface),
        color = backgroundColor,
        content = {
            Box(
                modifier = Modifier.padding(1.dp),
                contentAlignment = contentAlign,
                content = {
                    Text(
                        text = text,
                        textAlign = textAlign,
                        modifier = Modifier.padding(8.dp),
                    )
                }
            )
        }
    )
}

fun String.toNonAccent(): String {
    val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
    return "\\p{InCombiningDiacriticalMarks}+".toRegex().replace(temp, "")
}

// https://stackoverflow.com/a/64171625
fun String.calcMD5(): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(this.toByteArray())).toString(16).padStart(32, '0')
}

fun String.calcToSumByCharArray(): Int {
    var result = 0

    this.toByteArray().forEach {
        result += (it * 5)
    }

    return result
}

fun getRandomString(length: Int): String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}
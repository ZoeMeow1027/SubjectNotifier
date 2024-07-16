package io.zoemeow.dutschedule.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import io.zoemeow.dutschedule.activity.BrowserActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
            val intent = Intent(this, BrowserActivity::class.java)
            intent.putExtra("url", url)
            this.startActivity(intent)
//            val builder = CustomTabsIntent.Builder()
//            val defaultColors = CustomTabColorSchemeParams.Builder().build()
//            builder.setDefaultColorSchemeParams(defaultColors)
//
//            val customTabsIntent = builder.build()
//            customTabsIntent.launchUrl(this, Uri.parse(url))
        }
    }
}

fun String.capitalized(): String {
    return this.split(" ")
        .joinToString(separator = " ") { it.lowercase().replaceFirstChar(Char::uppercase) }
}

fun MutableState<String>.clear() {
    this.value = ""
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

fun launchOnScope(
    script: () -> Unit,
    invokeOnCompleted: ((Throwable?) -> Unit)? = null
) {
    var exRoot: Throwable? = null
    CoroutineScope(Dispatchers.Main).launch {
        withContext(Dispatchers.IO) {
            try {
                script()
            } catch (ex: Exception) {
                exRoot = ex
            }
        }
    }.invokeOnCompletion {
        invokeOnCompleted?.let { it(exRoot) }
    }
}
package io.zoemeow.dutschedule.activity

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import dagger.hilt.android.AndroidEntryPoint
import io.zoemeow.dutschedule.model.AppearanceState
import io.zoemeow.dutschedule.ui.view.miscellaneous.Activity_Miscellaneous_ExternalLinks
import io.zoemeow.dutschedule.utils.openLink

@AndroidEntryPoint
class MiscellaneousActivity : BaseActivity() {
    companion object {
        const val INTENT_EXTERNALLINKS = "view_externallink"
    }

    @Composable
    override fun OnPreloadOnce() {

    }

    @Composable
    override fun OnMainView(
        context: Context,
        snackBarHostState: SnackbarHostState,
        appearanceState: AppearanceState
    ) {
        when (intent.action) {
            INTENT_EXTERNALLINKS -> {
                Activity_Miscellaneous_ExternalLinks(
                    context = context,
                    snackBarHostState = snackBarHostState,
                    appearanceState = appearanceState,
                    onLinkClicked = { link ->
                        context.openLink(
                            url = link,
                            customTab = getMainViewModel().appSettings.value.openLinkInsideApp
                        )
                    },
                    onBack = {
                        setResult(RESULT_CANCELED)
                        finish()
                    }
                )
            }

            else -> {
                setResult(RESULT_CANCELED)
                finish()
            }
        }
    }
}
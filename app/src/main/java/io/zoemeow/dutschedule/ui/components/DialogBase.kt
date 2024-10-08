package io.zoemeow.dutschedule.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.zoemeow.dutschedule.GlobalVariables

@Composable
fun DialogBase(
    modifier: Modifier = Modifier.fillMaxWidth().padding(25.dp),
    content: @Composable () -> Unit,
    actionButtons: @Composable (RowScope.() -> Unit)? = null,
    title: String,
    isVisible: Boolean = false,
    canDismiss: Boolean = true,
    dismissClicked: (() -> Unit)? = null,
    isTitleCentered: Boolean = false,
) {
    val interactionSource = remember { MutableInteractionSource() }

    if (isVisible) {
        Dialog(
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = canDismiss,
                usePlatformDefaultWidth = false
            ),
            onDismissRequest = {
                if (canDismiss) dismissClicked?.let { it() }
            },
            content = {
                Surface(
                    modifier = modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                    ) { },
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(GlobalVariables.ROUNDED_CORNER_SHAPE_SIZE),
                    content = {
                        Column(
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.Top,
                            modifier = Modifier.padding(20.dp),
                        ) {
                            Column(
                                horizontalAlignment = if (isTitleCentered) Alignment.CenterHorizontally else Alignment.Start,
                                verticalArrangement = Arrangement.Top,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text(
                                    title,
                                    style = TextStyle(fontSize = 22.sp),
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 15.dp),
                                    textAlign = if (isTitleCentered) TextAlign.Center else TextAlign.Start
                                )
                            }
                            content()
                            actionButtons?.let {
                                Row(
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                        .padding(top = 10.dp),
                                    content = it,
                                )
                            }
                        }
                    },
                )
            }
        )
    }
}

package io.zoemeow.dutschedule.ui.view.settings.controls

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.ui.component.SimpleCardItem
import java.text.DecimalFormat

@Composable
fun SliderWithValue(
    modifier: Modifier = Modifier,
    title: String,
    description: String? = null,
    defaultValue: Float = 50f,
    onValueChanged: (Float) -> Unit,
    opacity: Float = 1f
) {
    val sliderPosition = remember { mutableFloatStateOf(defaultValue) }
    val context = LocalContext.current

    Box(modifier = modifier) {
        SimpleCardItem(
            title = title,
            opacity = opacity,
            content = {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 15.dp)
                        .padding(bottom = 10.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    description?.let {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                        ) {
                            Text(
                                it,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    Text(
                        text = "${DecimalFormat("0").format(sliderPosition.floatValue)}%",
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    Slider(
                        modifier = Modifier.fillMaxWidth(),
                        value = sliderPosition.floatValue,
                        valueRange = 0f..100f,
                        steps = 99,
                        colors = SliderDefaults.colors().copy(
                            activeTickColor = Color.Transparent,
                            inactiveTickColor = Color.Transparent,
                            inactiveTrackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                            disabledActiveTickColor = Color.Transparent,
                            disabledInactiveTickColor = Color.Transparent
                        ),
                        onValueChange = { sliderPosition.floatValue = it },
                        onValueChangeFinished = {
                            onValueChanged(sliderPosition.floatValue)
                        }
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(context.getString(R.string.settings_wallpaperandcontrols_resetbuttonleading))
                        Spacer(modifier = Modifier.size(7.dp))
                        ElevatedButton(
                            onClick = {
                                sliderPosition.floatValue = 66f
                                onValueChanged(sliderPosition.floatValue)
                            },
                            content = { Text(context.getString(R.string.action_reset)) }
                        )
                    }
                }
            }
        )
    }
}

@Preview(showSystemUi = false)
@Composable
private fun Preview() {
    val context = LocalContext.current
    SliderWithValue(
        title = context.getString(R.string.settings_wallpaperandcontrols_option_bgopacity),
        description = context.getString(R.string.settings_wallpaperandcontrols_option_bgopacity_description),
        defaultValue = 1f,
        onValueChanged = { }
    )
}
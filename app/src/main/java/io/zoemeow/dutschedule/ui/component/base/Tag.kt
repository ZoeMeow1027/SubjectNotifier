package io.zoemeow.dutschedule.ui.component.base

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Tag(
    text: String = "",
    textColor: Color = Color.Black,
    backColor: Color = Color.White
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = backColor,
        content = {
            Text(
                text = text,
                color = textColor,
                fontSize = 13.sp,
                modifier = Modifier.padding(
                    horizontal = 10.dp,
                    vertical = 2.dp
                )
            )
        }
    )
}

@Preview
@Composable
private fun TagPreview() {
    Tag(
        "Hello",
        textColor = Color.Black,
        backColor = Color.Green
    )
}

@Preview
@Composable
private fun TagPreview2() {
    Tag(
        "Failed tag!",
        textColor = Color.White,
        backColor = Color.Red
    )
}
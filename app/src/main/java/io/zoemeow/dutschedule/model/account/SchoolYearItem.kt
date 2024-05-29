package io.zoemeow.dutschedule.model.account

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import io.zoemeow.dutschedule.R
import java.io.Serializable
import java.util.Locale

data class SchoolYearItem(
    // School year (ex. 21 is for 2021-2022).
    var year: Int = 23,
    // School semester (in range 1-3, ex. 1 for semester 1, 3 for semester in summer).
    var semester: Int = 1
): Serializable {
    fun clone(
        year: Int? = null,
        semester: Int? = null,
    ): SchoolYearItem {
        return SchoolYearItem(
            year = year ?: this.year,
            semester = semester ?: this.semester
        )
    }

    override fun toString(): String {
        return String.format(
            Locale.ROOT,
            "School year: 20%2d-20%2d - Semester: %s",
            year,
            year + 1,
            if (semester == 3) "Summer semester" else semester.toString()
        )
    }

    @Composable
    fun composeToString(): String {
        val context = LocalContext.current
        return context.getString(
            R.string.account_schoolyear_main,
            year,
            year + 1,
            if (semester == 3) 2 else semester,
            if (semester == 3) context.getString(R.string.account_schoolyear_summer) else ""
        ).trim()
    }
}

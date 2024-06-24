package io.zoemeow.dutschedule

import androidx.compose.ui.unit.dp

class GlobalVariables {
    companion object {
        const val LINK_FORGOT_PASSWORD = "https://www.facebook.com/ctsvdhbkdhdn/posts/pfbid02G5sza1p8x7tEJ7S1Cac6a66EW3exgxLNmR9L26RZ8sX8xjhbEnguoeAXms31i7oxl"
        const val LINK_REPOSITORY = "https://github.com/ZoeMeow1027/DutSchedule"
        const val LINK_REPOSITORY_RELEASE = "${LINK_REPOSITORY}/releases"
        const val LINK_CHANGELOG = "${LINK_REPOSITORY}/blob/stable/CHANGELOG.md"

        const val REQUEST_EXPIRED_DURATION = 1000 * 60 * 5
        val ROUNDED_CORNER_SHAPE_SIZE = 7.dp
    }
}
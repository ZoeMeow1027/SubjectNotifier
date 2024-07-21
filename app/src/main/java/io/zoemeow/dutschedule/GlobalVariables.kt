package io.zoemeow.dutschedule

import androidx.compose.ui.unit.dp

class GlobalVariables {
    companion object {
        const val LINK_FORGOT_PASSWORD = "https://github.com/ZoeMeow1027/DutSchedule/wiki/Changing-Password-In-DUT#qu%C3%AAn-m%E1%BA%ADt-kh%E1%BA%A9u"
        const val LINK_REPOSITORY = "https://github.com/ZoeMeow1027/DutSchedule"
        const val LINK_REPOSITORY_LICENSE = "${LINK_REPOSITORY}/blob/stable/LICENSE"
        const val LINK_REPOSITORY_CREDITS = "${LINK_REPOSITORY}?tab=readme-ov-file#credits-and-license"
        const val LINK_REPOSITORY_RELEASE = "${LINK_REPOSITORY}/releases"
        const val LINK_REPOSITORY_CHANGELOG = "${LINK_REPOSITORY}/blob/stable/CHANGELOG.md"

        const val LICENSE_STRING = "MIT"

        const val REQUEST_EXPIRED_DURATION = 1000 * 60 * 5
        val ROUNDED_CORNER_SHAPE_SIZE = 7.dp
    }
}
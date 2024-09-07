package io.zoemeow.dutschedule.model.settings

import com.google.gson.annotations.SerializedName
import io.zoemeow.dutschedule.BuildConfig
import io.zoemeow.dutschedule.model.account.SchoolYearItem
import java.io.Serializable

@Suppress("SpellCheckingInspection")
data class AppSettings(
    /**
     * Config version. This will helpful when migrating to new version.
     *
     * @since v2.0-draft21
     */
    @SerializedName("version")
    val version: Int = BuildConfig.VERSION_CODE,

    @SerializedName("appsettings.layout.mainview.dashboardview")
    val mainScreenDashboardView: Boolean = false,

    @SerializedName("appsettings.appearance.thememode")
    val themeMode: ThemeMode = ThemeMode.FollowDeviceTheme,

    @SerializedName("appsettings.appearance.dynamiccolor")
    val dynamicColor: Boolean = true,

    @SerializedName("appsettings.appearance.blackbackground")
    val blackBackground: Boolean = false,

    @SerializedName("appsettings.appearance.backgroundimage.option")
    val backgroundImage: BackgroundImageOption = BackgroundImageOption.None,

    @SerializedName("appsettings.appearance.backgroundimage.backgroundopacity")
    val backgroundImageOpacity: Float = 0.65f,

    @SerializedName("appsettings.appearance.backgroundimage.componentopacity")
    val componentOpacity: Float = 0.65f,

    @SerializedName("appsettings.miscellaneous.openlinkinsideapp")
    val openLinkInsideApp: Boolean = true,

    @SerializedName("appsettings.newsbackground.filterlist")
    val newsBackgroundFilterList: ArrayList<SubjectCode> = arrayListOf(),

    @SerializedName("appsettings.newsbackground.duration")
    val newsBackgroundDuration: Int = 0,

    @SerializedName("appsettings.newsbackground.newsglobal.enabled")
    val newsBackgroundGlobalEnabled: Boolean = true,

    /**
     * Is subject news notify you?
     *
     * -1: Off;
     * 0: All;
     * 1: Your subject schedule list;
     * 2: Custom list (follow "newsBackgroundFilterList")
     * @since v2.0-draft17
     */
    @SerializedName("appsettings.newsbackground.newssubject.enabled")
    val newsBackgroundSubjectEnabled: Int = 0,

    @SerializedName("appsettings.newsbackground.parsenewssubject")
    val newsBackgroundParseNewsSubject: Boolean = false,

    @SerializedName("appsettings.globalvariables.schoolyear")
    val currentSchoolYear: SchoolYearItem = SchoolYearItem(),

    /**
     * Is news opened in bottom sheet?
     *
     * true: News will open in bottom sheet.
     * false: News will open in new activity.
     * @since v2.0-draft19
     */
    @SerializedName("appsettings.behavor.clicknewsinmain")
    val openNewsInModalBottomSheet: Boolean = true
): Serializable {
    fun clone(
        version: Int? = null,
        mainScreenDashboardView: Boolean? = null,
        themeMode: ThemeMode? = null,
        dynamicColor: Boolean? = null,
        blackBackground: Boolean? = null,
        backgroundImage: BackgroundImageOption? = null,
        openLinkInsideApp: Boolean? = null,
        newsFilterList: ArrayList<SubjectCode>? = null,
        backgroundImageOpacity: Float? = null,
        componentOpacity: Float? = null,
        fetchNewsBackgroundDuration: Int? = null,
        newsBackgroundGlobalEnabled: Boolean? = null,
        newsBackgroundSubjectEnabled: Int? = null,
        newsBackgroundParseNewsSubject: Boolean? = null,
        currentSchoolYear: SchoolYearItem? = null,
        openNewsInModalBottomSheet: Boolean? = null
    ): AppSettings {
        return AppSettings(
            version = version ?: this.version,
            mainScreenDashboardView = mainScreenDashboardView ?: this.mainScreenDashboardView,
            themeMode = themeMode ?: this.themeMode,
            dynamicColor = dynamicColor ?: this.dynamicColor,
            blackBackground = blackBackground ?: this.blackBackground,
            backgroundImage = backgroundImage ?: this.backgroundImage,
            openLinkInsideApp = openLinkInsideApp ?: this.openLinkInsideApp,
            newsBackgroundFilterList = newsFilterList ?: this.newsBackgroundFilterList,
            backgroundImageOpacity = backgroundImageOpacity ?: this.backgroundImageOpacity,
            componentOpacity = componentOpacity ?: this.componentOpacity,
            newsBackgroundDuration = when (fetchNewsBackgroundDuration) {
                null -> this.newsBackgroundDuration
                0 -> 0
                else -> if (fetchNewsBackgroundDuration >= 5) fetchNewsBackgroundDuration else 5
            },
            newsBackgroundGlobalEnabled = newsBackgroundGlobalEnabled ?: this.newsBackgroundGlobalEnabled,
            newsBackgroundSubjectEnabled = newsBackgroundSubjectEnabled ?: this.newsBackgroundSubjectEnabled,
            newsBackgroundParseNewsSubject = newsBackgroundParseNewsSubject ?: this.newsBackgroundParseNewsSubject,
            currentSchoolYear = currentSchoolYear ?: this.currentSchoolYear,
            openNewsInModalBottomSheet = openNewsInModalBottomSheet ?: this.openNewsInModalBottomSheet
        )
    }
}

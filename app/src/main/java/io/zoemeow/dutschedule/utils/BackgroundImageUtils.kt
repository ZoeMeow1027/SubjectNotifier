package io.zoemeow.dutschedule.utils

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.graphics.drawable.toBitmap
import io.zoemeow.dutschedule.model.settings.BackgroundImageOption
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File

class BackgroundImageUtils {
    companion object {
        private val backgroundOptionCache: MutableStateFlow<BackgroundImageOption> = MutableStateFlow(BackgroundImageOption.None)
        val backgroundImageCache: MutableState<Bitmap?> = mutableStateOf(null)

        fun setBackgroundImageCacheOption(context: Context, backgroundOption: BackgroundImageOption) {
            if (backgroundOption == BackgroundImageOption.PickFileFromMedia) {
                this.backgroundImageCache.value = getImageFromAppData(context)
            } else if (this.backgroundOptionCache.value != backgroundOption) {
                this.backgroundOptionCache.value = backgroundOption
                this.backgroundImageCache.value = when (backgroundOptionCache.value) {
                    BackgroundImageOption.None -> null
                    BackgroundImageOption.YourCurrentWallpaper -> getCurrentWallpaperBackground(context)
                    BackgroundImageOption.PickFileFromMedia -> getImageFromAppData(context)
                }
            }
        }

        private fun getCurrentWallpaperBackground(context: Context): Bitmap? {
            return try {
                // WallpaperManager API isn't working on Android 13 because Google has deprecated. So, return null instead.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    return null
                }
                val wallpaperManager = WallpaperManager.getInstance(context)
                wallpaperManager.drawable?.toBitmap()
            } catch (_: Exception) {
                null
            }
        }

        /**
         * Set a image to application background wallpaper.
         * https://stackoverflow.com/questions/76587418/how-save-an-image-from-gallery-to-internal-memory-using-jetpack-compose
         * https://stackoverflow.com/questions/5963535/java-lang-illegalargumentexception-contains-a-path-separator
         * @param context Android context
         * @param uri Image uri to copy to app data (and use only for background wallpaper). Empty uri will delete existing image in data.
         */
        fun setImageToAppData(
            context: Context,
            uri: Uri?,
            onCompleted: ((Boolean) -> Unit)? = null
        ) {
            // If url is not null, save them
            val result: Boolean = if (uri != null) {
                saveImageToAppData(context, uri)
            }
            // If null, delete image
            else {
                deleteImageFromAppData(context)
            }

            onCompleted?.let { it(result) }
        }

        private fun saveImageToAppData(context: Context, uri: Uri): Boolean {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val file = File("${context.filesDir.path}/image/background.jpg")
                run {
                    File("${context.filesDir.path}/image").mkdir()
                    file.createNewFile()
                }
                inputStream?.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                        output.close()
                    }
                    input.close()
                }

                return true
            } catch (_: Exception) {
                return false
            }
        }

        private fun deleteImageFromAppData(context: Context): Boolean {
            try {
                val file = File("${context.filesDir.path}/image/background.jpg")
                run {
                    File("${context.filesDir.path}/image").mkdir()
                    return file.delete()
                }
            } catch (_: Exception) {
                return false
            }
        }

        /**
         * Fetch image from app data (if exist).
         * https://stackoverflow.com/q/75172380
         * @param context Android context
         * @return If have image in app data, return this bitmap. Otherwise, return null.
         */
        fun getImageFromAppData(context: Context): Bitmap? {
            return try {
                val file = File("${context.filesDir.path}/image/background.jpg")
                BitmapFactory.decodeByteArray(file.readBytes(), 0, file.readBytes().size)
            } catch (_: Exception) {
                null
            }
        }
    }
}
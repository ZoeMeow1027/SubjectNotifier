package io.zoemeow.dutschedule.model.news

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class NewsGroupByDate<T>(
    @SerializedName("item_list")
    val itemList: ArrayList<T> = ArrayList(),

    @SerializedName("date")
    val date: Long = 0,
): Serializable

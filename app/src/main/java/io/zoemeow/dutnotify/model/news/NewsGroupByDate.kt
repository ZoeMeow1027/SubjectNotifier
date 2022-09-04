package io.zoemeow.dutnotify.model.news

import java.io.Serializable

data class NewsGroupByDate<T>(
    val itemList: ArrayList<T> = ArrayList(),
    val date: Long = 0,
) : Serializable
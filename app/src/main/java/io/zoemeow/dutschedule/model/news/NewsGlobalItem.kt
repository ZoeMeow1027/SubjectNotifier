package io.zoemeow.dutschedule.model.news

import io.dutwrapper.dutwrapper.News

data class NewsGlobalItem(
    var updated: Boolean = false
) : News.NewsItem() {
    fun update(newsItem: News.NewsItem) {
        if (this.title == newsItem.title && this.date == newsItem.date) {
            this.updated = true
        }

        this.title = newsItem.title
        this.content = newsItem.content
        this.contentHtml = newsItem.contentHtml
        this.resources = newsItem.resources
        this.date = newsItem.date
    }
}
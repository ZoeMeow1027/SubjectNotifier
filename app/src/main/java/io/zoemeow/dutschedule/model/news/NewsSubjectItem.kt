package io.zoemeow.dutschedule.model.news

import io.dutwrapper.dutwrapper.News

data class NewsSubjectItem(
    var updated: Boolean = false
) : News.NewsSubjectItem(News.NewsItem()) {
    fun update(newsItem: News.NewsSubjectItem) {
        this.title = newsItem.title
        this.contentHtml = newsItem.contentHtml
        this.content = newsItem.content
        this.resources = newsItem.resources
        this.date = newsItem.date

        this.affectedClass = newsItem.affectedClass
        this.affectedDate = newsItem.affectedDate
        this.lessonStatus = newsItem.lessonStatus
        this.affectedLesson = newsItem.affectedLesson
        this.affectedRoom = newsItem.affectedRoom
        this.lecturerName = newsItem.lecturerName
        this.lecturerGender = newsItem.lecturerGender

        this.updated = true
    }
}

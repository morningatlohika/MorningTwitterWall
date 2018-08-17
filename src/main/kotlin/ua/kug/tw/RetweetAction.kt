package ua.kug.tw

import twitter4j.Status
import twitter4j.Twitter
import twitter4j.util.function.Consumer

class RetweetAction(private val twitter: Twitter, private val stopWords: List<String> = emptyList(), private val ignore: List<String> = emptyList()) : Consumer<Status> {

    override fun accept(status: Status) {
        if (isAcceptable(status)) {
            twitter.retweetStatus(status.id)
        }
    }

    private fun isAcceptable(status: Status) =
            !(status.isRetweet or status.isRetweetedByMe) && isNotBadStatus(status) && isNotIgnoreUser(status)

    private fun isNotBadStatus(status: Status) = stopWords.none { it in status.text }

    private fun isNotIgnoreUser(status: Status) = ignore.none { it in status.user.name || it in status.user.screenName }
}
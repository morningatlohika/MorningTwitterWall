package ua.kug

import io.javalin.Javalin
import twitter4j.TwitterFactory
import twitter4j.TwitterStreamFactory
import twitter4j.conf.ConfigurationBuilder
import twitter4j.util.function.Consumer
import ua.kug.tw.RetweetAction
import ua.kug.tw.TwitterWallWithAction
import java.io.FileInputStream
import java.util.*

fun main(args: Array<String>) {
    val prop = Properties()

    var configLocation = System.getProperty("config.location", "./")
    val input = FileInputStream("${configLocation}application.properties")
    prop.load(input)

    val app = Javalin.create().port(Integer.valueOf(prop.getProperty("server.port", "7000"))).start()
    app.get("/") { ctx -> ctx.result("Hello World") }

    val configuration = ConfigurationBuilder().setDebugEnabled(true)
            .setOAuthConsumerKey(prop.getProperty("oauth.consumerKey"))
            .setOAuthConsumerSecret(prop.getProperty("oauth.consumerSecret"))
            .setOAuthAccessToken(prop.getProperty("oauth.accessToken"))
            .setOAuthAccessTokenSecret(prop.getProperty("oauth.accessTokenSecret"))
            .build()

    val twitterWall = TwitterWallWithAction(
            twitterStream = TwitterStreamFactory(configuration).instance,
            hashatgs = prop.getProperty("app.hashatgs", "#KUG,#MorningAtLohika,#Kotlin").split(","),
            size = 5,
            actions = listOf(
                    RetweetAction(
                            twitter = TwitterFactory(configuration).instance,
                            stopWords = prop.getProperty("app.stopwords", "ugly,no soup,bad,yegor256").split(","),
                            ignore = prop.getProperty("app.ignore", "user1,user2").split(",")
                    ),
                    Consumer {
                        println(it.text)
                        println(Thread.currentThread().name)
                    }
            )
    )

    app.get("/tw") { it.json(twitterWall.tweets()) }
}

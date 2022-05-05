package io.github.copecone.dashboard

interface Dashboard {
    companion object: Dashboard by LibraryLoader.loadImplement(Dashboard::class.java)

    fun printCoreMessage()
}
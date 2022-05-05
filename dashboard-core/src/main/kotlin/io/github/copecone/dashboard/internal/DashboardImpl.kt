package io.github.copecone.dashboard.internal

import io.github.copecone.dashboard.Dashboard

class DashboardImpl: Dashboard {
    //private val version = LibraryLoader.getVersion()

    override fun printCoreMessage() {
        println("This is core")
    }
}

interface Version {
    val value: String
}
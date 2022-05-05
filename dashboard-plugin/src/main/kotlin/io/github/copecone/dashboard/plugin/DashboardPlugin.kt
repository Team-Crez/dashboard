package io.github.copecone.dashboard.plugin

import io.github.copecone.dashboard.Dashboard
import org.bukkit.plugin.java.JavaPlugin

class DashboardPlugin: JavaPlugin() {
    override fun onEnable() {
        Dashboard.printCoreMessage()
    }
}
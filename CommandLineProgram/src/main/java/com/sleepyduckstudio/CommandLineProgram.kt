package com.sleepyduckstudio

import com.google.gson.GsonBuilder
import com.sleepyduckstudio.commandline.CommandParser
import com.sleepyduckstudio.controller.Registry
import com.sleepyduckstudio.controller.runBusinessStep
import com.sleepyduckstudio.controller.runProductionStep
import com.sleepyduckstudio.controller.runTradeStep
import com.sleepyduckstudio.model.Station

fun main() {
    val commandLineProgram = CommandLineProgram()

    while (true) {
        val commandText = readCommand()
        commandLineProgram.executeCommand(commandText)
    }
}

fun readCommand(text: String = ": "): String {
    print(text)
    return readLine() ?: ""
}

class CommandLineProgram : CommandParser() {
    private var running = false
    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val registry = RegistryDataStore()

    init {
        Registry.setImplementation(registry)
    }

    override fun start() {
        if (running) {
            println("Program already running")
        } else {
            running = true
            Thread(Runnable {
                println("World active")
                while (running) {
                    runProductionStep()
                    runBusinessStep()
                    runTradeStep()
                    Thread.sleep(100)
                }
                println("World paused")
            }).start()
        }
    }

    override fun pause() {
        if (running) {
            running = false
        } else {
            println("World not active")
        }
    }

    override fun init() {
        setupExampleWorld(registry)
        println("World initialized, only do this once!")
    }

    override fun listStations() {
        println("Stations:")
        println(gson.toJson(registry.stations()))
    }

    override fun addStation(station: Station) {
        registry.add(station)
        println("Added station")
        println(gson.toJson(station))
    }
}

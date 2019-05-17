package com.sleepyduckstudio

import com.google.gson.GsonBuilder
import com.sleepyduckstudio.commandline.CommandParser
import com.sleepyduckstudio.commandline.CommandParser.CommandParserListener
import com.sleepyduckstudio.commandline.CommandType
import com.sleepyduckstudio.controller.*
import com.sleepyduckstudio.model.Station
import kotlin.system.exitProcess

fun main() {
    val commandLineProgram = CommandLineProgram()
    while (true) {
        val command = readLine()
        commandLineProgram.execute(command ?: "")
    }
}

class CommandLineProgram : CommandParserListener {
    private val commandParser = CommandParser(this)
    private var running = false
    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val registry = RegistryDataStore()

    init {
        Registry.setImplementation(registry)
    }

    fun execute(command: String) = commandParser.executeCommand(command)

    override fun onError(message: String) = println(message)

    override fun unknownCommand() {
        println("Unknown command")
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

    override fun exit() {
        exitProcess(0)
    }

    override fun help() {
        CommandType.values().forEach { println(it.exampleCommand) }
    }

    override fun init() {
        setupExampleWorld(registry)
        println("World initialized, only do this once!")
    }

    override fun listCommodities() {
        println("Commodities:")
        println(gson.toJson(registry.commodities()))
    }

    override fun listEntities() {
        println("Entities:")
        println(gson.toJson(registry.entities()))
    }

    override fun listBusinesses() {
        println("Businesses:")
        println(gson.toJson(registry.businesses()))
    }

    override fun listProducers() {
        println("Producers:")
        println(gson.toJson(registry.productionUnits()))
    }

    override fun listStations() {
        println("Stations:")
        println(gson.toJson(registry.stations()))
    }

    override fun listStockpile() {
        println("Stockpiles:")
        println(gson.toJson(registry.stockpile()))
    }

    override fun listActiveTrades() {
        println("Active trades:")
        println(gson.toJson(registry.trades(isActive())))
    }

    override fun addStation(station: Station) {
        registry.add(station)
        println("Added station")
        println(gson.toJson(station))
    }
}

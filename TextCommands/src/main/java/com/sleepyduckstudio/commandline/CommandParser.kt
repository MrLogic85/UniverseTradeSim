package com.sleepyduckstudio.commandline

import com.sleepyduckstudio.commandline.CommandType.*
import com.sleepyduckstudio.model.Station

enum class CommandType(val text: String, val exampleCommand: String = text) {
    COMMAND_EXIT("exit"),
    COMMAND_HELP("help"),
    COMMAND_INIT("init", "init (initialize example world)"),
    COMMAND_START("start"),
    COMMAND_PAUSE("pause"),
    COMMAND_LIST_COMMODITIES("list commodities"),
    COMMAND_LIST_ENTITIES("list entities"),
    COMMAND_LIST_BUSINESS("list businesses"),
    COMMAND_LIST_PRODUCTION_UNITS("list producers"),
    COMMAND_LIST_STATIONS("list stations"),
    COMMAND_LIST_STOCKS("list stockpile"),
    COMMAND_LIST_OPEN_TRADES("list active trades"),
    COMMAND_ADD("add", """add [station] --name "name"""")
}

private infix fun String.isCommand(command: CommandType) = startsWith(command.text, ignoreCase = true)
private val UUIDMatcher = """[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}""".toRegex()

class CommandParser(val listener: CommandParserListener) {

    interface CommandParserListener {
        fun onError(message: String)
        fun unknownCommand()
        fun start()
        fun pause()
        fun exit()
        fun help()
        fun init()
        fun listCommodities()
        fun listEntities()
        fun listBusinesses()
        fun listProducers()
        fun listStations()
        fun listStockpile()
        fun listActiveTrades()
        fun addStation(station: Station)
    }

    fun executeCommand(command: String) = when {
        command isCommand COMMAND_EXIT -> listener.exit()
        command isCommand COMMAND_HELP -> listener.help()
        command isCommand COMMAND_INIT -> listener.init()
        command isCommand COMMAND_START -> listener.start()
        command isCommand COMMAND_PAUSE -> listener.pause()
        command isCommand COMMAND_LIST_COMMODITIES -> listener.listCommodities()
        command isCommand COMMAND_LIST_ENTITIES -> listener.listEntities()
        command isCommand COMMAND_LIST_BUSINESS -> listener.listBusinesses()
        command isCommand COMMAND_LIST_PRODUCTION_UNITS -> listener.listProducers()
        command isCommand COMMAND_LIST_STATIONS -> listener.listStations()
        command isCommand COMMAND_LIST_STOCKS -> listener.listStockpile()
        command isCommand COMMAND_LIST_OPEN_TRADES -> listener.listActiveTrades()
        command isCommand COMMAND_ADD -> add(command)
        else -> unknown()
    }

    private fun printUsage(command: CommandType) = listener.onError("Usage: ${command.exampleCommand}")

    private fun unknown() {
        listener.unknownCommand()
    }

    private fun add(command: String) {
        val secondCommand = command.split(' ', limit = 2)
        if (secondCommand.count() > 1) {
            when {
                secondCommand[1].startsWith("station") -> addStation(command)
                else -> printUsage(COMMAND_ADD)
            }
        } else {
            printUsage(COMMAND_ADD)
        }
    }

    private fun addStation(command: String) {
        val name = command.substringAfter("--name ", missingDelimiterValue = "").trim('"', ' ')
        if (name.isNotEmpty() && name.isNotBlank()) {
            listener.addStation(Station(name = name))
        } else {
            printUsage(COMMAND_ADD)
        }
    }
}
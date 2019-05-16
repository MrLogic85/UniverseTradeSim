package com.sleepyduckstudio.commandline

import com.sleepyduckstudio.commandline.CommandType.*
import com.sleepyduckstudio.model.Station
import kotlin.system.exitProcess

enum class CommandType(val text: String, val exampleCommand: String = text) {
    COMMAND_EXIT("exit"),
    COMMAND_HELP("help"),
    COMMAND_INIT("init", "init (initialize example world)"),
    COMMAND_START("start"),
    COMMAND_PAUSE("pause"),
    COMMAND_LIST_STATIONS("list stations"),
    COMMAND_LIST_COMMODITIES("list commodities"),
    COMMAND_ADD("add", """add [station] --name "name"""")
}

private infix fun String.isCommand(command: CommandType) = startsWith(command.text, ignoreCase = true)
private val UUIDMatcher = """[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}""".toRegex()

abstract class CommandParser {

    fun executeCommand(command: String) = when {
        command isCommand COMMAND_EXIT -> exit()
        command isCommand COMMAND_HELP -> help()
        command isCommand COMMAND_INIT -> init()
        command isCommand COMMAND_START -> start()
        command isCommand COMMAND_PAUSE -> pause()
        command isCommand COMMAND_LIST_STATIONS -> listStations()
        command isCommand COMMAND_LIST_COMMODITIES -> TODO()
        command isCommand COMMAND_ADD -> add(command)
        else -> unknown()
    }

    private fun printUsage(command: CommandType) = println("Usage: ${command.exampleCommand}")

    private fun unknown() {
        println("Unknown command")
    }

    private fun exit() {
        exitProcess(0)
    }

    private fun help() {
        CommandType.values().forEach { println(it.exampleCommand) }
    }

    abstract fun start()

    abstract fun pause()

    abstract fun init()

    abstract fun listStations()

    abstract fun addStation(station: Station)

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
            addStation(Station(name = name))
        } else {
            printUsage(COMMAND_ADD)
        }
    }
}
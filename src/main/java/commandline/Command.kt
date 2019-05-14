package commandline

import commandline.CommandType.*
import controller.Registry
import kotlin.system.exitProcess

enum class CommandType(val text: String, val exampleCommand: String = text) {
    COMMAND_EXIT("exit"),
    COMMAND_HELP("help"),
    COMMAND_LIST_STATIONS("list stations")
}

private infix fun String.isCommand(command: CommandType) = startsWith(command.text, ignoreCase = true)

fun parseCommand(command: String) = when {
    command isCommand COMMAND_EXIT -> Exit
    command isCommand COMMAND_HELP -> Help
    command isCommand COMMAND_LIST_STATIONS -> ListStations
    else -> Unknown()
}

open class Command {
    open fun execute() {}
}

class Unknown : Command() {
    override fun execute() {
        println("Unknown command")
    }
}

object Exit : Command() {
    override fun execute() {
        exitProcess(0)
    }
}

object Help : Command() {
    override fun execute() {
        CommandType.values().forEach { println(it.exampleCommand) }
    }
}

object ListStations : Command() {
    override fun execute() {
        Registry.stations.forEach(::println)
    }
}

val UUIDMatcher = """[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}""".toRegex()
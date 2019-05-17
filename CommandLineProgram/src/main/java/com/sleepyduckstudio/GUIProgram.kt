package com.sleepyduckstudio

import com.google.gson.GsonBuilder
import com.sleepyduckstudio.commandline.CommandParser
import com.sleepyduckstudio.commandline.CommandParser.CommandParserListener
import com.sleepyduckstudio.commandline.CommandType
import com.sleepyduckstudio.controller.Registry
import com.sleepyduckstudio.controller.runBusinessStep
import com.sleepyduckstudio.controller.runProductionStep
import com.sleepyduckstudio.controller.runTradeStep
import com.sleepyduckstudio.model.Station
import java.awt.Component
import java.awt.Frame
import java.awt.KeyboardFocusManager
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.util.*
import javax.swing.JTextArea
import javax.swing.JTextField
import javax.swing.SpringLayout
import javax.swing.SpringLayout.*
import kotlin.system.exitProcess

private const val VERTICAL = "width"
private const val HORIZONTAL = "height"

fun main() {
    GUIProgram()

    /*while (true) {
        val commandText = readCommand()
        commandLineProgram.executeCommand(commandText)
    }*/
}

class GUIProgram : Frame("UniverseTradeSim"), CommandParserListener {
    private val registry = RegistryDataStore()
    private val commandParser = CommandParser(this)
    private val commandHistory = LinkedList<String>()
    private val gson = GsonBuilder().setPrettyPrinting().create()
    private var commandHistoryIndex = -1
    private var running = false

    private val springLayout = SpringLayout().also { layout = it }
    private val textInput = JTextField().also { add(it) }
    private val textArea = JTextArea(20, 1).also { add(it) }

    // Init window
    init {
        isVisible = true
        setSize(800, 600)
        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent?) {
                System.exit(0)
            }
        })
        Registry.setImplementation(registry)
    }

    // Build layout
    init {
        springLayout.match(HORIZONTAL, textInput, this)
        springLayout.match(HORIZONTAL, textArea, this)
        springLayout.topToBottom(textArea, textInput)
    }

    // Add listeners
    init {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher {
            when {
                it.id == KeyEvent.KEY_TYPED && it.extendedKeyCode == KeyEvent.VK_ENTER -> {
                    val command = textInput.text
                    commandParser.executeCommand(command)
                    commandHistory.push(command)
                    commandHistoryIndex = -1
                    textInput.text = ""
                    true
                }

                it.id == KeyEvent.KEY_PRESSED && it.extendedKeyCode == KeyEvent.VK_DOWN -> {
                    if (commandHistoryIndex > 0) {
                        val command = commandHistory[--commandHistoryIndex]
                        textInput.text = command
                    } else if (commandHistoryIndex <= 0) {
                        commandHistoryIndex = -1
                        textInput.text = ""
                    }
                    true
                }

                it.id == KeyEvent.KEY_PRESSED && it.extendedKeyCode == KeyEvent.VK_UP -> {
                    if (commandHistoryIndex < commandHistory.size - 1) {
                        val command = commandHistory[++commandHistoryIndex]
                        textInput.text = command
                    }
                    true
                }

                else -> false
            }
        }
        textInput.addKeyListener(object : KeyAdapter() {
            override fun keyTyped(e: KeyEvent) {
                when (e.extendedKeyCode) {
                }
            }
        })
    }

    override fun onError(message: String) = println(message)

    override fun unknownCommand() {
        textArea.text = "Unknown command"
    }

    override fun start() {
        if (running) {
            textArea.text = "Program already running"
        } else {
            running = true
            Thread(Runnable {
                textArea.text = "World active"
                while (running) {
                    runProductionStep()
                    runBusinessStep()
                    runTradeStep()
                    Thread.sleep(100)
                }
                textArea.text = "World paused"
            }).start()
        }
    }

    override fun pause() {
        if (running) {
            running = false
        } else {
            textArea.text = "World not active"
        }
    }

    override fun exit() {
        exitProcess(0)
    }

    override fun help() {
        textArea.text = CommandType.values()
            .fold(StringBuilder()) { text, command -> text.appendln(command.exampleCommand) }
            .toString()
    }

    override fun init() {
        setupExampleWorld(registry)
        textArea.text = "World initialized, only do this once!"
    }

    override fun listCommodities() {
        textArea.text = "Commodities:\n${gson.toJson(registry.commodities())}"
    }

    override fun listEntities() {
        textArea.text = "Entities:\n${gson.toJson(registry.entities())}"
    }

    override fun listBusinesses() {
        textArea.text = "Businesses:\n${gson.toJson(registry.businesses())}"
    }

    override fun listProducers() {
        textArea.text = "Producers:\n${gson.toJson(registry.productionUnits())}"
    }

    override fun listStations() {
        textArea.text = "Stations:\n${gson.toJson(registry.stations())}"
    }

    override fun listStockpile() {
        textArea.text = "Stockpiles:\n${gson.toJson(registry.stockpile())}"
    }

    override fun listActiveTrades() {
        textArea.text = "Active trades:\n${gson.toJson(registry.trades(com.sleepyduckstudio.controller.isActive()))}"
    }

    override fun addStation(station: Station) {
        registry.add(station)
        textArea.text = "Added station:\n${gson.toJson(station)}"
    }
}

private fun SpringLayout.topToBottom(component: Component, anchor: Component, padding: Int = 0) {
    putConstraint(NORTH, component, padding, SOUTH, anchor)
}

private fun SpringLayout.align(edge: String, component: Component, anchor: Component, padding: Int = 0) {
    putConstraint(edge, component, padding, edge, anchor)
}

private fun SpringLayout.match(direction: String, component: Component, anchor: Component, padding: Int = 0) {
    when (direction) {
        HORIZONTAL -> {
            align(WEST, component, anchor, padding)
            align(EAST, component, anchor, padding)
        }
        VERTICAL -> {
            align(NORTH, component, anchor, padding)
            align(SOUTH, component, anchor, padding)
        }
    }
}
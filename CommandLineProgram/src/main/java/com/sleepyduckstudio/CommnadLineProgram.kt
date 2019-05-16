package com.sleepyduckstudio

import com.sleepyduckstudio.commandline.executeCommand

fun main() {
    while (true) {
        val commandText = readCommand()
        executeCommand(commandText)
    }
}

fun readCommand(text: String = ": "): String {
    print(text)
    return readLine() ?: ""
}

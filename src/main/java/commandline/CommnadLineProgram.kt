package commandline

import controller.Registry
import controller.runBusinessStep
import controller.runProductionStep
import controller.runTradeStep
import model.*

fun main() {
    println("Starting program")
    setup()
    startProgram()
    val commands = mutableListOf<Command>()

    while (true) {
        when {
            commands.isEmpty() -> {
                val commandText = readCommand()
                commands.add(parseCommand(commandText))
            }

            else -> {
                commands.removeAt(0).execute()
            }
        }
    }
}

fun readCommand(text: String = ": "): String {
    print(text)
    return readLine() ?: ""
}

fun startProgram() {
    Thread(Runnable {
        println("Program running")
        while (true) {
            Thread.sleep(100)
            runProductionStep()
            runBusinessStep()
            runTradeStep()
        }
    }).start()
}

fun setup() {
    val station1 = Station(name = "Station 1")
    val credits = Commodity(
        name = "Credits",
        abbrev = "Cr"
    )
    val water = Commodity(name = "Water")
    Registry.addAll(station1, credits, water)

    Entity(
        currencyId = credits.id,
        stationId = station1.id
    ).also { entity ->
        val creditStock = Stock(
            entityId = entity.id,
            stationId = station1.id,
            commodityId = credits.id
        )
        val waterStock = Stock(
            entityId = entity.id,
            stationId = station1.id,
            commodityId = water.id
        )
        val waterProduction = ProductionUnit(
            stockId = waterStock.id,
            productionAmount = 10
        )
        val waterBusiness = Business(
            entityId = entity.id,
            stationId = station1.id,
            buyStockId = creditStock.id,
            sellStockIds = listOf(waterStock.id)
        )
        Registry.addAll(entity, creditStock, waterProduction, waterStock, waterBusiness)
    }

    Entity(
        currencyId = credits.id,
        stationId = station1.id
    ).also { entity ->
        val creditStock = Stock(
            entityId = entity.id,
            stationId = station1.id,
            commodityId = credits.id,
            amount = 1000
        )
        val creditsProduction = ProductionUnit(
            stockId = creditStock.id,
            productionAmount = 100
        )
        val waterStock = Stock(
            entityId = entity.id,
            stationId = station1.id,
            commodityId = water.id
        )
        val waterBusiness = Business(
            entityId = entity.id,
            stationId = station1.id,
            buyStockId = waterStock.id,
            sellStockIds = listOf(creditStock.id)
        )
        Registry.addAll(entity, creditStock, creditsProduction, waterStock, waterBusiness)
    }

    println("Setup complete")
}
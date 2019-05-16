package com.sleepyduckstudio.controller

fun runProductionStep() {
    registry.productionUnits().forEach { prod ->
        val stock = registry.stock(prod.stockId)
        stock.copy(amount = stock.amount + prod.productionAmount).update()
    }
}

package controller

import model.Stock

fun runProductionStep() {
    Registry.productionUnits.forEach { prod ->
        val stock = Registry.get<Stock>(prod.stockId)
        Registry.update(stock.copy(amount = stock.amount + prod.productionAmount))
    }
}

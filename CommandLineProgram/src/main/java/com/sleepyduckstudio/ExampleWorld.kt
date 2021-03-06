package com.sleepyduckstudio

import com.sleepyduckstudio.controller.Registry
import com.sleepyduckstudio.model.*

fun setupExampleWorld(registry: Registry) {
    val station1 = Station(name = "Station 1")
    val credits = Commodity(
        name = "Credits",
        abbrev = "Cr"
    )
    val water = Commodity(name = "Water")
    registry.addAll(station1, credits, water)

    Entity(
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
        registry.addAll(entity, creditStock, waterProduction, waterStock, waterBusiness)
    }

    Entity(
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
        registry.addAll(entity, creditStock, creditsProduction, waterStock, waterBusiness)
    }
}
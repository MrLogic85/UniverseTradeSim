package com.sleepyduckstudio.model

import java.util.*

sealed class RegistryObject

data class Commodity(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val abbrev: String = name
) : RegistryObject()

data class Entity(
    val id: String = UUID.randomUUID().toString(),
    val currencyId: String,
    val stationId: String
) : RegistryObject()

data class Business(
    val id: String = UUID.randomUUID().toString(),
    val stationId: String,
    val entityId: String,
    val buyStockId: String,
    val sellStockIds: List<String>
) : RegistryObject()

data class ProductionUnit(
    val id: String = UUID.randomUUID().toString(),
    val stockId: String,
    val productionAmount: Long = 1
) : RegistryObject()

data class Station(
    val id: String = UUID.randomUUID().toString(),
    val name: String
) : RegistryObject()

const val DEFAULT_TRADE_LENGTH = 10000L

data class Stock(
    val id: String = UUID.randomUUID().toString(),
    val entityId: String,
    val stationId: String,
    val commodityId: String,
    val amount: Long = 0,
    val tradeLength: Long = DEFAULT_TRADE_LENGTH
) : RegistryObject()

data class Trade(
    val id: String = UUID.randomUUID().toString(),
    val sellingBusinessId: String,
    val buyingBusinessId: String? = null,
    val stationId: String,
    val sellCommodityId: String,
    val buyCommodityId: String,
    val sellAmount: Long,
    val buyAmount: Long = 0,
    val price: Double = buyAmount.toDouble() / sellAmount,
    val timestamp: Long = Date().time,
    val tradeLength: Long = 0
) : RegistryObject()

val Trade.isClosed get() = buyingBusinessId != null
val Trade.isActive get() = !isClosed

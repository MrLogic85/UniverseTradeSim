package com.sleepyduckstudio.controller

import com.sleepyduckstudio.model.Business
import com.sleepyduckstudio.model.Stock
import com.sleepyduckstudio.model.Trade
import kotlin.random.Random

fun runBusinessStep() {
    registry.businesses().forEach { business ->
        business.sellStockIds.map { registry.stock(it) }.forEach { sellStock ->
            if (sellStock.amount != 0L) {
                val price = getRecommendedSellPrice(business, sellStock)
                //println("Selling ${sellStock.commodity.abbrev} for ${business.buyStock.commodity.abbrev}, amount ${sellStock.amount}, price $price")
                if (sellStock.amount * price >= 1.0) {
                    registry.add(
                        Trade(
                            stationId = sellStock.stationId,
                            sellingBusinessId = business.id,
                            sellCommodityId = sellStock.commodityId,
                            buyCommodityId = business.buyStock.commodityId,
                            sellAmount = sellStock.amount,
                            price = price,
                            tradeLength = sellStock.tradeLength
                        )
                    )
                    sellStock.copy(amount = 0).update()
                }
            }
        }
    }
}

private fun getRecommendedSellPrice(business: Business, sellStock: Stock): Double {
    val trades = registry.trades(
        isActive(),
        isSelling(sellStock.commodityId),
        isBuying(business.buyStock.commodityId),
        atStation(sellStock.stationId)
    )

    // TODO Use a minimum price to sell for if there are no sellers
    val lowestSellPrice = trades.minBy { it.price }?.price ?: getRecommendedSellPriceFromBuyers(business, sellStock)
    val overSell = randomRange(0.9, 1.5)

    return when (lowestSellPrice) {
        null -> 1.0
        else -> lowestSellPrice * overSell
    }
}

fun getRecommendedSellPriceFromBuyers(business: Business, sellStock: Stock): Double? {
    val trades = registry.trades(
        isActive(),
        isSelling(business.buyStock.commodityId),
        isBuying(sellStock.commodityId),
        atStation(sellStock.stationId)
    )

    val maxSellPrice = trades.maxBy { it.price }?.price
    return maxSellPrice?.let { 1.0 / it }
}

fun randomRange(from: Double, to: Double): Double = Random.nextDouble() * (to - from) + from
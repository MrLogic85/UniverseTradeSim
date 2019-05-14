package controller

import model.*
import kotlin.random.Random

fun runBusinessStep() {
    Registry.businesses.forEach { business ->
        business.sellStockIds.map { Registry.get<Stock>(it) }.forEach { sellStock ->
            if (sellStock.amount != 0L) {
                val price = getRecommendedSellPrice(business, sellStock)
                println("Selling ${sellStock.commodity.abbrev} for ${business.buyStock.commodity.abbrev}, amount ${sellStock.amount}, price ${price.toDouble()}")
                Registry.add(
                    Trade(
                        stationId = sellStock.stationId,
                        sellingBusinessId = business.id,
                        sellCommodityId = sellStock.commodityId,
                        buyCommodityId = business.buyStockId,
                        sellAmount = sellStock.amount,
                        price = price,
                        tradeLength = sellStock.tradeLength
                    )
                )
                Registry.update(sellStock.copy(amount = 0))
            }
        }
    }
}

private fun getRecommendedSellPrice(business: Business, produce: Stock): Rational {
    val trades = Registry.trades
        .active()
        .selling(produce.commodityId)
        .buying(business.buyStockId)
        .atStation(produce.stationId)

    val lowestSellPrice = trades.minBy { it.price }?.price
    val overSell = randomRange(0.9, 1.5)

    return when (lowestSellPrice) {
        null -> produce.defaultPrice
        else -> lowestSellPrice * overSell
    }
}

fun randomRange(from: Double, to: Double): Double = Random.nextDouble() * (to - from) + from
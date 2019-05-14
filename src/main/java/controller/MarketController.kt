package controller

import model.*

fun runTradeStep() {
    trade()
    timeoutTrades()
}

private fun trade() {
    Registry.stations.forEach { tradeAtStation(it) }
    Registry.trades.active()
        .distinctBy { it.stationId }
        .map { Registry.get<Station>(it.stationId) }
        .forEach { tradeAtStation(it) }
}

private fun tradeAtStation(it: Station) {
    val localTrades = Registry.trades.active().atStation(it.id)
    val sellingCommodities = localTrades.map { it.sellCommodityId }.distinct()
    val buyingCommodities = localTrades.map { it.buyCommodityId }.distinct()
    sellingCommodities.forEach { sellCommodity ->
        buyingCommodities.forEach { buyCommodity ->
            trade(localTrades, sellCommodity, buyCommodity)
        }
    }
}

private fun trade(
    trades: Sequence<Trade>,
    sellCommodityId: String,
    buyCommodityId: String
) {
    val selling = trades.selling(sellCommodityId).buying(buyCommodityId).sortedBy { it.price }.toMutableList()
    val buying = trades.selling(buyCommodityId).buying(sellCommodityId).sortedByDescending { it.price }.toMutableList()

    while (buying.isNotEmpty() && selling.isNotEmpty() && buying[0].price >= selling[0].price) {
        var sell = selling[0]
        var buy = buying[0]
        val sellerPrice = sell.price
        val buyerPrice = 1L / buy.price

        val sellAmount = sell.sellAmount
        val buyAmount = (buy.sellAmount * buy.price).toLong()
        val amount = Math.min(sellAmount, buyAmount)
        val payAmount = (sellerPrice * amount).toLong()
        val price = payAmount.toDouble() / amount

        if (price >= sellerPrice && price <= buyerPrice) {
            val trade = Trade(
                sellingBusinessId = sell.sellingBusinessId,
                buyingBusinessId = buy.sellingBusinessId,
                stationId = sell.stationId,
                sellCommodityId = sellCommodityId,
                buyCommodityId = buyCommodityId,
                price = price.toRational(),
                sellAmount = amount
            ).also { Registry.add(it) }

            println("Trade, amount ${trade.sellAmount}, price ${trade.price}")
            closeTrade(trade)

            if (buy.sellAmount == payAmount) {
                buying.removeAt(0)
                Registry.delete(buy)
            } else {
                buy = buy.copy(sellAmount = buy.sellAmount - payAmount)
                buying[0] = buy
                Registry.update(buy)
            }

            if (sell.sellAmount == amount) {
                selling.removeAt(0)
                Registry.delete(sell)
            } else {
                sell = sell.copy(sellAmount = sell.sellAmount - amount)
                selling[0] = sell
                Registry.update(sell)
            }
        }
    }
}

private fun closeTrade(trade: Trade) {
    if (trade.isActive) {
        throw IllegalStateException("Tried to close trade with no buyer")
    }

    val sellerToStock = trade.sellingBusiness.buyStock
    val buyerToStock =
        trade.buyingBusiness?.buyStock ?: throw IllegalStateException("Tried to close trade with no buyer stocks")

    val sellAmount = trade.sellAmount
    val buyAmount = (trade.sellAmount * trade.price).toLong()

    Registry.update(buyerToStock.copy(amount = buyerToStock.amount + sellAmount))
    Registry.update(sellerToStock.copy(amount = sellerToStock.amount + buyAmount))
}

private fun timeoutTrades() {
    Registry.trades
        .active()
        .timedOut()
        .forEach(::timeoutTrade)
}

private fun timeoutTrade(trade: Trade) {
    if (trade.isClosed) {
        println("ERROR: Timed out trade that was already closed")
        return
    }

    val amount = trade.sellAmount
    val stock = trade.sellingBusiness.sellStocks.withCommodity(trade.sellCommodityId).first()
    Registry.update(stock.copy(amount = stock.amount + amount))
    Registry.delete(trade)
}

package controller

import model.Station
import model.Trade
import model.isActive
import model.isClosed

fun runTradeStep() {
    trade()
    timeoutTrades()
}

private fun trade() {
    Registry.stations.forEach { tradeAtStation(it) }
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

    while (buying.isNotEmpty() && selling.isNotEmpty() && 1.0 / buying[0].price >= selling[0].price) {
        var sell = selling[0]
        var buy = buying[0]
        val sellerPrice = sell.price
        val buyerPrice = 1.0 / buy.price

        val canSellAmount = sell.sellAmount // Seller can sell this many or less
        val canPayAmount = buy.sellAmount // This is how many the buyer can pay with
        val wantToBuyAmount = Math.ceil(buy.sellAmount * buy.price).toLong() // Buyer wants this many or more
        val amount = Math.min(canSellAmount, wantToBuyAmount) // This is the amount we can work with
        val maxPayAmount = Math.min(canPayAmount, (buyerPrice * amount).toLong()) // Buyer will pay this many or less
        val price = maxPayAmount.toDouble() / amount

        if (price in sellerPrice..buyerPrice) {
            val trade = Trade(
                sellingBusinessId = sell.sellingBusinessId,
                buyingBusinessId = buy.sellingBusinessId,
                stationId = sell.stationId,
                sellCommodityId = sellCommodityId,
                buyCommodityId = buyCommodityId,
                sellAmount = amount,
                buyAmount = maxPayAmount
            ).also { Registry.add(it) }

            println("Trading ${trade.sellAmount} ${trade.sellCommodity.abbrev} for ${trade.buyAmount} ${trade.buyCommodity.abbrev}, price ${trade.price}")
            closeTrade(trade)

            if (buy.sellAmount == maxPayAmount) {
                buying.removeAt(0)
                Registry.delete(buy)
            } else {
                buy = buy.copy(sellAmount = buy.sellAmount - maxPayAmount)
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
        } else if (canSellAmount < wantToBuyAmount) {
            selling.removeAt(0)
        } else {
            buying.removeAt(0)
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

    Registry.update(buyerToStock.copy(amount = buyerToStock.amount + trade.sellAmount))
    Registry.update(sellerToStock.copy(amount = sellerToStock.amount + trade.buyAmount))
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

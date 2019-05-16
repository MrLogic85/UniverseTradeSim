package com.sleepyduckstudio.controller

import com.sleepyduckstudio.model.*

internal lateinit var registry: Registry

abstract class Registry {

    companion object {
        fun setImplementation(reg: Registry) {
            registry = reg
        }
    }

    fun commodity(id: String): Commodity = commodities(withId(id)).first()
    fun entity(id: String): Entity = entities(withId(id)).first()
    fun business(id: String): Business = businesses(withId(id)).first()
    fun productionUnit(id: String): ProductionUnit = productionUnits(withId(id)).first()
    fun station(id: String): Station = stations(withId(id)).first()
    fun stock(id: String): Stock = stockpile(withId(id)).first()
    fun trade(id: String): Trade = trades(withId(id)).first()

    abstract fun commodities(vararg matchers: Matcher<Commodity>): List<Commodity>
    abstract fun entities(vararg matchers: Matcher<Entity>): List<Entity>
    abstract fun businesses(vararg matchers: Matcher<Business>): List<Business>
    abstract fun productionUnits(vararg matchers: Matcher<ProductionUnit>): List<ProductionUnit>
    abstract fun stations(vararg matchers: Matcher<Station>): List<Station>
    abstract fun stockpile(vararg matchers: Matcher<Stock>): List<Stock>
    abstract fun trades(vararg matchers: Matcher<Trade>): List<Trade>

    abstract fun add(obj: RegistryObject)
    abstract fun deleteCommodity(id: String): Commodity?
    abstract fun deleteEntity(id: String): Entity?
    abstract fun deleteBusiness(id: String): Business?
    abstract fun deleteProductionUnit(id: String): ProductionUnit?
    abstract fun deleteStation(id: String): Station?
    abstract fun deleteStock(id: String): Stock?
    abstract fun deleteTrade(id: String): Trade?
    abstract fun delete(obj: RegistryObject): RegistryObject?

    fun addAll(vararg objects: RegistryObject) = objects.forEach(::add)
}

fun <T : RegistryObject> T.update() {
    registry.add(this)
}

fun <T : RegistryObject> T.delete() {
    registry.add(this)
}

val Business.sellStocks: List<Stock> get() = sellStockIds.map(registry::stock)
val Business.buyStock: Stock get() = registry.stock(buyStockId)

val Stock.commodity: Commodity get() = registry.commodity(commodityId)

val Trade.sellingBusiness: Business get() = registry.business(sellingBusinessId)
val Trade.buyingBusiness: Business? get() = buyingBusinessId?.let(registry::business)
val Trade.sellCommodity: Commodity get() = registry.commodity(sellCommodityId)
val Trade.buyCommodity: Commodity get() = registry.commodity(buyCommodityId)

package com.sleepyduckstudio

import com.sleepyduckstudio.controller.Matcher
import com.sleepyduckstudio.controller.Registry
import com.sleepyduckstudio.model.*

class RegistryDataStore : Registry() {
    private val commodityMap = mutableMapOf<String, Commodity>()
    private val entityMap = mutableMapOf<String, Entity>()
    private val businessMap = mutableMapOf<String, Business>()
    private val productionUnitMap = mutableMapOf<String, ProductionUnit>()
    private val stationMap = mutableMapOf<String, Station>()
    private val stockMap = mutableMapOf<String, Stock>()
    private val tradeMap = mutableMapOf<String, Trade>()

    private val commodities: Sequence<Commodity> get() = commodityMap.values.asSequence()
    private val entities: Sequence<Entity> get() = entityMap.values.asSequence()
    private val businesses: Sequence<Business> get() = businessMap.values.asSequence()
    private val productionUnits: Sequence<ProductionUnit> get() = productionUnitMap.values.asSequence()
    private val stations: Sequence<Station> get() = stationMap.values.asSequence()
    private val stockpile: Sequence<Stock> = stockMap.values.asSequence()
    private val trades: Sequence<Trade> get() = tradeMap.values.asSequence()

    override fun add(obj: RegistryObject) = when (obj) {
        is Commodity -> commodityMap[obj.id] = obj
        is Entity -> entityMap[obj.id] = obj
        is Business -> businessMap[obj.id] = obj
        is ProductionUnit -> productionUnitMap[obj.id] = obj
        is Station -> stationMap[obj.id] = obj
        is Stock -> stockMap[obj.id] = obj
        is Trade -> tradeMap[obj.id] = obj
    }

    override fun deleteCommodity(id: String) = commodityMap.remove(id)
    override fun deleteEntity(id: String) = entityMap.remove(id)
    override fun deleteBusiness(id: String) = businessMap.remove(id)
    override fun deleteProductionUnit(id: String) = productionUnitMap.remove(id)
    override fun deleteStation(id: String) = stationMap.remove(id)
    override fun deleteStock(id: String) = stockMap.remove(id)
    override fun deleteTrade(id: String) = tradeMap.remove(id)

    override fun delete(obj: RegistryObject) = when (obj) {
        is Commodity -> commodityMap.remove(obj.id)
        is Entity -> entityMap.remove(obj.id)
        is Business -> businessMap.remove(obj.id)
        is ProductionUnit -> productionUnitMap.remove(obj.id)
        is Station -> stationMap.remove(obj.id)
        is Stock -> stockMap.remove(obj.id)
        is Trade -> tradeMap.remove(obj.id)
    }

    private fun <T : RegistryObject> match(matchers: List<Matcher<T>>, entities: Sequence<T>): List<T> =
        matchers.fold(entities) { ents, matcher -> ents.filter(matcher) }
            .toList()

    override fun commodities(vararg matchers: Matcher<Commodity>): List<Commodity> =
        match(matchers.toList(), commodities)

    override fun entities(vararg matchers: Matcher<Entity>): List<Entity> = match(matchers.toList(), entities)

    override fun businesses(vararg matchers: Matcher<Business>): List<Business> = match(matchers.toList(), businesses)

    override fun productionUnits(vararg matchers: Matcher<ProductionUnit>): List<ProductionUnit> =
        match(matchers.toList(), productionUnits)

    override fun stations(vararg matchers: Matcher<Station>): List<Station> = match(matchers.toList(), stations)

    override fun stockpile(vararg matchers: Matcher<Stock>): List<Stock> = match(matchers.toList(), stockpile)

    override fun trades(vararg matchers: Matcher<Trade>): List<Trade> = match(matchers.toList(), trades)
}
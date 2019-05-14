package controller

import model.*

object Registry {
    private var commodityMap = mapOf<String, Commodity>()
    private var entityMap = mapOf<String, Entity>()
    private var businessMap = mapOf<String, Business>()
    private var productionUnitMap = mapOf<String, ProductionUnit>()
    private var stationMap = mapOf<String, Station>()
    private var stockMap = mapOf<String, Stock>()
    private var tradeMap = mapOf<String, Trade>()

    val commodities get() = commodityMap.values.asSequence()
    val entities get() = entityMap.values.asSequence()
    val businesses get() = businessMap.values.asSequence()
    val productionUnits get() = productionUnitMap.values.asSequence()
    val stations get() = stationMap.values.asSequence()
    val stockpile = stockMap.values.asSequence()
    val trades get() = tradeMap.values.asSequence()

    fun addAll(vararg obj: RegistryObject) {
        obj.forEach(::add)
    }

    fun <T : RegistryObject> add(t: T) {
        update(t)
    }

    fun <T : RegistryObject> update(t: T) {
        when (t) {
            is Commodity -> commodityMap = commodityMap + (t.id to t as Commodity)
            is Entity -> entityMap = entityMap + (t.id to t as Entity)
            is Business -> businessMap = businessMap + (t.id to t as Business)
            is ProductionUnit -> productionUnitMap = productionUnitMap + (t.id to t as ProductionUnit)
            is Station -> stationMap = stationMap + (t.id to t as Station)
            is Stock -> stockMap = stockMap + (t.id to t as Stock)
            is Trade -> tradeMap = tradeMap + (t.id to t as Trade)
        }
    }

    fun <T : RegistryObject> delete(t: T) {
        when (t) {
            is Commodity -> commodityMap = commodityMap - t.id
            is Entity -> entityMap = entityMap - t.id
            is Business -> businessMap = businessMap - t.id
            is ProductionUnit -> productionUnitMap = productionUnitMap - t.id
            is Station -> stationMap = stationMap - t.id
            is Stock -> stockMap = stockMap - t.id
            is Trade -> tradeMap = tradeMap - t.id
        }
    }

    fun getCommodity(id: String): Commodity = commodityMap[id] ?: throw NoSuchElementException()
    fun getEntity(id: String): Entity = entityMap[id] ?: throw NoSuchElementException()
    fun getBusiness(id: String): Business = businessMap[id] ?: throw NoSuchElementException()
    fun getProduction(id: String): ProductionUnit = productionUnitMap[id] ?: throw NoSuchElementException()
    fun getStation(id: String): Station = stationMap[id] ?: throw NoSuchElementException()
    fun getStock(id: String): Stock = stockMap[id] ?: throw NoSuchElementException()
    fun getTrade(id: String): Trade = tradeMap[id] ?: throw NoSuchElementException()

    inline operator fun <reified T : RegistryObject> get(id: String): T = when (T::class) {
        Commodity::class -> getCommodity(id) as T
        Entity::class -> getEntity(id) as T
        Business::class -> getBusiness(id) as T
        ProductionUnit::class -> getProduction(id) as T
        Station::class -> getStation(id) as T
        Stock::class -> getStock(id) as T
        Trade::class -> getTrade(id) as T
        else -> throw NoSuchElementException("Failed to verify class of id")
    }
}

fun <T : RegistryObject> Sequence<T>.update(function: (T) -> T) {
    forEach { Registry.update(function(it)) }
}

fun Sequence<Stock>.withCommodity(id: String): Sequence<Stock> = filter { it.commodityId == id }

fun Sequence<Trade>.active(): Sequence<Trade> = filter { it.isActive }
fun Sequence<Trade>.selling(id: String): Sequence<Trade> = filter { it.sellCommodityId == id }
fun Sequence<Trade>.buying(id: String): Sequence<Trade> = filter { it.buyCommodityId == id }
fun Sequence<Trade>.atStation(id: String): Sequence<Trade> = filter { it.stationId == id }
fun Sequence<Trade>.timedOut(): Sequence<Trade> = filter { it.timestamp + it.tradeLength < System.currentTimeMillis() }

val Business.sellStocks: Sequence<Stock> get() = sellStockIds.asSequence().map { Registry.getStock(it) }
val Business.buyStock: Stock get() = Registry[buyStockId]

val Stock.commodity: Commodity get() = Registry[commodityId]

val Trade.sellingBusiness: Business get() = Registry[sellingBusinessId]
val Trade.buyingBusiness: Business? get() = buyingBusinessId?.let(Registry::get)
val Trade.sellCommodity: Commodity get() = Registry[sellCommodityId]
val Trade.buyCommodity: Commodity get() = Registry[buyCommodityId]

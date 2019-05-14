package controller

import model.*

object Registry {
    private val commodityMap = mutableMapOf<String, Commodity>()
    private val entityMap = mutableMapOf<String, Entity>()
    private val businessMap = mutableMapOf<String, Business>()
    private val productionUnitMap = mutableMapOf<String, ProductionUnit>()
    private val stationMap = mutableMapOf<String, Station>()
    private val stockMap = mutableMapOf<String, Stock>()
    private val tradeMap = mutableMapOf<String, Trade>()

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
            is Commodity -> commodityMap[t.id] = t
            is Entity -> entityMap[t.id] = t
            is Business -> businessMap[t.id] = t
            is ProductionUnit -> productionUnitMap[t.id] = t
            is Station -> stationMap[t.id] = t
            is Stock -> stockMap[t.id] = t
            is Trade -> tradeMap[t.id] = t
        }
    }

    fun <T : RegistryObject> delete(t: T) {
        when (t) {
            is Commodity -> commodityMap.remove(t.id)
            is Entity -> entityMap.remove(t.id)
            is Business -> businessMap.remove(t.id)
            is ProductionUnit -> productionUnitMap.remove(t.id)
            is Station -> stationMap.remove(t.id)
            is Stock -> stockMap.remove(t.id)
            is Trade -> tradeMap.remove(t.id)
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

fun RegistryObject.delete() {
    Registry.delete(this)
}

fun <T : RegistryObject> Sequence<T>.update(function: (T) -> T) {
    forEach { Registry.update(function(it)) }
}

fun Sequence<Stock>.withCommodity(id: String): Sequence<Stock> = filter { it.commodityId == id }

fun Sequence<Trade>.active(): Sequence<Trade> = filter { it.isActive }
fun Sequence<Trade>.withBuyer(id: String): Sequence<Trade> = filter { it.buyingBusiness?.entityId == id }
fun Sequence<Trade>.selling(id: String): Sequence<Trade> = filter { it.sellCommodityId == id }
fun Sequence<Trade>.buying(id: String): Sequence<Trade> = filter { it.buyCommodityId == id }
fun Sequence<Trade>.atStation(id: String): Sequence<Trade> = filter { it.stationId == id }
fun Sequence<Trade>.timedOut(): Sequence<Trade> = filter { it.timestamp + it.tradeLength < System.currentTimeMillis() }

val Business.sellStocks: Sequence<Stock> get() = sellStockIds.asSequence().map { Registry.getStock(it) }
val Business.buyStock: Stock get() = Registry[buyStockId]

val Entity.purchases: Sequence<Trade> get() = Registry.trades.withBuyer(id)
val Entity.stockpile: Sequence<Stock> get() = Registry.stockpile.filter { it.entityId == id }

val ProductionUnit.stock: Stock get() = Registry[stockId]
val ProductionUnit.entity: Entity get() = stock.entity

val Stock.entity: Entity get() = Registry[entityId]
val Stock.commodity: Commodity get() = Registry[commodityId]

val Trade.sellingBusiness: Business get() = Registry[sellingBusinessId]
val Trade.buyingBusiness: Business? get() = buyingBusinessId?.let(Registry::get)

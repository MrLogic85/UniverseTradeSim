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

    val commodities: Collection<Commodity> get() = commodityMap.values
    val entities: Collection<Entity> get() = entityMap.values
    val businesses: Collection<Business> get() = businessMap.values
    val productionUnits: Collection<ProductionUnit> get() = productionUnitMap.values
    val stations: Collection<Station> get() = stationMap.values
    val stockpile: Collection<Stock> = stockMap.values
    val activeTrades: List<Trade> get() = tradeMap.asSequence().map { it.value }.filter { it.isActive }.toList()
    val closedTrades: List<Trade> get() = tradeMap.asSequence().map { it.value }.filter { it.isClosed }.toList()

    fun addAll(vararg obj: RegistryObject) {
        obj.forEach(::add)
    }

    fun <T : RegistryObject> add(t: T) {
        update(t)
    }

    fun <T : RegistryObject> update(t: T) {
        when (t) {
            is Commodity -> commodityMap[t.id] = t as Commodity
            is Entity -> entityMap[t.id] = t as Entity
            is Business -> businessMap[t.id] = t as Business
            is ProductionUnit -> productionUnitMap[t.id] = t as ProductionUnit
            is Station -> stationMap[t.id] = t as Station
            is Stock -> stockMap[t.id] = t as Stock
            is Trade -> tradeMap[t.id] = t as Trade
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

fun <T : RegistryObject> Collection<T>.update(function: (T) -> T) {
    forEach { Registry.update(function(it)) }
}

fun Iterable<Stock>.withCommodity(id: String): Collection<Stock> = filter { it.commodityId == id }

fun Iterable<Trade>.selling(id: String): List<Trade> = filter { it.sellCommodityId == id }
fun Iterable<Trade>.buying(id: String): List<Trade> = filter { it.buyCommodityId == id }
fun Iterable<Trade>.atStation(id: String): List<Trade> = filter { it.stationId == id }
fun Iterable<Trade>.timedOut(): List<Trade> = filter { it.timestamp + it.tradeLength < System.currentTimeMillis() }

val Business.sellStocks: List<Stock> get() = sellStockIds.map { Registry.getStock(it) }
val Business.buyStock: Stock get() = Registry[buyStockId]

val Stock.commodity: Commodity get() = Registry[commodityId]

val Trade.sellingBusiness: Business get() = Registry[sellingBusinessId]
val Trade.buyingBusiness: Business? get() = buyingBusinessId?.let(Registry::get)
val Trade.sellCommodity: Commodity get() = Registry[sellCommodityId]
val Trade.buyCommodity: Commodity get() = Registry[buyCommodityId]

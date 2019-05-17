package com.sleepyduckstudio.controller

import com.sleepyduckstudio.model.*

typealias Matcher<T> = (T) -> Boolean

fun <T : RegistryObject> Iterable<T>.matches(vararg matchers: Matcher<T>): List<T> =
    matchers.fold(this) { objects, matcher ->
        objects.filter(matcher)
    }.toList()

fun withId(id: String) = { obj: RegistryObject ->
    when (obj) {
        is Commodity -> obj.id == id
        is Entity -> obj.id == id
        is Business -> obj.id == id
        is ProductionUnit -> obj.id == id
        is Station -> obj.id == id
        is Stock -> obj.id == id
        is Trade -> obj.id == id
    }
}

fun atStation(id: String) = { obj: RegistryObject ->
    when (obj) {
        is Commodity -> false
        is Entity -> obj.stationId == id
        is Business -> obj.stationId == id
        is ProductionUnit -> false
        is Station -> false
        is Stock -> obj.stationId == id
        is Trade -> obj.stationId == id
    }
}

fun isActive() = { obj: Trade -> obj.isActive }
fun isTimedOut() = { obj: Trade -> obj.timestamp + obj.tradeLength < System.currentTimeMillis() }
fun isSelling(commodityId: String) = { obj: Trade -> obj.sellCommodityId == commodityId }
fun isBuying(commodityId: String) = { obj: Trade -> obj.buyCommodityId == commodityId }

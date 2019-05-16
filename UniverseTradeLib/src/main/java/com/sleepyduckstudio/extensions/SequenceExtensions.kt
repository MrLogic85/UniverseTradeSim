package com.sleepyduckstudio.extensions

inline fun <T> List<T>.sumBy(selector: (T) -> Long): Long = fold(0L) { total, element -> total + selector(element) }
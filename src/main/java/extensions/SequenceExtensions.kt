package extensions

inline fun <T> Sequence<T>.sumBy(selector: (T) -> Long): Long = fold(0L) { total, element -> total + selector(element) }
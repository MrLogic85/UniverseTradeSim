package model

class Rational(
    num: Long,
    den: Long = 1
) : Comparable<Rational> {
    val num: Long
    val den: Long

    init {
        var n = num
        var d = den
        while (n % 2L == 0L && d % 2L == 0L) {
            n /= 2L
            d /= 2L
        }
        while (n % 5L == 0L && d % 5L == 0L) {
            n /= 5L
            d /= 5L
        }
        this.num = n
        this.den = n
    }

    override operator fun compareTo(other: Rational): Int = (this.num * other.den).compareTo(other.num * this.den)
    operator fun compareTo(value: Long): Int = (num * den).compareTo(value * den)
    operator fun compareTo(value: Double): Int = toDouble().compareTo(value)

    operator fun plus(value: Long): Rational = Rational(num + den * value, den)
    operator fun times(double: Double): Rational = (toDouble() * double).toRational()
    operator fun times(value: Long): Rational = Rational(num * value, den)

    override fun toString() = "$num/$den"

    override fun equals(other: Any?): Boolean = when {
        other == null -> false
        other !is Rational -> false
        this.compareTo(other) == 0 -> true
        else -> false
    }

    override fun hashCode(): Int {
        var result = num.hashCode()
        result = 31 * result + den.hashCode()
        return result
    }
}


operator fun Double.compareTo(value: Rational): Int = -value.compareTo(this)

operator fun Long.div(rational: Rational): Rational = Rational(this * rational.den, rational.num)
operator fun Long.times(rational: Rational): Rational = Rational(this * rational.num, rational.den)

fun Rational.toDouble(): Double = num.toDouble() / den
fun Rational.toLong(): Long = toDouble().toLong()

fun Double.toRational(): Rational {
    val ds = toString().trimEnd('0').trimEnd('.')
    val index = ds.indexOf('.')
    if (index == -1) return Rational(ds.toLong(), 1L)
    val num = ds.replace(".", "").toLong()
    var den = 1L
    for (n in 1 until ds.length - index) den *= 10L
    return Rational(num, den)
}

fun Rational.limitValues(limit: Long): Rational = TODO("not implemented")

fun main() {
    val decimals = doubleArrayOf(0.9054054, 0.518518, 2.405308, .75, 0.0, -0.64, 123.0, -14.6)
    for (decimal in decimals)
        println("${decimal.toString().padEnd(9)} = ${decimal.toRational()}")
}
object Utils {
    fun <T> retry(
        times: Int,
        delay: Long,
        block: () -> T,
    ): T {
        var throwable: Throwable? = null
        for (i in 0..times) {
            try {
                return block()
            } catch (e: Throwable) {
                throwable = e
                Thread.sleep(delay)
            }
        }
        throw throwable!!
    }

    fun String.replaceIfExists(s1: String, s2: String): String {
        if (this.indexOf(s1) == -1) throw Exceptions.StringNotFoundException("$s1 not found in $this")
        return this.replace(s1, s2)
    }

    fun softAssert(message: String, block: () -> Boolean) {
        if (!block()) softAssertions.add(message)
    }

    val softAssertions: MutableList<String> = mutableListOf()

    fun Collection<String>.concat(char: Char): String {
        var str = ""
        this.forEach {
            str += it + char
        }
        return str.dropLast(1)
    }

    fun <T> onException(exceptionMessage: String, block: () -> T): T {
        try {
            return block()
        } catch (e: Throwable) {
            throw Exceptions.EasyRestException("$exceptionMessage\n${e.message}")
        }
    }

    fun MutableMap<String,Any>.putAllNotDuplicate(map: Map<String,Any>) {
        map.forEach{
            this[it.key] = it.value
        }
    }
}
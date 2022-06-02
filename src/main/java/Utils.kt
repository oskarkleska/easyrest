object Utils {
     fun <T> retry(
        times: Int = TestManager.getConfig().defaultRetryCount,
        delay: Long = TestManager.getConfig().defaultPollingDelay,
        block: () -> T): T
    {
        for(i  in 0..times){
            try {
                return block()
            } catch (e: Throwable) {
                // TODO: log.warn
                Thread.sleep(delay)
            }
        }
        return block()
    }

    fun String.replaceIfExists(s1: String, s2: String) : String {
        if(this.indexOf(s1) == -1) throw Exceptions.StringNotFoundException("$s1 not found in $this")
        return this.replace(s1,s2)
    }

    fun softAssert(message: String, block: () -> Boolean) {
        if(!block()) softAssertions.add(message)
    }

    val softAssertions:MutableList<String> = mutableListOf()
}
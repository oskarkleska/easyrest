object Utils {
     fun <T> retry(
        times: Int = 5,
        delay: Long = 500,
        block: () -> T): T
    {
        for(i  in 0..times){
            try {
                return block()
            } catch (e: Throwable) {
                println("EKSEPSZĄ: ${e.message}")
                Thread.sleep(delay)
            }
        }
        return block()
    }

    fun String.replaceIfExists(s1: String, s2: String) : String {
        if(this.indexOf(s1) == -1) throw Exceptions.StringNotFoundException("$s1 not found in $this")
        return this.replace(s1,s2)
    }
}
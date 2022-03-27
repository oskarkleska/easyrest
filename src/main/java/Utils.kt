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
                Thread.sleep(delay)
            }
        }
        return block()
    }
}
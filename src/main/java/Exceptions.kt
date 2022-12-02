object Exceptions {
    class NoParamsException(msg: String? = null) : RuntimeException(msg)
    class StringNotFoundException(msg: String? = null) : RuntimeException(msg)
    class EndpointNotCalledYetException(msg: String? = null) : RuntimeException(msg)
    class ArgumentNotFoundException(msg: String? = null) : RuntimeException(msg)

    class EasyRestException(msg: String? = null) : RuntimeException(msg)
}
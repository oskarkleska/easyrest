package tests.callcheckandcast

data class SimpleResponseForCasting(val id: String, val name: String, val number: Int)
data class WrongResponseForCasting(val ids: String, val name: String, val number: Int)
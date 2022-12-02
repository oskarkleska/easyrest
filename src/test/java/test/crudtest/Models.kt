package test.crudtest

data class RandomResource(val name: String, val count: Int, var isTrue: Boolean)

data class RandomResourceResponse(var name: String, var count: Int, var isTrue: Boolean, val _id: String)
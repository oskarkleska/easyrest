package test.crudtest

data class RandomResource(val name: String, val count: Int, val isTrue: Boolean)

data class RandomResourceResponse(val name: String, val count: Int, val isTrue: Boolean, val _id: String)
package test.crudtest

import E
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import src.CrudPost
import java.util.*

class ExampleTest {
    private val resource = UUID.randomUUID().toString()
    private lateinit var id: String

    //val get = E<RandomResource>(CrudGet()).setParamsForPath(mapOf("resource" to resource, "id" to id))
    private val post = E<RandomResourceResponse>(CrudPost())

    @Test
    @Order(1)
    fun createResource() {
        val resp = post.setBody(RandomResource("Something", 3, true)).ccc()
        this.id = resp._id
    }
}
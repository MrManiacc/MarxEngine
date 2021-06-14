package math

import marx.engine.math.*
import marx.engine.math.MathDSL.Extensions.via
import mu.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import kotlin.random.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VecTest {
    val log = KotlinLogging.logger { }
    val random = Random(69420)
    val randomFloat: Float get() = random.nextFloat() * 1000f
    val randomVec3: Vec3 get() = randomFloat via randomFloat via randomFloat

    /*This should be [467.5128f via 599.2213f via 510.64264f]*/
    val firstVector = randomVec3

    @Test fun `ensure random object and seed are consistent`() {
        assertEquals(firstVector, 467.5128f via 599.2213f via 510.64264f)
        assertNotEquals(firstVector, randomVec3)
    }

    @Test fun `test vector immutability`() {
        val vec3 = 3 via 5 via 6
    }

}
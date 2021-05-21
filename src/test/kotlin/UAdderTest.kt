import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class UAdderTest {

    @ExperimentalUnsignedTypes
    @Test
    fun getBinary32() {
        val width = 32
        val adder = UAdder(width)
        assertEquals(width, adder.binary.size)
        adder.setupAdd(1U, 1U)
        assertEquals(List(width){AdderType.Bit.Zero}.mapIndexed { i, bit ->
            if(i == 1) AdderType.Bit.One else AdderType.Bit.Zero
        }.reversed(), adder.binary)
    }

    @ExperimentalUnsignedTypes
    @Test
    fun getBinary1() {
        val width = 1
        val adder = UAdder(width)
        assertEquals(width, adder.binary.size)
        adder.setupAdd(1U, 0U)
        assertEquals(List(width){AdderType.Bit.Zero}.mapIndexed { i, bit ->
            if(i == 0) AdderType.Bit.One else AdderType.Bit.Zero
        }.reversed(), adder.binary)
    }

    @Test
    fun reset() {
    }

    @Test
    fun setupAdd() {
    }

    @Test
    fun add() {
    }

    @Test
    fun repFromBit() {
    }

    @Test
    fun bitFromRep() {
    }
}

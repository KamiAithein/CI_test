import AdderType.Bit
interface Reset {
    fun reset()
}
interface Add<T> {
    fun add(a1: T, a2: T): T
}
interface AdderType<T> : Add<T> {
    enum class Bit {
        Zero,
        One,
        ;
    }
    fun repFromBit(b: Bit): T
    val Bit.rep: T
    fun bitFromRep(rep: T): Bit
    fun setupAdd(a1: T, a2: T)
    val binary: List<Bit>
}
@ExperimentalUnsignedTypes
class UAdder(private val width: Int) : Reset, AdderType<UInt> {

    override val Bit.rep
        get() = repFromBit(this)

    inner class AdderUnit(val prev: AdderUnit?) : Reset {
        var input_l:   Bit   = Bit.Zero
        var input_r:   Bit   = Bit.Zero
        val carry_in:  Bit
            get() = if(prev?.carry_out == null) Bit.Zero else prev.carry_out
        val carry_out: Bit
            get() = if((input_l == Bit.One && input_r == Bit.One) ||
                    ((input_l == Bit.One || input_r == Bit.One) && carry_in == Bit.One)) Bit.One else Bit.Zero
        val output:    Bit
            get() = bitFromRep((input_l.rep).xor(input_r.rep).xor(carry_in.rep))

        override fun reset() {
            input_l = Bit.Zero
            input_r = Bit.Zero
        }
    }
    private val adders: List<AdderUnit> = {
        val temp = mutableListOf<AdderUnit>()
        for(i in 0 until width) {
            temp.add(AdderUnit(if(i != 0) temp[i - 1] else null)) //chain together from right to left
        }
        temp.toList()
    }.invoke()
    override val binary: List<Bit>
        get() = {
            val temp = mutableListOf<Bit>()
            adders.forEach {temp.add(it.output)}
            temp.toList().reversed()
        }.invoke()

    override fun reset() {
        adders.forEach(AdderUnit::reset)
    }
    override fun setupAdd(a1: UInt, a2: UInt) {
        var i = 0
        var a1_c = a1 //a1_counter
        var a2_c = a2 //a2_counter
        while(a1_c != 0U || a2_c != 0U) {
            if(i >= adders.size) throw IllegalStateException("Overflow!")
            adders[i].input_l = if(a1_c % 2U == 0U) Bit.Zero else Bit.One
            adders[i].input_r = if(a2_c % 2U == 0U) Bit.Zero else Bit.One

            a1_c = a1_c.shr(1)
            a2_c = a2_c.shr(1)
            i++
        }
    }
    override fun add(a1: UInt, a2: UInt): UInt {
        reset()
        setupAdd(a1, a2)
        val binary = this.binary
        return Integer.parseUnsignedInt((binary.joinToString(separator=""){""+it.rep}), 2).toUInt()
    }

    override fun repFromBit(b: Bit): UInt {
        return when(b) {
            Bit.Zero -> 0U
            Bit.One -> 1U
        }
    }

    override fun bitFromRep(rep: UInt): Bit {
        return when(rep) {
            0U -> Bit.Zero
            1U -> Bit.One
            else -> throw IllegalArgumentException("$rep out of bounds of Z in [0,1]!")
        }
    }
}
interface Reset {
    fun reset()
}
interface Add<T> {
    fun add(a1: T, a2: T): T
}
class Adder(private val width: Int) : Reset, Add<UInt> {
    enum class Bit(val rep: UInt) {
        Zero(0U),
        One(1U),
        ;
        companion object {
            fun fromRep(rep: UInt): Bit =
                when(rep) {
                    0U -> Zero
                    1U -> One
                    else -> throw IllegalArgumentException("${rep} out of bounds Z in [0,1]")
                }
        }

    }
    class AdderUnit(val prev: AdderUnit?) : Reset {
        var input_l:   Bit   = Bit.Zero
        var input_r:   Bit   = Bit.Zero
        val carry_in:  Bit
            get() = if(prev?.carry_out == null) Bit.Zero else prev.carry_out
        val carry_out: Bit
            get() = if((input_l == Bit.One && input_r == Bit.One) ||
                    ((input_l == Bit.One || input_r == Bit.One) && carry_in == Bit.One)) Bit.One else Bit.Zero
        val output:    Bit
            get() = Bit.fromRep(input_l.rep.xor(input_r.rep).xor(carry_in.rep))

        override fun reset() {
            input_l = Bit.Zero
            input_r = Bit.Zero
        }
    }
    val adders: List<AdderUnit> = {
        val temp = mutableListOf<AdderUnit>()
        for(i in 0..width) {
            temp.add(AdderUnit(if(i != 0) temp[i - 1] else null)) //chain together from right to left
        }
        temp.toList()
    }.invoke()
    val binary: List<Bit>
        get() = {
            val temp = mutableListOf<Bit>()
            adders.forEach {temp.add(it.output)}
            temp.toList().reversed()
        }.invoke()

    override fun reset() {
        adders.forEach(AdderUnit::reset)
    }
    fun setupAdd(a1: UInt, a2: UInt) {
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
}
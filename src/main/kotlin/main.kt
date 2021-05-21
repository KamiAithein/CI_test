fun main(args: Array<String>) {
    try {
        println(UAdder(32).add(args[0].toInt().toUInt(), args[1].toInt().toUInt()))
    } catch(nfe: NumberFormatException) {
        println("invalid params ${args.joinToString()}")
    }
}
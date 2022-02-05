package machine

fun main() {
    val machine = Machine()
    shop(machine)
}

private fun shop(machine: Machine) {
    var machineChange = machine
    while (true) {
        println("Write action (buy, fill, take, remaining, exit):")
        val action = readLine()!!
        when (action) {
            "buy" -> {
                println(
                    "What do you want to buy? " +
                            "1 - espresso, 2 - latte, 3 - cappuccino, back - to main menu:"
                )
                val type = readLine()!!
                val coffee: Coffee = when (type) {
                    "1" -> Espresso()
                    "2" -> Latte()
                    "3" -> Cappuccino()
                    "back" -> continue
                    else -> throw Exception("Invalid type")
                }
                try {
                    machineChange = coffee.buyFrom(machineChange)
                    println("I have enough resources, making you a coffee!")
                } catch (e: Exception) {
                    println(e.message)
                }
            }
            "fill" -> {
                println("Write how many ml of water do you want to add:")
                val water = readLine()!!.toInt()
                println("Write how many ml of milk do you want to add:")
                val milk = readLine()!!.toInt()
                println("Write how many grams of coffee beans do you want to add:")
                val beans = readLine()!!.toInt()
                println("Write how many disposable cups of coffee do you want to add:")
                val cups = readLine()!!.toInt()
                val filler = Filler(water, milk, beans, cups)
                machineChange = machineChange.fill(filler)
            }
            "take" -> {
                println("I gave you \$${machineChange.money}")
                machineChange = machineChange.getMoney()
            }
            "remaining" -> {
                println(machineChange.getIngredients())
            }
            "exit" -> {
                break
            }
            else -> throw Exception("Invalid action")
        }
    }

}

abstract class Coffee(
    open val waterNeed: Int = 0,
    open val milkNeed: Int = 0,
    open val beansNeed: Int = 0,
    open val cost: Int = 0
) {
    abstract fun buyFrom(machine: Machine): Machine
}

class Cappuccino : Coffee() {
    override val waterNeed = 200
    override val milkNeed = 100
    override val beansNeed = 12
    override val cost = 6
    override fun buyFrom(machine: Machine): Machine {
        return Machine(
            machine.water - waterNeed,
            machine.milk - milkNeed,
            machine.beans - beansNeed,
            machine.money + cost,
            machine.disposableCups - 1
        )
    }
}

class Response(val text: String, val value: Boolean)

class Latte : Coffee() {
    override val waterNeed = 350
    override val milkNeed = 75
    override val beansNeed = 20
    override val cost = 7

    override fun buyFrom(machine: Machine): Machine {
        val response = Checker.canIBuyCoffee(this, machine)
        if (response.value) {
            return Machine(
                machine.water - waterNeed,
                machine.milk - milkNeed,
                machine.beans - beansNeed,
                machine.money + cost,
                machine.disposableCups - 1
            )
        }
        throw Exception(response.text)
    }
}

class Checker {
    companion object {
        fun canIBuyCoffee(coffee: Coffee, machine: Machine): Response {
            if (machine.water - coffee.waterNeed < 0) {
                return Response("Sorry, not enough water!", false)
            } else if (machine.milk - coffee.milkNeed < 0) {
                return Response("Sorry, not enough milk", false)
            }
            return Response("", true)
        }
    }
}

class Espresso : Coffee() {
    override val waterNeed = 250
    override val beansNeed = 16
    override val cost = 4
    override fun buyFrom(machine: Machine): Machine {
        return Machine(
            machine.water - waterNeed,
            milk = machine.milk,
            machine.beans - beansNeed,
            machine.money + cost,
            machine.disposableCups - 1
        )
    }
}

data class Filler(
    val water: Int,
    val milk: Int,
    val beans: Int,
    val disposableCups: Int
)

data class Machine(
    val water: Int = 400,
    val milk: Int = 540,
    val beans: Int = 120,
    val money: Int = 550,
    val disposableCups: Int = 9
) {

    fun getMoney(): Machine {
        return Machine(
            this.water,
            this.milk,
            this.beans,
            0,
            this.disposableCups
        )
    }

    fun getIngredients(): String {
        val ingredients = "The coffee machine has:\n" +
                "$water of water\n" +
                "$milk of milk\n" +
                "$beans of coffee beans\n" +
                "$disposableCups of disposable cups\n" +
                "$money of money"
        return ingredients
    }

    fun fill(filler: Filler): Machine {
        return Machine(
            this.water + filler.water,
            this.milk + filler.milk,
            this.beans + filler.beans,
            this.money,
            this.disposableCups + filler.disposableCups
        )
    }
}

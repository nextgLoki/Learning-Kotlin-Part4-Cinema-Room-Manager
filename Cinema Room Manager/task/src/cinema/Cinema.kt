package cinema

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.roundToLong

fun main() {
    val rowsNumber = requestNumber("Enter the number of rows:")
    val seatsNumber = requestNumber("Enter the number of seats in each row:")
    val seats = generateEmptySeats(rowsNumber, seatsNumber)
    do {
        printCommands()
        when (readLine()!!) {
            "0" -> return
            "1" -> printSeats(seats)
            "2" -> requestBuyTicket(seats)
            "3" -> printStatistics(seats)
        }
    } while (true)
}

fun printCommands() = println(
    """
    1. Show the seats
    2. Buy a ticket
    3. Statistics
    0. Exit
""".trimIndent()
)

fun requestNumber(message: String): Int {
    println(message)
    return readLine()!!.toInt()
}

fun generateEmptySeats(rows: Int, seatsInRow: Int) = Array(rows) { Array(seatsInRow) { "S" } }

fun printSeats(seats: Array<Array<String>>) {
    println("Cinema:\n  ${(1..seats.first().size).joinToString(" ")}")
    repeat(seats.size) {
        println("${it + 1} ${seats[it].joinToString(" ")}")
    }
}

fun requestBuyTicket(seats: Array<Array<String>>) {
    do {
        val rowNumber = requestNumber("Enter a row number:")
        val seatNumber = requestNumber("Enter a seat number in that row:")
        try {
            if (seats[rowNumber - 1][seatNumber - 1] != "B") {
                processTicket(seats, rowNumber, seatNumber)
                return
            } else {
                println("That ticket has already been purchased!")
            }
        } catch (e: Exception) {
            println("Wrong input!")
        }
    } while (true)
}

fun getPrice(row: Int, seats: Array<Array<String>>): Int {
    val rows = seats.size
    val seatsInRow = seats.first().size
    return when {
        rows * seatsInRow < 60 -> 10
        row <= rows / 2 -> 10
        else -> 8
    }
}

fun processTicket(seats: Array<Array<String>>, row: Int, seat: Int) {
    seats[row - 1][seat - 1] = "B"
    println("Ticket price: $${getPrice(row, seats)}")
}

fun getIncome(seats: Array<Array<String>>, filterSeats: String) = seats
    .flatMapIndexed { index, row -> row.map { seat -> Pair(index + 1, seat) }.toList() }
    .filter { it.second in filterSeats }
    .sumOf { seatPair -> getPrice(seatPair.first, seats) }


fun printStatistics(seats: Array<Array<String>>) {
    val flatSeats = seats.flatMap { it.toList() }
    val purchasesCount = flatSeats.count { it == "B" }
    val purchasesPercents = purchasesCount.toDouble() / flatSeats.size * 100
    println("""
        Number of purchased tickets: $purchasesCount
        Percentage: ${BigDecimal(purchasesPercents).setScale(2, RoundingMode.HALF_UP)}%
        Current income: $${getIncome(seats, "B")}
        Total income: $${getIncome(seats, "SB")}
    """.trimIndent())
}


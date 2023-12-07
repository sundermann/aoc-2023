enum class HandType : Comparable<HandType> {
    FIVE_OF_A_KIND,
    FOUR_OF_A_KIND,
    FULL_HOUSE,
    THREE_OF_A_KIND,
    TWO_PAIR,
    ONE_PAIR,
    HIGH_CARD
}

fun main() {
    data class Hand(val hand: String, val bid: Int): Comparable<Hand> {
        fun type(): HandType {
            val frequencies = this.hand.groupingBy { it }.eachCount().filterKeys { it != '1' } + ('0' to 0)
            val counts = frequencies.values
            val jokers = this.hand.count { it == '1' }

            return if (counts.any { it == 5 - jokers })
                HandType.FIVE_OF_A_KIND
            else if (counts.any { it == 4 - jokers })
                HandType.FOUR_OF_A_KIND
            else if ((counts.any { it == 3 } && counts.any { it == 2 }) ||
                (jokers == 1 && (counts.count { it == 2 } == 2 || counts.any { it == 3 })))
                HandType.FULL_HOUSE
            else if (counts.any { it == 3 - jokers })
                HandType.THREE_OF_A_KIND
            else if (counts.count { it == 2 } == 2 || (jokers == 1 && counts.any { it == 2 }))
                HandType.TWO_PAIR
            else if (counts.any { it == 2 - jokers })
                HandType.ONE_PAIR
            else HandType.HIGH_CARD
        }

        fun strongest() = Hand(hand.replace('J', '1'), bid)

        override operator fun compareTo(other: Hand): Int {
            val cardOrder = listOf('A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2', '1')
                .mapIndexed { i, it -> it to i }.toMap()

            val type = type()
            val otherType = other.type()

            if (type > otherType)
                return -1
            if (type < otherType)
                return 1

            return hand.zip(other.hand) { a, b -> if (cardOrder[a]!! < cardOrder[b]!!) 1 else if (a == b) 0 else -1 }
                .first { it != 0 }
        }
    }

    fun String.toHand() = split(' ').let { (hand, bid) -> Hand(hand, bid.toInt()) }

    fun part1(input: List<String>): Int {
        return input.map { it.toHand() }.sorted().mapIndexed { i, hand -> hand.bid * (i + 1) }.sum()
    }

    fun part2(input: List<String>): Int {
        return input.map { it.toHand().strongest() }.sorted().mapIndexed { i, hand -> hand.bid * (i + 1) }.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    check(part1(testInput) == 6440)
    check(part2(testInput) == 5905)

    val input = readInput("Day07")
    part1(input).println()
    part2(input).println()
}

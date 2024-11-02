package org.socratesbe.hearts2

interface Event

data class GameStarted(val players: Players) : Event

data class CardsDealt(
    val player1WithCards: PlayerWithCards,
    val player2WithCards: PlayerWithCards,
    val player3WithCards: PlayerWithCards,
    val player4WithCards: PlayerWithCards
) : Event {
    fun cardsForPlayer(player: Player): Set<Card> {
        return if (player1WithCards.player == player)
            player1WithCards.cards
        else if (player2WithCards.player == player)
            player2WithCards.cards
        else if (player3WithCards.player == player)
            player3WithCards.cards
        else player4WithCards.cards
    }

    val players: Players = Players(
        player1WithCards.player,
        player2WithCards.player,
        player3WithCards.player,
        player4WithCards.player
    )
    val allCards = player1WithCards.cards.toSet() +
            player2WithCards.cards.toSet() +
            player3WithCards.cards.toSet() +
            player4WithCards.cards.toSet()
}

data class CardPlayed(val player: Player, val card: Card) : Event

data class CardsPassed(
    val playerPassing: List<PlayerPassing>,
) : Event {
    fun byPlayer(player: Player) =
        playerPassing.first { it.from == player }

    fun toPlayer(player: Player) =
        playerPassing.first { it.to == player }

    data class PlayerPassing(
        val from: Player,
        val to: Player,
        val card1: Card,
        val card2: Card,
        val card3: Card,
    ) {
        fun cards() = setOf(card1, card2, card3)
    }
}
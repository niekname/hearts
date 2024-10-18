package org.socratesbe.hearts2

interface Event

data object GameStarted : Event
data class CardsDealt(
    val player1WithCards: PlayerWithCards,
    val player2WithCards: PlayerWithCards,
    val player3WithCards: PlayerWithCards,
    val player4WithCards: PlayerWithCards
) : Event {
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

package org.socratesbe.hearts2

interface Event

data class GameStarted(val players: Players) : Event

data class CardsDealt(
    val player1WithCards: PlayerWithCards,
    val player2WithCards: PlayerWithCards,
    val player3WithCards: PlayerWithCards,
    val player4WithCards: PlayerWithCards
) : Event {
    fun whoHasCard(card: Card): Player {
        return if (player1WithCards.hasCard(card))
            player1WithCards.player
        else if (player2WithCards.hasCard(card))
            player2WithCards.player
        else if (player3WithCards.hasCard(card))
            player3WithCards.player
        else player4WithCards.player
    }

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
    val player1: PlayerWithCards,
    val player2: PlayerWithCards,
    val player3: PlayerWithCards,
    val player4: PlayerWithCards
) : Event

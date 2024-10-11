package org.socratesbe.hearts.domain

import org.socratesbe.hearts.vocabulary.Card
import org.socratesbe.hearts.vocabulary.Deck
import org.socratesbe.hearts.vocabulary.PlayerName

class Game {
    private val events: MutableList<DomainEvent> = mutableListOf()

    fun playerJoins(name: PlayerName) {
        events += PlayerJoined(Player(name))
    }

    private fun hasEnoughPlayers() = events.filterIsInstance<PlayerJoined>().size == 4

    fun hasStarted() = events.contains(GameStarted)

    fun start() {
        if (hasEnoughPlayers()) {
            dealCards()
            events += GameStarted
        }
    }

    private fun dealCards() {
        Deck().cards.chunked(13)
            .mapIndexed { idx, cards -> CardsDealt(players()[idx], cards) }
            .forEach { events += it }
    }

    private fun players() = events.filterIsInstance<PlayerJoined>().map { it.player }
    fun cardsInHandOf(playerName: PlayerName): List<Card> =
        events.filterIsInstance<CardsDealt>()
            .first { it.player.name == playerName }
            .cards
}

data class PlayerJoined(val player: Player) : DomainEvent
data class CardsDealt(val player: Player, val cards: List<Card>) : DomainEvent
data object GameStarted : DomainEvent
interface DomainEvent

data class Player(val name: PlayerName)

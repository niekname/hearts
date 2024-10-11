package org.socratesbe.hearts.domain

import org.socratesbe.hearts.vocabulary.Card
import org.socratesbe.hearts.vocabulary.Deck
import org.socratesbe.hearts.vocabulary.Player
import org.socratesbe.hearts.vocabulary.PlayerName

class Game {
    private val events: MutableList<DomainEvent> = mutableListOf()

    fun playerJoins(name: PlayerName) {
        events += PlayerJoined(Player(name))
    }

    fun start() {
        if (!hasEnoughPlayers()) return
        dealCards()
        events += GameStarted
    }

    fun hasStarted() = events.contains(GameStarted)

    fun cardsInHandOf(playerName: PlayerName): List<Card> =
        events.filterIsInstance<CardsDealt>()
            .first { it.player.name == playerName }
            .cards


    private fun hasEnoughPlayers() = events.filterIsInstance<PlayerJoined>().size == NUMBER_OF_PLAYERS

    private fun dealCards() {
        Deck().dealCardsFor(players())
            .map { CardsDealt(it.player, it.cards) }
            .forEach { events += it }
    }

    private fun players() = events.filterIsInstance<PlayerJoined>().map { it.player }

    companion object {
        const val NUMBER_OF_PLAYERS = 4
    }
}

data class PlayerJoined(val player: Player) : DomainEvent
data class CardsDealt(val player: Player, val cards: List<Card>) : DomainEvent
data object GameStarted : DomainEvent
interface DomainEvent



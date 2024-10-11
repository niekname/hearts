package org.socratesbe.hearts.domain

class Game {
    private val events: MutableList<DomainEvent> = mutableListOf()

    fun playerJoins() {
        events += PlayerJoined
    }

    private fun hasEnoughPlayers() = events.filterIsInstance<PlayerJoined>().size == 4

    fun hasStarted() = events.contains(GameStarted)

    fun start() {
        if (hasEnoughPlayers())
            events += GameStarted
    }
}

data object PlayerJoined : DomainEvent
data object GameStarted : DomainEvent
interface DomainEvent
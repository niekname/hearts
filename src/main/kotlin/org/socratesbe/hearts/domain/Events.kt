package org.socratesbe.hearts.domain

data class PlayerJoined(val player: Player) : DomainEvent
data class CardsDealt(val player: Player, val cards: List<Card>) : DomainEvent
data object GameStarted : DomainEvent
data class CardPlayed(val player: Player) : DomainEvent
interface DomainEvent

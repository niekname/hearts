package org.socratesbe.hearts2

interface Dealer {
    fun dealCardsFor(players: Players): List<PlayerWithCards>
}
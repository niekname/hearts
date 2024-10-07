package org.socratesbe.hearts

import org.socratesbe.hearts.application.api.command.PlayCard
import org.socratesbe.hearts.vocabulary.Card
import org.socratesbe.hearts.vocabulary.Suit
import org.socratesbe.hearts.vocabulary.Symbol
import java.io.InputStream
import java.util.Scanner

object ReadCardPlays

fun readCardPlaysFromResource(resource: String): Iterator<PlayCard> {
    val inputStream = ReadCardPlays.javaClass.getResourceAsStream(resource)
    return readCardPlaysFromInputStream(inputStream!!)
}

private fun readCardPlaysFromInputStream(inputStream: InputStream) = iterator {
    Scanner(inputStream).use {
        while (it.hasNextLine()) {
            val line = it.nextLine()
            if (line.startsWith("-")) continue
            val (player, _, cardString) = line.split(" ")
            val card = parseCard(cardString)
            yield(PlayCard(card, player))
        }
    }
}

private fun parseCard(cardString: String): Card {
    val symbolString = cardString.substring(0 until cardString.length - 2)
    val suitString = cardString.substring(cardString.length - 2 until cardString.length)
    val symbol = parseSymbol(symbolString)
    val suit = parseSuit(suitString)
    return Card(suit, symbol)
}

private fun parseSuit(suitString: String): Suit = Suit.entries.first { it.toString() == suitString }
private fun parseSymbol(symbolString: String): Symbol = Symbol.entries.first { it.toString() == symbolString }

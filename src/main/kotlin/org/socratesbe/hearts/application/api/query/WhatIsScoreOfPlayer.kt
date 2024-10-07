package org.socratesbe.hearts.application.api.query

import org.socratesbe.hearts.vocabulary.PlayerName

data class WhatIsScoreOfPlayer(val player: PlayerName) : Query<Int>

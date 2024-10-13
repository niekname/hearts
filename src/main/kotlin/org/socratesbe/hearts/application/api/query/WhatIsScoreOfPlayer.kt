package org.socratesbe.hearts.application.api.query

import org.socratesbe.hearts.domain.PlayerName

data class WhatIsScoreOfPlayer(val player: PlayerName) : Query<Int>

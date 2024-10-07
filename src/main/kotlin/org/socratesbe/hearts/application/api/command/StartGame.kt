package org.socratesbe.hearts.application.api.command

class StartGame : Command<StartGameResponse>

sealed interface StartGameResponse
data object GameHasStarted : StartGameResponse
data class GameHasNotStarted(val reason: String) : StartGameResponse

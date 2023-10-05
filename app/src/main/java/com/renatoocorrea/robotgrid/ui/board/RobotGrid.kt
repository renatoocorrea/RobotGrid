package com.renatoocorrea.robotgrid.ui.board

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import com.renatoocorrea.robotgrid.ui.movement.generateRandomMove
import com.renatoocorrea.robotgrid.ui.Game

@Composable
fun RobotGrid(game: Game) {
    val state = game.state.collectAsState(initial = null)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        state.value?.let {
            Board(it)
        }

        game.move = Pair(0, 1)
        game.move = generateRandomMove()

        Button {
            game.move = it
        }
    }
}
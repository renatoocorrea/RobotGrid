package com.renatoocorrea.robotgrid.ui.board

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.renatoocorrea.robotgrid.R
import com.renatoocorrea.robotgrid.ui.theme.DarkGreen
import com.renatoocorrea.robotgrid.ui.theme.Game
import com.renatoocorrea.robotgrid.ui.State

@Composable
fun Board(state: State) {
    Column {
        BoxWithConstraints(Modifier.padding(16.dp)) {
            //Score Box.
            Box(
                Modifier
                    .size(width = maxWidth, height = 64.dp)
                    .border(2.dp, DarkGreen)
                    .background(Color.Black)
            ) {
                Column {
                    Text(text = "SCORE RED ROBOT: " + state.scoreRobotOne, color = Color.Red)
                    Text(text = "SCORE MEGAMAN : " + state.scoreRobotTwo, color = Color.Blue)
                }
            }
        }

        BoxWithConstraints(Modifier.padding(8.dp)) {
            val tileSize = maxWidth / Game.BOARD_SIZE
            //Board Game
            Box(
                Modifier
                    .size(maxWidth)
                    .border(2.dp, DarkGreen)
                    .background(Color.Black)
            )

            //Prize
            Box(
                Modifier
                    .offset(x = tileSize * state.prize.first, y = tileSize * state.prize.second)
                    .size(tileSize)
                    .paint(
                        painterResource(id = R.drawable.trophy),
                        contentScale = ContentScale.Crop
                    )
            )

            // First Robot (red robot) instance.
            state.firstRobot.forEach {
                Box(
                    modifier = Modifier
                        .offset(x = tileSize * it.first, y = tileSize * it.second)
                        .size(tileSize)
                        .paint(
                            painterResource(id = R.drawable.robot),
                            contentScale = ContentScale.Crop
                        )
                )
            }

            // Second Robot (megaman) instance.
            state.secondRobot.forEach {
                Box(
                    modifier = Modifier
                        .offset(x = tileSize * it.first, y = tileSize * it.second)
                        .size(tileSize)
                        .paint(
                            painterResource(id = R.drawable.megaman),
                            contentScale = ContentScale.Crop
                        )
                )
            }
        }
    }
}
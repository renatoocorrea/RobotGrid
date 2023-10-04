package com.renatoocorrea.robotgrid.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.renatoocorrea.robotgrid.ui.board.RobotGrid
import com.renatoocorrea.robotgrid.ui.theme.RobotGridTheme

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val game = Game(lifecycleScope)

        setContent {
            RobotGridTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RobotGrid(game)
                }
            }
        }
    }
}

data class State(
    val prize: Pair<Int, Int>,
    val firstRobot: List<Pair<Int, Int>>,
    val secondRobot: List<Pair<Int, Int>>,
    var scoreRobotOne: Int,
    var scoreRobotTwo: Int,
    var whoseTurn: String,
    var endGame: Boolean
)







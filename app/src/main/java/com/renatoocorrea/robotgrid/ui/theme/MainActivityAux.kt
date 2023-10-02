package com.renatoocorrea.robotgrid.ui.theme

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*

class MainActivityAux : ComponentActivity() {
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
                    Snake(game)
                }
            }
        }
    }
}

data class State(
    val food: Pair<Int, Int>,
    val snake: List<Pair<Int, Int>>,
    val secondRobot: List<Pair<Int, Int>>,
    var scoreRobotOne: Int,
    var scoreRobotTwo: Int
)

class Game(private val scope: CoroutineScope) {

    private val mutex = Mutex()
    private val mutableState =
        MutableStateFlow(
            State(
                food = Pair(0, 0),
                snake = listOf(Pair(7, 7)),
                secondRobot = listOf(Pair(3,3)),
                scoreRobotOne = 0,
                scoreRobotTwo = 0,
            )
        )
    val state: Flow<State> = mutableState
    private var snakeLength = 1

    var move = Pair(1, 0)
        set(value) {
            scope.launch {
                mutex.withLock {
                    field = value
                    tryToMove()
                }
            }
        }

    fun tryToMove() {
        scope.launch {
            delay(500)
            mutableState.update {
                val newPosition: Pair<Int,Int> = createThePosition(it.snake)

                val newPositionRobot2 = createThePosition(it.secondRobot)
                //GAME HAVE BEEN WON
                if (newPosition == it.food) {
                    it.scoreRobotOne++
                }

                if (it.snake.contains(newPosition)) {
                    snakeLength = 1
                    it.scoreRobotOne = 0
                }

                if (newPosition == it.secondRobot.first()) {
                    Log.e("TESTE", "SNAKE EATING ROBOT")
                    snakeLength = 1
                }
                if (it.secondRobot.contains(newPosition)) {
                    Log.e("TESTE", "SNAKE EATING ROBOT2")
                    snakeLength = 1
                }
                if (it.snake.contains(newPosition)) {
                    snakeLength = 1
                }


                it.copy(
                    food = if (newPosition == it.food) Pair(
                        Random().nextInt(BOARD_SIZE),
                        Random().nextInt(BOARD_SIZE)
                    ) else it.food,
                    snake = listOf(newPosition) + it.snake.take(snakeLength - 1),
                    secondRobot = listOf(newPositionRobot2) + it.secondRobot.take(snakeLength - 1)
                )
            }
        }

    }

    private suspend fun createThePosition(robot: List<Pair<Int, Int>>): Pair<Int,Int> {
        robot.first().let { poz ->
            var direction = ""
            when (move) {
                Pair(1, 0) -> {
                    direction = "frente"
                }

                Pair(0, 1) -> {
                    direction = "baixo"
                }

                Pair(-1, 0) -> {
                    direction = "trás"
                }

                Pair(0, -1) -> {
                    direction = "cima"
                }
            }
            val boolean = checkIfCanMove(direction, poz)

            if (boolean) {
                snakeLength++
                 mutex.withLock {
                     return Pair(
                        (poz.first + move.first + BOARD_SIZE) % BOARD_SIZE,
                        (poz.second + move.second + BOARD_SIZE) % BOARD_SIZE
                    )
                }
            } else {
                return Pair(
                    (poz.first + 0 + BOARD_SIZE) % BOARD_SIZE,
                    (poz.second + 0 + BOARD_SIZE) % BOARD_SIZE
                )
            }
        }
    }
    init {
        scope.launch {
            var snakeLength = 1

//            while (true) {
            delay(500)
            mutableState.update {
                val newPosition = it.snake.first().let { poz ->
                    mutex.withLock {
                        Pair(
                            (poz.first + move.first + BOARD_SIZE) % BOARD_SIZE,
                            (poz.second + move.second + BOARD_SIZE) % BOARD_SIZE
                        )
                    }
                }

                if (newPosition == it.food) {
                    snakeLength++
                }
                Log.e("TESTE", "NEW POSITION: " + newPosition)
                Log.e("TESTE", "NP ROBOT 2: " + it.secondRobot.first())
                if (newPosition == it.secondRobot.first()) {
                    Log.e("TESTE", "SNAKE EATING ROBOT")
                    snakeLength = 1
                }
                if (it.snake.contains(newPosition) || it.snake == (it.secondRobot)) {
                    snakeLength = 1
                }

                it.copy(
                    food = if (newPosition == it.food) Pair(
                        Random().nextInt(BOARD_SIZE),
                        Random().nextInt(BOARD_SIZE)
                    ) else it.food,
                    snake = listOf(newPosition) + it.snake.take(snakeLength - 1)
                )
            }
        }
//        }
    }

    companion object {
        const val BOARD_SIZE = 7
    }

    private fun checkIfCanMove(direction: String, poz: Pair<Int, Int>): Boolean {
        var result = false

        when (direction) {
            "frente" -> {
                if (poz.first > 5) {
                    result = false
                } else {
                    result = true
                }
            }

            "baixo" -> {
                if (poz.second > 5) {
                    result = false
                } else {
                    result = true
                }
            }

            "trás" -> {
                if (poz.first < 1) {
                    result = false
                } else {
                    result = true
                }
            }

            "cima" -> {
                if (poz.second < 1) {
                    result = false
                } else {
                    result = true
                }
            }
        }

        return result
    }
}

@Composable
fun YouHaveWon() {
    Text(text = "YOU HAVE WON")
}

@Composable
fun Snake(game: Game) {
    val state = game.state.collectAsState(initial = null)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        state.value?.let {
            Board(it)
        }
        Buttons {
            game.move = it
        }
    }

}

@Composable
fun Buttons(onDirectionChange: (Pair<Int, Int>) -> Unit) {
    val buttonSize = Modifier.size(64.dp)
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
        Button(onClick = { onDirectionChange(Pair(0, -1)) }, modifier = buttonSize) {
            Icon(Icons.Default.KeyboardArrowUp, null)
        }
        Row {
            Button(onClick = { onDirectionChange(Pair(-1, 0)) }, modifier = buttonSize) {
                Icon(Icons.Default.KeyboardArrowLeft, null)
            }
            Spacer(modifier = buttonSize)
            Button(onClick = { onDirectionChange(Pair(1, 0)) }, modifier = buttonSize) {
                Icon(Icons.Default.KeyboardArrowRight, null)
            }
        }
        Button(onClick = { onDirectionChange(Pair(0, 1)) }, modifier = buttonSize) {
            Icon(Icons.Default.KeyboardArrowDown, null)
        }
    }
}

@Composable
fun Board(state: State) {
    Text(text = "SCORE1: " + state.scoreRobotOne)
    Text(text = "SCORE2: " + state.scoreRobotOne)
    BoxWithConstraints(Modifier.padding(16.dp)) {
        val tileSize = maxWidth / Game.BOARD_SIZE


        //QUADRO DO JOGO
        Box(
            Modifier
                .size(maxWidth)
                .border(2.dp, DarkGreen)
        )

        //Põe a fruta
        Box(
            Modifier
                .offset(x = tileSize * state.food.first, y = tileSize * state.food.second)
                .size(tileSize)
                .background(
                    Color.Red, CircleShape
                )
        )

        state.snake.forEach {
            Log.e("TESTE", "Stado da Cobra: $it")
            Box(
                modifier = Modifier
                    .offset(x = tileSize * it.first, y = tileSize * it.second)
                    .size(tileSize)
                    .background(
                        Color.Cyan, RoundedCornerShape(percent = 90)
                    )
            )
        }

        state.secondRobot.forEach {
            Log.e("TESTE", "Second Robot: $it")
            Box(
                modifier = Modifier
                    .offset(x = tileSize * it.first, y = tileSize * it.second)
                    .size(tileSize)
                    .background(
                        Color.Green, RoundedCornerShape(percent = 90)
                    )
            )
        }
    }
}


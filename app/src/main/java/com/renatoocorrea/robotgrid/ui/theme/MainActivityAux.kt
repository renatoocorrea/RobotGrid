package com.renatoocorrea.robotgrid.ui.theme

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.renatoocorrea.robotgrid.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.Random

class MainActivityAux : ComponentActivity() {

    lateinit var game: Game

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        game = Game(lifecycleScope)

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
    var scoreRobotTwo: Int,
    var whoseTurn: String,
    var endGame: Boolean
)

class Game(private val scope: CoroutineScope) {

    private val mutex = Mutex()
    private val mutableState =
        MutableStateFlow(
            State(
                food = Pair(3, 3),
                snake = listOf(Pair(0, 0)),
                secondRobot = listOf(Pair(6, 0)),
                scoreRobotOne = 0,
                scoreRobotTwo = 0,
                whoseTurn = "Robot1",
                endGame = false
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
                if (it.whoseTurn == "Robot1") {
                    val newPosition: Pair<Int, Int> = createThePosition(it.snake, it)
                    if (newPosition == it.food) {
                        it.scoreRobotOne++
                        it.endGame = true
                    }
                    it.whoseTurn = "Robot2"
                    if (it.endGame) {
                        snakeLength = 1

                        it.copy(
                            food = if (it.snake.contains(it.food) || newPosition == it.food) {
                                checkPrizeLocation(it)
                            } else {
                                it.food
                            },
                            snake = listOf(Pair(0, 0)),
                            secondRobot = listOf(Pair(6, 0)),
                            endGame = false
                        )

                    } else {
                        it.copy(
                            food = if (newPosition == it.food || it.secondRobot.contains(it.food)) {
                                checkPrizeLocation(it)
                            } else {
                                it.food
                            },
                            snake = listOf(newPosition) + it.snake.take(snakeLength - 1),
                            secondRobot = it.secondRobot + it.secondRobot.take(snakeLength - 1),
                            endGame = false
                        )
                    }

                } else {
                    val newPositionRobot2 = createThePosition(it.secondRobot, it)
                    if (newPositionRobot2 == it.food) {
                        it.scoreRobotTwo++
                        it.endGame = true
                    }
                    it.whoseTurn = "Robot1"
                    if (it.endGame) {
                        snakeLength = 1

                        it.copy(
                            food = if (it.snake.contains(it.food) || newPositionRobot2 == it.food) {
                                checkPrizeLocation(it)
                            } else {
                                it.food
                            },
                            snake = listOf(Pair(0, 0)),
                            secondRobot = listOf(Pair(6, 0)),
                        )

                    } else {
                        it.copy(
                            food = if (it.snake.contains(it.food) || newPositionRobot2 == it.food) {
                                checkPrizeLocation(it)
                            } else {
                                it.food
                            },
                            snake = it.snake + it.snake.take(snakeLength - 1),
                            secondRobot = listOf(newPositionRobot2) + it.secondRobot.take(
                                snakeLength - 1
                            )
                        )
                    }
                }
            }
        }

    }

    private fun checkPrizeLocation(state: State): Pair<Int, Int> {
        var x = Random().nextInt(BOARD_SIZE)
        var y = Random().nextInt(BOARD_SIZE)
        var validPairForPrize = Pair(x, y)

        while (state.snake.contains(validPairForPrize) || state.secondRobot.contains(
                validPairForPrize
            )
        ) {
            x = Random().nextInt(BOARD_SIZE)
            y = Random().nextInt(BOARD_SIZE)
            validPairForPrize = Pair(x, y)
        }

        Log.e("TESTE", "PrizeLocation: $validPairForPrize")
        return validPairForPrize
    }

    private suspend fun createThePosition(
        robot: List<Pair<Int, Int>>,
        state: State
    ): Pair<Int, Int> {
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

            val finalMoveStart = (poz.first + move.first + BOARD_SIZE) % BOARD_SIZE
            val finalMoveEnd = (poz.second + move.second + BOARD_SIZE) % BOARD_SIZE
            val futurePoz = Pair(finalMoveStart, finalMoveEnd)
            var boolean = checkIfCanMove(direction, poz)

            if (state.secondRobot.contains(futurePoz)) {
                Log.e("TESTE", "SNAKE EATING ROBOT2")
                boolean = false
            }

            if (state.snake.contains(futurePoz)) {
                Log.e("TESTE", "Self Target Robot1")
                boolean = false
            }

            if (state.secondRobot.contains(futurePoz)) {
                Log.e("TESTE", "Self Target Robot2")
                boolean = false
            }

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
fun Snake(game: Game) {
    val state = game.state.collectAsState(initial = null)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        state.value?.let {
            Board(it)
        }

        val random = Random()
        val arr = arrayOf("UP", "DOWN", "LEFT", "RIGHT")
        val select: Int = random.nextInt(arr.size)
        Log.e("TESTE", "SELECTED: " + arr[select])
        val pairMovement = getMovementPair(arr[select])
        game.move = pairMovement

        Button {
            game.move = it
        }
    }

}

@Composable
fun getMovementPair(s: String): Pair<Int, Int> {
    return when (s) {
        "UP" -> Pair(0, -1)
        "DOWN" -> Pair(0, 1)
        "LEFT" -> Pair(-1, 0)
        "RIGHT" -> Pair(1, 0)
        else -> {
            Pair(1, 0)
        }
    }
}

@Composable
fun Button(onDirectionChange: (Pair<Int, Int>) -> Unit) {
    val buttonSize = Modifier.size(64.dp)
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
        Button(onClick = { onDirectionChange(Pair(0, -1)) }, modifier = buttonSize) {
            Icon(Icons.Default.PlayArrow, null)
        }
    }
}

@Composable
fun Board(state: State) {
    Column {
        BoxWithConstraints(Modifier.padding(16.dp)) {
            //QUADRO DO JOGO
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

            //QUADRO DO JOGO
            Box(
                Modifier
                    .size(maxWidth)
                    .border(2.dp, DarkGreen)
                    .background(Color.Black)
            )

            //Põe a fruta
            Box(
                Modifier
                    .offset(x = tileSize * state.food.first, y = tileSize * state.food.second)
                    .size(tileSize)
                    .paint(
                        painterResource(id = R.drawable.trophy),
                        contentScale = ContentScale.Crop
                    )
            )

            state.snake.forEach {
                Log.e("TESTE", "Stado da Cobra: $it")

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

            state.secondRobot.forEach {
                Log.e("TESTE", "Second Robot: $it")
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



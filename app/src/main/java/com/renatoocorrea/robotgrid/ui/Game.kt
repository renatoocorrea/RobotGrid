package com.renatoocorrea.robotgrid.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.Random


class Game(private val scope: CoroutineScope) {

    private val mutex = Mutex()
    private val mutableState =
        MutableStateFlow(
            State(
                prize = Pair(3, 3),
                firstRobot = listOf(Pair(0, 0)),
                secondRobot = listOf(Pair(6, 0)),
                scoreRobotOne = 0,
                scoreRobotTwo = 0,
                whoseTurn = Utils.Turn.ROBOT_ONE,
                endGame = false
            )
        )
    val state: Flow<State> = mutableState
    private var robotSize = 1

    var move = Pair(1, 0)
        set(value) {
            scope.launch {
                mutex.withLock {
                    field = value
                    tryToMove()
                }
            }
        }

    private fun tryToMove() {

        scope.launch {
            delay(500)
            mutableState.update {
                if (it.whoseTurn == Utils.Turn.ROBOT_ONE) {
                    val newPosition: Pair<Int, Int> = createThePosition(it.firstRobot, it)
                    if (newPosition == it.prize) {
                        it.scoreRobotOne++
                        it.endGame = true
                    }
                    it.whoseTurn = Utils.Turn.ROBOT_TWO
                    if (it.endGame) {
                        robotSize = 1
                        resetPositions(it, newPosition)
                    } else {
                        it.copy(
                            prize = if (newPosition == it.prize || it.secondRobot.contains(it.prize)) {
                                checkPrizeLocation(it)
                            } else {
                                it.prize
                            },
                            firstRobot = listOf(newPosition) + it.firstRobot.take(robotSize - 1),
                            secondRobot = it.secondRobot + it.secondRobot.take(robotSize - 1),
                            endGame = false
                        )
                    }

                } else {
                    val newPositionRobot2 = createThePosition(it.secondRobot, it)
                    if (newPositionRobot2 == it.prize) {
                        it.scoreRobotTwo++
                        it.endGame = true
                    }
                    it.whoseTurn = Utils.Turn.ROBOT_ONE
                    if (it.endGame) {
                        robotSize = 1
                        resetPositions(it, newPositionRobot2)
                    } else {
                        it.copy(
                            prize = if (it.firstRobot.contains(it.prize) || newPositionRobot2 == it.prize) {
                                checkPrizeLocation(it)
                            } else {
                                it.prize
                            },
                            firstRobot = it.firstRobot + it.firstRobot.take(robotSize - 1),
                            secondRobot = listOf(newPositionRobot2) + it.secondRobot.take(
                                robotSize - 1
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

        while (state.firstRobot.contains(validPairForPrize) || state.secondRobot.contains(
                validPairForPrize
            )
        ) {
            x = Random().nextInt(BOARD_SIZE)
            y = Random().nextInt(BOARD_SIZE)
            validPairForPrize = Pair(x, y)
        }

        return validPairForPrize
    }

    private suspend fun createThePosition(
        robot: List<Pair<Int, Int>>,
        state: State
    ): Pair<Int, Int> {
        robot.first().let { poz ->
            var direction = Utils.Movements.UP
            when (move) {
                Pair(1, 0) -> {
                    direction = Utils.Movements.RIGHT
                }

                Pair(0, 1) -> {
                    direction = Utils.Movements.DOWN
                }

                Pair(-1, 0) -> {
                    direction = Utils.Movements.LEFT
                }

                Pair(0, -1) -> {
                    direction = Utils.Movements.UP
                }
            }

            val finalMoveStart = (poz.first + move.first + BOARD_SIZE) % BOARD_SIZE
            val finalMoveEnd = (poz.second + move.second + BOARD_SIZE) % BOARD_SIZE
            val futurePoz = Pair(finalMoveStart, finalMoveEnd)
            var boolean = checkIfCanMove(direction, poz)

            if (state.secondRobot.contains(futurePoz)) {
                boolean = false
            }

            if (state.firstRobot.contains(futurePoz)) {
                boolean = false
            }

            if (state.secondRobot.contains(futurePoz)) {
                boolean = false
            }

            if (boolean) {
                robotSize++
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

    private fun resetPositions(it: State, newPosition: Pair<Int,Int>): State {
        return it.copy(
            prize = if (it.firstRobot.contains(it.prize) || newPosition == it.prize) {
                checkPrizeLocation(it)
            } else {
                it.prize
            },
            firstRobot = listOf(Pair(0, 0)),
            secondRobot = listOf(Pair(6, 0)),
            endGame = false
        )
    }

    companion object {
        const val BOARD_SIZE = 7
    }

    private fun checkIfCanMove(direction: Utils.Movements, poz: Pair<Int, Int>): Boolean {
        var result = false

        when (direction) {
            Utils.Movements.RIGHT -> {
                if (poz.first > 5) {
                    result = false
                } else {
                    result = true
                }
            }

            Utils.Movements.DOWN -> {
                if (poz.second > 5) {
                    result = false
                } else {
                    result = true
                }
            }

            Utils.Movements.LEFT -> {
                if (poz.first < 1) {
                    result = false
                } else {
                    result = true
                }
            }

            Utils.Movements.UP -> {
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

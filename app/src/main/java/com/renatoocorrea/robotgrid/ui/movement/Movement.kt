package com.renatoocorrea.robotgrid.ui.movement

import androidx.compose.runtime.Composable
import java.util.Random


@Composable
fun generateRandomMove(): Pair<Int, Int> {
    val random = Random()
    val arr = arrayOf("UP", "DOWN", "LEFT", "RIGHT")
    val select: Int = random.nextInt(arr.size)
    return getMovementPair(arr[select])
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
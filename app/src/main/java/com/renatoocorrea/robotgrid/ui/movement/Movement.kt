package com.renatoocorrea.robotgrid.ui.movement

import android.util.Log
import androidx.compose.runtime.Composable
import com.renatoocorrea.robotgrid.ui.Utils
import java.util.Random


@Composable
fun generateRandomMove(): Pair<Int, Int> {
    val random = Random()
    val arr = arrayOf(
        Utils.Movements.UP,
        Utils.Movements.DOWN,
        Utils.Movements.LEFT,
        Utils.Movements.RIGHT
    )
    val select: Int = random.nextInt(arr.size)
    return getMovementPair(arr[select])
}

@Composable
fun getMovementPair(s: Utils.Movements): Pair<Int, Int> {
    return when (s) {
        Utils.Movements.UP -> Pair(0, -1)
        Utils.Movements.DOWN -> Pair(0, 1)
        Utils.Movements.LEFT -> Pair(-1, 0)
        Utils.Movements.RIGHT -> Pair(1, 0)
    }
}
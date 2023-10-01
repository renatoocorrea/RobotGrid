package com.renatoocorrea.robotgrid

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.renatoocorrea.robotgrid.ui.theme.DarkGreen
import com.renatoocorrea.robotgrid.ui.theme.LightGreen
import com.renatoocorrea.robotgrid.ui.theme.Purple40
import com.renatoocorrea.robotgrid.ui.theme.RED_VIBRANT
import com.renatoocorrea.robotgrid.ui.theme.RobotGridTheme
import com.renatoocorrea.robotgrid.ui.theme.models.Game
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class MainActivity : ComponentActivity() {

    val mutableState = MutableStateFlow(State(prize = Pair(3, 3), robot1 = listOf(Pair(7, 7))))
    val state: Flow<State> = mutableState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            setContent {
                RobotGridTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        RobotGame()
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RobotGridTheme {
        Greeting("Android")
    }
}


@Composable
fun RobotGame() {
//    val state = game.state.collectAsState(initial = null)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        /*state.value?.let {
            Board(it)
        }*/
        Board()
        /*Buttons {
            game.move = it
        }*/
    }

}


@Composable
fun Board() {
    BoxWithConstraints(Modifier.padding(16.dp)) {
        val tileSize = maxWidth / 7

        //QUADRO DO JOGO
        Box(
            Modifier
                .size(maxWidth)
                .border(2.dp, DarkGreen)
                .background(Color.White)
        )

        //Põe a fruta
        Box(
            Modifier
                .offset(x = tileSize, y = tileSize)
                .size(tileSize)
                .background(
                    Purple40, CircleShape
                )
        )

        //Robotzinho 1
        Log.e("TESTE", "TileSize: $tileSize")
        Box(
            Modifier
                .offset(x = tileSize * 0, y = tileSize * 0)
                .size(tileSize)
                .background(
                    LightGreen, RoundedCornerShape(percent = 90)
                )
        )

        //Robotzinho 2
        Log.e("TESTE", "TileSize: $tileSize")
        /*val robot2 = Box(
            Modifier
                .offset(x = tileSize * 6, y = tileSize * 0)
                .size(tileSize)
                .background(
                    RED_VIBRANT, RoundedCornerShape(percent = 90)
                )
        )

        Button(onClick = {
            robot2
        }) {
            Text(text = "CLICK")
        }*/

       /* state.robot.forEach {
            Log.e("TESTE", "Stado da Cobra: $it")
            Box(
                modifier = Modifier
                    .offset(x = tileSize * it.first, y = tileSize * it.second)
                    .size(tileSize)
                    .background(
                        DarkGreen, Shapes.small
                    )
            )
        }*/
    }
}


data class State(val prize: Pair<Int, Int>, val robot1: List<Pair<Int, Int>>)
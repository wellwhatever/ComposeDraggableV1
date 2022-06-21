package com.example.composedraggable

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.composedraggable.ui.theme.ComposeDraggableTheme
import kotlin.math.abs
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeDraggableTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    BoxWithDraggableCards()
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeDraggableTheme {
        BoxWithDraggableCards()
    }
}

@Composable
fun BoxWithDraggableCards() {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {
                DraggableCardView()
                DraggableCardView()
            }
        }
    }
}

@Composable
fun DraggableCardView() {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    var floatRangeX by remember { mutableStateOf(mutableListOf<Int>()) }
    var floatRangeY by remember { mutableStateOf(mutableListOf<Int>()) }

    if (offsetX != 0f && floatRangeX.isNotEmpty()) {
        offsetX = floatRangeX.removeFirst().toFloat()
    }
    if (offsetY != 0f && floatRangeY.isNotEmpty()) {
        offsetY = floatRangeY.removeFirst().toFloat()
    }

    Card(modifier = Modifier
        .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
        .size(50.dp)
        .pointerInput(Unit) {
            detectDragGestures(onDragEnd = {
                floatRangeX =
                    createRangeFromOffsetToZero(offsetX.roundToInt()).toMutableList()
                floatRangeY =
                    createRangeFromOffsetToZero(value = offsetY.roundToInt()).toMutableList()
            }) { change, dragAmount ->
                change.consumeAllChanges()
                offsetX += dragAmount.x
                offsetY += dragAmount.y
            }
        },
        backgroundColor = Color.Blue
    ) {}
}

private fun createRangeFromOffsetToZero(
    value: Int,
    stepPercentage: Float = 0.1f
): MutableList<Int> {
    // Add 1, because progression step has to be positive
    val defaultStep = abs(value * stepPercentage).roundToInt() + 1
    val range = when (value > 0) {
        true -> {
            (value downTo 0 step defaultStep).toMutableList()
        }
        false -> {
            (value..0 step defaultStep).toMutableList()
        }
    }
    // Add 0, so view will return to it start position
    range.add(0)
    return range
}
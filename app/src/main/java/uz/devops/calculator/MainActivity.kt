@file:Suppress("UNUSED_EXPRESSION", "NAME_SHADOWING")

package uz.devops.calculator

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import org.mariuszgromada.math.mxparser.Expression
import uz.devops.calculator.ui.theme.CalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        setContent {
            CalculatorTheme {
                val historyClick = remember {
                    mutableStateOf(false)
                }
                val results= remember {
                    mutableStateOf("")
                }
                val expression= remember {
                    mutableStateOf("")
                }
                val historyList= listOf(
                    HistoryModel("$results","$expression")
                )
                if (historyClick.value) {
                    History(historyItems = historyList)
                } else {
                    Greeting(
                        modifier = Modifier,
                        history = {
                            historyClick.value = true
                        },
                        results = {
                            results.value=it
                        },
                        expression = {
                            expression.value=it
                        }
                    )
                }
            }
        }
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun History(modifier: Modifier = Modifier,historyItems: List<HistoryModel>) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        items(historyItems) { historyItem ->
            HistoryRow(historyItem)
        }
    }
}
@Composable
fun HistoryRow(historyItem: HistoryModel, modifier: Modifier=Modifier){
    Column(modifier= modifier
        .fillMaxWidth()
        .padding(12.dp)) {
        Text(
            text = "Expectation: ${historyItem.expectation}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
        Text(
            text = "Result: ${historyItem.result}",
            fontSize = 16.sp,
            color = Color.Black
        )
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier, history: () -> Unit,results:(String)->Unit,expression: (String)->Unit) {
    val excpression = remember { mutableStateOf("") }
    val isPercentUsed = remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Row(
            modifier = modifier
                .weight(0.75f)
                .fillMaxWidth()
        ) {
            Box(

                modifier = modifier
                    .weight(0.23f)
                    .fillMaxHeight()
                    .padding(bottom = 12.dp)
            ) {
//                Image(
//                    painter = painterResource(id = R.drawable.time_icon),
//                    contentDescription = null,
//                    modifier = modifier
//                        .align(alignment = Alignment.BottomEnd)
//                        .size(40.dp)
//                        .clickable {
//                            his tory
//                        }
//                )
            }
            Box(
                modifier = modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(end = 22.dp)
            ) {

                Box(
                    modifier = modifier
                        .fillMaxWidth()
                        .align(
                            alignment = Alignment
                                .BottomCenter
                        )
                ) {
                    Text(
                        text = excpression.value.ifEmpty { "0" },
                        color = Color.White,
                        fontSize = 24.sp,
                        modifier = modifier.align(alignment = Alignment.CenterEnd)
                    )
                }

            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        {
            Column(modifier = modifier.padding(start = 12.dp, end = 12.dp, bottom = 10.dp)) {
                Row(
                    modifier = modifier
                        .weight(1f)
                ) {
                    Box(
                        modifier = modifier
                            .padding(5.dp)
                            .weight(1f)
                            .clip(RoundedCornerShape(50))
                            .background(Color.Cyan)
                            .fillMaxHeight()
                            .clickable {
                                excpression.value = ""
                                if (!excpression.value.contains("%")) {
                                    isPercentUsed.value = false
                                }
                            },
                    ) {
                        Text(
                            text = "AC", modifier = modifier.align(Alignment.Center),
                            style = TextStyle(fontSize = 26.sp)
                        )
                    }
                    Box(
                        modifier = modifier
                            .weight(1f)
                            .padding(5.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color.Cyan)
                            .fillMaxHeight()
                            .clickable {
                                var formattedString =
                                    excpression.value
                                        .replace("×", "*")
                                        .replace("÷", "/")
                                if (excpression.value.isNotEmpty() && formattedString.last() == '.') {
                                    formattedString = formattedString.dropLast(1)
                                }
                                if (excpression.value.isNotEmpty() && !isPercentUsed.value) {
                                    val expression = Expression(formattedString)
                                    val result = expression.calculate()
                                    if (result
                                            .isNaN()
                                            .not()
                                    ) {
                                        excpression.value =
                                            if (result == result
                                                    .toInt()
                                                    .toDouble()
                                            ) {
                                                "(${result.toLong()})%"
                                            } else {
                                                "($result)%"
                                            }

                                        isPercentUsed.value = true
                                    }
                                }
                            }
                    ) {
                        Text(
                            text = "%",
                            modifier = modifier.align(Alignment.Center),
                            style = TextStyle(
                                fontSize = 32.sp
                            )
                        )
                    }
                    Box(
                        modifier = modifier
                            .padding(5.dp)
                            .weight(1f)
                            .clip(RoundedCornerShape(50))
                            .background(Color.Cyan)
                            .fillMaxHeight()
                            .clickable {
                                if (excpression.value.isNotEmpty()) {
                                    val notDigit = excpression.value.none() { it.isDigit() }
                                    if (notDigit) {
                                        excpression.value = ""
                                    } else {
                                        excpression.value = excpression.value.dropLast(1)
                                    }
                                    if (excpression.value.contains("(") && !excpression.value.contains(
                                            ")"
                                        )
                                    ) {
                                        excpression.value = excpression.value.replace("(", "")
                                    }
                                    if (!excpression.value.contains("%")) {
                                        isPercentUsed.value = false
                                    }
                                }
                            }
                    ) {
                        Text(
                            text = "C", modifier = modifier.align(Alignment.Center),
                            style = TextStyle(
                                fontSize = 32.sp
                            )
                        )
                    }
                    Box(
                        modifier = modifier
                            .weight(1f)
                            .padding(5.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color.Blue)
                            .fillMaxHeight()
                            .clickable {
                                val lasts = excpression.value.lastOrNull()
                                if (lasts?.isDigit() == false && isPercentUsed.value) {
                                    return@clickable
                                }
                                if (lasts != null && lasts.isDigit()) {
                                    if (excpression.value.length <= 60) {
                                        excpression.value += "÷"
                                    }
                                } else if (lasts == '×') {
                                    excpression.value = excpression.value.dropLast(1) + "÷"
                                }
                            }
                    ) {
                        Text(
                            text = "÷", modifier = modifier.align(Alignment.Center),
                            style = TextStyle(
                                fontSize = 40.sp
                            )
                        )
                    }

                }
                Row(
                    modifier = modifier
                        .weight(1f)
                ) {
                    Box(
                        modifier = modifier
                            .weight(1f)
                            .padding(5.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color.Green)
                            .fillMaxHeight()
                            .clickable {
                                if (excpression.value.length <= 60) {
                                    excpression.value += "7"
                                }
                            }
                    ) {
                        Text(
                            text = "7", modifier = modifier.align(Alignment.Center),
                            style = TextStyle(
                                fontSize = 32.sp
                            )
                        )
                    }
                    Box(
                        modifier = modifier
                            .padding(5.dp)
                            .weight(1f)
                            .clip(RoundedCornerShape(50))
                            .background(Color.Green)
                            .fillMaxHeight()
                            .clickable {
                                if (excpression.value.length <= 60) {
                                    excpression.value += "8"
                                }
                            }
                    ) {
                        Text(
                            text = "8", modifier = modifier.align(Alignment.Center),
                            style = TextStyle(
                                fontSize = 32.sp
                            )
                        )
                    }
                    Box(
                        modifier = modifier
                            .weight(1f)
                            .padding(5.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color.Green)
                            .fillMaxHeight()
                            .clickable {
                                if (excpression.value.length <= 60) {
                                    excpression.value += "9"
                                }
                            }
                    ) {
                        Text(
                            text = "9", modifier = modifier.align(Alignment.Center),
                            style = TextStyle(
                                fontSize = 32.sp
                            )
                        )
                    }
                    Box(
                        modifier = modifier
                            .padding(5.dp)
                            .weight(1f)
                            .clip(RoundedCornerShape(50))
                            .background(Color.Blue)
                            .fillMaxHeight()
                            .clickable {
                                val lasts = excpression.value.lastOrNull()
                                if (lasts?.isDigit() == false && isPercentUsed.value) {
                                    return@clickable
                                }
                                if (lasts != null && lasts.isDigit()) {
                                    if (excpression.value.length <= 60) {
                                        excpression.value += "×"
                                    }
                                } else if (lasts == '÷') {
                                    excpression.value = excpression.value.dropLast(1) + "×"
                                }
                            }
                    ) {
                        Text(
                            text = "×", modifier = modifier.align(Alignment.Center),
                            style = TextStyle(
                                fontSize = 40.sp
                            )
                        )
                    }
                }
                Row(
                    modifier = modifier
                        .weight(1f)
                ) {
                    Box(
                        modifier = modifier
                            .padding(5.dp)
                            .weight(1f)
                            .clip(RoundedCornerShape(50))
                            .background(Color.Green)
                            .fillMaxHeight()
                            .clickable {
                                if (excpression.value.length <= 60) {
                                    excpression.value += '4'
                                }
                            }
                    ) {
                        Text(
                            text = "4", modifier = modifier.align(Alignment.Center),
                            style = TextStyle(
                                fontSize = 32.sp
                            )
                        )
                    }
                    Box(
                        modifier = modifier
                            .weight(1f)
                            .padding(5.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color.Green)
                            .fillMaxHeight()
                            .clickable {
                                if (excpression.value.length <= 60) {
                                    excpression.value += "5"
                                }
                            }
                    ) {
                        Text(
                            text = "5", modifier = modifier.align(Alignment.Center),
                            style = TextStyle(
                                fontSize = 32.sp
                            )
                        )
                    }
                    Box(
                        modifier = modifier
                            .padding(5.dp)
                            .weight(1f)
                            .clip(RoundedCornerShape(50))
                            .background(Color.Green)
                            .fillMaxHeight()
                            .clickable {
                                if (excpression.value.length <= 60) {
                                    excpression.value += "6"
                                }
                            }
                    ) {
                        Text(
                            text = "6", modifier = modifier.align(Alignment.Center),
                            style = TextStyle(
                                fontSize = 32.sp
                            )
                        )
                    }
                    Box(
                        modifier = modifier
                            .weight(1f)
                            .padding(5.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color.Blue)
                            .fillMaxHeight()
                            .clickable {
                                val lasts = excpression.value.lastOrNull()
                                if (lasts?.isDigit() == false && isPercentUsed.value) {
                                    return@clickable
                                }
                                if (lasts != null && lasts.isDigit()) {
                                    if (excpression.value.length <= 60) {
                                        excpression.value += "-"
                                    }
                                } else if (lasts == '+') {
                                    excpression.value = excpression.value.dropLast(1) + "-"
                                }
                            }
                    ) {
                        Text(
                            text = "一", modifier = modifier.align(Alignment.Center),
                            style = TextStyle(
                                fontSize = 31.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
                Row(
                    modifier = modifier
                        .weight(1f)
                ) {
                    Box(
                        modifier = modifier
                            .weight(1f)
                            .padding(5.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color.Green)
                            .fillMaxHeight()
                            .clickable {
                                if (excpression.value.length <= 60) {
                                    excpression.value += "1"
                                }
                            }
                    ) {
                        Text(
                            text = "1", modifier = modifier.align(Alignment.Center),
                            style = TextStyle(
                                fontSize = 32.sp
                            )
                        )
                    }
                    Box(
                        modifier = modifier
                            .padding(5.dp)
                            .weight(1f)
                            .clip(RoundedCornerShape(50))
                            .background(Color.Green)
                            .fillMaxHeight()
                            .clickable {
                                if (excpression.value.length <= 60) {
                                    excpression.value += "2"
                                }
                            }
                    ) {
                        Text(
                            text = "2", modifier = modifier.align(Alignment.Center),
                            style = TextStyle(
                                fontSize = 32.sp
                            )
                        )
                    }
                    Box(
                        modifier = modifier
                            .weight(1f)
                            .padding(5.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color.Green)
                            .fillMaxHeight()
                            .clickable {
                                if (excpression.value.length <= 60) {
                                    excpression.value += "3"
                                }
                            }
                    ) {
                        Text(
                            text = "3", modifier = modifier.align(Alignment.Center),
                            style = TextStyle(
                                fontSize = 32.sp
                            )
                        )
                    }
                    Box(
                        modifier = modifier
                            .padding(5.dp)
                            .weight(1f)
                            .clip(RoundedCornerShape(50))
                            .background(Color.Blue)
                            .fillMaxHeight()
                            .clickable {
                                val lasts = excpression.value.lastOrNull()
                                if (lasts?.isDigit() == false && isPercentUsed.value) {
                                    return@clickable
                                }
                                if (lasts != null && lasts.isDigit()) {
                                    if (excpression.value.length <= 60) {
                                        excpression.value += "+"
                                    }
                                } else if (lasts == '-') {
                                    excpression.value = excpression.value.dropLast(1) + "+"
                                }
                            }
                    ) {
                        Text(
                            text = "+", modifier = modifier.align(Alignment.Center),
                            style = TextStyle(
                                fontSize = 38.sp
                            )
                        )
                    }
                }
                Row(
                    modifier = modifier
                        .weight(1f)
                ) {
                    Box(
                        modifier = modifier
                            .padding(5.dp)
                            .weight(2f)
                            .clip(RoundedCornerShape(50))
                            .background(Color.Green)
                            .fillMaxHeight()
                            .clickable {
                                if (excpression.value.length <= 60) {
                                    excpression.value += "0"
                                }
                            }
                    ) {
                        Text(
                            text = "0", modifier = modifier.align(Alignment.Center),
                            style = TextStyle(
                                fontSize = 32.sp
                            )
                        )
                    }
                    Box(
                        modifier = modifier
                            .padding(5.dp)
                            .weight(1f)
                            .clip(RoundedCornerShape(50))
                            .background(Color.Cyan)
                            .fillMaxHeight()
                            .clickable {
                                if (excpression.value.length <= 60) {
                                    excpression.value += "."
                                }
                            }
                    ) {
                        Text(
                            text = ".", modifier = modifier.align(Alignment.Center),
                            style = TextStyle(
                                fontSize = 32.sp
                            )
                        )
                    }
                    Box(
                        modifier = modifier
                            .weight(1f)
                            .padding(5.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color.Blue)
                            .fillMaxHeight()
                            .clickable {
                                expression(excpression.value)
                                var formattedString =
                                    excpression.value
                                        .replace("×", "*")
                                        .replace("÷", "/")
                                if (excpression.value.isNotEmpty() && formattedString.last() == '.') {
                                    formattedString = formattedString.dropLast(1)
                                }
                                if (excpression.value.isNotEmpty() && excpression.value.contains("%")) {
                                    val percentIndex = formattedString.indexOf("%")
                                    val beforePercent = excpression.value.substring(
                                        1,
                                        percentIndex - 1
                                    )
                                    val afterPercent =
                                        formattedString.substring(percentIndex + 1)
                                    val result =
                                        beforePercent.toDouble() * afterPercent.toDouble() / 100
                                    excpression.value = if (result == result
                                            .toInt()
                                            .toDouble()
                                    ) {
                                        result
                                            .toLong()
                                            .toString()
                                    } else {
                                        result.toString()
                                    }
                                    isPercentUsed.value = false
                                } else {
                                    if (excpression.value.isNotEmpty()) {
                                        val expressionResult = Expression(formattedString)
                                        val result = expressionResult.calculate()
                                        results(result.toString())
                                        excpression.value = if (result == result
                                                .toInt()
                                                .toDouble()
                                        ) {
                                            result
                                                .toLong()
                                                .toString()
                                        } else {
                                            result.toString()
                                        }
                                    }
                                }
                            }
                    ) {
                        Text(
                            text = "=", modifier = modifier.align(Alignment.Center),
                            style = TextStyle(
                                fontSize = 38.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    CalculatorTheme {
        Greeting(history = { /*TODO*/ },
            results ={} ) {
            
        }
    }
}


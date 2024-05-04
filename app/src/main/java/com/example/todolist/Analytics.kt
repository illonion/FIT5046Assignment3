package com.example.todolist

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import com.example.todolist.ui.theme.CompleteGreen
import com.example.todolist.ui.theme.IncompleteGrey
import android.graphics.Paint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import com.example.todolist.ui.theme.ChartGray
import com.example.todolist.ui.theme.ChartWhite
//import com.kapps.piechartyt.ui.theme.*
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.atan2

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Analytics(navController: NavHostController) {

    // Top Bar
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "To Do List Analytics") },
                // Back button to go back home
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = { navController.navigate("Home") }) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 45.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center)
            ) {
                // Text for task completion for today
                Spacer(modifier = Modifier.height(70.dp))
                Text(
                    text = "56% completed today.\nYou got this!",
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp,
                )


                /* Task Completion Pie Chart
                Image(
                    painter = painterResource(R.drawable.today_completed),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(width = 301.dp, height = 353.dp)
                )
                 */
                //-----------------------------------------------------------------
                PieChart(
                    modifier = Modifier
                        .size(500.dp),
                    input = listOf(
                        PieChartInput(
                            color = CompleteGreen,
                            value = 29,
                            description = "Tasks Completed"
                        ),
                        PieChartInput(
                            color = IncompleteGrey,
                            value = 21,
                            description = "Tasks Incomplete"
                        )
                    ),
                    centerText = "Your Progress"
                )

                //-----------------------------------------------------------------
                /*
                Spacer(modifier = Modifier.height(16.dp))
                // Task completion for yesterday (only if there are tasks yesterday)
                Text(
                    text = "Compared to yesterday, you are currently down 2%!\nAlmost there!",
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Texts for last 7 days for tags
                Text(
                    text = "Over the last 7 days, here's what your tasks look like!",
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                )
                // Tag last 7 days Pie Chart
                Image(
                    painter = painterResource(R.drawable.tags),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(width = 592.dp, height = 486.dp)

                )
                //-----------------------------------------------------------------
                */
            }
        }
    }
}

@Composable
fun PieChart(
    modifier: Modifier = Modifier,
    radius:Float = 500f,
    innerRadius:Float = 250f,
    transparentWidth:Float = 70f,
    input:List<PieChartInput>,
    centerText:String = ""
) {
    var circleCenter by remember {
        mutableStateOf(Offset.Zero)
    }

    var inputList by remember {
        mutableStateOf(input)
    }
    var isCenterTapped by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ){
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(true){

                }
        ){
            val width = size.width
            val height = size.height
            circleCenter = Offset(x= width/2f,y= height/2f)

            val totalValue = input.sumOf {
                it.value
            }
            val anglePerValue = 360f/totalValue
            var currentStartAngle = 0f

            inputList.forEach {pieChartInput ->
                val scale = if(pieChartInput.isTapped) 1.1f else 1.0f
                val angleToDraw = pieChartInput.value * anglePerValue
                scale(scale){
                    drawArc(
                        color = pieChartInput.color,
                        startAngle = currentStartAngle,
                        sweepAngle = angleToDraw,
                        useCenter = true,
                        size = Size(
                            width = radius * 2f,
                            height = radius * 2f
                        ),
                        topLeft = Offset(
                            (width - radius * 2f) / 2f,
                            (height - radius * 2f) / 2f
                        )
                    )
                    currentStartAngle += angleToDraw
                }
            }
            drawContext.canvas.nativeCanvas.apply {
                drawCircle(
                    circleCenter.x,
                    circleCenter.y,
                    innerRadius,
                    Paint().apply {
                        color = ChartWhite.copy(alpha = 0.6f).toArgb()
                        setShadowLayer(10f, 0f, 0f, ChartGray.toArgb())

                    }
                )
            }

            drawCircle(
                color = ChartWhite.copy(0.2f),
                radius = innerRadius + transparentWidth / 2f
            )

        }
        Text(
            centerText,
            modifier = Modifier
                .width(Dp(innerRadius / 1.5f))
                .padding(25.dp),
            fontWeight = FontWeight.SemiBold,
            fontSize = 17.sp,
            textAlign = TextAlign.Center
        )
    }
}


data class PieChartInput(
    val color: androidx.compose.ui.graphics.Color,
    val value:Int,
    val description: String,
    val isTapped:Boolean = false
)
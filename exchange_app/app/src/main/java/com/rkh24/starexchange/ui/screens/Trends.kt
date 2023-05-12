package com.rkh24.starexchange.ui.screens
import android.graphics.Paint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rkh24.starexchange.TrendsViewModel
import com.rkh24.starexchange.data.GraphResponse
import com.rkh24.starexchange.ui.theme.CardRed
import java.time.LocalDate
import kotlin.math.round
import kotlin.math.roundToInt


data class IntradayInfo(
    val date: LocalDate,
    val close: Double,
    val sell: Double
)
@RequiresApi(Build.VERSION_CODES.O)
fun graphResponseToIntradayInfo(graphResponse: GraphResponse): List<IntradayInfo> {
    val infos = mutableListOf<IntradayInfo>()

    for (i in graphResponse.date_last_week.indices) {
        val date = graphResponse.date_last_week[i]
        val buyPrice = graphResponse.average_buy_last_week[i]
        val sellPrice = graphResponse.average_sell_last_week[i]
        val closePrice = (buyPrice + sellPrice) / 2

        infos.add(
            IntradayInfo(
                date = LocalDate.parse(date),
                close = buyPrice ,
                sell=sellPrice

            )
        )
    }

    return infos
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TrendsUI() {
    val trendsViewModel: TrendsViewModel = viewModel()
    val graphResponse: GraphResponse? by trendsViewModel.graphResponse

    val intradayInfos = graphResponse?.let { graphResponseToIntradayInfo(it) } ?: emptyList()
    val visibleRange = remember(intradayInfos) { mutableStateOf(0 until 2 )}
    val visibleIntradayInfos = intradayInfos.safeSlice(visibleRange.value)

    Column {
        Text(
            text = "Exchange Rate Trends",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center,
            color = CardRed
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        if (visibleRange.value.first > 0) {
                            visibleRange.value =
                                (visibleRange.value.first - 1) until visibleRange.value.last - 1
                        }
                    },
                    enabled = visibleRange.value.first > 0
                ) {
                    Text("Previous")
                }

                Button(
                    onClick = {
                        if (visibleRange.value.last < intradayInfos.size - 1) {
                            visibleRange.value =
                                (visibleRange.value.first + 1)..(visibleRange.value.last + 1)
                        }
                    },
                    enabled = visibleRange.value.last < intradayInfos.size - 1
                ) {
                    Text("Next", color = Color.Black)
                }
            }
        }

        Box(
            modifier = Modifier
                .height(400.dp),
            contentAlignment = Alignment.Center
        ) {
            StockChart(
                infos = visibleIntradayInfos,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
                graphColor = Color.Blue,
                valueSelector = { it.close }
            )
        }

        Box(
            modifier = Modifier
                .height(400.dp),
            contentAlignment = Alignment.Center
        ) {
            StockChart(
                infos = visibleIntradayInfos,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
                graphColor = Color.Red,
                valueSelector = { it.sell }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StockChart(
    infos: List<IntradayInfo> = emptyList(),
    modifier: Modifier = Modifier,
    graphColor: Color = Color.Blue,
    valueSelector: (IntradayInfo) -> Double
) {
    val spacing = 40f
    val transparentGraphColor = remember {
        graphColor.copy(alpha = 0.5f)
    }
    val upperValue = remember(infos) {
        ((infos.maxOfOrNull { valueSelector(it) }?.plus(1))?.roundToInt() ?: 0) * 2
    }
    val lowerValue = remember(infos) {
        (infos.minOfOrNull { valueSelector(it) }?.toInt() ?: 0) / 2
    }
    val density = LocalDensity.current
    val textPaint = remember(density) {
        Paint().apply {
            color = android.graphics.Color.BLACK
            textAlign = Paint.Align.CENTER
            textSize = density.run { 12.sp.toPx() }
        }
    }
    Canvas(modifier = modifier) {
        val spacePerHour = (size.width - 2 * spacing) / infos.size
        (0 until infos.size - 1 step 2).forEach { i ->
            val info = infos[i]
            val date = info.date
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    date.toString(),
                    spacing + i * spacePerHour,
                    size.height - 5,
                    textPaint
                )
            }
        }
        val priceStep = (upperValue - lowerValue) / 5f
        (0..4).forEach { i ->
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    round(lowerValue + priceStep * i).toString(),
                    30f,
                    size.height - spacing - i * (size.height - 2 * spacing) / 5f,
                    textPaint
                )
            }
        }
        var lastX = 0f
        val strokePath = Path().apply {
            val height = size.height
            for (i in infos.indices) {
                val info = infos[i]
                val nextInfo = infos.getOrNull(i + 1) ?: infos.last()
                val leftRatio = (info.close - lowerValue) / (upperValue - lowerValue)
                val rightRatio = (nextInfo.close - lowerValue) / (upperValue - lowerValue)

                val x1 = spacing + i * spacePerHour
                val y1 = height - spacing - (leftRatio * (height - 2 * spacing)).toFloat()
                val x2 = spacing + (i + 1) * spacePerHour
                val y2 = height - spacing - (rightRatio * (height - 2 * spacing)).toFloat()
                if (i == 0) {
                    moveTo(x1, y1)
                }
                lastX = (x1 + x2) / 2f
                quadraticBezierTo(
                    x1, y1, lastX, (y1 + y2) / 2f
                )
            }
        }
        val fillPath = android.graphics.Path(strokePath.asAndroidPath())
            .asComposePath()
            .apply {
                lineTo(lastX, size.height - spacing)
                lineTo(spacing, size.height - spacing)
                close()
            }
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    transparentGraphColor,
                    Color.Transparent
                ),
                endY = size.height - spacing)
        )
        drawPath(
            path = strokePath,
            color = graphColor,
            style = Stroke(
                width = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
        )
    }
}


fun <T> List<T>.safeSlice(range: IntRange): List<T> {
    val fromIndex = range.first.coerceIn(0, this.size)
    val toIndex = range.last.coerceIn(fromIndex, this.size)
    return this.subList(fromIndex, toIndex)
}

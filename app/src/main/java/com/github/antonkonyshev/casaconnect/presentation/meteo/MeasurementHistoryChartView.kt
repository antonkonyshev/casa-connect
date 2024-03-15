package com.github.antonkonyshev.casaconnect.presentation.meteo

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class MeasurementHistoryChartView(
    private val context: Context, private val units: String = "°C",
    private val granularity: Float? = null
) : LineChart(context) {
    init {
        description.isEnabled = false
        setTouchEnabled(false)
        isDragEnabled = false
        setScaleEnabled(false)
        legend.isEnabled = false
        setDrawMarkers(false)
        xAxis.position = XAxis.XAxisPosition.BOTH_SIDED
        xAxis.setDrawGridLines(false)
        xAxis.setDrawLabels(false)
        axisLeft.isEnabled = true
        axisLeft.setDrawLabels(false)
        if (granularity != null) {
            axisRight.granularity = granularity
        }
        axisRight.valueFormatter = object : IndexAxisValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return "${
                    String.format(
                        context.resources.configuration.locales.get(0),
                        when (granularity) {
                            1f -> "%.0f"
                            0.1f -> "%.1f"
                            else -> "%.2f"
                        }, value
                    )
                } ${units}"
            }
        }
    }

    private fun prepareEntries(
        history: List<Float>,
        measurement: Float
    ): List<Entry> {
        return history.mapIndexed { idx, record ->
            Entry(idx.toFloat(), record)
        }.toMutableList().apply {
            add(Entry(size.toFloat(), measurement))
        }.toList()
    }

    private class MeasurementHistoryDataSet(
        entries: List<Entry>, label: String = "", axisMinimum: Float = 0f,
        color: Color = Color.Red, alpha: Int = 75,
        axisDependencyValue: YAxis.AxisDependency = YAxis.AxisDependency.RIGHT
    ) : LineDataSet(entries, label) {
        init {
            mode = Mode.CUBIC_BEZIER
            cubicIntensity = 0.2f
            setDrawFilled(true)
            setDrawCircles(false)
            lineWidth = 3f
            circleRadius = 4f
            axisDependency = axisDependencyValue
            setCircleColor(Color.White.toArgb())
            highLightColor = color.toArgb()
            setColor(color.copy(alpha = 0.7f).toArgb())
            fillColor = color.toArgb()
            fillAlpha = alpha
            setDrawHorizontalHighlightIndicator(false)
            setDrawValues(false)

            setFillFormatter { _, _ -> axisMinimum }
        }
    }

    fun updateData(
        history: List<Float>, measurement: Float, label: String = "", color: Color
    ) {
        val entries = prepareEntries(history, measurement)

        if (data != null && data.dataSetCount > 0) {
            (data.getDataSetByIndex(0) as MeasurementHistoryDataSet).values = entries
        } else {
            data = LineData(
                MeasurementHistoryDataSet(entries, label, axisRight.axisMinimum, color = color)
            ).apply {
                setDrawValues(false)
            }
        }

        data.notifyDataChanged()
        notifyDataSetChanged()
        invalidate()
    }
}

@Composable
fun MeasurementHistoryChart(
    history: List<Float>, measurement: Float, label: String = "", units: String = "",
    color: Color = Color.Red, granularity: Float? = null
) {
    AndroidView(
        modifier = Modifier
            .fillMaxSize(),
        factory = { context ->
            MeasurementHistoryChartView(context, units = units, granularity = granularity).apply {
                updateData(history, measurement, label, color)
            }
        }
    ) { chartView ->
        chartView.updateData(history, measurement, label, color)
    }
}

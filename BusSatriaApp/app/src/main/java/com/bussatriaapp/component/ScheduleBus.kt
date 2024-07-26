package com.bussatriaapp.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bussatriaapp.data.BusStop

@Composable
fun ScheduleTable(busStop: BusStop, textColor: Color) {
    Table(
        columnCount = 5,
        cellContent = { rowIndex, columnIndex ->
            val style = MaterialTheme.typography.caption.copy(
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
            when {
                rowIndex == 0 -> {
                    when (columnIndex) {
                        0 -> Text("Bus", fontWeight = FontWeight.Bold, color = textColor, style = style)
                        1 -> Text("Pagi", fontWeight = FontWeight.Bold, color = textColor, style = style)
                        2 -> Text("Siang", fontWeight = FontWeight.Bold, color = textColor, style = style)
                        3 -> Text("Sore", fontWeight = FontWeight.Bold, color = textColor, style = style)
                        4 -> Text("Malam", fontWeight = FontWeight.Bold, color = textColor, style = style)
                    }
                }
                rowIndex == 1 -> {
                    when (columnIndex) {
                        0 -> Text("Bus 1", fontWeight = FontWeight.Bold, color = textColor, style = style)
                        1 -> Text(busStop.bus1Morning, color = textColor, style = style)
                        2 -> Text(busStop.bus1Afternoon, color = textColor, style = style)
                        3 -> Text(busStop.bus1Evening, color = textColor, style = style)
                        4 -> Text(busStop.bus1Night, color = textColor, style = style)
                    }
                }
                rowIndex == 2 -> {
                    when (columnIndex) {
                        0 -> Text("Bus 2", fontWeight = FontWeight.Bold, color = textColor, style = style)
                        1 -> Text(busStop.bus2Morning, color = textColor, style = style)
                        2 -> Text(busStop.bus2Afternoon, color = textColor, style = style)
                        3 -> Text(busStop.bus2Evening, color = textColor, style = style)
                        4 -> Text(busStop.bus2Night, color = textColor, style = style)
                    }
                }
                rowIndex == 3 -> {
                    when (columnIndex) {
                        0 -> Text("Bus 3", fontWeight = FontWeight.Bold, color = textColor, style = style)
                        1 -> Text(busStop.bus3Morning, color = textColor, style = style)
                        2 -> Text(busStop.bus3Afternoon, color = textColor, style = style)
                        3 -> Text(busStop.bus3Evening, color = textColor, style = style)
                        4 -> Text(busStop.bus3Night, color = textColor, style = style)
                    }
                }
            }
        },
        modifier = Modifier.fillMaxWidth(),
        divider = {
            Divider(color = textColor.copy(alpha = 0.2f), thickness = 0.5.dp)
        }
    )
}
@Composable
fun Table(
    columnCount: Int,
    cellContent: @Composable (rowIndex: Int, columnIndex: Int) -> Unit,
    modifier: Modifier = Modifier,
    divider: @Composable () -> Unit = {}
) {
    Column(modifier) {
        repeat(4) { rowIndex ->
            Row(Modifier.fillMaxWidth()) {
                repeat(columnCount) { columnIndex ->
                    Box(
                        Modifier
                            .weight(1f)
                            .border(1.dp, Color.Gray)
                            .padding(4.dp)
                    ) {
                        cellContent(rowIndex, columnIndex)
                    }
                }
            }
            if (rowIndex < 3) divider()
        }
    }
}
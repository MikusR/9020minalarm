package com.mikusr.a9020minalarm

import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AlarmApp(::setAlarm)
                }
            }
        }
    }

    private fun setAlarm(minutes: Int, delayHours: Int, delayMinutes: Int) {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MINUTE, minutes + delayHours * 60 + delayMinutes)

        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
            putExtra(AlarmClock.EXTRA_HOUR, hour)
            putExtra(AlarmClock.EXTRA_MINUTES, minute)
            putExtra(
                AlarmClock.EXTRA_MESSAGE,
                "$minutes Minute Alarm (Delayed by ${delayHours}h ${delayMinutes}m)"
            )
            putExtra(AlarmClock.EXTRA_SKIP_UI, true)
        }

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }
}

@Composable
fun AlarmApp(onSetAlarm: (Int, Int, Int) -> Unit) {
    var delayHours by remember { mutableStateOf(0) }
    var delayMinutes by remember { mutableStateOf(0) }
    val alarmOptions =
        listOf(20) + (1..8).map { it * 90 } // 20 min + 90 min increments up to 12 hours

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Set Alarm Delay",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            TimeSelector(
                label = "Hours",
                value = delayHours,
                onValueChange = { delayHours = it },
                range = 0..23
            )
            Spacer(modifier = Modifier.width(16.dp))
            TimeSelector(
                label = "Minutes",
                value = delayMinutes,
                onValueChange = { delayMinutes = it },
                range = 0..59
            )
        }

        LazyColumn {
            items(alarmOptions) { minutes ->
                AlarmOptionButton(
                    minutes = minutes,
                    delayHours = delayHours,
                    delayMinutes = delayMinutes,
                    onSetAlarm = onSetAlarm
                )
            }
        }
    }
}

@Composable
fun TimeSelector(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        IconButton(onClick = { onValueChange((value + 1).coerceIn(range)) }) {
            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Increase")
        }
        Text(
            text = String.format("%02d", value),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(40.dp)
        )
        IconButton(onClick = { onValueChange((value - 1).coerceIn(range)) }) {
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Decrease")
        }
    }
}

@Composable
fun AlarmOptionButton(
    minutes: Int,
    delayHours: Int,
    delayMinutes: Int,
    onSetAlarm: (Int, Int, Int) -> Unit
) {
    val targetTime = remember(minutes, delayHours, delayMinutes) {
        calculateTargetTime(minutes, delayHours, delayMinutes)
    }

    Button(
        onClick = { onSetAlarm(minutes, delayHours, delayMinutes) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text("Set ${minutes}min alarm (${targetTime})")
    }
}

fun calculateTargetTime(minutes: Int, delayHours: Int, delayMinutes: Int): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.MINUTE, minutes + delayHours * 60 + delayMinutes)
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(calendar.time)
}
package com.mikusr.a9020minalarm

import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
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

    private fun setAlarm(minutes: Int, delayMinutes: Int) {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MINUTE, minutes + delayMinutes)

        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
            putExtra(AlarmClock.EXTRA_HOUR, hour)
            putExtra(AlarmClock.EXTRA_MINUTES, minute)
            putExtra(
                AlarmClock.EXTRA_MESSAGE,
                "$minutes Minute Alarm (Delayed by $delayMinutes min)"
            )
            putExtra(AlarmClock.EXTRA_SKIP_UI, true)
        }

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }
}

@Composable
fun AlarmApp(onSetAlarm: (Int, Int) -> Unit) {
    var delayMinutes by remember { mutableStateOf("0") }
    val alarmOptions =
        listOf(20) + (1..8).map { it * 90 } // 20 min + 90 min increments up to 12 hours

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Set Alarm Delay (minutes)",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        TextField(
            value = delayMinutes,
            onValueChange = { delayMinutes = it.filter { char -> char.isDigit() } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn {
            items(alarmOptions) { minutes ->
                AlarmOptionButton(
                    minutes = minutes,
                    delayMinutes = delayMinutes.toIntOrNull() ?: 0,
                    onSetAlarm = onSetAlarm
                )
            }
        }
    }
}

@Composable
fun AlarmOptionButton(minutes: Int, delayMinutes: Int, onSetAlarm: (Int, Int) -> Unit) {
    val targetTime = remember(minutes, delayMinutes) {
        calculateTargetTime(minutes, delayMinutes)
    }

    Button(
        onClick = { onSetAlarm(minutes, delayMinutes) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text("Set ${minutes}min alarm (${targetTime})")
    }
}

fun calculateTargetTime(minutes: Int, delayMinutes: Int): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.MINUTE, minutes + delayMinutes)
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(calendar.time)
}
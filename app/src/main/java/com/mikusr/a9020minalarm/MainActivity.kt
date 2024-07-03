package com.mikusr.a9020minalarm

import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    private fun setAlarm(minutes: Int) {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MINUTE, minutes)

        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
            putExtra(AlarmClock.EXTRA_HOUR, hour)
            putExtra(AlarmClock.EXTRA_MINUTES, minute)
            putExtra(AlarmClock.EXTRA_MESSAGE, "$minutes Minute Alarm")
            putExtra(AlarmClock.EXTRA_SKIP_UI, true)
        }

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }
}

@Composable
fun AlarmApp(onSetAlarm: (Int) -> Unit) {
    var alarmTime by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                onSetAlarm(20)
                updateAlarmTime(20, { alarmTime = it })
            },
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Text("Set 20 Minute Alarm")
        }

        Button(
            onClick = {
                onSetAlarm(90)
                updateAlarmTime(90, { alarmTime = it })
            },
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text("Set 90 Minute Alarm")
        }

        Text(
            text = alarmTime,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

private fun updateAlarmTime(minutes: Int, onUpdate: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.MINUTE, minutes)
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    val formattedTime = sdf.format(calendar.time)
    onUpdate("Alarm set for: $formattedTime")
}
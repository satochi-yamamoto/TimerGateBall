package com.example.timergateball

import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var timerText: TextView
    private lateinit var startButton: Button
    private lateinit var resetButton: Button

    private var countDownTimer: CountDownTimer? = null
    private var timerRunning = false
    private var timeLeftInMillis: Long = 30 * 60 * 1000 // 30 minutos

    private lateinit var tts: TextToSpeech
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var alarmPlayer: MediaPlayer

    private var preTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timerText = findViewById(R.id.timerText)
        startButton = findViewById(R.id.startButton)
        resetButton = findViewById(R.id.resetButton)

        // Inicializa o TextToSpeech
        tts = TextToSpeech(this, this)

        // Carrega os sons
        mediaPlayer = MediaPlayer.create(this, R.raw.beep) // Você precisa criar este arquivo de som
        alarmPlayer = MediaPlayer.create(this, R.raw.alarm) // Você precisa criar este arquivo de som

        startButton.setOnClickListener {
            if (timerRunning) {
                pauseTimer()
            } else {
                startTimer()
            }
        }

        resetButton.setOnClickListener {
            resetTimer()
        }

        updateCountDownText()
    }

    private fun startTimer() {
        if (timerRunning) return // Evita múltiplos timers
        speak("Iniciando contagem regressiva de 30 minutos")
        preTimer?.cancel()
        preTimer = object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                try {
                    mediaPlayer.seekTo(0)
                    mediaPlayer.start()
                } catch (_: Exception) {}
            }
            override fun onFinish() {
                startMainTimer()
            }
        }.start()
    }

    private fun startMainTimer() {
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateCountDownText()
                when (millisUntilFinished) {
                    in (15 * 60 * 1000)..(15 * 60 * 1000 + 999) -> speak("Já se passaram 15 minutos")
                    in (10 * 60 * 1000)..(10 * 60 * 1000 + 999) -> speak("Faltam 10 minutos")
                    in (5 * 60 * 1000)..(5 * 60 * 1000 + 999) -> speak("Faltam 5 minutos")
                }
            }
            override fun onFinish() {
                timerRunning = false
                startButton.text = "Iniciar"
                speak("Contagem regressiva finalizada")
                alarmPlayer.seekTo(0)
                alarmPlayer.start()
            }
        }.start()
        timerRunning = true
        startButton.text = "Pausar"
    }

    private fun pauseTimer() {
        countDownTimer?.cancel()
        timerRunning = false
        startButton.text = "Iniciar"
    }

    private fun resetTimer() {
        timeLeftInMillis = 30 * 60 * 1000
        updateCountDownText()
        if (timerRunning) {
            countDownTimer?.cancel()
            timerRunning = false
            startButton.text = "Iniciar"
        }
    }

    private fun updateCountDownText() {
        val minutes = (timeLeftInMillis / 1000) / 60
        val seconds = (timeLeftInMillis / 1000) % 60

        val timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        timerText.text = timeFormatted
    }

    private fun speak(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale("pt", "BR")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tts.shutdown()
        mediaPlayer.release()
        alarmPlayer.release()
        countDownTimer?.cancel()
        preTimer?.cancel()
    }
}
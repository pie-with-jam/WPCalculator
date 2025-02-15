package ru.alertkaput.calculator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var display: MaterialTextView
    private var currentNumber = StringBuilder()
    private val maxDigits = 16 // Максимальное количество символов

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        display = findViewById(R.id.display)

        // Назначение обработчиков нажатий на кнопки
        val numberButtons = listOf(
            R.id.button_0, R.id.button_1, R.id.button_2, R.id.button_3,
            R.id.button_4, R.id.button_5, R.id.button_6, R.id.button_7,
            R.id.button_8, R.id.button_9, R.id.button_decimal
        )

        numberButtons.forEach { buttonId ->
            findViewById<MaterialButton>(buttonId).setOnClickListener {
                onNumberButtonClick(it as MaterialButton)
            }
        }

        findViewById<MaterialButton>(R.id.button_clear).setOnClickListener {
            currentNumber.clear()
            updateDisplay()
        }

        findViewById<MaterialButton>(R.id.button_backspace).setOnClickListener {
            if (currentNumber.isNotEmpty()) {
                currentNumber.deleteCharAt(currentNumber.length - 1)
                updateDisplay()
            }
        }
    }

    private fun onNumberButtonClick(button: MaterialButton) {
        val text = button.text.toString()

        // Проверяем, не превышает ли текущее число максимальное количество символов
        if (currentNumber.length + text.length <= maxDigits) {
            currentNumber.append(text)
            updateDisplay()
        }
    }

    private fun updateDisplay() {
        val number = currentNumber.toString()
        val formattedNumber = formatNumber(number)
        display.text = formattedNumber
    }

    private fun formatNumber(number: String): String {
        return try {
            val symbols = DecimalFormatSymbols(Locale.getDefault()).apply {
                groupingSeparator = ','
            }
            val decimalFormat = DecimalFormat("#,###,###,###,###,###", symbols)
            decimalFormat.format(number.toDouble())
        } catch (e: NumberFormatException) {
            number
        }
    }
}
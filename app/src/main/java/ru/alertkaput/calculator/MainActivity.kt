package ru.alertkaput.calculator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import java.math.BigDecimal
import java.math.RoundingMode

class MainActivity : AppCompatActivity() {

    private lateinit var display: MaterialTextView // Поле для отображения ввода и результата
    private var currentNumber = StringBuilder() // Текущее введенное число
    private var memory: BigDecimal = BigDecimal.ZERO // Память калькулятора
    private var firstOperand: BigDecimal? = null // Первый операнд операции
    private var currentOperator: String? = null // Текущий оператор (+, -, ×, ÷)
    private val maxDigits = 16 // Ограничение на количество знаков

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        display = findViewById(R.id.display)

        // Сопоставляем кнопки с их значениями
        val buttons = mapOf(
            R.id.button_0 to "0", R.id.button_1 to "1", R.id.button_2 to "2", R.id.button_3 to "3",
            R.id.button_4 to "4", R.id.button_5 to "5", R.id.button_6 to "6", R.id.button_7 to "7",
            R.id.button_8 to "8", R.id.button_9 to "9", R.id.button_decimal to "."
        )

        buttons.forEach { (id, value) ->
            findViewById<MaterialButton>(id).setOnClickListener { onNumberButtonClick(value) }
        }

        findViewById<MaterialButton>(R.id.button_clear).setOnClickListener { onClear() }
        findViewById<MaterialButton>(R.id.button_backspace).setOnClickListener { onBackspace() }
        findViewById<MaterialButton>(R.id.button_plus_minus).setOnClickListener { onPlusMinus() }
        findViewById<MaterialButton>(R.id.button_percent).setOnClickListener { onPercent() }
        findViewById<MaterialButton>(R.id.button_equals).setOnClickListener { onEquals() }
        findViewById<MaterialButton>(R.id.button_memory_clear).setOnClickListener { onMemoryClear() }
        findViewById<MaterialButton>(R.id.button_memory_recall).setOnClickListener { onMemoryRecall() }
        findViewById<MaterialButton>(R.id.button_memory_add).setOnClickListener { onMemoryAdd() }
        findViewById<MaterialButton>(R.id.button_add).setOnClickListener { onOperatorClick("+") }
        findViewById<MaterialButton>(R.id.button_subtract).setOnClickListener { onOperatorClick("-") }
        findViewById<MaterialButton>(R.id.button_multiply).setOnClickListener { onOperatorClick("×") }
        findViewById<MaterialButton>(R.id.button_divide).setOnClickListener { onOperatorClick("÷") }
    }

    /** Обрабатывает нажатие на цифровые кнопки и точку. */
    private fun onNumberButtonClick(value: String) {
        if (value == "." && currentNumber.contains(".")) return
        if (value == "." && currentNumber.isEmpty()) currentNumber.append("0")
        if (currentNumber.length < maxDigits) {
            currentNumber.append(value)
            updateDisplay()
        }
    }

    /** Очищает ввод и сбрасывает операнды и оператор. */
    private fun onClear() {
        currentNumber.clear()
        firstOperand = null
        currentOperator = null
        updateDisplay()
    }

    /** Удаляет последний введенный символ. */
    private fun onBackspace() {
        if (currentNumber.isNotEmpty()) {
            currentNumber.deleteCharAt(currentNumber.length - 1)
            updateDisplay()
        }
    }

    /** Инвертирует знак текущего числа. */
    private fun onPlusMinus() {
        if (currentNumber.isNotEmpty()) {
            val number = BigDecimal(currentNumber.toString()).negate()
            currentNumber.clear().append(number.stripTrailingZeros().toPlainString())
            updateDisplay()
        }
    }

    /** Преобразует текущее число в процент. */
    private fun onPercent() {
        if (currentNumber.isNotEmpty()) {
            val number = BigDecimal(currentNumber.toString()).divide(BigDecimal(100), maxDigits, RoundingMode.HALF_UP)
            currentNumber.clear().append(number.stripTrailingZeros().toPlainString())
            updateDisplay()
        }
    }

    /** Обрабатывает нажатие операторов (+, -, ×, ÷). */
    private fun onOperatorClick(operator: String) {
        if (currentNumber.isNotEmpty()) {
            firstOperand = BigDecimal(currentNumber.toString())
            currentOperator = operator
            currentNumber.clear()
        }
    }

    /** Выполняет вычисления при нажатии кнопки '='. */
    private fun onEquals() {
        if (currentNumber.isNotEmpty() && firstOperand != null && currentOperator != null) {
            val secondOperand = BigDecimal(currentNumber.toString())
            val result = when (currentOperator) {
                "+" -> firstOperand!! + secondOperand
                "-" -> firstOperand!! - secondOperand
                "×" -> firstOperand!! * secondOperand
                "÷" -> if (secondOperand != BigDecimal.ZERO) firstOperand!!.divide(secondOperand, maxDigits, RoundingMode.HALF_UP) else BigDecimal.ZERO
                else -> BigDecimal.ZERO
            }
            currentNumber.clear().append(result.stripTrailingZeros().toPlainString())
            firstOperand = null
            currentOperator = null
            updateDisplay()
        }
    }

    /** Очищает память калькулятора. */
    private fun onMemoryClear() { memory = BigDecimal.ZERO }

    /** Восстанавливает число из памяти. */
    private fun onMemoryRecall() {
        currentNumber.clear().append(memory.stripTrailingZeros().toPlainString())
        updateDisplay()
    }

    /** Добавляет текущее число в память. */
    private fun onMemoryAdd() {
        if (currentNumber.isNotEmpty()) {
            memory = memory.add(BigDecimal(currentNumber.toString()))
        }
    }


    /**
     * Форматирует число, добавляя разделители тысяч (запятые).
     *
     * @param number Число в виде строки, которое нужно отформатировать.
     * @return Отформатированное число с разделителями тысяч.
     */
    private fun formatNumber(number: String): String {
        return try {
            val parts = number.split(".") // Разделяем целую и дробную части
            val integerPart = parts[0].toBigDecimal().toPlainString().reversed()
                .chunked(3).joinToString(",").reversed() // Разбиваем по 3 цифры, добавляем запятые
            if (parts.size > 1) "$integerPart.${parts[1]}" else integerPart // Объединяем с дробной частью, если есть
        } catch (e: Exception) {
            number // В случае ошибки возвращаем исходное число
        }
    }



    /** Обновляет отображение чисел на экране. */
    private fun updateDisplay() {
        display.text = if (currentNumber.isEmpty()) "0" else formatNumber(currentNumber.toString())
    }
}
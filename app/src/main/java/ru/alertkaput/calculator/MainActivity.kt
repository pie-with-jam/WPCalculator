package ru.alertkaput.calculator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import java.math.BigDecimal
import java.math.RoundingMode

class MainActivity : AppCompatActivity() {

    private lateinit var display: MaterialTextView // Display field for input and result
    private lateinit var operationDisplay: MaterialTextView // Display field for operation history
    private var currentNumber = StringBuilder() // Currently entered number
    private var memory: BigDecimal = BigDecimal.ZERO // Calculator memory
    private var firstOperand: BigDecimal? = null // First operation operand
    private var currentOperator: String? = null // Current operator (+, -, ×, ÷)
    private val maxDigits = 16 // Maximum digit limit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        display = findViewById(R.id.display)
        operationDisplay = findViewById(R.id.operation_display) // Initialize new TextView

        // Map buttons to their values
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

    /** Handles digit and decimal point button clicks */
    private fun onNumberButtonClick(value: String) {
        if (value == "." && currentNumber.contains(".")) return
        if (value == "." && currentNumber.isEmpty()) currentNumber.append("0")
        if (currentNumber.length < maxDigits) {
            currentNumber.append(value)
            updateDisplay()
        }
    }

    /** Clears input and resets operands and operator */
    private fun onClear() {
        currentNumber.clear()
        firstOperand = null
        currentOperator = null
        updateDisplay()
        operationDisplay.visibility = android.view.View.GONE // Hide top TextView
    }

    /** Deletes last entered character */
    private fun onBackspace() {
        if (currentNumber.isNotEmpty()) {
            currentNumber.deleteCharAt(currentNumber.length - 1)
            updateDisplay()
        }
    }

    /** Inverts sign of current number */
    private fun onPlusMinus() {
        if (currentNumber.isNotEmpty()) {
            val number = BigDecimal(currentNumber.toString()).negate()
            currentNumber.clear().append(number.stripTrailingZeros().toPlainString())
            updateDisplay()
        }
    }

    /** Converts current number to percentage */
    private fun onPercent() {
        if (currentNumber.isNotEmpty()) {
            val number = BigDecimal(currentNumber.toString()).divide(BigDecimal(100), maxDigits, RoundingMode.HALF_UP)
            currentNumber.clear().append(number.stripTrailingZeros().toPlainString())
            updateDisplay()
        }
    }

    /** Handles operator button clicks (+, -, ×, ÷) */
    private fun onOperatorClick(operator: String) {
        if (currentNumber.isNotEmpty()) {
            firstOperand = BigDecimal(currentNumber.toString())
            currentOperator = operator
            currentNumber.clear()
            updateOperationDisplay()
            operationDisplay.visibility = android.view.View.VISIBLE // Show top TextView
        }
    }

    /** Performs calculation when equals button is pressed */
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
            operationDisplay.visibility = android.view.View.GONE // Hide top TextView
        }
    }

    /** Clears calculator memory */
    private fun onMemoryClear() { memory = BigDecimal.ZERO }

    /** Recalls number from memory */
    private fun onMemoryRecall() {
        currentNumber.clear().append(memory.stripTrailingZeros().toPlainString())
        updateDisplay()
    }

    /** Adds current number to memory */
    private fun onMemoryAdd() {
        if (currentNumber.isNotEmpty()) {
            memory = memory.add(BigDecimal(currentNumber.toString()))
        }
    }

    /**
     * Formats number by adding thousand separators (commas)
     *
     * @param number Number as string to format
     * @return Formatted number with thousand separators
     */
    private fun formatNumber(number: String): String {
        return try {
            val parts = number.split(".") // Split integer and fractional parts
            val integerPart = parts[0].toBigDecimal().toPlainString().reversed()
                .chunked(3).joinToString(",").reversed() // Split into groups of 3 digits, add commas
            if (parts.size > 1) "$integerPart.${parts[1]}" else integerPart // Combine with fractional part if exists
        } catch (e: Exception) {
            number // Return original number in case of error
        }
    }

    /** Updates number display on screen */
    private fun updateDisplay() {
        display.text = if (currentNumber.isEmpty()) "0" else formatNumber(currentNumber.toString())
    }

    /** Updates operation history display */
    private fun updateOperationDisplay() {
        if (firstOperand != null && currentOperator != null) {
            operationDisplay.text = "${firstOperand!!.stripTrailingZeros().toPlainString()} $currentOperator"
        }
    }
}
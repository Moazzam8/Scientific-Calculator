package com.example.cal;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private EditText display;
    private final StringBuilder input = new StringBuilder();
    private final Stack<Double> numbers = new Stack<>();
    private final Stack<Character> operators = new Stack<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = findViewById(R.id.edit_text_display);

        // Set click listeners for all the buttons
        findViewById(R.id.button_0).setOnClickListener(this::onDigitClick);
        findViewById(R.id.button_1).setOnClickListener(this::onDigitClick);
        findViewById(R.id.button_2).setOnClickListener(this::onDigitClick);
        findViewById(R.id.button_3).setOnClickListener(this::onDigitClick);
        findViewById(R.id.button_4).setOnClickListener(this::onDigitClick);
        findViewById(R.id.button_5).setOnClickListener(this::onDigitClick);
        findViewById(R.id.button_6).setOnClickListener(this::onDigitClick);
        findViewById(R.id.button_7).setOnClickListener(this::onDigitClick);
        findViewById(R.id.button_8).setOnClickListener(this::onDigitClick);
        findViewById(R.id.button_9).setOnClickListener(this::onDigitClick);
        findViewById(R.id.button_dot).setOnClickListener(this::onDecimalClick);
        findViewById(R.id.button_add).setOnClickListener(this::onOperatorClick);
        findViewById(R.id.button_subtract).setOnClickListener(this::onOperatorClick);
        findViewById(R.id.button_multiply).setOnClickListener(this::onOperatorClick);
        findViewById(R.id.button_divide).setOnClickListener(this::onOperatorClick);
        findViewById(R.id.button_sin).setOnClickListener(this::onFunctionClick);
        findViewById(R.id.button_cos).setOnClickListener(this::onFunctionClick);
        findViewById(R.id.button_tan).setOnClickListener(this::onFunctionClick);
        findViewById(R.id.button_log).setOnClickListener(this::onFunctionClick);
        findViewById(R.id.button_clear).setOnClickListener(v -> clearDisplay());
        findViewById(R.id.button_equal).setOnClickListener(v -> calculateResult());
    }

    private void onDigitClick(View view) {
        Button button = (Button) view;
        input.append(button.getText().toString());
        display.setText(input.toString());
    }

    private void onDecimalClick(View view) {
        if (input.length() == 0 || !input.toString().contains(".")) {
            input.append(".");
            display.setText(input.toString());
        }
    }

    private void onOperatorClick(View view) {
        if (input.length() > 0 && Character.isDigit(input.charAt(input.length() - 1))) {
            Button button = (Button) view;
            input.append(button.getText().toString());
            display.setText(input.toString());
        } else {
            Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show();
        }
    }

    private void onFunctionClick(View view) {
        if (input.length() == 0) {
            Toast.makeText(this, "Enter a value first", Toast.LENGTH_SHORT).show();
            return;
        }

        Button button = (Button) view;
        String function = button.getText().toString();

        try {
            double value = Double.parseDouble(input.toString());
            double result;

            switch (function) {
                case "sin":
                    result = Math.sin(Math.toRadians(value));
                    break;
                case "cos":
                    result = Math.cos(Math.toRadians(value));
                    break;
                case "tan":
                    result = Math.tan(Math.toRadians(value));
                    break;
                case "log":
                    if (value <= 0) {
                        throw new ArithmeticException("Logarithm undefined for non-positive values");
                    }
                    result = Math.log10(value);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown function: " + function);
            }

            display.setText(String.valueOf(result));
            input.setLength(0);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show();
        } catch (ArithmeticException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void calculateResult() {
        if (!isValidInput()) {
            Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            numbers.clear();
            operators.clear();

            String expression = input.toString();
            StringBuilder numBuffer = new StringBuilder();

            for (int i = 0; i < expression.length(); i++) {
                char c = expression.charAt(i);

                if (Character.isDigit(c) || c == '.') {
                    numBuffer.append(c);
                } else {
                    if (numBuffer.length() > 0) {
                        numbers.push(Double.parseDouble(numBuffer.toString()));
                        numBuffer.setLength(0);
                    }
                    while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(c)) {
                        evaluate();
                    }
                    operators.push(c);
                }
            }

            if (numBuffer.length() > 0) {
                numbers.push(Double.parseDouble(numBuffer.toString()));
            }

            while (!operators.isEmpty()) {
                evaluate();
            }

            if (!numbers.isEmpty()) {
                double result = numbers.pop();
                display.setText(String.valueOf(result));
                input.setLength(0);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidInput() {
        return input.length() > 0 && Character.isDigit(input.charAt(input.length() - 1));
    }

    private void evaluate() {
        double b = numbers.pop();
        double a = numbers.pop();
        char op = operators.pop();

        double result;
        switch (op) {
            case '+':
                result = a + b;
                break;
            case '-':
                result = a - b;
                break;
            case '*':
                result = a * b;
                break;
            case '/':
                if (b == 0) {
                    throw new ArithmeticException("Cannot divide by zero");
                }
                result = a / b;
                break;
            default:
                throw new IllegalArgumentException("Unknown operator: " + op);
        }

        numbers.push(result);
    }

    private int precedence(char op) {
        switch (op) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            default:
                return -1;
        }
    }

    private void clearDisplay() {
        input.setLength(0);
        display.setText("");
    }
}

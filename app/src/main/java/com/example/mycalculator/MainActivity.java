package com.example.mycalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Stack;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class MainActivity extends AppCompatActivity {

    ArrayList<MaterialButton> btnList = new ArrayList<>();
    MaterialButton btnEqual, btnClear, btnAllClear;
    TextView inputTxt, outputTxt;
    String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputTxt = findViewById(R.id.sol_txt);
        outputTxt = findViewById(R.id.result_txt);

        btnList.add(findViewById(R.id.btn_0));
        btnList.add(findViewById(R.id.btn_1));
        btnList.add(findViewById(R.id.btn_2));
        btnList.add(findViewById(R.id.btn_3));
        btnList.add(findViewById(R.id.btn_4));
        btnList.add(findViewById(R.id.btn_5));
        btnList.add(findViewById(R.id.btn_6));
        btnList.add(findViewById(R.id.btn_7));
        btnList.add(findViewById(R.id.btn_8));
        btnList.add(findViewById(R.id.btn_9));
        btnList.add(findViewById(R.id.btn_dot));
        btnList.add(findViewById(R.id.btn_close_bracket));
        btnList.add(findViewById(R.id.btn_open_bracket));
        btnList.add(findViewById(R.id.btn_plus));
        btnList.add(findViewById(R.id.btn_minus));
        btnList.add(findViewById(R.id.btn_multiply));
        btnList.add(findViewById(R.id.btn_divide));

        for (final MaterialButton btn : btnList) {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String btnTxt = btn.getText().toString();
                    data = inputTxt.getText().toString();

                    String concat = data + btnTxt;
                    inputTxt.setText(concat);
                    data = inputTxt.getText().toString();
                    if (data.endsWith("+") || data.endsWith("-") || data.endsWith("×") || data.endsWith("÷")) {
                        outputTxt.setText("");
                    } else if (data.length() == 0) {
                        outputTxt.setText("0");
                    } else {
                        data = evaluateExpression(data);
                        if (!data.equals("Err")) {
                            outputTxt.setText(data);
                        }
                        inputTxt.setText(concat);
                    }

                }
            });
        }

        inputTxt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    data = evaluateExpression(inputTxt.getText().toString());
                    outputTxt.setText(data);
                    return true;
                }
                return false;
            }
        });

        btnEqual = findViewById(R.id.btn_equals);
        btnAllClear = findViewById(R.id.btn_allclear);
        btnClear = findViewById(R.id.btn_clear);

        btnAllClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputTxt.setText("");
                outputTxt.setText("0");
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data = inputTxt.getText().toString();
                if (data.length() > 0) {
                    data = data.substring(0, data.length() - 1);
                    inputTxt.setText(data);

                    if (data.endsWith("+") || data.endsWith("-") || data.endsWith("×") || data.endsWith("÷")) {
                        outputTxt.setText("");
                    } else if (data.length() == 0) {
                        outputTxt.setText("0");
                    } else {
                        String res = evaluateExpression(data);
                        if (!res.equals("Err")) {
                            outputTxt.setText(res);
                        } else {
                            outputTxt.setText("");
                            inputTxt.setText(data);
                        }
                    }
                }
            }
        });

        btnEqual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = evaluateExpression(inputTxt.getText().toString());
                inputTxt.setText(result);
                outputTxt.setText("");
            }
        });
    }

    private String evaluateExpression(String expression) {
        if (expression.length() == 0)
            return "";

        data = expression;
        expression = expression.replaceAll("×", "*");
        expression = expression.replaceAll("%", "/100");
        expression = expression.replaceAll("÷", "/");


        String cleanedExpression = removeUnmatchedOpenBrackets(expression);

        try {
            Context rhino = Context.enter();
            rhino.setOptimizationLevel(-1);

            String finalResult;

            Scriptable scriptable = rhino.initStandardObjects();
            finalResult = rhino.evaluateString(scriptable, cleanedExpression, "Javsscript", 1, null).toString();
            if (finalResult.endsWith(".0"))
                finalResult = finalResult.substring(0, finalResult.length() - 2);

            inputTxt.setText(data);
            return finalResult;
        } catch (Exception e) {
            return "Err";
        }
    }

    private static String removeUnmatchedOpenBrackets(String expression) {
        Stack<Integer> stack = new Stack<>();
        StringBuilder result = new StringBuilder(expression);

        for (int i = 0; i < result.length(); i++) {
            char c = result.charAt(i);
            if (c == '(') {
                stack.push(i);
            } else if (c == ')' && !stack.isEmpty()) {
                stack.pop();
            }
        }

        while (!stack.isEmpty()) {
            int index = stack.pop();
            result.deleteCharAt(index);
        }

        return result.toString();
    }
}

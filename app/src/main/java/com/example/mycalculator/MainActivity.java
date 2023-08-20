package com.example.mycalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Stack;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class MainActivity extends AppCompatActivity {

    ArrayList<MaterialButton> btnList = new ArrayList<>();
    MaterialButton btnEqual, btnClear, btnAllClear, btnbracket;
    TextView inputTxt, outputTxt;

    int leftBracket = 0, rightBracket = 0;
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
        btnList.add(findViewById(R.id.btn_plus));
        btnList.add(findViewById(R.id.btn_minus));
        btnList.add(findViewById(R.id.btn_multiply));
        btnList.add(findViewById(R.id.btn_divide));
        btnList.add(findViewById(R.id.btn_percent));

        for (final MaterialButton btn : btnList) {
            btn.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View v) {

                    String btnTxt = btn.getText().toString();
                    data = inputTxt.getText().toString();

                    if (data.isEmpty() && btnTxt.matches("[+\\-×÷%]")) {
                        Toast.makeText(MainActivity.this, "Invalid format used.", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (data.isEmpty() && btnTxt.equals(".")) {
                        inputTxt.setText("0" + btnTxt);
                        return;
                    }

                    if (data.length() > 0) {
                        String lastChar = data.substring(data.length() - 1);
                        if (lastChar.matches("[+\\-×÷]") && btnTxt.matches("[+\\-×÷]")) {
                            return;
                        } else if (lastChar.equals("%") && btnTxt.matches("[0-9]")) {
                            inputTxt.setText(data + "×" + btnTxt);
                            return;
                        } else if (lastChar.equals(".") && btnTxt.equals(".")) {
                            return;
                        }
                    }

                    String concat = data + btnTxt;
                    inputTxt.setText(concat);
                    data = inputTxt.getText().toString();
                    String lastChar = data.substring(data.length() - 1);
                    if (lastChar.matches("[+\\-×÷%]")) {
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
        btnbracket = findViewById(R.id.btn_bracket);

        btnbracket.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                String txt = inputTxt.getText().toString();
                if (txt.length() == 0) {
                    inputTxt.setText("(");
                    leftBracket++;
                } else {
                    String lastChar = txt.substring(txt.length() - 1);
                    if (lastChar.matches("[+\\-×÷]")) {
                        inputTxt.setText(txt + "(");
                        leftBracket++;
                    } else if (lastChar.equals("(")) {
                        inputTxt.setText(txt + "(");
                        leftBracket++;
                    } else if (lastChar.matches("[0-9]") && (leftBracket != 0 && leftBracket > rightBracket)) {
                        inputTxt.setText(txt + ")");
                        rightBracket++;
                    } else if (lastChar.equals(")") && (leftBracket != rightBracket)) {
                        inputTxt.setText(txt + ")");
                        rightBracket++;
                    } else {
                        inputTxt.setText(txt + "×(");
                        leftBracket++;
                    }
                }
            }
        });

        btnAllClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputTxt.setText("");
                outputTxt.setText("0");
                leftBracket = 0;
                rightBracket = 0;
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt = inputTxt.getText().toString();
                if (txt.length() > 0) {
                    String lastChar = txt.substring(txt.length() - 1);
                    if (lastChar.equals("(")) {
                        leftBracket--;
                    } else if (lastChar.equals(")")) {
                        rightBracket--;
                    }
                    txt = txt.substring(0, txt.length() - 1);
                    inputTxt.setText(txt);
                    lastChar = txt.length() > 0 ? txt.substring(txt.length() - 1) : "";
                    if (lastChar.matches("[+\\-×÷%]")) {
                        outputTxt.setText("");
                    } else if (txt.length() == 0) {
                        outputTxt.setText("0");
                    } else {
                        String res = evaluateExpression(txt);
                        if (!res.equals("Err")) {
                            outputTxt.setText(res);
                        } else {
                            outputTxt.setText("");
                            inputTxt.setText(txt);
                        }
                    }
                }
            }
        });

        btnEqual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = inputTxt.getText().toString();
                if (result.length() > 0) {
                    String lastChar = result.substring(result.length() - 1);
                    if (lastChar.matches("[+\\-×÷()]")) {
                        Toast.makeText(MainActivity.this, "Invalid format used.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                result = evaluateExpression(inputTxt.getText().toString());
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
            if (!cleanedExpression.isEmpty()) {
                finalResult = rhino.evaluateString(scriptable, cleanedExpression, "Javsscript", 1, null).toString();
                if (finalResult.endsWith(".0"))
                    finalResult = finalResult.substring(0, finalResult.length() - 2);

                inputTxt.setText(data);
                return finalResult;
            } else {
                inputTxt.setText(data);
                return "";
            }
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

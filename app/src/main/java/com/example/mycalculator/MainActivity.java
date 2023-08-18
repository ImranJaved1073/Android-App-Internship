package com.example.mycalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class MainActivity extends AppCompatActivity {

    ArrayList<MaterialButton> btnList = new ArrayList<>();
    MaterialButton btnEqual, btnClear,btnAllClear;
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
                    String data = inputTxt.getText().toString();
                    String btnTxt = btn.getText().toString();
                    String concat = data + btnTxt;
                    inputTxt.setText(concat);
                }
            });
        }



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
                }
            }
        });

        btnEqual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data = inputTxt.getText().toString();

                if (data.length() == 0)
                    return;

                data = data.replaceAll("×", "*");
                data = data.replaceAll("%", "/100");
                data = data.replaceAll("÷", "/");

                try{
                    Context rhino = Context.enter();
                    rhino.setOptimizationLevel(-1);

                    String finalResult;

                    Scriptable scriptable = rhino.initStandardObjects();
                    finalResult = rhino.evaluateString(scriptable, data, "Javsscript", 1, null).toString();
                    if(finalResult.endsWith(".0"))
                        finalResult = finalResult.substring(0, finalResult.length() - 2);
                    outputTxt.setText(finalResult);
                }

                catch (Exception e){
                    outputTxt.setText("");
                    inputTxt.setText("");
                }


            }
        });


    }
}

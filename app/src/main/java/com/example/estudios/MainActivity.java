package com.example.estudios;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    EditText jetUsuario, jetClave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        // Asociar Objetos JAVA A Objetos XML Programables
        jetUsuario = findViewById(R.id.etUsuario);
        jetClave = findViewById(R.id.etClave);
    }

    public void ingresar(View view) {
        String nombreCliente = jetUsuario.getText().toString();

        Intent intMenu = new Intent(this, MenuActivity.class);
        intMenu.putExtra("datos", nombreCliente);
        startActivity(intMenu);
    }

    public void limpiar(View view) {
        jetUsuario.setText("");
        jetClave.setText("");
        jetUsuario.requestFocus();
    }
}
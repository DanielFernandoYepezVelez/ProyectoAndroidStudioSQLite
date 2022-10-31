package com.example.estudios;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MenuActivity extends AppCompatActivity {
    TextView jtvUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        getSupportActionBar().hide();

        String usuario;
        jtvUsuario = findViewById(R.id.tvUsuario);

        // Mostrar En Un TextView El Nombre Del Usuario Que Viene De La Primera Actividad
        usuario = getIntent().getStringExtra("datos");
        jtvUsuario.setText(usuario);
    }

    public void credito(View view) {
        Intent intCreditos = new Intent(this, CreditoActivity.class);
        startActivity(intCreditos);
    }

    public void usuario(View view) {
        Intent intUsuarios = new Intent(this, UsuarioActivity.class);
        startActivity(intUsuarios);
    }

    public void salir(View view) {
        Intent intMain = new Intent(this, MainActivity.class);
        startActivity(intMain);
    }
}
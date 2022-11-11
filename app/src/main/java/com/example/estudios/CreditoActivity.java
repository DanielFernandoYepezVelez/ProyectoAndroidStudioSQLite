package com.example.estudios;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CreditoActivity extends AppCompatActivity {
    EditText jetCodigoPrestamo, jetIdentificacion;
    TextView jtvNombre, jtvProfesion, jtvSalario, jtvIngresoExtra, jtvGastos, jtvValorPrestamo;

    ClsOpenHelper admin = new ClsOpenHelper(this, "banco.bd", null, 1);

    String identificacion, codigoPrestamo, valorPrestamo;
    long respuestaDB;
    byte sw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credito);
        getSupportActionBar().hide();

        jetCodigoPrestamo = findViewById(R.id.etcodigoPrestamo);
        jetIdentificacion = findViewById(R.id.etIdentificacion);

        jtvNombre = findViewById(R.id.tvNombre);
        jtvProfesion = findViewById(R.id.tvProfesion);
        jtvSalario = findViewById(R.id.tvSalario);
        jtvIngresoExtra = findViewById(R.id.tvIngresoExtra);
        jtvGastos = findViewById(R.id.tvGastos);
        jtvValorPrestamo = findViewById(R.id.tvValorPrestamo);
    }

    public void buscarUsuario(View view) {
         identificacion = jetIdentificacion.getText().toString();

        if (identificacion.isEmpty()) {
            Toast.makeText(this, "La Identificación Es Requerida", Toast.LENGTH_LONG).show();
            jetIdentificacion.requestFocus();
        } else {
            SQLiteDatabase fila = admin.getReadableDatabase();
            Cursor dato = fila.rawQuery("select * from TblCliente where identificacion='" + identificacion + "'", null);

            if (dato.moveToNext()) {
                // Validando Que El Usuario Este Activo En Mi Base De Datos
                String userActive = dato.getString(7);

                if (userActive.equalsIgnoreCase("si")) {
                    System.out.println("Usuario Encontrado Existosamente En La Base De Datos");
                    Toast.makeText(this, "Usuario Encontrado Existosamente En La Base De Datos", Toast.LENGTH_LONG).show();

                    // Los Valores Que Me Retorna La Base De Datos Los Llevo, A Mi Formulario XML
                    jtvNombre.setText(dato.getString(1));
                    jtvProfesion.setText(dato.getString(2));
                    jtvSalario.setText(dato.getString(4));
                    jtvIngresoExtra.setText(dato.getString(5));
                    jtvGastos.setText(dato.getString(6));
                } else {
                    Toast.makeText(this, "El Usuario Existe, Pero, Esta INACTIVO, Busca Un Usuario ACTIVO.", Toast.LENGTH_LONG).show();
                }

            } else {
                System.out.println("El Usuario No Esta Registrado En La Base De Datos");
                Toast.makeText(this, "El Usuario No Esta Registrado En La Base De Datos", Toast.LENGTH_LONG).show();
                jetIdentificacion.requestFocus();
            }
        }
    }

    public void calcularPrestamo(View view) {
        String salario, ingresoExtra, gastos;
        int salarioNum, ingresoExtraNum, gastosNum, prestamoBruto, prestamoNeto;

        // Obteniendo Los Valores De Los Campos De Formulario XML
        salario = jtvSalario.getText().toString();
        ingresoExtra = jtvIngresoExtra.getText().toString();
        gastos = jtvGastos.getText().toString();

        // Parseando Lo Valores De Tipo String
        salarioNum = Integer.parseInt(salario);
        ingresoExtraNum = Integer.parseInt(ingresoExtra);
        gastosNum = Integer.parseInt(gastos);

        prestamoBruto = (salarioNum + ingresoExtraNum) - gastosNum;
        prestamoNeto = prestamoBruto * 10;

        jtvValorPrestamo.setText(String.valueOf(prestamoNeto));
    }

    private boolean buscarCredito(String codigoPrestamo) {
        boolean respuesta = false;
        SQLiteDatabase fila = admin.getReadableDatabase();

        try {
            Cursor dato = fila.rawQuery("select * from TblCredito where codigoCredito='" + codigoPrestamo + "'", null);

            if (dato.moveToNext()) {
                respuesta = false;
                Toast.makeText(this, "El codigo ya existe", Toast.LENGTH_LONG).show();
            } else {
                respuesta = true;
                System.out.println("guardo el usuario");
            }
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("La Exception se disparo");
        }

        return respuesta;
    }

    public void guardarPrestamo(View view) {
        identificacion = jetIdentificacion.getText().toString();
        codigoPrestamo = jetCodigoPrestamo.getText().toString();
        valorPrestamo = jtvValorPrestamo.getText().toString();

        if (identificacion.isEmpty() || codigoPrestamo.isEmpty() || valorPrestamo.isEmpty()) {
            Toast.makeText(this, "Todos Los Campos Son Requeridos", Toast.LENGTH_LONG).show();
            jetCodigoPrestamo.requestFocus();
        } else {
            // Validar Que El Codigo Del Credito No Exista Con Anterioridad
            boolean respuestaFinal = buscarCredito(codigoPrestamo);

            if (respuestaFinal) {
                System.out.println("si funciona correcto");
                // Estableciendo La Conexion Con La Base
                // Modos De Apertura De Una Base De Datos (Escritura o Lectura)
                SQLiteDatabase fila = admin.getWritableDatabase();

                // Aqui Va En El Contenedor Donde Va A Guardar La Información Para La Base de datos
                ContentValues registro = new ContentValues();

                registro.put("codigoCredito", codigoPrestamo);
                registro.put("identificacion", identificacion);
                registro.put("valorPestamo", Integer.parseInt(valorPrestamo));

                // Insertando En La Base De Datos Y Obteniendo Su Respuesta
                try {
                    respuestaDB = fila.insert("TblCredito", null, registro);
                } catch (Exception e) {
                    System.out.println(e);
                }

                // Respuesta De La Base De Datos
                if (respuestaDB == 0) {
                    Toast.makeText(this, "ERROR, El Credito NO Se Guardo", Toast.LENGTH_LONG).show();
                    System.out.println("ERROR, El Credito No Se Guardo");
                } else {
                    Toast.makeText(this, "Guardando Regristro Exitosamente", Toast.LENGTH_LONG).show();
                    System.out.println("Guardando Regristro Exitosamente");
                    //limpiarCampos();
                }

                // Cerrando La Conexion de la base de datos
                fila.close();

            } else {
                System.out.println("El Codigo Ya Esta Registrado En La Base De Datos");
                Toast.makeText(this, "El Codigo Ya Esta Registrado En La Base De Datos", Toast.LENGTH_LONG).show();
            }


        }
    }

    public void regresar(View view) {
        Intent intentMenu = new Intent(this, MenuActivity.class);
        startActivity(intentMenu);
    }

    public void cancelar(View view) {
        //limpiarCampos();
    }
}
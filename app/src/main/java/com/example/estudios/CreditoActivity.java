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

    public boolean buscarUsuario(View view) {
        boolean isUserExist = false;
        identificacion = jetIdentificacion.getText().toString();

        if (identificacion.isEmpty()) {
            Toast.makeText(this, "La Identificación Del Usuario Es Requerida", Toast.LENGTH_LONG).show();
            jetIdentificacion.requestFocus();
        } else {
            /* El try - catch Me Permite Visualizar Los Errores De La Base De Datos De Una Forma
               Más Amigable Y Además, Permite Que La Ejecuciòn De Mi Programa No Termine De Froma
               Abrubta, En Caso Tal, De Que Se Produzca Un Error */
            try {
                // MODOS DE APERTURA DE UNA BASE DE DATOS (Lectura)
                SQLiteDatabase fila = admin.getReadableDatabase();
                Cursor dato = fila.rawQuery("select * from TblCliente where identificacion='" + identificacion + "'", null);

                if (dato.moveToNext()) {
                    // Validando Que El Usuario Este Activo En Mi Base De Datos
                    String userActive = dato.getString(7);

                    if (userActive.equalsIgnoreCase("si")) {
                        // Los Valores Que Me Retorna La Base De Datos Los Llevo, A Mi Formulario XML
                        jtvNombre.setText(dato.getString(1));
                        jtvProfesion.setText(dato.getString(2));
                        jtvSalario.setText(dato.getString(4));
                        jtvIngresoExtra.setText(dato.getString(5));
                        jtvGastos.setText(dato.getString(6));
                        isUserExist = true;
                    } else {
                        limpiarCampos();
                        Toast.makeText(this, "El Usuario Existe, Pero, Esta INACTIVO, Busca Un Usuario ACTIVO", Toast.LENGTH_LONG).show();
                    }

                    // Cerrando La Conexion de la base de datos
                    fila.close();
                } else {
                    //System.out.println("El Usuario No Esta Registrado En La Base De Datos");
                    Toast.makeText(this, "El Usuario No Esta Registrado En La Base De Datos", Toast.LENGTH_LONG).show();
                    limpiarCampos();
                }
            } catch (Exception e) {
                System.out.println("Exception Result " + e);
            }
        }

        return isUserExist;
    }

    public void calcularPrestamo(View view) {
        String salario, ingresoExtra, gastos;

        // Obteniendo Los Valores De Los Campos Del Formulario XML
        salario = jtvSalario.getText().toString();
        ingresoExtra = jtvIngresoExtra.getText().toString();
        gastos = jtvGastos.getText().toString();

        if (salario.isEmpty() || ingresoExtra.isEmpty() || gastos.isEmpty()) {
            Toast.makeText(this, "La Idetificación Del Usuario Es Requerida", Toast.LENGTH_LONG).show();
            jetIdentificacion.requestFocus();
        } else {
            int salarioNum, ingresoExtraNum, gastosNum, prestamoBruto, prestamoNeto;

            // Parseando Lo Valores De Tipo String A Int
            salarioNum = Integer.parseInt(salario);
            ingresoExtraNum = Integer.parseInt(ingresoExtra);
            gastosNum = Integer.parseInt(gastos);

            // Operaciones Aritméticas (Para Obtener El Valor Del Prestamo)
            prestamoBruto = (salarioNum + ingresoExtraNum) - gastosNum;
            prestamoNeto = prestamoBruto * 10;

            jtvValorPrestamo.setText(String.valueOf(prestamoNeto));
        }
    }

    private boolean buscarCreditoExistente(String codigoPrestamo) {
        boolean creditResult = false;

        /* El try - catch Me Permite Visualizar Los Errores De La Base De Datos De Una Forma
        Más Amigable Y Además, Permite Que La Ejecuciòn De Mi Programa No Termine Su Ejecuciòn
        De Froma Abrubta, En Caso Tal, De Que Se Produzca Un Error */
        try {
            // MODOS DE APERTURA DE UNA BASE DE DATOS (Lectura)
            SQLiteDatabase fila = admin.getReadableDatabase();
            Cursor dato = fila.rawQuery("select * from TblCredito where codigoCredito='" + codigoPrestamo + "'", null);

            if (!dato.moveToNext()) {
                //System.out.println("El Credito No Existe, Se Puede Registrar Satisfactoriamente");
                creditResult = true;
            }

            // Cerrando La Conexion de la base de datos
            fila.close();
        } catch (Exception e) {
            System.out.println("Exception Result " + e);
        }

        return creditResult;
    }

    public void guardarCredito(View view) {
        identificacion = jetIdentificacion.getText().toString();
        codigoPrestamo = jetCodigoPrestamo.getText().toString();
        valorPrestamo = jtvValorPrestamo.getText().toString();

        if (identificacion.isEmpty() || codigoPrestamo.isEmpty() || valorPrestamo.isEmpty()) {
            Toast.makeText(this, "Todos Los Campos Son Requeridos", Toast.LENGTH_LONG).show();
            jetCodigoPrestamo.requestFocus();
        } else {
            /* Validar Que El Código Del Credito No Exista Con Anterioridad */
            boolean isCreditExist = buscarCreditoExistente(codigoPrestamo);

            if (isCreditExist) {
                /* Validar Que El Usuario Este Registrado Con Anterioridad */
                boolean isUserExist = buscarUsuario(view);

                if (isUserExist) {
                    /* El try - catch Me Permite Visualizar Los Errores De La Base De Datos De Una Forma
                    Más Amigable Y Además, Permite Que La Ejecuciòn De Mi Programa No Termine De Froma
                    Abrubta, En Caso Tal, De Que Se Produzca Un Error */
                    try {
                        // MODOS DE APERTURA DE UNA BASE DE DATOS (Escritura)
                        SQLiteDatabase fila = admin.getWritableDatabase();

                        // Aqui Va El Contenedor Donde Va A Guardar La Información Para La Base de datos
                        ContentValues registro = new ContentValues();

                        registro.put("codigoCredito", codigoPrestamo);
                        registro.put("identificacion", identificacion);
                        registro.put("valorPrestamo", Integer.parseInt(valorPrestamo));

                        // Insertando En La Base De Datos Y Obteniendo Su Respuesta
                        respuestaDB = fila.insert("TblCredito", null, registro);
                        //respuestaDB == -1 Error En El insert En La DB
                        //respuestaDB == 1 Exito En El insert En La DB

                        // Respuesta De La Base De Datos
                        if (respuestaDB == (-1)) {
                            //System.out.println("ERROR En La DB, El Credito No Se Guardo");
                            Toast.makeText(this, "ERROR, El Credito NO Se Registro Exitosamente", Toast.LENGTH_LONG).show();
                        } else {
                            //System.out.println("Registrando Credito Exitosamente");
                            Toast.makeText(this, "El Credito Se Regristro Exitosamente", Toast.LENGTH_LONG).show();
                            limpiarCampos();
                        }

                        // Cerrando La Conexion de la base de datos
                        fila.close();
                    } catch (Exception e) {
                        System.out.println("Exception Result " + e);
                    }
                }
            } else {
                try {
                    // MODOS DE APERTURA DE UNA BASE DE DATOS (Lectura)
                    SQLiteDatabase fila = admin.getReadableDatabase();
                    Cursor dato = fila.rawQuery("select * from TblCredito where codigoCredito='" + codigoPrestamo + "'", null);

                    if (dato.moveToNext()) {
                        String creditActive = dato.getString(3);
                        System.out.println(creditActive);

                        if (creditActive.equalsIgnoreCase("si")) {
                            Toast.makeText(this, "El Credito Ya Esta Registrado En La Base De Datos", Toast.LENGTH_LONG).show();
                            limpiarCampos();
                        } else {
                            Toast.makeText(this, "El Credito Ya Esta Registrado Y Además, Esta INACTIVO, Busca Un Credito ACTIVO", Toast.LENGTH_LONG).show();
                            limpiarCampos();
                        }
                    }

                    // Cerrando La Conexion de la base de datos
                    fila.close();
                } catch (Exception e) {
                    System.out.println("Exception Result 2 " + e);
                }
            }
        }
    }

    public void consultarCredito(View view) {
        codigoPrestamo = jetCodigoPrestamo.getText().toString();

        if (codigoPrestamo.isEmpty()) {
            Toast.makeText(this, "El Codigo Del Credito Es Requerido", Toast.LENGTH_LONG).show();
            jetCodigoPrestamo.requestFocus();
        } else {
            // Validar Que El Código Del Credito Exista Con Anterioridad
            boolean isCreditExist = buscarCreditoExistente(codigoPrestamo);

            if (isCreditExist) {
                //System.out.println("El Codigo NO Esta Registrado En La Base De Datos");
                Toast.makeText(this, "El Credito NO Esta Registrado En La Base De Datos", Toast.LENGTH_LONG).show();
            } else {
                /* El try - catch Me Permite Visualizar Los Errores De La Base De Datos De Una Forma
                   Más Amigable Y Además, Permite Que La Ejecuciòn De Mi Programa No Termine De Froma
                   Abrubta, En Caso Tal, De Que Se Produzca Un Error */
                try {
                    // MODOS DE APERTURA DE UNA BASE DE DATOS (Lectura)
                    SQLiteDatabase fila = admin.getReadableDatabase();
                    Cursor dato = fila.rawQuery("select * from TblCredito where codigoCredito='" + codigoPrestamo + "'", null);

                    if (dato.moveToNext()) {
                        String creditActive = dato.getString(3);

                        if (creditActive.equalsIgnoreCase("si")) {
                            dato = fila.rawQuery("select TblCredito.valorPrestamo, TblCliente.identificacion, TblCliente.nombre, " +
                                    "TblCliente.profesion, TblCliente.salario, TblCliente.ingresoExtra, TblCliente.gastos " +
                                    "from TblCredito inner join TblCliente on TblCredito.identificacion = TblCliente.identificacion " +
                                    "where TblCredito.codigoCredito='" + codigoPrestamo + "'", null);

                            if (dato.moveToNext()) {
                                // Los Valores Que Me Retornó La Base De Datos, Los Llevo A Mi Formulario XML
                                jetIdentificacion.setText(dato.getString(1));
                                jtvNombre.setText(dato.getString(2));
                                jtvProfesion.setText(dato.getString(3));
                                jtvSalario.setText(dato.getString(4));
                                jtvIngresoExtra.setText(dato.getString(5));
                                jtvGastos.setText(dato.getString(6));
                                jtvValorPrestamo.setText(dato.getString(0));
                            }

                            // Cerrando La Conexion de la base de datos
                            fila.close();
                        } else {
                            Toast.makeText(this, "El Credito Existe, Pero, Esta INACTIVO, Busca Un Credito ACTIVO", Toast.LENGTH_LONG).show();
                            limpiarCampos();
                        }
                    } else {
                        Toast.makeText(this, "El Credito NO Esta Registrado En La Base De Datos", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    System.out.println("Exception Result " + e);
                }
            }
        }
    }

    public void anularCredito(View view) {
        codigoPrestamo = jetCodigoPrestamo.getText().toString();

        if (codigoPrestamo.isEmpty()) {
            Toast.makeText(this, "El Codigo Del Credito Es Requerido", Toast.LENGTH_LONG).show();
            jetCodigoPrestamo.requestFocus();
        } else {
            // Validar Que El Código Del Credito Exista Con Anterioridad
            boolean isCreditExist = buscarCreditoExistente(codigoPrestamo);

            if (isCreditExist) {
                //System.out.println("El Codigo NO Esta Registrado En La Base De Datos");
                Toast.makeText(this, "El Credito NO Esta Registrado En La Base De Datos", Toast.LENGTH_LONG).show();
            } else {
                /* El try - catch Me Permite Visualizar Los Errores De La Base De Datos De Una Forma
                   Más Amigable Y Además, Permite Que La Ejecuciòn De Mi Programa No Termine De Froma
                   Abrubta, En Caso Tal, De Que Se Produzca Un Error */
                try {
                    // MODOS DE APERTURA DE UNA BASE DE DATOS (Lectura)
                    SQLiteDatabase fila = admin.getReadableDatabase();
                    Cursor dato = fila.rawQuery("select * from TblCredito where codigoCredito='" + codigoPrestamo + "'", null);

                    if (dato.moveToNext()) {
                        // Validando Que El Credito Este Activo En Mi Base De Datos
                        String creditActive = dato.getString(3);

                        if (creditActive.equalsIgnoreCase("si")) {
                            // MODOS DE APERTURA DE UNA BASE DE DATOS (Escritura)
                            fila = admin.getWritableDatabase();

                            // Aquí Va En El Contenedor Donde Va La Información Para La Base de datos
                            ContentValues registro = new ContentValues();
                            registro.put("activo", "no");

                            // Actualizando En La Base De Datos Y Obteniendo Su Respuesta
                            respuestaDB = fila.update("TblCredito", registro, "codigoCredito='" + codigoPrestamo + "'", null);

                            // Respuesta De La Base De Datos
                            if (respuestaDB == (-1)) {
                                //System.out.println("ERROR En La DB, El Credito No Se Guardo");
                                Toast.makeText(this, "ERROR, El Credito NO Se Anuló Exitosamente", Toast.LENGTH_LONG).show();
                            } else {
                                //System.out.println("Registrando Credito Exitosamente");
                                Toast.makeText(this, "El Credito Se Anuló Exitosamente", Toast.LENGTH_LONG).show();
                                limpiarCampos();
                            }
                        } else {
                            Toast.makeText(this, "El Credito Existe, Pero, Esta INACTIVO, Busca Un Credito ACTIVO.", Toast.LENGTH_LONG).show();
                        }
                    }

                    // Cerrando La Conexion de la base de datos
                    fila.close();
                } catch (Exception e) {
                    System.out.println("Exception Result " + e);
                }
            }
        }
    }

    public void regresar(View view) {
        Intent intentMenu = new Intent(this, MenuActivity.class);
        startActivity(intentMenu);
    }

    public void cancelar(View view) {
        limpiarCampos();
    }

    public void limpiarCampos() {
        jetCodigoPrestamo.setText("");
        jetIdentificacion.setText("");
        jtvNombre.setText("");
        jtvProfesion.setText("");
        jtvSalario.setText("");
        jtvIngresoExtra.setText("");
        jtvGastos.setText("");
        jtvValorPrestamo.setText("");

        jetIdentificacion.requestFocus();
    }
}
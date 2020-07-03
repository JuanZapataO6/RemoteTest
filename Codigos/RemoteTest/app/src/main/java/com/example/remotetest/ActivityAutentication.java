package com.example.remotetest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class ActivityAutentication extends AppCompatActivity {
    private EditText ET_mail;
    private EditText ET_password;
    private Button Btn_Register;
    private Button Btn_Login;
    private FirebaseAuth DataBaseAuth;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autentication);

        DataBaseAuth= FirebaseAuth.getInstance();
        mAuth= FirebaseAuth.getInstance();
        Btn_Login=  findViewById(R.id.ButtonLogin);
        Btn_Register =findViewById(R.id.ButtonRegister);
        ET_mail = findViewById(R.id.ET_email);
        ET_password = findViewById(R.id.ET_password);

        Btn_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Registrar_NewUser();
            }
        });

        Btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login_User();
            }
        });

        FirebaseUser currentUser =DataBaseAuth.getCurrentUser();
        updateUI(currentUser);

        /*String email = ET_mail.getText().toString().trim();
        String password  = ET_password.getText().toString().trim();
        */
    }


    private void updateUI(FirebaseUser currentUser) {
    }

    public void Registrar_NewUser(){

        //Obtenemos el email y la contraseña desde las cajas de texto
        String email = ET_mail.getText().toString().trim();
        String password  = ET_password.getText().toString().trim();

        //Verificamos que las cajas de texto no esten vacías
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Se debe ingresar un email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Falta ingresar la contraseña",Toast.LENGTH_LONG).show();
            return;
        }

        //creating a new user
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ActivityAutentication.this,"Se ha registrado el usuario con el email: "+ ET_mail.getText(),Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(ActivityAutentication.this, MainActivity.class);
                            startActivity(intent);
                        }else{
                            if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                Toast.makeText(getApplicationContext(), "El usuario ya esta registrado", Toast.LENGTH_SHORT).show();
                            }else{

                            Toast.makeText(ActivityAutentication.this,"No se pudo registrar el usuario ",Toast.LENGTH_LONG).show();
                            }
                        }
                     }
                });
    }

    public void Login_User(){
        //Obtenemos el email y la contraseña desde las cajas de texto
        String email = ET_mail.getText().toString().trim();
        String password  = ET_password.getText().toString().trim();

        //Verificamos que las cajas de texto no esten vacías
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Se debe ingresar un email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Falta ingresar la contraseña",Toast.LENGTH_LONG).show();
            return;
        }

        //Loguear user
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ActivityAutentication.this,"Bienvenido:"+ ET_mail.getText(),Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(ActivityAutentication.this, MainActivity.class);
                            startActivity(intent);
                        }else{
                            if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                Toast.makeText(getApplicationContext(), "El usuario ya esta registrado", Toast.LENGTH_SHORT).show();
                            }else{

                                Toast.makeText(ActivityAutentication.this,"No se pudo ingresar  ",Toast.LENGTH_LONG).show();

                            }
                        }
                    }
                });

}
}

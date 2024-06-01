package com.ues.tourismedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
//Agregar destinos turisticos

public class MainActivity extends AppCompatActivity {

    Button btnGuardarDestinos;
    CircleImageView cirimgDestinos;
    ImageView imgAtrasListaDestinos;
    TextView tempVal;
    String idDestino="",imgdestinourl="", getimgdestinoFirebaseurl="";
    DatabaseReference databaseReference;
    String miToken="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //cambiar color barra estado
        cambiarColorBarraEstado(getResources().getColor(R.color.rackley));

        //valores para los productos
        EditText txtnombre_dest= (EditText)findViewById(R.id.txtNombreDestino);
        EditText txtdescripcion_dest= (EditText)findViewById(R.id.txtDescripcionDestino);
        EditText txtubicacion_dest= (EditText)findViewById(R.id.txtUbicacion);

        //Regresar atras a la lista de usuarios o amigos
        imgAtrasListaDestinos= findViewById(R.id.imgAtras);
        imgAtrasListaDestinos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irListaDestinos();
            }
        });

        //Cambiar la imagen del usuario con camara o galeria
        cirimgDestinos = findViewById(R.id.imgDestino);
        cirimgDestinos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(MainActivity.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });

        //Guardar los datos de los usuarios o amigos con el boton
        btnGuardarDestinos = findViewById(R.id.btn_añadirDestino);
        btnGuardarDestinos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(txtnombre_dest.getText().toString().isEmpty() || txtdescripcion_dest.getText().toString().isEmpty() ||
                        txtubicacion_dest.getText().toString().isEmpty()  ){
                    txtnombre_dest.setError("Campo requerido");
                    txtdescripcion_dest.setError("Campo requerido");
                    txtubicacion_dest.setError("Campo requerido");
                    return;
                } else {
                    //Guardar usuarios con firebase
                    try {
                        //subirFotoFirestore();
                    }catch (Exception e){
                        mostrarMsg("Error al llamar metodos de subir fotos: "+ e.getMessage());
                    }

                }

            }
        });



    }

    //private voids

    private void guardarDestinos(){
        try {
            tempVal = findViewById(R.id.txtNombreDestino);
            String nombredest= tempVal.getText().toString();

            tempVal = findViewById(R.id.txtDescripcionDestino);
            String descripciondest = tempVal.getText().toString();

            tempVal = findViewById(R.id.txtUbicacion);
            String ubicaciondest = tempVal.getText().toString();

            databaseReference = FirebaseDatabase.getInstance().getReference("destinos");//el nombre de la propiedad java
            String key  = databaseReference.push().getKey();

            if( miToken.equals("") || miToken==null ){
                obtenerToken();
            }
            destinos destino = new destinos(idDestino,nombredest,descripciondest,ubicaciondest,imgdestinourl,getimgdestinoFirebaseurl,miToken); //
            if( key!=null ){
                databaseReference.child(key).setValue(destino).addOnSuccessListener(unused -> {
                    mostrarMsg("Destino registrado con exito.");
                    irListaDestinos();
                });
            }else{
                mostrarMsg("Error no se registro el destino en la base de datos.");
            }
        }catch (Exception ex){
            mostrarMsg("Error al guardar destino: "+ex.getMessage());
        }
    }

//Para subir las fotos con firestore xd
    private void subirFotoFirestore(){
        mostrarMsg("Subiendo foto al servidor...");
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        Uri file = Uri.fromFile(new File(imgdestinourl)); //local
        final StorageReference reference = storageReference.child("fotosdestinos/"+file.getLastPathSegment()); //nombre de la carpeta de firestores fotos

        final UploadTask uploadTask = reference.putFile(file);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mostrarMsg("Error al subir la foto: "+ e.getMessage());
            }
        });
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mostrarMsg("Foto subida con exito.");
                Task<Uri> downloadUri = uploadTask.continueWithTask(task -> reference.getDownloadUrl()).addOnCompleteListener(task -> {
                    if( task.isSuccessful() ){
                        getimgdestinoFirebaseurl = task.getResult().toString(); //firestore
                        guardarDestinos();
                    }else{
                        mostrarMsg("La foto se subio, pero con errores, nose pudo obtener el enlace.");
                    }
                });
            }
        });
    }

    void obtenerToken(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(tarea-> {
            if (!tarea.isSuccessful()) {
                return;
            }
            miToken = tarea.getResult();
        });
    }

    private void irListaDestinos(){ //ir o regresar a la lista
        Intent abrirVentana = new Intent(getApplicationContext(), ListaDestinos.class);
        startActivity(abrirVentana);
    }

    private void mostrarMsg(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    //CAMBIAR EL COLOR DE LA BARRA DE ESTADO
    private void cambiarColorBarraEstado(int color) {
        // Verificar si la versión del SDK es Lollipop o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    } //fin cambiar colorbarraestado

} //fin mainactivity
package com.adans.app_10;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class Cursos extends AppCompatActivity {


    private RecyclerView recyclerViewcurso;
    private RecyclerViewAdapt AdaptadorCursos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cursos);

        recyclerViewcurso=(RecyclerView)findViewById(R.id.recyclerCantante);
        recyclerViewcurso.setLayoutManager(new LinearLayoutManager(this));

        MantBDD mantBDD= new MantBDD(getApplicationContext());


        //Se cambia (obtenerCursos()) por (mostrarCursos()) para usar los datos de la BD
        AdaptadorCursos= new RecyclerViewAdapt(mantBDD.mostrarCursos());
        recyclerViewcurso.setAdapter(AdaptadorCursos);


    }
}

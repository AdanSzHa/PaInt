package com.adans.app_10;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class PerfilAct extends MenuToolbar{

    SqliteHelper conn;
    TextView Mail,Nombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        //User Permission [Write SD]
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
        }

        // setSupportActionBar(toolbar);

        //MENU
        //-----------------------------------------------
        // Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbarMain);
//        MenuToolbar menu = new MenuToolbar(toolbar,"Main",this);
        this.setToolbar(toolbar);
        this.setContext(this);
        this.setTitleToolbar("CowTech54 DrivingStyles");
        this.loadToolbar();


        conn= new SqliteHelper(getApplicationContext());

        Mail = (TextView) findViewById(R.id.tvMail);
        Nombre= (TextView)findViewById(R.id.tvNombre);

        Bundle MBundle=this.getIntent().getExtras();

        if (MBundle!=null){
            String Email = MBundle.getString("Email");
            Mail.setText(Email);
            setNombre();
        }
    }

    private void setNombre() {
        SQLiteDatabase db=conn.getReadableDatabase();
        String[] parametros={Mail.getText().toString()};
        String[] campos={SqliteHelper.KEY_USER_NAME};

        try {
            Cursor cursor =db.query(SqliteHelper.TABLE_USERS,campos,SqliteHelper.KEY_EMAIL+"=?",parametros,null,null,null);
            cursor.moveToFirst();
            Nombre.setText(cursor.getString(0));
            cursor.close();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"El documento no existe",Toast.LENGTH_LONG).show();
        }
    }

    public void onClick(View view) {

        Intent mintent=new Intent(PerfilAct.this,PruebaAct.class);

        Bundle NBund = new Bundle();
        NBund.putString("nomb",Nombre.getText().toString());
        mintent.putExtras(NBund);
        startActivity(mintent);

    }
}

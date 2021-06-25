package com.example.modelosegundoparcial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Handler.Callback {
    private List<Usuario> usuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.usuarios = this.recuperarUsuarios();
        this.actualizarTextView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Armo el menú de la toolbar según el "menu_principal.xml"
        super.getMenuInflater().inflate(R.menu.menu_principal, menu);

        // Creo el SearchView y su respectivo listener
        MenuItem menuItem = menu.findItem(R.id.buscar);
        ListenerSearchView listenerSearchView = new ListenerSearchView(this.usuarios, this);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(listenerSearchView);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.agregar_usuario) {
            View viewDialog = LayoutInflater.from(this).inflate(R.layout.agregar_usuario, null);
            ListenerAgregarUsuario listenerAgregarUsuario = new ListenerAgregarUsuario(this, viewDialog, this.usuarios);

            Button btnCancelar = viewDialog.findViewById(R.id.btnCancelar);
            Button btnGuardar = viewDialog.findViewById(R.id.btnGuardar);
            CompoundButton tglAdmin = viewDialog.findViewById(R.id.tglAdmin);
            Spinner slcRol = viewDialog.findViewById(R.id.slcRol);
            btnCancelar.setOnClickListener(listenerAgregarUsuario);
            btnGuardar.setOnClickListener(listenerAgregarUsuario);
            tglAdmin.setOnCheckedChangeListener(listenerAgregarUsuario);
            slcRol.setOnItemSelectedListener(listenerAgregarUsuario);

            ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(this, R.array.roles_array, android.R.layout.simple_spinner_item);
            adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            slcRol.setAdapter(adapterSpinner);

            // Muestro el dialog
            DialogDefault dialog = new DialogDefault("Crear Usuario", null, null, null, null, viewDialog, null);
            // Seteo el Dialog en el listener de la view para poder recuperarlo allí
            listenerAgregarUsuario.setDialog(dialog);
            dialog.show(this.getSupportFragmentManager(), "Dialog agregar contacto");

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private List<Usuario> recuperarUsuarios() {
        SharedPreferences prefs = this.getSharedPreferences("misUsuarios", Context.MODE_PRIVATE);
        String usuariosString = prefs.getString("usuarios", "sinUsuarios");

        if ("sinUsuarios".equals(usuariosString)) {
            Handler handler = new Handler(this);
            HiloSecundario hiloHttp = new HiloSecundario(handler);
            hiloHttp.start();

            return new ArrayList<Usuario>();
        }
        else {
            return this.parserJsonUsuarios(usuariosString);
        }
    }

    private List<Usuario> parserJsonUsuarios(String string) {
        List<Usuario> usuarios = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(string);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                Integer id = Integer.valueOf(jsonObject.getString("id"));
                String username = jsonObject.getString("username");
                String rol = jsonObject.getString("rol");
                Boolean admin = Boolean.valueOf(jsonObject.getString("admin"));

                Usuario usuario = new Usuario(id, username, rol, admin);
                usuarios.add(usuario);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return usuarios;
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        this.usuarios = this.parserJsonUsuarios(msg.obj.toString());
        this.actualizarTextView();
        this.actualizarSharedPreferences();

        return false;
    }

    public void actualizarTextView() {
        TextView textView = this.findViewById(R.id.usuarios);
        textView.setText(this.usuarios.toString());
    }

    public void actualizarSharedPreferences() {
        SharedPreferences prefs = this.getSharedPreferences("misUsuarios", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("usuarios", this.usuarios.toString());
        editor.commit();
    }
}
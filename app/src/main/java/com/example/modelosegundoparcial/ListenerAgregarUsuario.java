package com.example.modelosegundoparcial;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.fragment.app.DialogFragment;

import java.util.List;

public class ListenerAgregarUsuario implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, AdapterView.OnItemSelectedListener {
    private DialogFragment dialog;
    private View viewDialog;
    private Activity activity;
    private List<Usuario> usuarios;
    private Usuario usuario;

    public ListenerAgregarUsuario(Activity activity, View viewDialog, List<Usuario> usuarios) {
        this.activity = activity;
        this.viewDialog = viewDialog;
        this.usuarios = usuarios;
        this.usuario = new Usuario();
        this.usuario.setAdmin(false);
        this.usuario.setRol("Supervisor");
    }

    @Override
    public void onClick(View viewClicked) {
        if (viewClicked.getId() == R.id.btnCancelar) {
            this.dialog.dismiss();
        }
        else if (viewClicked.getId() == R.id.btnGuardar) {
            if (this.validarCampos()) {
                String username = ((EditText) this.viewDialog.findViewById(R.id.inputUsername)).getText().toString();
                this.usuario.setId(this.generarId());
                this.usuario.setUsername(username);
                this.usuarios.add(this.usuario);

                ((MainActivity) this.activity).actualizarSharedPreferences();
                ((MainActivity) this.activity).actualizarTextView();
                this.dialog.dismiss();
            }
            else {
                Toast.makeText(viewDialog.getContext(), "Los campos no pueden estar vacíos", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        this.usuario.setAdmin(Boolean.valueOf(isChecked));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0)
            this.usuario.setRol("Supervisor");
        else if (position == 1)
            this.usuario.setRol("Construction Manager");
        else if (position == 2)
            this.usuario.setRol("Project Manager");
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void setDialog(DialogFragment dialog) {
        this.dialog = dialog;
    }

    private boolean validarCampos() {
        EditText inputUsername = this.viewDialog.findViewById(R.id.inputUsername);

        if (inputUsername.getText().toString().isEmpty()) {
            return false;
        }

        return true;
    }

    private int generarId() {
        int id_anterior = 0;

        for (int i = 0; i < this.usuarios.size(); i++) {
            if (this.usuarios.get(i).getId() == null)
                continue;

            int id = this.usuarios.get(i).getId();

            if (id > id_anterior)
                id_anterior = id;
        }

        return id_anterior + 1;
    }


}

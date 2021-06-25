package com.example.modelosegundoparcial;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import java.util.List;

public class ListenerSearchView implements SearchView.OnQueryTextListener {
    private List<Usuario> usuarios;
    private AppCompatActivity activity;

    public ListenerSearchView(List<Usuario> usuarios, AppCompatActivity activity) {
        this.usuarios = usuarios;
        this.activity = activity;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        for (int i = 0; i < this.usuarios.size(); i++) {
            Usuario usuario = this.usuarios.get(i);

            if (query.equals(usuario.getUsername())) {
                String mensaje = "El rol del usuario es ".concat(usuario.getRol());
                DialogDefault dialog = new DialogDefault("Usuario encontrado", mensaje, "Cerrar", null, null, null, null);
                dialog.show(this.activity.getSupportFragmentManager(), "Dialog encontró usuario");
                return false;
            }
        }

        DialogDefault dialog = new DialogDefault("Usuario no encontrado", "El usuario ".concat(query).concat(" no esta dentro de la lista"), "Cerrar", null, null, null, null );
        dialog.show(this.activity.getSupportFragmentManager(), "Dialog NO encontró usuario");
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}

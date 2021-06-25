package com.example.modelosegundoparcial;

import android.os.Handler;
import android.os.Message;

public class HiloSecundario extends Thread {
    private Handler handler;

    public HiloSecundario(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        ConexionHTTP conexionHTTP = new ConexionHTTP();
        byte[] respuestaJson = conexionHTTP.obtenerRespuesta("http://192.168.1.40:3001/usuarios");

        String respuestaString = new String(respuestaJson);
        Message msg = new Message();

        msg.obj = respuestaString;
        this.handler.sendMessage(msg);
    }
}

package com.example.demotandemmensajeiso;

import org.jpos.iso.*;
import org.jpos.iso.channel.ASCIIChannel;

import java.io.IOException;


/**
 * Esta clase implementa un servidor ISO8583 utilizando la biblioteca JPOS.
 * El servidor escucha en un puerto específico y procesa los mensajes ISO8583 entrantes. */
public class IsoServer implements ISORequestListener {
    private IsoMessageHandler handler;


    public IsoServer(IsoMessageHandler handler) {
        this.handler = handler;
    }



    /**
     * Inicia el servidor ISO8583.
     */
    public void startServer() throws Exception {
        try {
            ISOServer server = getIsoServer();
            server.addISORequestListener(this);                                                       // Se registra el objeto actual (this) como un ISORequestListener en el servidor. (Un ISORequestListener es una interfaz de jPOS que define un método process que se llama cuando el servidor recibe un mensaje ISO8583). Al registrar el objeto actual como un ISORequestListener, estás diciendo que el método process de este objeto debe ser llamado cada vez que el servidor reciba un mensaje.
            new Thread(server).start();                                                                 // Iniciar el servidor en un hilo separado
            System.out.println(" ##### Server started successfully on port 8001 #####");
        } catch (Exception e) {
            System.err.println("Error starting server: " + e.getMessage());
        }

    }


    /**
     * Crea un nuevo servidor ISO8583 con un canal ASCII.
     */
    private ISOServer getIsoServer() throws IOException {
        ServerChannel channel = new ASCIIChannel(handler.getPackager());                             // Crea un nuevo ASCIIChannel utilizando el GenericPackager de IsoMessageHandler
        ISOServer server = new ISOServer(8001, channel, null);                             // Crear un servidor ISO con el canal y este objeto como listener. Esta línea está creando una nueva instancia de ISOServer (implementa un servidor ISO8583). Este servidor escucha en el puerto especificado (8080 en este caso) y utiliza el ServerChannel proporcionado para manejar la comunicación de red. El tercer argumento es un Logger que se puede utilizar para registrar eventos del servidor, pero en este caso se está pasando null .
        return server;
    }


    /**
     * Procesa los mensajes ISO8583 entrantes.
     * Esto parte de la interfaz ISORequestListener de la biblioteca jPOS.
     * Cuando se implementa esta interfaz y registra la clase como un listener en un ISOServer, el método process se llama automáticamente cada vez que el servidor recibe un mensaje ISO8583.
     */
    @Override
    public boolean process(ISOSource source, ISOMsg request) {
        IsoMessageReciver reciver = new IsoMessageReciver(handler);          // Crea una instancia de IsoMessageReciver
        reciver.receiveMessage(request);                                     // Llama al método receiveMessage para procesar el mensaje ISO8583
        return true;
    }



}
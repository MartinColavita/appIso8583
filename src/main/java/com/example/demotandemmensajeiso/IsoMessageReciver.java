package com.example.demotandemmensajeiso;

import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOException;

import java.util.logging.Logger;


/** Clase responsable de Recibir y procesar los msj iso entrantes.*/
public class IsoMessageReciver {
    private IsoMessageHandler handler;


    public IsoMessageReciver(IsoMessageHandler handler) {
        this.handler = handler;
    }



    /** Método para procesar el msj iso recibido del servidor */
    public void clientReceiveMessage(byte[] message) {
        ISOMsg msg = handler.unpackMessage(message);            // Desempaqueta el mensaje
        handler.printMessageXMLFormat(msg);
    }


    /** Método para procesar el msj iso recibido */
    public void receiveMessage(ISOMsg msg) {
        try {
            // Suma 10 al mti
            int mti = Integer.parseInt(msg.getMTI()) + 10;
            msg.setMTI(String.valueOf(mti));

            handler.printMessageXMLFormat(msg);
        } catch (ISOException e) {
            System.err.println("Error receiving message: " + e.getMessage());
        }
    }


}


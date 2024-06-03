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
        String[] keys = processWK(msg);
        String WK1 = keys[0];
        String WK2 = keys[1];
        System.out.println("Working key 1: " + WK1);
        System.out.println("Working key 2: " + WK2);
    }


    /** Método para procesar el msj iso recibido en el server*/
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


    /** Método para procesar el campo 60 y desglozar las WK segun su tipo
     *  CADENA COMPLETA en CAMPO 60-> LL(2) + LW(1) +T1(1) + WK1 (32) + CD1(2) + T2(1) + WK2 (32) + CD(2)
     * T(1) -> 1 en ascii es dato y tien la longitud de un byte | esta en la posicion del 4 byte o 40
     * T(2) -> 2 en ascii es pines y tien la longitud de un byte | esta en la posicion del 4 byte o 40
     * WK1 / WK2  -> 32 bytes | esta en la posicion del 5 byte o 41 */
    public String[] processWK(ISOMsg msg) {
        String field60 = msg.getString(60);
        System.out.println("Field 60: " + field60);
        System.out.println("Field 60 length: " + field60.length());


        if (field60.length() < 74) {
            System.out.println("The length of the string is less than expected. Please, check the message.");
            return new String[] {"", ""};
        }

        // Desglozar la cadena
        String LL = field60.substring(0, 4);
        String LW = field60.substring(4, 5);
        String T1 = field60.substring(5, 6);
        String WK1, WK2;
        if (T1.equals("1")) { // Si T1 es "dato"
            WK1 = field60.substring(6, 38);
            WK2 = field60.substring(41, 73);
        } else { //  T1 es "pines"
            WK1 = field60.substring(41, 73);
            WK2 = field60.substring(6, 38);
        }
        String CD1 = field60.substring(38, 40);
        String T2 = field60.substring(40, 41);
        String CD2 = field60.substring(73, 75);

        System.out.println("LL: " + LL);
        System.out.println("LW: " + LW);
        System.out.println("T1: " + T1);
        System.out.println("WK1: " + WK1);
        System.out.println("CD1: " + CD1);
        System.out.println("T2: " + T2);
        System.out.println("WK2: " + WK2);
        System.out.println("CD2: " + CD2);

        return new String[] {WK1, WK2};
    }


}


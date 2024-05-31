package com.example.demotandemmensajeiso;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import java.util.logging.Logger;


/** Clase responsable de crear y empaquetar el mensaje.*/
public class IsoMessageCreator {
    private IsoMessageHandler handler;


    public IsoMessageCreator(IsoMessageHandler handler) {
        this.handler = handler;
    }


    /** Método para crear un mensaje ISO 8583 de prueba */
    public ISOMsg createMessage() throws ISOException {
        ISOMsg msg = new ISOMsg();
        msg.setPackager(handler.getPackager());

        //msg.setHeader("6000000000".getBytes());
        msg.setHeader(handler.parseHexBinary("6000000000"));
        msg.set(0, "0800");                      // MTI
        msg.set(3, "920000");                    // Processing Code
        msg.set(7, "0527100111");                // Date and Time
        msg.set(11, "000001");                   // System Trace Audit Number
        msg.set(24, "014");                      // Function Code
        msg.set(41, "POS12345");                 // Card Acceptor Terminal Identification (serial number + id derivacion)
        msg.set(62, "00160100000000014631");       // Id Derivación (tiene q tener 16 digitos ascii) -- SIN LOS ESPACIOS
       // msg.set(62, "01 00000 000014631");

        handler.printMessageXMLFormat(msg);

        return msg;
    }


    /** Clase con el msj q uso en packet sender para pegarle a jpos */
    public byte[] TestMsgPacketSender() throws ISOException {
        String hexString = "003660000000000800222001000080000492000005271001110000010014504F533132333435001630313030303030303030303134363331";

        // solo para imprimir en xml el mensaje
        handler.printMessageXMLFormat(handler.parseHexToIsoMsg(hexString));

        return handler.parseHexBinary(hexString);
    }





}
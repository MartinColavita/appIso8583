package com.example.demotandemmensajeiso;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.logging.Logger;


/** Clase responsable de crear y empaquetar el mensaje.*/
public class IsoMessageCreator {
    private IsoMessageHandler handler;


    public IsoMessageCreator(IsoMessageHandler handler) {
        this.handler = handler;
    }


    /** Método para crear un mensaje ISO 8583 de prueba */
/*    public ISOMsg createMessage() throws ISOException {
        ISOMsg msg = new ISOMsg();
        msg.setPackager(handler.getPackager());
        msg.set(0, "0800");                      // MTI
        msg.set(3, "920000");                    // Processing Code
        msg.set(7, "0527100111");                // Date and Time
        msg.set(11, "000001");                   // System Trace Audit Number
        msg.set(24, "014");                      // Function Code
        msg.set(41, "POS12345");                 // Card Acceptor Terminal Identification (serial number + id derivacion)
        msg.set(62, "00160100000000014631");      // Id Derivación (tiene q tener 16 digitos ascii) -- SIN LOS ESPACIOS
        return msg;
    }*/
    public ISOMsg createMessage(String terminalNumber, String derivationId) throws ISOException {
        ISOMsg msg = new ISOMsg();
        msg.setPackager(handler.getPackager());

        // Campo 7 - Fecha y hora
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMHHmmss");
        String formattedTime = now.format(formatter);
        msg.set(7, formattedTime);

        // Campo 41 - Número de terminal
        msg.set(41, terminalNumber);

        // Campo 62 - ID de derivación
        String formattedDerivationId = String.format("%016d", new BigInteger(derivationId));
        msg.set(62, "16" + formattedDerivationId);

        msg.set(0, "0800");                      // MTI
        msg.set(3, "920000");                    // Processing Code
        msg.set(11, "000001");                   // System Trace Audit Number
        msg.set(24, "014");                      // Function Code

        return msg;
    }


    /** Clase con el msj q uso en packet sender para pegarle a jpos */
    public byte[] TestMsgPacketSender() throws ISOException {
        String hexString = "003660000000000800222001000080000492000005271001110000010014504F533132333435001630313030303030303030303134363331";

        // solo para imprimir en xml el mensaje
        handler.printMessageXMLFormat(handler.parseHexToIsoMsg(hexString));

        return handler.parseHexBinary(hexString);
    }


    /** Método para preparar un mensaje ISO 8583 para ser enviado (con formato para tandem) */
    public byte[] prepareMessage() throws ISOException {
        // Crear el mensaje ISO 8583
        ISOMsg message = this.createMessage("POS12345", "00160100000000014631");
        System.out.println("Mensaje ISO 8583 creado:");
        handler.printMessageXMLFormat(message);

        // Empaquetar el mensaje
        byte[] packedMessage = handler.packMessage(message);
        System.out.println("Mensaje empaquetado:");
        System.out.println(Arrays.toString(packedMessage));

        // Agregar el encabezado al mensaje
        byte[] header = handler.parseHexBinary("6000000000");
        byte[] messageWithHeader = new byte[header.length + packedMessage.length];
        System.arraycopy(header, 0, messageWithHeader, 0, header.length);
        System.arraycopy(packedMessage, 0, messageWithHeader, header.length, packedMessage.length);
        System.out.println("Encabezado agregado al mensaje:");
        System.out.println(Arrays.toString(messageWithHeader));

        // Calcular la longitud del mensaje completo (encabezado + mensaje)
        int messageLength = messageWithHeader.length;
        System.out.println("Longitud del mensaje completo (encabezado + mensaje): " + messageLength);

        // Convertir esa longitud a un formato adecuado ( hexadecimal)
        String lengthHexString = String.format("%04x", messageLength); // Asegurarse de que la longitud siempre tenga 4 caracteres (2 bytes)
        System.out.println("Longitud del mensaje en formato hexadecimal: " + lengthHexString);

        // el mensaje final concatenando la longitud, el encabezado y el mensaje
        byte[] lengthBytes = handler.parseHexBinary(lengthHexString);
        byte[] finalMessage = new byte[lengthBytes.length + messageWithHeader.length];
        System.arraycopy(lengthBytes, 0, finalMessage, 0, lengthBytes.length);
        System.arraycopy(messageWithHeader, 0, finalMessage, lengthBytes.length, messageWithHeader.length);
        System.out.println("Mensaje final preparado:");
        System.out.println(Arrays.toString(finalMessage));


        String finalMessageHex = new BigInteger(1, finalMessage).toString(16);
        System.out.println("Mensaje final en formato hexadecimal: " + finalMessageHex);

        return finalMessage;
    }




}
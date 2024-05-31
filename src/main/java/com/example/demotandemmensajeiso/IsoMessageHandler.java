package com.example.demotandemmensajeiso;

import lombok.Getter;
import lombok.Setter;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.logging.Logger;


/** Clase auxiliar para manejar la configuración del packager y proporcionar métodos compartidos
 * - Empaquetar un msj iso
 * - Desempaquetar un msj iso
 * - Imprimir en formato xml un msj iso. */
@Getter @Setter
public class IsoMessageHandler {
    private static final Logger logger = Logger.getLogger(IsoClient.class.getName());

    private GenericPackager packager;


    public IsoMessageHandler() throws ISOException {
        try {
            InputStream cfg = getClass().getResourceAsStream("/iso87binary.xml");          // Carga el packager desde el archivo xml
            packager = new GenericPackager(cfg);                                                // Inicializa el packager con la configuración cargada
        } catch (ISOException e) {
            System.err.println("Error initializing packager: " + e.getMessage());
            throw e;
        }
    }



    /** Método para empaquetar un mensaje en binario */
    public byte[] packMessage(ISOMsg msg) {
        try {
            byte[] packedMessage = msg.pack();                                      // Empaqueta el mensaje ISO en un array de bytes

            return packedMessage;
        } catch (ISOException e) {
            System.err.println("Error packing message: " + e.getMessage());
            return null;
        }
    }


    /** Método para desempaquetar un mensaje binario  */
    public ISOMsg unpackMessage(byte[] binaryMessage) {
        try {
            ISOMsg msg = new ISOMsg();                     // Crea un nuevo mensaje ISO
            msg.setPackager(packager);                     // Establece el packager para el mensaje

            byte[] aux = Arrays.copyOfRange(binaryMessage, 7, binaryMessage.length); // le sacamos la longitus (2 bytes) y el header (5 bytes) para poder desempaquetarlo. Ya que si no sacamos esto no lo desempaqueta
            msg.unpack(aux);

            return msg;
        } catch (ISOException e) {
            System.err.println("Error unpacking message: " + e.getMessage());
            if (e.getMessage().contains("Field length")) {     //si se produce una ISOException y el mensaje de la excepción contiene la cadena "Field length", entonces se imprime un mensaje de error adicional y se ignora el mensaje.
                System.err.println("Received message with field that is too long. Ignoring message.");
            }
            return null;
        }
    }


    /** Método para convertir una cadena hexadecimal en un array de bytes */
    public byte[] parseHexBinary(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }


    /** Método para convertir un array de bytes en una cadena hexadecimal */
    public String parseBytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }


    /** Método para convertir una cadena hexadecimal a un mensaje ISO  */
    public ISOMsg parseHexToIsoMsg(String hexString) throws ISOException {
        byte[] messageBytes = parseHexBinary(hexString);
        ISOMsg unpackedMessage = unpackMessage(messageBytes);
        return unpackedMessage;
    }


    /** Imprimir el Mensaje completo en formato xml  */
    public void printMessageXMLFormat(ISOMsg msg) {
        try {
            ByteArrayOutputStream aux = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(aux);
            msg.dump(ps, "");
            String content = new String(aux.toByteArray(), StandardCharsets.UTF_8);
            String callingClassName = new Exception().getStackTrace()[1].getClassName();             // Obtengo el nombre de la clase que llamó a este método
            String messageType = callingClassName.contains("IsoMessageCreator") ? "Sent MSG" : "Received MSG";
            System.out.println(messageType + ": \n" + content + "\n");
        } catch (Exception e) {
            System.err.println("Error printing message: " + e.getMessage());
        }
    }



}


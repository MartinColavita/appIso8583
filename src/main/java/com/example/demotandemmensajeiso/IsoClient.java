package com.example.demotandemmensajeiso;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Logger;



/** Clase que se encargará de establecer la conexión con un servidor y enviar el mensaje ISO 8583  */
public class IsoClient {
    private String host;
    private int port;
    private IsoMessageHandler handler;
    private IsoMessageReciver reciver;
    private static final Logger logger = Logger.getLogger(IsoClient.class.getName());


    public IsoClient(String host, int port, IsoMessageHandler handler) throws IOException {
        this.host = host;                                       // Dirección IP del servidor
        this.port = port;                                       // Puerto del servidor
        this.handler = handler;                                 // Instancia de IsoMessageHandler
    }



    /** Método para enviar un mensaje al servidor y recibir una respuesta */
    public void sendAndReceiveMessage(byte[] message) throws IOException, ISOException {
        try (Socket socket = new Socket(host, port);  // Crea un nuevo socket con la dirección IP y el puerto especificados
             DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());  // Se crea un DataOutputStream para enviar el mensaje a través del socket
             DataInputStream dataInputStream = new DataInputStream(socket.getInputStream())) {  // Se crea un DataInputStream para leer datos del socket
            socket.setSoTimeout(30000);                                                         // Set timeout de 30 segundos
            sendMessage(dataOutputStream, message);
            receiveResponse(dataInputStream);
        }
    }


    /** Método para enviar el mensaje al servidor JPOS */
    public void sendMessage(DataOutputStream dataOutputStream, byte[] message) throws IOException, ISOException {
        logger.info("Connecting to server...");
        logger.info("Connection established with server " + host + ":" + port);
        logger.info("Sending message: " + Arrays.toString(message));
        logger.info("Message sent in Hexadecimal: " + handler.parseBytesToHex(message));
        dataOutputStream.write(message);                                                                    // Se escribe el mensaje en el DataOutputStream
        dataOutputStream.flush();                                                                           // Se vacía el DataOutputStream, asegurando que el mensaje se envíe
        logger.info("Message sent, waiting for response...");
        logger.info("...");
    }


    /** Método para recibir una respuesta del servidor */
    public void receiveResponse(DataInputStream dataInputStream) throws IOException, ISOException {
        byte[] response = readResponse(dataInputStream);
        reciver = new IsoMessageReciver(handler);

        if (response != null) {
            logger.info("Received response: " + Arrays.toString(response));
            logger.info("Response received in Hexadecimal: " + handler.parseBytesToHex(response));
            reciver.clientReceiveMessage(response);
        } else {
            logger.warning("No response received.");
        }
    }


    /** Método para recibir una respuesta del servidor */
    private byte[] readResponse(DataInputStream in) throws IOException {

        // Leer la longitud del mensaje del DataInputStream (2 bytes)
        byte[] lengthBytes = new byte[2];
        in.readFully(lengthBytes);

        // Convertir la longitud del mensaje a un entero
        int length = ((lengthBytes[0] & 0xff) << 8) | (lengthBytes[1] & 0xff);

        // Crear un array de bytes para almacenar el mensaje completo (longitud del mensaje + bytes de longitud)
        byte[] fullMessage = new byte[length + 2];

        // Leer el mensaje del DataInputStream y almacenar en el mensaje completo
        in.readFully(fullMessage, 2, length);

        // Copiar los bytes de longitud al inicio del mensaje completo
        System.arraycopy(lengthBytes, 0, fullMessage, 0, 2);

        return fullMessage;
    }


}

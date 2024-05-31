package com.example.demotandemmensajeiso;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.ISO87BPackager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.HexFormat;
import java.util.Scanner;

@SpringBootApplication
public class DemoTandemMensajeIsoApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(DemoTandemMensajeIsoApplication.class, args);

            IsoMessageHandler handler = new IsoMessageHandler();

            Scanner scanner = new Scanner(System.in);
            String mode = "";
            do {
                System.out.println("Choose the startup mode of the app: client, server or test: ");
                mode = scanner.nextLine();

                if (mode.equals("client")) {
                    System.out.println("####  App running in client mode. ####");

                    IsoClient client = new IsoClient("127.0.0.1", 7940, handler);          // Instancia un nuevo cliente con ip , puerto y handler
                    IsoMessageCreator creator = new IsoMessageCreator(handler);                     // Instancia un nuevo creador de mensajes

                    /*
                    ISOMsg message = creator.createMessage();                                        // Crea un mensaje
                    byte[] packedMessage = handler.packMessage(message);                                // Empaqueta el mensaje
                    client.sendAndReceiveMessage(packedMessage);                                        // Envia el mensaje
                    */



                    byte[] message = creator.TestMsgPacketSender();
                    client.sendAndReceiveMessage(message);

                } else if (mode.equals("server")){
                    System.out.println("####  App running in server mode. ####");
                    IsoServer server = new IsoServer(handler);                                      // Instancia un nuevo servidor
                    server.startServer();                                                           // Inicia el servidor
                } else if (mode.equals("test")) {
                    System.out.println("####  App running in test mode. ####");
                    processTestMessage(handler);
                } else {
                    System.out.println("Invalid mode. Please choose client, server or test.");
                }
            } while (!mode.equals("client") && !mode.equals("server") && !mode.equals("test"));

        } catch (ISOException e) {
            System.err.println("Error: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /** PARA HACER PRUEBASSS q este funcionando  */
    private static void processTestMessage(IsoMessageHandler handler) throws ISOException {
        // Inicializa las clases SendMsj y IsoMessageReciver
        IsoMessageCreator isoMessageCreator = new IsoMessageCreator(handler);

        // Crea un mensaje
        ISOMsg message = isoMessageCreator.createMessage();
        System.out.println("....");
        System.out.println("Mensaje creado...");

        // Empaqueta el mensaje
        byte[] packedMessage = handler.packMessage(message);
        System.out.println("....");

        // Desempaqueta el mensaje
        ISOMsg messageUnpacked = handler.unpackMessage(packedMessage);
        System.out.println("....");

    }


}
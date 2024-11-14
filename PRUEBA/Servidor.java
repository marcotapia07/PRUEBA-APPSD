import java.net.*;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Servidor {
    private static final int PUERTO = 5000;

    public static void main(String[] args) {
        try (DatagramSocket socketServidor = new DatagramSocket(PUERTO)) {
            ExecutorService pool = Executors.newFixedThreadPool(10);
            System.out.println("Servidor en espera de clientes...");

            while (true) {
                byte[] bufferRecibido = new byte[1024];
                DatagramPacket paqueteRecibido = new DatagramPacket(bufferRecibido, bufferRecibido.length);
                
                // Esperar a que un cliente envíe un mensaje inicial
                socketServidor.receive(paqueteRecibido);

                String mensajeInicial = new String(paqueteRecibido.getData(), 0, paqueteRecibido.getLength());
                System.out.println("Conexión recibida: " + mensajeInicial);

                // Enviar bienvenida al cliente
                String mensajeBienvenida = "¡Bienvenido al cuestionario!";
                socketServidor.send(new DatagramPacket(mensajeBienvenida.getBytes(), mensajeBienvenida.length(), paqueteRecibido.getAddress(), paqueteRecibido.getPort()));

                // Preguntas y respuestas
                String[] preguntas = {
                    "¿Cuál es la capital de Francia?",
                    "¿Cuánto es 2 + 2?",
                    "¿En qué año se descubrió América?",
                    "¿Cuál es el planeta más grande del sistema solar?",
                    "¿Cuál es el océano más grande?"
                };

                String[] respuestas = {"paris", "4", "1492", "jupiter", "pacifico"};

                pool.execute(new HiloClienteServ(socketServidor, paqueteRecibido.getAddress(), paqueteRecibido.getPort(), preguntas, respuestas));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
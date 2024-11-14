import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class Cliente {
    private static final String SERVIDOR_IP = "localhost";
    private static final int PUERTO_SERVIDOR = 5000;

    public static void main(String[] args) {
        try (
            DatagramSocket socketCliente = new DatagramSocket();
            Scanner scanner = new Scanner(System.in)) {
            
            InetAddress direccionServidor = InetAddress.getByName(SERVIDOR_IP);

            // Enviar mensaje de conexión
            String mensajeConexion = "Solicitud de conexión";
            byte[] bufferConexion = mensajeConexion.getBytes("UTF-8");
            DatagramPacket paqueteConexion = new DatagramPacket(bufferConexion, bufferConexion.length, direccionServidor, PUERTO_SERVIDOR);
            socketCliente.send(paqueteConexion);

            // Recibir bienvenida del servidor
            byte[] bufferBienvenida = new byte[1024];
            DatagramPacket paqueteBienvenida = new DatagramPacket(bufferBienvenida, bufferBienvenida.length);
            socketCliente.receive(paqueteBienvenida);
            String mensajeBienvenida = new String(paqueteBienvenida.getData(), 0, paqueteBienvenida.getLength(), "UTF-8");
            System.out.println(mensajeBienvenida);

            // Ciclo para recibir preguntas y enviar respuestas
            while (true) {
                byte[] bufferPregunta = new byte[1024];
                DatagramPacket paquetePregunta = new DatagramPacket(bufferPregunta, bufferPregunta.length);
                socketCliente.receive(paquetePregunta);
                String pregunta = new String(paquetePregunta.getData(), 0, paquetePregunta.getLength(), "UTF-8");

                if (pregunta.equalsIgnoreCase("Fin del juego.")) {
                    System.out.println("Cuestionario completado.");
                    break;
                }

                System.out.println("Pregunta: " + pregunta);

                System.out.print("Respuesta: ");
                String respuesta = scanner.nextLine();
                byte[] bufferRespuesta = respuesta.getBytes("UTF-8");
                DatagramPacket paqueteRespuesta = new DatagramPacket(bufferRespuesta, bufferRespuesta.length, direccionServidor, PUERTO_SERVIDOR);
                socketCliente.send(paqueteRespuesta);

                // Recibir confirmación del servidor
                byte[] bufferConfirmacion = new byte[1024];
                DatagramPacket paqueteConfirmacion = new DatagramPacket(bufferConfirmacion, bufferConfirmacion.length);
                socketCliente.receive(paqueteConfirmacion);
                String confirmacion = new String(paqueteConfirmacion.getData(), 0, paqueteConfirmacion.getLength(), "UTF-8");
                System.out.println(confirmacion);
            }

            // Recibir puntaje final
            byte[] bufferPuntaje = new byte[1024];
            DatagramPacket paquetePuntaje = new DatagramPacket(bufferPuntaje, bufferPuntaje.length);
            socketCliente.receive(paquetePuntaje);
            String puntajeFinal = new String(paquetePuntaje.getData(), 0, paquetePuntaje.getLength(), "UTF-8");
            System.out.println(puntajeFinal);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
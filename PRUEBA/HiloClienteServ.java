import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HiloClienteServ extends Thread {
    private DatagramSocket socket;
    private InetAddress direccionCliente;
    private int puertoCliente;
    private String[] preguntas;
    private String[] respuestas;
    private int puntaje;

    public HiloClienteServ(DatagramSocket socket, InetAddress direccionCliente, int puertoCliente, String[] preguntas, String[] respuestas) {
        this.socket = socket;
        this.direccionCliente = direccionCliente;
        this.puertoCliente = puertoCliente;
        this.preguntas = preguntas;
        this.respuestas = respuestas;
        this.puntaje = 0; // Inicializar puntaje
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < preguntas.length; i++) {
                // Enviar pregunta al cliente
                byte[] datosPregunta = preguntas[i].getBytes();
                DatagramPacket paquetePregunta = new DatagramPacket(datosPregunta, datosPregunta.length, direccionCliente, puertoCliente);
                socket.send(paquetePregunta);

                // Esperar respuesta del cliente
                byte[] bufferRespuesta = new byte[1024];
                DatagramPacket paqueteRespuesta = new DatagramPacket(bufferRespuesta, bufferRespuesta.length);
                socket.receive(paqueteRespuesta);
                
                String respuestaCliente = new String(paqueteRespuesta.getData(), 0, paqueteRespuesta.getLength()).trim();
                
                // Registrar la respuesta en un archivo
                registrarRespuesta(respuestaCliente);

                boolean esCorrecta = respuestaCliente.equalsIgnoreCase(respuestas[i]);
                
                if (esCorrecta) {
                    puntaje += 4; // Sumar puntos por respuesta correcta
                    String mensajeCorrecto = "¡Correcto! +4 puntos";
                    socket.send(new DatagramPacket(mensajeCorrecto.getBytes(), mensajeCorrecto.length(), direccionCliente, puertoCliente));
                } else {
                    String mensajeIncorrecto = "Incorrecto. La respuesta correcta era: " + respuestas[i];
                    socket.send(new DatagramPacket(mensajeIncorrecto.getBytes(), mensajeIncorrecto.length(), direccionCliente, puertoCliente));
                }
            }

            // Enviar puntaje final al cliente
            String mensajeFinal = "Fin del juego. Tu puntaje final: " + puntaje + " / 20 ";
            socket.send(new DatagramPacket(mensajeFinal.getBytes(), mensajeFinal.length(), direccionCliente, puertoCliente));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void registrarRespuesta(String respuesta) {
        try (FileWriter writer = new FileWriter("respuestas.txt", true)) {
            String fechaHora = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String ipOrigen = direccionCliente.getHostAddress();
            String mensajeRegistro = String.format("%s - IP: %s - Respuesta: %s%n", fechaHora, ipOrigen, respuesta);
            writer.write(mensajeRegistro);
        } catch (IOException e) {
            System.out.println("Error al registrar la respuesta: " + e.getMessage());
        }
    }
}
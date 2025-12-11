package gestor;

import entidades.*;
import eventos.*;
import acciones.*;
import gestor.GestorPrincipal;
import gestor.ManejadorCliente;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import modeloLogicoServidor.TableroModeloLogico;

public class Partida {

    private final String codigoSala;
    private final GestorPrincipal gestor;
    private final TableroModeloLogico modeloLogico;
    private final List<ManejadorCliente> manejadores = Collections.synchronizedList(new ArrayList<>());
    private final List<Jugador> jugadoresConfigurados = Collections.synchronizedList(new ArrayList<>());
    private boolean partidaIniciada = false;

    public Partida(String codigoSala, GestorPrincipal gestor) {
        this.codigoSala = codigoSala;
        this.gestor = gestor;
        this.modeloLogico = new TableroModeloLogico(
                JuegoConfig.TAMANIO_TABLERO,
                jugadoresConfigurados
        );
        System.out.println("[PARTIDA " + codigoSala + "] Creada.");
    }

    /**
     * Agrega un nuevo jugador (conexión) a la partida.
     */
    public synchronized void agregarJugador(ManejadorCliente manejador) {
        if (partidaIniciada) {
            manejador.enviarError("La partida ya ha comenzado.");
            return;
        }
        if (manejadores.size() >= 4) {
            manejador.enviarError("La sala está llena.");
            return;
        }
        manejadores.add(manejador);
        manejador.setPartidaActual(this);
        manejador.enviarDTO(new RespuestaUnionExitosa(generarEstadoDTO()));
        System.out.println("[PARTIDA " + codigoSala + "] Jugador " + manejador.getIdJugador() + " se unió. Total: " + manejadores.size());
    }

    public synchronized void configurarJugador(ManejadorCliente manejador, Jugador jugadorCliente) {
        // 'jugadorCliente' es el DTO que viene del cliente (con ID=0)
        Jugador jugadorActualDelManejador = manejador.getJugador();

        for (Jugador jugadorExistente : jugadoresConfigurados) {

            // Si el jugador se está re-configurando, no debe compararse consigo mismo.
            // Si el manejador ya tiene un ID y coincide con el de la lista,
            // significa que es el mismo jugador. Lo saltamos.
            if (jugadorActualDelManejador != null
                    && jugadorActualDelManejador.id() == jugadorExistente.id()) {
                continue;
            }

            // Comprobar Nombre
            if (jugadorExistente.nombre().equalsIgnoreCase(jugadorCliente.nombre())) {
                manejador.enviarError("El nombre '" + jugadorCliente.nombre() + "' ya está en uso.");
                return; // Detiene la configuración
            }

            // Comprobar Color
            // Usamos Objects.equals para manejar de forma segura si algun color fuera null
            if (Objects.equals(jugadorExistente.color(), jugadorCliente.color())) {
                manejador.enviarError("Ese color ya fue elegido.");
                return; // Detiene la configuración
            }

            // Comprobar Avatar
            if (jugadorExistente.avatarPath().equals(jugadorCliente.avatarPath())) {
                manejador.enviarError("Ese avatar ya fue elegido.");
                return; // Detiene la configuración
            }
        }

        // Si pasa todas las validaciones, continúa con la lógica de asignación de ID
        int idAsignado;
        boolean yaExisteEnLista;

        // Determinar el ID
        // Si el manejador ya tiene un Jugador ASIGNADO POR NOSOTROS (con ID > 0),
        // significa que es una re-configuración (ej. cambió su nombre).
        if (jugadorActualDelManejador != null && jugadorActualDelManejador.id() > 0) {
            idAsignado = jugadorActualDelManejador.id(); // Reusamos el ID
            yaExisteEnLista = true;
        } else {
            // Es un jugador nuevo (o uno que aún tenía ID=0).
            // Le asignamos un nuevo ID basado en el tamaño de la lista.
            idAsignado = jugadoresConfigurados.size() + 1; // ID 1 para el Host
            yaExisteEnLista = false;
        }

        // Crear el objeto Jugador final con el ID correcto
        Jugador jugadorConId = new Jugador(
                idAsignado,
                jugadorCliente.nombre(),
                jugadorCliente.avatarPath(),
                jugadorCliente.color()
        );

        // Actualizar el Manejador (¡Corrige el bug del Host!)
        // Esto asegura que manejador.getJugador().id() sea 1 (para el host).
        manejador.setJugador(jugadorConId);

        // Actualizar la Lista (¡Corrige el bug de "no se ven jugadores"!)
        if (yaExisteEnLista) {
            // Reemplazar al jugador en la lista
            for (int i = 0; i < jugadoresConfigurados.size(); i++) {
                if (jugadoresConfigurados.get(i).id() == idAsignado) {
                    jugadoresConfigurados.set(i, jugadorConId);
                    break;
                }
            }
        } else {
            // Agregar el NUEVO jugador a la lista
            jugadoresConfigurados.add(jugadorConId);
        }

        System.out.println("[PARTIDA " + codigoSala + "] Jugador " + manejador.getIdJugador() + " configurado como: " + jugadorConId.nombre() + " (ID: " + idAsignado + ")");
        distribuirEstadoATodos();
    }

    public synchronized void iniciarPartida(ManejadorCliente manejador) {
        if (manejador.getJugador() == null || manejador.getJugador().id() != 1) {
            manejador.enviarError("Solo el Host (Jugador 1) puede iniciar la partida.");
            return;
        }
        if (jugadoresConfigurados.size() < 2) {
            manejador.enviarError("Se necesitan al menos 2 jugadores para iniciar.");
            return;
        }
        partidaIniciada = true;
        modeloLogico.iniciarTurno();
        System.out.println("[PARTIDA " + codigoSala + "] ¡Partida iniciada por Host!");
        distribuirDTOaTodos(new EventoPartidaIniciada());
        distribuirEstadoATodos();
    }

    public synchronized void reclamarLinea(ManejadorCliente manejador, AccionReclamarLinea accion) {
        Jugador jugador = manejador.getJugador();
        if (!partidaIniciada) {
            manejador.enviarError("La partida aún no ha comenzado.");
            return;
        }
        if (jugador.id() != modeloLogico.getJugadorActual().id()) {
            manejador.enviarError("No es tu turno.");
            return;
        }
        boolean movimientoValido = modeloLogico.agregarLinea(
                accion.getFila(),
                accion.getColumna(),
                accion.isHorizontal(),
                jugador.id()
        );
        if (!movimientoValido) {
            manejador.enviarError("Esa línea ya está ocupada.");
            return;
        }
        boolean completoCuadrado = modeloLogico.verificarCuadrados(jugador.id());
        if (!completoCuadrado) {
            modeloLogico.siguienteTurno();
        }
        distribuirEstadoATodos();

        // 3. NUEVO: Verificar si el juego terminó y notificar ganador
        if (modeloLogico.isJuegoTerminado()) {
            Jugador ganador = modeloLogico.obtenerGanador();
            String mensaje = (ganador == null) ? "¡Es un empate!" : "¡Ganador: " + ganador.nombre() + "!";

            EventoPartidaTerminada eventoFin = new EventoPartidaTerminada(
                    ganador,
                    modeloLogico.getPuntajes(),
                    mensaje
            );

            System.out.println("[PARTIDA " + codigoSala + "] Juego terminado. " + mensaje);
            distribuirDTOaTodos(eventoFin);
        }
    }

    public synchronized void eliminarJugador(ManejadorCliente manejador) {
        manejadores.remove(manejador);
        if (manejador.getJugador() != null) {
            jugadoresConfigurados.removeIf(j -> j.id() == manejador.getJugador().id());
        }
        System.out.println("[PARTIDA " + codigoSala + "] Jugador " + manejador.getIdJugador() + " desconectado. Restantes: " + manejadores.size());
        // 2. LÓGICA DE FIN DE PARTIDA / GANADOR POR ABANDONO
        int jugadoresRestantes = manejadores.size();

        if (jugadoresRestantes == 0) {
            // La partida se queda vacía
            gestor.eliminarPartida(codigoSala);

        } else if (partidaIniciada && jugadoresRestantes == 1) {
            // ¡ÚNICO JUGADOR RESTANTE ES EL GANADOR!
            ManejadorCliente manejadorGanador = manejadores.get(0);
            Jugador ganador = manejadorGanador.getJugador();

            System.out.println("[PARTIDA " + codigoSala + "] ¡FIN POR ABANDONO! Ganador: " + ganador.nombre());

            // Creamos el evento de partida terminada.
            // Nota: El puntaje actual del ganador será el real, y el resto serán 0.
            // El puntaje es irrelevante para tu requerimiento, pero se envía el objeto completo.
            EventoPartidaTerminada eventoFin = new EventoPartidaTerminada(
                    ganador,
                    modeloLogico.getPuntajes(),
                    "¡Felicidades, has ganado por abandono de tus oponentes!"
            );

            // Enviamos el evento de fin de partida al único jugador restante.
            distribuirDTOaTodos(eventoFin);

            // Finalizamos y eliminamos la sala
            gestor.eliminarPartida(codigoSala);

        } else if (partidaIniciada && jugadoresRestantes > 1) {
            // La partida sigue (más de 1 jugador).
            // Si el jugador que se fue tenía el turno, el turno debe avanzar.
            // Haremos que el turno avance solo si el jugador que se fue era el actual
            // y si el juego no está terminando (esto requiere lógica adicional compleja).
            // Por ahora, solo actualizamos el estado:
            distribuirEstadoATodos();

        } else {
            // Partida en lobby con > 1 jugador.
            distribuirEstadoATodos();
        }
    }

    private void distribuirEstadoATodos() {
        EstadoPartidaDTO estadoActual = generarEstadoDTO();
        distribuirDTOaTodos(new EventoEstadoActualizado(estadoActual));
    }

    private void distribuirDTOaTodos(Object dto) {
        for (ManejadorCliente m : new ArrayList<>(manejadores)) {
            m.enviarDTO(dto);
        }
    }

    private EstadoPartidaDTO generarEstadoDTO() {
        return new EstadoPartidaDTO(
                this.codigoSala,
                modeloLogico.getTamaño(),
                modeloLogico.getLineasHorizontales(),
                modeloLogico.getLineasVerticales(),
                modeloLogico.getCuadrados(),
                modeloLogico.getJugadorActualIdx(),
                new ArrayList<>(jugadoresConfigurados), // Esta lista ahora se poblará correctamente
                modeloLogico.getPuntajes(),
                modeloLogico.getTotalCuadrados(),
                modeloLogico.getCuadradosCompletados(),
                modeloLogico.isJuegoTerminado()
        );
    }

}

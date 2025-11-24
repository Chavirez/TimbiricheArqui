package filtros;

//package filtros;
//
//import interfaces.IEscuchadorDeAplicacion;
//
///**
// * SINK DE ENTRADA (Sumidero de la tubería de Recepción).
// * * Responsabilidad: Es el punto final de la tubería de entrada. Recibe el objeto
// * de dominio ya procesado (desempaquetado y deserializado) y lo entrega
// * a la aplicación a través de la interfaz IEscuchadorDeAplicacion.
// * * Arquitectura: Implementa inyección estricta por constructor para garantizar
// * que la tubería solo exista si hay un destinatario válido.
// */
//public class EntregaAppFilter extends FilterBase<Object, Void> {
//
//    private final IEscuchadorDeAplicacion escuchador;
//
//    /**
//     * Crea el filtro final con su dependencia obligatoria.
//     * @param escuchador La interfaz de la aplicación que recibirá el objeto.
//     * @throws IllegalArgumentException si el escuchador es nulo.
//     */
//    public EntregaAppFilter(IEscuchadorDeAplicacion escuchador) {
//        super();
//        if (escuchador == null) {
//            throw new IllegalArgumentException("EntregaAppFilter requiere un escuchador válido. No se puede entregar a 'null'.");
//        }
//        this.escuchador = escuchador;
//    }
//
//    @Override
//    public void procesar(Object entrada) {
//        // El efecto final (Side Effect) de la tubería de entrada:
//        escuchador.alRecibirDato(entrada);
//    }
//}
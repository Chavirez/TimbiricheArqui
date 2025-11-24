package interfaces;

/**
 * Define el contrato para el componente de red que envía datos.
 * Es la abstracción que usa la capa de arquitectura para enviar
 * datos serializados a la red.
 */
public interface IDispatcher {
    /**
     * Envía un string de datos (usualmente JSON) a la red.
     * @param datoSerializado El string con los datos serializados.
     */
    void enviar(String datoSerializado);
}
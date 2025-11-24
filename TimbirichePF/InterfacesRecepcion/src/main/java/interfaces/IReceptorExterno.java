package interfaces;

/**
 * Define el contrato para el componente de arquitectura que recibe
 * datos desde la red.
 */
public interface IReceptorExterno {
    /**
     * Método invocado por el componente de red cuando un nuevo
     * mensaje (string) es recibido desde el exterior.
     * @param datoSerializado El string con los datos serializados.
     */
    void alRecibirMensaje(String datoSerializado);
}
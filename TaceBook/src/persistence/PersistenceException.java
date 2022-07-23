/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence;

/**
 *
 * @author @author Jorge Val Gil e Adrián Fernández Pérez
 */
public class PersistenceException extends Exception {

    /**
     * Atributo code
     */
    private int code;

    /**
     * Constantes que le dan valor al atributo code
     */
    public static final int CONECTION_ERROR = 0;

    public static final int CANNOT_READ = 1;

    public static final int CANNOT_WRITE = 2;

    /**
     * Método get del atributo code
     *
     * @return Devuelve el atributo code
     */
    public int getCode() {
        return code;
    }

    /**
     * Método set del atributo code
     *
     * @param code
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * Constructor de la clase
     *
     * @param code
     * @param exception
     */
    public PersistenceException(int code, String exception) {
        super(exception);
        this.code = code;
    }

}

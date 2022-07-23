/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence;

import model.Message;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Jorge Val Gil e Adrián Fernández Pérez
 */
public class MessageDB {

    /**
     * Método que almacena unha nova mensaxe
     *
     * @param message mensaxe
     * @throws persistence.PersistenceException
     */
    public static void save(Message message) throws PersistenceException {
        Connection c = TacebookDB.getConnection();
        try {
            PreparedStatement pst = c.prepareStatement("INSERT INTO MESSAGE (TEXT,DATE,ISREAD,SOURCE,DESTINATION) VALUES (?,?,?,?,?)");
            pst.setString(1, message.getText());
            pst.setTimestamp(2, new java.sql.Timestamp(message.getDate().getTime()));
            pst.setBoolean(3, message.isRead());
            pst.setString(4, message.getSourceProfile().getName());
            pst.setString(5, message.getDestProfile().getName());
            pst.execute();
            pst.close();
        } catch (SQLException ex) {
            throw new PersistenceException(PersistenceException.CANNOT_WRITE, ex.getMessage());
        }
    }

    /**
     * Método que actualiza a información dunha mensaxe
     *
     * @param message mensaxe
     * @throws persistence.PersistenceException
     */
    public static void update(Message message) throws PersistenceException {
        Connection c = TacebookDB.getConnection();
        try {
            PreparedStatement pst = c.prepareStatement("UPDATE MESSAGE SET TEXT=?, ISREAD=? WHERE ID=?");
            pst.setString(1, message.getText());
            pst.setBoolean(2, message.isRead());
            pst.setInt(3, message.getId());
            pst.execute();
            pst.close();
        } catch (SQLException ex) {
            throw new PersistenceException(PersistenceException.CANNOT_WRITE, ex.getMessage());
        }
    }

    /**
     * Método que borra unha mensaxe
     *
     * @param message mensaxe
     * @throws persistence.PersistenceException
     */
    public static void remove(Message message) throws PersistenceException {
        Connection c = TacebookDB.getConnection();
        try {
            PreparedStatement pst = c.prepareStatement("DELETE FROM MESSAGE WHERE ID=?");
            pst.setInt(1, message.getId());
            pst.execute();
            pst.close();
        } catch (SQLException ex) {
            throw new PersistenceException(PersistenceException.CANNOT_WRITE, ex.getMessage());
        }
    }
}

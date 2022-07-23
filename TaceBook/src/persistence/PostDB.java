/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence;

import model.Profile;
import model.Post;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Jorge Val Gil e Adrián Fernández Pérez
 */
public class PostDB {

    /**
     * Método que almacena unha nova publicación
     *
     * @param post Publicación
     * @throws persistence.PersistenceException
     */
    public static void save(Post post) throws PersistenceException {
        Connection c = TacebookDB.getConnection();
        try {
            PreparedStatement pst = c.prepareStatement("INSERT INTO POST (TEXT, DATE, PROFILE, AUTHOR) VALUES(?,?,?,?)");
            pst.setString(1, post.getText());
            pst.setTimestamp(2, new java.sql.Timestamp(post.getDate().getTime()));
            pst.setString(3, post.getProfile().getName());
            pst.setString(4, post.getAuthor().getName());
            pst.execute();
            pst.close();
        } catch (SQLException ex) {
            throw new PersistenceException(PersistenceException.CANNOT_WRITE, ex.getMessage());
        }
    }

    /**
     * Método que garda un like sobre unha publicación
     *
     * @param post Publicación
     * @param profile Perfil
     * @throws persistence.PersistenceException
     */
    public static void saveLike(Post post, Profile profile) throws PersistenceException {
        Connection c = TacebookDB.getConnection();
        try {
            PreparedStatement pst = c.prepareStatement("INSERT INTO PROFILELIKESPOST VALUES (?,?)");
            pst.setInt(1, post.getId());
            pst.setString(2, profile.getName());
            pst.execute();
            pst.close();
        } catch (SQLException ex) {
            throw new PersistenceException(PersistenceException.CANNOT_WRITE, ex.getMessage());
        }
    }
}

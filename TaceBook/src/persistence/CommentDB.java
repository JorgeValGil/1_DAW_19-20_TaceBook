/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence;

import model.Comment;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Jorge Val Gil e Adrián Fernández Pérez
 */
public class CommentDB {

    /**
     * Método que almacena un novo comentario
     *
     * @param comment comentario
     * @throws persistence.PersistenceException
     */
    public static void save(Comment comment) throws PersistenceException {
        Connection c = TacebookDB.getConnection();
        try {
            PreparedStatement pst = c.prepareStatement("INSERT INTO COMMENT (TEXT,DATE,AUTHOR,IDPOST) VALUES(?,?,?,?)");
            pst.setString(1, comment.getText());
            pst.setTimestamp(2, new java.sql.Timestamp(comment.getDate().getTime()));
            pst.setString(3, comment.getSourceProfile().getName());
            pst.setInt(4, comment.getPost().getId());
            pst.execute();
            pst.close();
        } catch (SQLException ex) {
            throw new PersistenceException(PersistenceException.CANNOT_WRITE, ex.getMessage());
        }
    }
}

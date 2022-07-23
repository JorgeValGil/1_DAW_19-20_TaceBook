/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import model.Comment;
import model.Message;
import model.Post;
import model.Profile;

/**
 * Clase que implementa a persistencia dos perfis
 *
 * @author Jorge Val Gil e Adrián Fernández Pérez
 */
public class ProfileDB {

    /*
    Método o cal usaremos pra cifrar as contrasinais
     */
    private static String getPasswordHash(String password) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(password.getBytes());
        return new String(messageDigest.digest());
    }

    /**
     * Método que recorre todos os perfis e serve para encontrar o perfil dun
     * usuario introducindo o nome e o número de publicacións do perfil
     *
     * @param name Nome do perfil
     * @param numberOfPosts Número de publicacións
     * @return Devolve o nome do perfil
     * @throws persistence.PersistenceException
     */
    public static Profile findByName(String name, int numberOfPosts) throws PersistenceException {
        Profile foundedprofile = null;
        Connection c = TacebookDB.getConnection();
        try {
            PreparedStatement pst = c.prepareStatement("SELECT * FROM PROFILE WHERE NAME=?");
            pst.setString(1, name);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                foundedprofile = new Profile(name, rs.getString("PASSWORD"), rs.getString("STATUS"));
                loadProfileData(c, foundedprofile);
            }
            rs.close();
            pst.close();
        } catch (SQLException ex) {
            throw new PersistenceException(PersistenceException.CANNOT_READ, ex.getMessage());
        }
        return foundedprofile;
    }

    /**
     * Método que recorre todos os perfis e serve para encontrar o perfil dun
     * usuario introducindo o nome , o contrasinal e o número de publicacións do
     * perfil
     *
     * @param name Nome do perfil
     * @param password Contrasinal do perfil
     * @param numberOfPosts Número de publicacións do perfil
     * @return Devolve o perfil
     * @throws persistence.PersistenceException
     */
    public static Profile findByNameAndPassword(String name, String password, int numberOfPosts) throws PersistenceException {
        Profile foundedprofile = null;
        Connection c = TacebookDB.getConnection();
        try {
            PreparedStatement pst = c.prepareStatement("SELECT * FROM PROFILE WHERE NAME=? AND PASSWORD=?");
            pst.setString(1, name);
            pst.setString(2, getPasswordHash(password));
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                foundedprofile = new Profile(name, rs.getString("PASSWORD"), rs.getString("STATUS"));
                loadProfileData(c, foundedprofile);
            }
            rs.close();
            pst.close();
        } catch (SQLException | NoSuchAlgorithmException ex) {
            throw new PersistenceException(PersistenceException.CANNOT_READ, ex.getMessage());
        }
        return foundedprofile;
    }

    /**
     * Método que almacena o perfil do usuario no almacenamento
     *
     * @param profile Perfil do usuario
     * @throws persistence.PersistenceException
     */
    public static void save(Profile profile) throws PersistenceException {
        Connection c = TacebookDB.getConnection();
        try {
            PreparedStatement pst = c.prepareStatement("INSERT INTO PROFILE VALUES (?,?,?)");
            pst.setString(1, profile.getName());
            pst.setString(2, getPasswordHash(profile.getPassword()));
            pst.setString(3, profile.getStatus());
            pst.execute();
            pst.close();
        } catch (SQLException | NoSuchAlgorithmException ex) {
            throw new PersistenceException(PersistenceException.CANNOT_WRITE, ex.getMessage());
        }
    }

    /**
     * Método que actualiza el perfil en el almacenamiento
     *
     * @param profile Perfil do usuario
     * @throws persistence.PersistenceException
     */
    public static void update(Profile profile) throws PersistenceException {
        Connection c = TacebookDB.getConnection();
        try {
            PreparedStatement pst = c.prepareStatement("UPDATE PROFILE SET STATUS=? WHERE NAME=?");
            pst.setString(1, profile.getStatus());
            pst.setString(2, profile.getName());
            pst.execute();
            pst.close();
        } catch (SQLException ex) {
            throw new PersistenceException(PersistenceException.CANNOT_WRITE, ex.getMessage());
        }
    }

    /**
     * Método que cambia a contrasinal
     *
     * @param profile perfil
     * @throws PersistenceException
     */
    public static void changePassword(Profile profile) throws PersistenceException {
        Connection c = TacebookDB.getConnection();
        try {
            PreparedStatement pst = c.prepareStatement("UPDATE PROFILE SET PASSWORD=? WHERE NAME=?");
            pst.setString(1, profile.getPassword());
            pst.setString(2, profile.getName());
            pst.execute();
            pst.close();
        } catch (SQLException ex) {
            throw new PersistenceException(PersistenceException.CANNOT_WRITE, ex.getMessage());
        }
    }

    /**
     * Méotod que almacena unha nova solicitude de amizade
     *
     * @param destProfile Perfil de destino
     * @param sourceProfile Perfil de orixe
     * @throws persistence.PersistenceException
     */
    public static void saveFrienshipRequest(Profile destProfile, Profile sourceProfile) throws PersistenceException {
        Connection c = TacebookDB.getConnection();
        try {
            PreparedStatement pst = c.prepareStatement("INSERT INTO FRIENDREQUEST VALUES(?,?)");
            pst.setString(1, sourceProfile.getName());
            pst.setString(2, destProfile.getName());
            pst.execute();
            pst.close();
        } catch (SQLException ex) {
            throw new PersistenceException(PersistenceException.CANNOT_WRITE, ex.getMessage());
        }
    }

    /**
     * Método que almacena unha solicitude amizade
     *
     * @param destProfile Perfil de destino
     * @param sourceProfile Perfil de orixe
     * @throws persistence.PersistenceException
     */
    public static void removeFrienshipRequest(Profile destProfile, Profile sourceProfile) throws PersistenceException {
        Connection c = TacebookDB.getConnection();
        try {
            PreparedStatement pst = c.prepareStatement("DELETE FROM FRIENDREQUEST WHERE SOURCEPROFILE=? AND DESTINATIONPROFILE=?");
            pst.setString(1, sourceProfile.getName());
            pst.setString(2, destProfile.getName());
            pst.execute();
            pst.close();
        } catch (SQLException ex) {
            throw new PersistenceException(PersistenceException.CANNOT_WRITE, ex.getMessage());
        }
    }

    /**
     * Método que borra a un amigo
     *
     * @param shownProfile perfil do usuario
     * @param friend Peril do amigo
     * @throws PersistenceException
     */
    public static void removeFriend(Profile shownProfile, Profile friend) throws PersistenceException {
        Connection c = TacebookDB.getConnection();
        try {
            //borramos a tupla onde se garda a relacion de amistade
            PreparedStatement pst = c.prepareStatement("DELETE FROM FRIEND WHERE PROFILE1=? AND PROFILE2=?");
            pst.setString(1, shownProfile.getName());
            pst.setString(2, friend.getName());
            pst.execute();
            pst.close();
            //como pode estar de 2 maneiras distintas, buscámos a tupla das 2 formas
            pst = c.prepareStatement("DELETE FROM FRIEND WHERE PROFILE1=? AND PROFILE2=?");
            pst.setString(1, friend.getName());
            pst.setString(2, shownProfile.getName());
            pst.execute();
            pst.close();
        } catch (SQLException ex) {
            throw new PersistenceException(PersistenceException.CANNOT_WRITE, ex.getMessage());
        }
    }

    /**
     * Método que almacena unha amizade entre dous perfís
     *
     * @param profile1 Un perfil
     * @param profile2 Outro perfil
     * @throws persistence.PersistenceException
     */
    public static void saveFriendship(Profile profile1, Profile profile2) throws PersistenceException {
        Connection c = TacebookDB.getConnection();
        try {
            PreparedStatement pst = c.prepareStatement("INSERT INTO FRIEND VALUES(?,?)");
            pst.setString(1, profile1.getName());
            pst.setString(2, profile2.getName());
            pst.execute();
            pst.close();
        } catch (SQLException ex) {
            throw new PersistenceException(PersistenceException.CANNOT_WRITE, ex.getMessage());
        }
    }

    private static void loadProfileData(Connection c, Profile p) throws SQLException {

        PreparedStatement pst = c.prepareStatement("SELECT * FROM POST WHERE PROFILE=?");
        pst.setString(1, p.getName());
        ResultSet rs = pst.executeQuery();
        while (rs.next()) {
            Post post = new Post(rs.getInt("ID"), new Date(rs.getTimestamp("DATE").getTime()),
                    rs.getString("TEXT"), p, new Profile(rs.getString("AUTHOR"), "", ""));
            p.getPosts().add(post);
            loadPostData(c, post);
        }
        rs.close();
        pst.close();

        //engadir os amigos
        pst = c.prepareStatement("SELECT * FROM FRIEND AS F LEFT JOIN PROFILE AS PROF1 ON F.PROFILE1=PROF1.NAME "
                + "LEFT JOIN PROFILE AS PROF2 ON F.PROFILE2=PROF2.NAME WHERE PROFILE1=? OR PROFILE2=?");
        pst.setString(1, p.getName());
        pst.setString(2, p.getName());
        rs = pst.executeQuery();
        while (rs.next()) {
            if (rs.getString("PROFILE1").equals(p.getName())) {
                Profile friend = new Profile(rs.getString("PROFILE2"), "", rs.getString("STATUS"));
                p.getFriends().add(friend);
            } else {
                Profile friend = new Profile(rs.getString("PROFILE1"), "", rs.getString("STATUS"));
                p.getFriends().add(friend);
            }
        }
        rs.close();
        pst.close();
        //engadir as solicitudes de amizade
        pst = c.prepareStatement("SELECT SOURCEPROFILE FROM FRIENDREQUEST WHERE DESTINATIONPROFILE=?");
        pst.setString(1, p.getName());
        rs = pst.executeQuery();
        while (rs.next()) {
            Profile frienddrequest = new Profile(rs.getString("SOURCEPROFILE"), "", "");
            p.getFriendshipRequests().add(frienddrequest);
        }
        rs.close();
        pst.close();

        //engadir as mensaxes
        pst = c.prepareStatement("SELECT * FROM MESSAGE WHERE DESTINATION=?");
        pst.setString(1, p.getName());
        rs = pst.executeQuery();
        while (rs.next()) {
            Message message = new Message(rs.getInt("ID"), rs.getString("TEXT"), rs.getDate("DATE"),
                    rs.getBoolean("ISREAD"), new Profile(rs.getString("SOURCE"), "", ""),
                    new Profile(rs.getString("DESTINATION"), "", ""));
            p.getMessages().add(message);
        }
        rs.close();
        pst.close();
    }

    private static void loadPostData(Connection c, Post post) throws SQLException {
        PreparedStatement pst = c.prepareStatement("SELECT * FROM COMMENT WHERE IDPOST=?");
        pst.setInt(1, post.getId());
        ResultSet rs = pst.executeQuery();
        while (rs.next()) {
            Comment comment = new Comment(rs.getInt("ID"), new Date(rs.getTimestamp("DATE").getTime()), rs.getString("TEXT"), new Profile(rs.getString("AUTHOR"), "", ""), post);
            post.getComments().add(comment);
        }
        rs.close();
        pst.close();

        pst = c.prepareStatement("SELECT * FROM PROFILELIKESPOST WHERE IDPOST=?");
        pst.setInt(1, post.getId());
        rs = pst.executeQuery();
        while (rs.next()) {
            post.getProfileLikes().add(new Profile(rs.getString("profile"), "", ""));
        }
        rs.close();
        pst.close();
    }
}

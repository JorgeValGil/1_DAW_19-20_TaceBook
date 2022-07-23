package model;

import java.util.Date;

/**
 * Clase dos comentarios do usuario
 *
 * @author Jorge Val Gil e Adrián Fernández Pérez
 */
public class Comment {

    //Id do usuario
    private int id;
    //Fecha do comentario
    private Date date;
    //Texto do comentario
    private String text;
    //Perfil fonte
    private Profile sourceProfile;
    //Post do usuario
    private Post post;

    /**
     * Obten o Id
     *
     * @return Devolve o Id do usuario
     */
    public int getId() {
        return id;
    }

    /**
     * Determina o Id do usuario
     *
     * @param id Id do usuario
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obten a fecha do comentario
     *
     * @return Devolve a fecha do comentario
     */
    public Date getDate() {
        return date;
    }

    /**
     * Determina a fecha do comentario
     *
     * @param date Fecha do comentario
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Obten o texto do comentario
     *
     * @return Devolve o texto do comentario
     */
    public String getText() {
        return text;
    }

    /**
     * Determina o texto do comentario
     *
     * @param text Texto do comentario
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Obten o perfil fonte
     *
     * @return Devolve o perfil fonte
     */
    public Profile getSourceProfile() {
        return sourceProfile;
    }

    /**
     * Determina o perfil fonte
     *
     * @param sourceProfile Perfil fonte
     */
    public void setSourceProfile(Profile sourceProfile) {
        this.sourceProfile = sourceProfile;
    }

    /**
     * Obten o post do usuario
     *
     * @return Devolve o post
     */
    public Post getPost() {
        return post;
    }

    /**
     * Determina o post do usuario
     *
     * @param post Post do usuario
     */
    public void setPost(Post post) {
        this.post = post;
    }

    /**
     * Constructor da clase
     *
     * @param id Id do usuario
     * @param date Fecha do comentario
     * @param text Texto do comentario
     * @param sourceProfile Perfil fonte
     * @param post Post do usuario
     */
    public Comment(int id, Date date, String text, Profile sourceProfile, Post post) {
        this.id = id;
        this.date = date;
        this.text = text;
        this.sourceProfile = sourceProfile;
        this.post = post;
    }

}

package model;

import java.util.ArrayList;
import java.util.Date;

/**
 * Clase dos posts do usuario
 *
 * @author Jorge Val Gil e Adrián Fernández Pérez
 */
public class Post {

    //Id do usuario
    private int id;
    //Fecha do post
    private Date date;
    //Texto do post
    private String text;
    //Perfil do post
    private Profile profile;
    //Autor do post
    private Profile author;
    //ArrayList no que almacenamos os perfis que deron like ao post
    private ArrayList<Profile> profileLikes = new ArrayList<>();
    //ArrayList no que almacenamos os comentarios do post
    private ArrayList<Comment> comments = new ArrayList<>();

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
     * Obten a fecha do post
     *
     * @return Devolve a fecha do post
     */
    public Date getDate() {
        return date;
    }

    /**
     * Determina a fecha do post
     *
     * @param date Fecha do post
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Obten o texto do post
     *
     * @return Devolve o texto do post
     */
    public String getText() {
        return text;
    }

    /**
     * Determina o texto do post
     *
     * @param text Texto do post
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Obten o perfil do post
     *
     * @return Devolve o perfil do post
     */
    public Profile getProfile() {
        return profile;
    }

    /**
     * Determina o perfil do usuario do post
     *
     * @param profile Perfil do usuario do post
     */
    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    /**
     * Obten o autor do post
     *
     * @return Devolve o autor do post
     */
    public Profile getAuthor() {
        return author;
    }

    /**
     * Determina o autor do post
     *
     * @param author Autor do post
     */
    public void setAuthor(Profile author) {
        this.author = author;
    }

    /**
     * Obten os perfis que deron like no post
     *
     * @return Devolve os perfis que deron like no post
     */
    public ArrayList<Profile> getProfileLikes() {
        return profileLikes;
    }

    /**
     * Determina que perfis deron like no post
     *
     * @param profileLikes Perfis que deron like no post
     */
    public void setProfileLikes(ArrayList<Profile> profileLikes) {
        this.profileLikes = profileLikes;
    }

    /**
     * Obten os comentarios do post
     *
     * @return Devolve os comentarios do post
     */
    public ArrayList<Comment> getComments() {
        return comments;
    }

    /**
     * Determina que comentarios estan no post
     *
     * @param comments Comentarios do post
     */
    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    /**
     * Constructor da clase
     *
     * @param id Id do perfil
     * @param date Fecha do post
     * @param text Texto do post
     * @param profile Perfil do post
     * @param author Autor do post
     */
    public Post(int id, Date date, String text, Profile profile, Profile author) {
        this.id = id;
        this.date = date;
        this.text = text;
        this.profile = profile;
        this.author = author;
    }

}

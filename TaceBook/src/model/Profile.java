package model;

import java.util.ArrayList;

/**
 * Clase do perfil do usuario
 *
 * @author Jorge Val Gil e Adrián Fernández Pérez
 */
public class Profile {

    //Nome do perfil
    private String name;
    //Contrasinal do perfil
    private String password;
    //Estado do perfil
    private String status;
    //ArrayList das mensaxes do perfil
    private ArrayList<Message> messages = new ArrayList<>();
    //ArrayList dos amigos do perfil
    private ArrayList<Profile> friends = new ArrayList<>();
    //ArraytList das solicitudes de amizade do perfil
    private ArrayList<Profile> friendshipRequests = new ArrayList<>();
    //ArrayList dos posts do perfil
    private ArrayList<Post> posts = new ArrayList<>();

    /**
     * Obten o nome do perfil
     *
     * @return Devolve o nome do perfil
     */
    public String getName() {
        return name;
    }

    /**
     * Determina o nome do perfil
     *
     * @param name Nome do perfil
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Obten a contrasinal do perfil
     *
     * @return Devolve a contrasinal do perfil
     */
    public String getPassword() {
        return password;
    }

    /**
     * Determina a contrasinal do perfil
     *
     * @param password Contrasinal do perfil
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Obten o estado do perfil
     *
     * @return Devolve o estado do perfil
     */
    public String getStatus() {
        return status;
    }

    /**
     * Determina o estado do perfil
     *
     * @param status Estado do perfil
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Obten as mensaxes do perfil
     *
     * @return Devolve as mensaxes do perfil
     */
    public ArrayList<Message> getMessages() {
        return messages;
    }

    /**
     * Determina as mensaxes do perfil
     *
     * @param messages Mensaxes
     */
    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    /**
     * Obten a lista de amigos do perfil
     *
     * @return Devolve a lista amigos do perfil
     */
    public ArrayList<Profile> getFriends() {
        return friends;
    }

    /**
     * Determina os amigos do perfil
     *
     * @param friends Amigos
     */
    public void setFriends(ArrayList<Profile> friends) {
        this.friends = friends;
    }

    /**
     * Obten as solicitudes de amizade do perfil
     *
     * @return Devolve as solicitudes de amizade do perfil
     */
    public ArrayList<Profile> getFriendshipRequests() {
        return friendshipRequests;
    }

    /**
     * Determina as solicitudes de amizade do perfil
     *
     * @param friendshipRequests Solicitudes de amizade do perfil
     */
    public void setFriendshipRequests(ArrayList<Profile> friendshipRequests) {
        this.friendshipRequests = friendshipRequests;
    }

    /**
     * Obten os posts do perfil
     *
     * @return Devolve os posts do perfil
     */
    public ArrayList<Post> getPosts() {
        return posts;
    }

    /**
     * Determina os posts do perfil
     *
     * @param posts Posts do perfil
     */
    public void setPosts(ArrayList<Post> posts) {
        this.posts = posts;
    }

    /**
     * Constructor da clase
     *
     * @param name Nome do perfil
     * @param password Contrasinal do perfil
     * @param status Estado do perfil
     */
    public Profile(String name, String password, String status) {
        this.name = name;
        this.password = password;
        this.status = status;
    }

}

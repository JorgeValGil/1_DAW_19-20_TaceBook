package model;

import java.util.Date;

/**
 * Clase das mensaxes do usuario
 *
 * @author Jorge Val Gil e Adrián Fernández Pérez
 */
public class Message {

    //Id do usuario
    private int id;
    //Texto da mensaxe
    private String text;
    //Fecha da mensaxe
    private Date date;
    //Foi lido ou non
    private boolean read;
    //Perfil fonte
    private Profile sourceProfile;
    //Perfil destino
    private Profile destProfile;

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
     * Obten o texto da mensaxe
     *
     * @return Devolve o texto da mensaxe
     */
    public String getText() {
        return text;
    }

    /**
     * Determina o texto da mensaxe
     *
     * @param text Texto da mensaxe
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Obten a fecha da mensaxe
     *
     * @return Devolve a fecha da mensaxe
     */
    public Date getDate() {
        return date;
    }

    /**
     * Determina a fecha da mensaxe
     *
     * @param date Fecha da mensaxe
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Obten se foi lida ou non a mensaxe
     *
     * @return Devolve se foi lida ou non a mensaxe
     */
    public boolean isRead() {
        return read;
    }

    /**
     * Determina se foi lida ou non a mensaxe
     *
     * @param read Booleano que nos di se foi lida ou non a mensaxe
     */
    public void setRead(boolean read) {
        this.read = read;
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
     * Obten o perfil destino da mensaxe
     *
     * @return Devolve o perfil destino da mensaxe
     */
    public Profile getDestProfile() {
        return destProfile;
    }

    /**
     * Determina o perfil destino da mensaxe
     *
     * @param destProfile Perfil destino
     */
    public void setDestProfile(Profile destProfile) {
        this.destProfile = destProfile;
    }

    /**
     * Constructor da clase
     *
     * @param id Id do perfil
     * @param text Texto da mensaxe
     * @param date Fecha da mensaxe
     * @param read Nos di se foi lida ou non a mensaxe
     * @param sourceProfile Perfil fonte
     * @param destProfile Perfil destino
     */
    public Message(int id, String text, Date date, boolean read, Profile sourceProfile, Profile destProfile) {
        this.id = id;
        this.text = text;
        this.date = date;
        this.read = read;
        this.sourceProfile = sourceProfile;
        this.destProfile = destProfile;
    }

}

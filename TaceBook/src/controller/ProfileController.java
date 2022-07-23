package controller;

import java.util.Date;
import model.Comment;
import persistence.CommentDB;
import model.Message;
import persistence.MessageDB;
import model.Post;
import persistence.PostDB;
import model.Profile;
import persistence.PersistenceException;
import persistence.ProfileDB;
import view.GUIProfileView;
import view.ProfileView;
import view.TextProfileView;

/**
 * Método que controlará as accións do menú principal
 *
 * @author Jorge Val Gil e Adrián Fernández Pérez
 */
public class ProfileController {

    //Referencia ao obxecto vista 
    private ProfileView profileView;
    //Referencia ao perfil que abre a sesión
    private Profile sessionProfile;
    private Profile shownProfile;

    private boolean textMode;

    /**
     * Método que cambia a contrasinal
     *
     * @param newpassword nova contrasinal
     * @throws PersistenceException
     */
    public void changePassword(String newpassword) throws PersistenceException {
        //establece a nova contrasinal
        this.sessionProfile.setPassword(newpassword);
        //chama a changepassword da clase profileDB
        ProfileDB.changePassword(sessionProfile);
        reloadProfile();
    }

    public ProfileController(boolean textMode) {
        this.textMode = textMode;
        if (textMode) {
            profileView = new TextProfileView(this);
        } else {

            profileView = new GUIProfileView(null, true, this);
        }
    }

    /**
     * Get de shownProfile
     *
     * @return devolve shownProfile
     */
    public Profile getShownProfile() {
        return shownProfile;
    }

    /**
     * Set de shownProfile
     *
     * @param shownProfile perfil que se está visualizando
     */
    public void setShownProfile(Profile shownProfile) {
        this.shownProfile = shownProfile;
        reloadProfile();
    }

    /**
     * Método que borra un amigo
     *
     * @param shownProfile perfil do usuario
     * @param friend perfil do amigo
     * @throws PersistenceException
     */
    public void removeFriend(Profile shownProfile, Profile friend) throws PersistenceException {
        //chamamos ao método removefriend da clase profileDB
        ProfileDB.removeFriend(shownProfile, friend);
    }

    /**
     * Obten o perfil que abriu a sesión
     *
     * @return Devolve o perfil que abriu a sesión
     */
    public Profile getSessionProfile() {
        return sessionProfile;
    }

    /**
     * Obten o número de publicacións a mostrar
     *
     * @return Devolve o número de publicacións a mostrar
     */
    public int getPostsShowed() {
        return profileView.getPostsShowed();
    }

    /**
     * Método que obten o perfil da sesión e mostra o menú do perfil para el
     */
    public void reloadProfile() {
        try {
            shownProfile = ProfileDB.findByName(shownProfile.getName(), profileView.getPostsShowed());

        } catch (PersistenceException ex) {
            proccessPersistenceException(ex);
        }
        profileView.showProfileMenu(shownProfile);
    }

    /**
     * Método que abre unha sesión con un perfil e chama ao método
     * showProfileMenu() do obxecto vista
     *
     * @param sessionProfile Perfil da sesión
     */
    public void openSession(Profile sessionProfile) {
        this.sessionProfile = sessionProfile;
        this.shownProfile = sessionProfile;
        this.profileView.showProfileMenu(shownProfile);
    }

    /**
     * Método que actualiza o estado do perfil modificando o atributo do obxecto
     * profile e chamando a ProfileDB para gardar o cambio.Despois chama ao
     * metodo reloadProfile() para recargar o perfil e mostrar de novo o menu
     *
     * @param newStatus Novo estado para o perfil
     * @throws persistence.PersistenceException
     */
    public void updateProfileStatus(String newStatus) throws PersistenceException {
        this.sessionProfile.setStatus(newStatus);
        ProfileDB.update(this.sessionProfile);
        reloadProfile();
    }

    /**
     * Método que crea un novo Post
     *
     * @param text texto
     * @param destProfile Perfil de destino
     */
    public void newPost(String text, Profile destProfile) {
        try {
            Post post1 = new Post(0, new Date(), text, destProfile, sessionProfile);
            PostDB.save(post1);

        } catch (PersistenceException ex) {
            proccessPersistenceException(ex);
        }
        reloadProfile();
    }

    /**
     * Método que crea un novo comentario
     *
     * @param post post
     * @param commentText texto do comentario
     */
    public void newComment(Post post, String commentText) {
        try {
            Comment comment1 = new Comment(0, new Date(), commentText, this.sessionProfile, post);
            CommentDB.save(comment1);
            reloadProfile();
        } catch (PersistenceException ex) {
            proccessPersistenceException(ex);
        }
    }

    /**
     * Método que comproba se se pode dar like ou non
     *
     * @param post post
     */
    public void newLike(Post post) {
        //comprobase que o perfil actual non sexa o autor, se ese é o caso chamamos a showCannotLikeOwnPostMessage
        if (post.getAuthor().getName().equals(sessionProfile.getName())) {
            this.profileView.showCannotLikeOwnPostMessage();
        } else {
            boolean likes = false;
            //comprobase que non tiveramos dado like antes
            for (Profile like : post.getProfileLikes()) {
                if (like.getName().equals(sessionProfile.getName())) {
                    likes = true;
                }
            }
            //se xa temos dado like de antes chamamos a showAlreadyLikedPostMessage
            if (likes) {
                profileView.showAlreadyLikedPostMessage();
            } else {
                try {
                    //senon gardamos o like
                    PostDB.saveLike(post, sessionProfile);
                } catch (PersistenceException ex) {
                    proccessPersistenceException(ex);
                }
            }
        }
        //invocamos a reloadProfile
        reloadProfile();
    }

    /**
     * Método dende o que se envia un solicitude de amizade
     *
     * @param profileName nome do perfil
     */
    public void newFriendshipRequest(String profileName) {
        try {
            Profile profile1 = ProfileDB.findByName(profileName, 0);
            //comprobamos que exista un perfil co nome do parámetro, se non existe chamamos a showProfileNotFoundMessage
            if (profile1 == null) {
                this.profileView.showProfileNotFoundMessage();
            } else {
                //comprobamos se xa son amigos, se xa son amigos chamamos a showIsAlreadyFriendMessage
                for (Profile friend : profile1.getFriends()) {
                    if (friend.getName().equals(this.sessionProfile.getName())) {
                        this.profileView.showIsAlreadyFriendMessage(profileName);
                        reloadProfile();
                        return;
                    }
                }
                //comprobamos que non enviaramos xa unha peticion a ese perfil, se xa a enviamos chamamos a showDuplicateFrienshipRequestMessage
                for (Profile friendRequest : profile1.getFriendshipRequests()) {
                    if (friendRequest.getName().equals(this.sessionProfile.getName())) {
                        this.profileView.showDuplicateFrienshipRequestMessage(profileName);
                        reloadProfile();
                        return;
                    }
                }
                //comprobamos que ese perfil non nos haxa enviado unha peticion a nos, se xa no la enviou chamamos a showExistsFrienshipRequestMessage
                for (Profile friendRequest : this.sessionProfile.getFriendshipRequests()) {
                    if (friendRequest.getName().equals(profileName)) {
                        this.profileView.showExistsFrienshipRequestMessage(profileName);
                        reloadProfile();
                        return;
                    }
                }
                //se non ocorre nada do anterior gardamos a peticion de amizade
                ProfileDB.saveFrienshipRequest(profile1, this.sessionProfile);
            }
            //invocamos a reloadProfile
            reloadProfile();
        } catch (PersistenceException ex) {
            proccessPersistenceException(ex);
        }

    }

    /**
     * Método que acepta a solicitude de amizade
     *
     * @param sourceProfile perfil de orixe
     */
    public void acceptFriendshipRequest(Profile sourceProfile) {
        try {
            ProfileDB.removeFrienshipRequest(sessionProfile, sourceProfile);
            ProfileDB.saveFriendship(sessionProfile, sourceProfile);
            reloadProfile();
        } catch (PersistenceException ex) {
            proccessPersistenceException(ex);
        }
    }

    /**
     * Método que rexeita a solicitude a amizade
     *
     * @param sourceProfile perfil de orixe
     */
    public void rejectFriendshipRequest(Profile sourceProfile) {
        try {
            ProfileDB.removeFrienshipRequest(sessionProfile, sourceProfile);
            reloadProfile();
        } catch (PersistenceException ex) {
            proccessPersistenceException(ex);
        }
    }

    /**
     * Método que crea unha nova mensaxe
     *
     * @param destProfile perfil de destino
     * @param text texto
     */
    public void newMessage(Profile destProfile, String text) {
        try {
            Message message1 = new Message(0, text, new Date(), true, sessionProfile, destProfile);
            MessageDB.save(message1);
            reloadProfile();
        } catch (PersistenceException ex) {
            proccessPersistenceException(ex);
        }
    }

    /**
     * Método que borra unha mensaxe
     *
     * @param message mensaxe
     */
    public void deleteMessage(Message message) {
        try {
            MessageDB.remove(message);
            reloadProfile();
        } catch (PersistenceException ex) {
            proccessPersistenceException(ex);
        }
    }

    /**
     * Método que marca unha mensaxe como leida
     *
     * @param message mensaxe
     */
    public void markMessageAsRead(Message message) {
        try {
            message.setRead(true);
            MessageDB.remove(message);
            reloadProfile();
        } catch (PersistenceException ex) {
            proccessPersistenceException(ex);
        }
    }

    /**
     * Método que serve para responder a unha mensaxe
     *
     * @param message mensaxe
     * @param text texto
     */
    public void replyMessage(Message message, String text) {
        try {
            message.setRead(true);
            MessageDB.remove(message);
            newMessage(message.getSourceProfile(), text);
        } catch (PersistenceException ex) {
            proccessPersistenceException(ex);
        }
    }

    private void proccessPersistenceException(PersistenceException ex) {
        switch (ex.getCode()) {
            case 0 ->
                this.profileView.showConnectionErrorMessage();
            case 1 ->
                this.profileView.showReadErrorMessage();
            case 2 ->
                this.profileView.showWriteErrorMessage();
        }
    }
}

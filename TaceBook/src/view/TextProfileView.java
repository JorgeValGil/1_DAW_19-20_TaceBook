package view;

import controller.ProfileController;
import java.text.SimpleDateFormat;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Comment;
import model.Message;
import model.Post;
import model.Profile;
import persistence.PersistenceException;

/**
 * Clase que encargase de mostrar as opcións do menú principal
 *
 * @author Jorge Val Gil e Adrián Fernández Pérez
 */
public class TextProfileView implements ProfileView {

    //Numero de publicacións que se mostran na visualización
    private int postsShowed = 10;
    //Referencia ao obxecto controlador
    private ProfileController profileController;
    //Atributo para formatear as datas
    private SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy 'ás' HH:mm:ss");

    /**
     * Constructor da clase
     *
     * @param profileController Referencia ao obxeto controlador
     */
    public TextProfileView(ProfileController profileController) {
        this.profileController = profileController;
    }

    /**
     * Obtén o numero de publicacións que se mostran na visualización
     *
     * @return Devolve o numero de publicacións que se mostran na visualización
     */
    public int getPostsShowed() {
        return postsShowed;
    }

    /*
    * Método que se encarga de mostrar toda a información do perfil
    * ownProfile --> Di se estamos vendo o noso perfil ou outro
     */
    private void showProfileInfo(boolean ownProfile, Profile profile) {
        System.out.println();
        System.out.println("Tacebook --> Perfil do usuario: " + profile.getName());
        System.out.println("Estado actual: " + profile.getStatus());
        System.out.println("A túa biografía (" + this.postsShowed + " últimas publicacións)");
        int i;
        for (i = 0; i < this.postsShowed && i < profile.getPosts().size(); i++) {
            Post posted = profile.getPosts().get(i);
            System.out.print(+i + ". O " + this.formatter.format(posted.getDate()));
            if (posted.getAuthor().getName().equals(this.profileController.getSessionProfile().getName())) {
                System.out.print(" ti escribiches");
            } else {
                System.out.print(" " + posted.getAuthor().getName() + " escribíu");
            }
            System.out.println(" (" + posted.getProfileLikes().size() + " me gusta):");
            System.out.println("" + posted.getText());
            for (Comment commented : posted.getComments()) {
                System.out.println("--> " + commented.getText() + " -- " + commented
                        .getSourceProfile().getName() + " -- " + this.formatter
                                .format(commented.getDate()));
            }
        }
        System.out.println("Lista de amigos:");
        for (i = 0; i < profile.getFriends().size(); i++) {
            Profile friend = profile.getFriends().get(i);
            System.out.println(+i + ". " + friend.getName() + " - " + friend.getStatus());
        }
        if (ownProfile) {
            if (!profile.getMessages().isEmpty()) {
                System.out.println("Mensaxes privadas:");
                int unreadMessages = 0;
                for (Message messages : profile.getMessages()) {
                    if (!messages.isRead()) {
                        unreadMessages++;
                    }
                }
                if (unreadMessages > 0) {
                    System.out.println("Ti tes " + unreadMessages + " mensaxes sen ler");
                }
                for (int j = 0; j < profile.getMessages().size(); j++) {
                    Message messages = profile.getMessages().get(j);
                    if (!messages.isRead()) {
                        System.out.print("*");
                    }
                    System.out.println("" + j + ". De " + messages.getSourceProfile().getName() + "(" + this.formatter.format(messages.getDate()) + ")");
                    System.out.println(messages.getText().substring(0,
                            Math.min(10, messages.getText().length() - 1)) + "..");
                }
            }
            if (!profile.getFriendshipRequests().isEmpty()) {
                System.out.println("Ti tes solicitudes de amizade dos seguintes perfís");
                for (i = 0; i < profile.getFriendshipRequests().size(); i++) {
                    Profile source = profile.getFriendshipRequests().get(i);
                    System.out.println("" + i + ". " + source.getName() + " quere ser teu amigo.");
                }
            }
        }
    }

    /*
    * Método que encargase de pedir a un usuario que introduza un número
    * para seleccionar un elemento dunha lista
     */
    private int selectElement(String text, int maxNumber, Scanner scanner) {
        int selected;
        do {
            System.out.println(text);
            selected = readNumber(scanner);
            if (selected >= 0 && selected < maxNumber) {
                continue;
            }
            System.out.println("Introduce un número entre 0 e " + (maxNumber - 1));
        } while (selected < 0 || selected >= maxNumber);
        return selected;
    }

    /*
    * Método que pide o texto para crear unha nova publicación
     */
    private void writeNewPost(Scanner scanner, Profile profile) {
        System.out.println("Introduce o texto da nova publicación:");
        String text = scanner.nextLine();
        this.profileController.newPost(text, profile);
    }

    /*
    * Método que pide ao usuario que seleccione unha publicación e que 
    * introduza un texto para crear un comentario con el
     */
    private void commentPost(Scanner scanner, Profile profile) {
        if (profile.getPosts().isEmpty()) {
            System.out.println("Non hai publicacións feitas");
            showProfileMenu(profile);
        } else {
            int postCommentedNumbers = selectElement("Indica a publicación a comentar:",
                    Math.min(profile.getPosts().size(), this.postsShowed), scanner);
            Post postComment = profile.getPosts().get(postCommentedNumbers);
            System.out.println("Introduce o comentario da publicación:");
            String commentedText = scanner.nextLine();
            this.profileController.newComment(postComment, commentedText);
        }
    }

    /*
     * Método que pide ao usuario que seleccione unha publicación e chama ao 
     * controlador para facer like sobre ela
     */
    private void addLike(Scanner scanner, Profile profile) {
        if (profile.getPosts().isEmpty()) {
            System.out.println("Non hai publicacións feitas");
            showProfileMenu(profile);
        } else {
            int postLikeNum = selectElement("Selecciona a publicación sobre a que queres dar me gusta:",
                    Math.min(profile.getPosts().size(), this.postsShowed), scanner);
            Post postLiked = profile.getPosts().get(postLikeNum);
            this.profileController.newLike(postLiked);
        }
    }

    /*
  * Método que se estamos vendo o propio perfil, pide ao usuario seleccionar 
  * unha amizade para ver a súa biografía, e senón volve a mostrar a propia
  * biografía
     */
    private void showBiography(boolean ownProfile, Scanner scanner, Profile profile) {
        if (ownProfile) {
            if (profile.getFriends().isEmpty()) {
                System.out.println("Non tes amig@s agregados");
                showProfileMenu(profile);
            } else {
                int friendNumberProfile = selectElement("Indica @ amig@ d@ queres ver a biografía:", profile
                        .getFriends().size(), scanner);
                this.profileController.setShownProfile(profile.getFriends().get(friendNumberProfile));
            }
        } else {
            this.profileController.setShownProfile(this.profileController.getSessionProfile());
        }
    }

    /*
  * Método que pide o nome dun perfil e chama ao controlador para enviarlle
  * unha solicitude de amizade
     */
    private void sendFriendshipRequest(boolean ownProfile, Scanner scanner, Profile profile) {
        if (ownProfile) {
            System.out.println("Introduce o nome do perfil do que queres ser amigo");
            String profileName = scanner.nextLine();
            this.profileController.newFriendshipRequest(profileName);
        } else {
            System.out.println("So podes facer isto dende a túa biografía");
            showProfileMenu(profile);
        }
    }

    /*
  * Método que pide o número dunha solicitude de amizade e chama ao controlador 
  * para aceptala ou rexeitala
     */
    private void proccessFriendshipRequest(boolean ownProfile, Scanner scanner, Profile profile, boolean accept) {
        if (ownProfile) {
            if (profile.getFriendshipRequests().isEmpty()) {
                System.out.println("Non tes solicitudes de amizade sen comprobar");
                showProfileMenu(profile);
            } else {

                int friendshipRequest = selectElement("Indica o número de solicitude que queres enviar:", profile
                        .getFriendshipRequests().size(), scanner);

                if (accept) {
                    this.profileController.acceptFriendshipRequest(profile
                            .getFriendshipRequests().get(friendshipRequest));
                } else {
                    this.profileController.rejectFriendshipRequest(profile
                            .getFriendshipRequests().get(friendshipRequest));
                }
            }
        } else {
            System.out.println("So podes facer isto dende a túa biografía");
            showProfileMenu(profile);
        }
    }

    /*
  * Método que se estamos vendo o propio perfil, pide ao usuario selecciona un 
  * amigo e o texto da mensaxe e chama ao controlador para enviar unha mensaxe. 
  * Se estamos vendo o perfil dunha amizade, pide o texto para enviarlle unha 
  * mensaxe a ese perfil.
     */
    private void sendPrivateMessage(boolean ownProfile, Scanner scanner, Profile profile) {
        Profile destinationProfile;
        if (ownProfile) {
            if (profile.getFriends().isEmpty()) {
                System.out.println("Non tes amig@s agregados");
                showProfileMenu(profile);
                return;
            }
            int friendNumber = selectElement("Indica @ amig@ a o que queres enviarlle a mensaxe:", profile
                    .getFriends().size(), scanner);
            destinationProfile = profile.getFriends().get(friendNumber);
        } else {
            destinationProfile = profile;
        }
        System.out.println("Introduce o texto da mensaxe a escribir:");
        String messageText = scanner.nextLine();
        this.profileController.newMessage(destinationProfile, messageText);
    }

    /*
  * Método que pide ao usuario que seleccione unha mensaxe e a mostra completa, 
  * dando as opcións de respondela, eliminala ou simplemente volver á 
  * biografia marcando a mensaxe como lida
     */
    private void readPrivateMessage(boolean ownProfile, Scanner scanner, Profile profile) {
        if (ownProfile) {
            if (profile.getMessages().isEmpty()) {
                System.out.println("Non tes mensaxes pendentes");
                showProfileMenu(profile);
            } else {
                String messageText;
                int messageNumber = selectElement("Selecciona a mensaxe que queres ler:", profile
                        .getMessages().size(), scanner);
                Message message = profile.getMessages().get(messageNumber);
                System.out.println();
                System.out.println("Mensaxe privada");
                System.out.println("De: " + message.getSourceProfile().getName());
                System.out.println("Data da mensaxe: " + this.formatter.format(message.getDate()));
                System.out.println("Texto da mensaxe: ");
                System.out.println(message.getText());
                System.out.println();
                System.out.println("Escolle algunha das opción:");
                System.out.println("1. Responder mensaxe actual");
                System.out.println("2. Borrar a mensaxe actual");
                System.out.println("3. Volver a biografía");
                int options = readNumber(scanner);
                switch (options) {
                    case 1:
                        System.out.println("Introduce o texto da mensaxe a escribir:");
                        messageText = scanner.nextLine();
                        this.profileController.replyMessage(message, messageText);
                        return;
                    case 2:
                        this.profileController.deleteMessage(message);
                        return;
                    case 3:
                        this.profileController.markMessageAsRead(message);
                        return;
                }
                System.out.println("Tes que introducir un número entre 1 e 3");
                showProfileMenu(profile);
            }
        } else {
            System.out.println("So podes facer isto dende a túa biografía");
            showProfileMenu(profile);
        }
    }

    /*
  * Método que pide ao usuario que seleccione unha mensaxe e chama ao 
  * controlador para borrala
     */
    private void deletePrivateMessage(boolean ownProfile, Scanner scanner, Profile profile) {
        if (ownProfile) {
            if (profile.getMessages().isEmpty()) {
                System.out.println("Non tes mensaxes pendentes");
                showProfileMenu(profile);
            } else {
                int messageNumber = selectElement("Indica a mensaxe que queres eliminar:", profile
                        .getMessages().size(), scanner);
                this.profileController.deleteMessage(profile.getMessages().get(messageNumber));
            }
        } else {
            System.out.println("So podes facer isto dende a túa biografía");
            showProfileMenu(profile);
        }
    }

    /*
    * Método que pide o número de publicacións que se queren visualizar e 
    * chamar ao controlador para recargar o perfil
     */
    private void showOldPosts(Scanner scanner, Profile profile) {
        System.out.println("Introduce o número de publicación que queres ver:");
        this.postsShowed = readNumber(scanner);
        this.profileController.reloadProfile();
    }

    /*
    * Método que serve para cambiar o estado do perfil do usuario
     */
    private void changeStatus(boolean ownProfile, Scanner scanner, Profile profile) {
        String statusInfo;
        if (ownProfile) {
            System.out.println("Introduce o novo estado: ");
            statusInfo = scanner.nextLine();
            try {
                this.profileController.updateProfileStatus(statusInfo);
            } catch (PersistenceException ex) {
                Logger.getLogger(TextProfileView.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("So podes facer isto dende a túa biografía");
            showProfileMenu(profile);
        }
    }

    /**
     * Método que mostra as diferentes funcions que pode facer un usuario dentro
     * do seu perfil
     *
     * @param profile Perfil do usuario
     */
    public void showProfileMenu(Profile profile) {
        Scanner sc = new Scanner(System.in);
        boolean ownProfile = this.profileController.getSessionProfile().getName().equals(profile.getName());
        showProfileInfo(ownProfile, profile);
        System.out.println();
        System.out.println("Escolle algunha das diferentes opcións");
        System.out.println("1. Escribir unha nova publicación");
        System.out.println("2. Comentar algunha publicación");
        System.out.println("3. Facer me gusta sobre algunha publicación");
        if (ownProfile) {
            System.out.println("4. Ver a biografía dalgún amigo");
            System.out.println("5. Enviar algunha solicitude de amizade");
            System.out.println("6. Aceptar algunha solicitude de amizade");
            System.out.println("7. Rexeitar algunha solicitude de amizade");
            System.out.println("8. Enviar unha mensaxe privada a un amigo");
            System.out.println("9. Ler unha mensaxe privada");
            System.out.println("10. Eliminar unha mensaxe privada");
            System.out.println("11. Ver publicacións feitas");
            System.out.println("12. Cambiar o estado actual");
            System.out.println("13. Pechar a sesión actual");
        } else {
            System.out.println("4. Volver á miña biografía");
            System.out.println("8. Enviar mensaxe privada a este amigo");
            System.out.println("11. Ver publicacións feitas");
            System.out.println("13. Pechar a sesión actual");
        }
        int profileMenu = readNumber(sc);
        switch (profileMenu) {
            case 1:
                writeNewPost(sc, profile);
                return;
            case 2:
                commentPost(sc, profile);
                return;
            case 3:
                addLike(sc, profile);
                return;
            case 4:
                showBiography(ownProfile, sc, profile);
                return;
            case 5:
                sendFriendshipRequest(ownProfile, sc, profile);
                return;
            case 6:
                proccessFriendshipRequest(ownProfile, sc, profile, true);
                return;
            case 7:
                proccessFriendshipRequest(ownProfile, sc, profile, false);
                return;
            case 8:
                sendPrivateMessage(ownProfile, sc, profile);
                return;
            case 9:
                readPrivateMessage(ownProfile, sc, profile);
                return;
            case 10:
                deletePrivateMessage(ownProfile, sc, profile);
                return;
            case 11:
                showOldPosts(sc, profile);
                return;
            case 12:
                changeStatus(ownProfile, sc, profile);
                return;
            case 13:
                return;
        }
        System.out.println("Tes que introducir un número entre 1 e 13");
        showProfileMenu(profile);
    }

    /**
     * Método que informa que un perfil non se atopou
     */
    public void showProfileNotFoundMessage() {
        System.out.println("Non existe un perfil con ese nome");
    }

    /**
     * Método que informa de que non se pode facer like sobre unha publicación
     * propia
     */
    public void showCannotLikeOwnPostMessage() {
        System.out.println("Non podes facer me gusta sobre as túas propias publicacións");
    }

    /**
     * Método que informa de que non se pode facer like sobre unha publicación
     * sobre a que xa se fixo like
     */
    public void showAlreadyLikedPostMessage() {
        System.out.println("Xa fixeches me gusta sobre esta publicación");
    }

    /**
     * Método que informa de que xa tes amizade con ese perfil
     *
     * @param profileName Nome do perfil
     */
    public void showIsAlreadyFriendMessage(String profileName) {
        System.out.println("Xa eres amigo de " + profileName + "!");
    }

    /**
     * Método que informa de que ese perfil xa ten unha solicitude de amizade
     * contigo
     *
     * @param profileName Nome do perfil
     */
    public void showExistsFrienshipRequestMessage(String profileName) {
        System.out.println("Xa recibiches unha solicitude de amizade de " + profileName + "!");
    }

    /**
     * Método que informa de que xa tes unha solicitude de amizade con ese
     * perfil
     *
     * @param profileName Nome do perfil
     */
    public void showDuplicateFrienshipRequestMessage(String profileName) {
        System.out.println("Xa mandaches unha solicitude de amizade para " + profileName + "!");
    }

    private int readNumber(Scanner scanner) {
        try {
            int number = scanner.nextInt();
            scanner.nextLine();
            return number;
        } catch (NoSuchElementException e) {
            System.out.println("Débese introducir un número");
            scanner.nextLine();
            return readNumber(scanner);
        }
    }

    public void showConnectionErrorMessage() {
        System.out.println("Erro na conexión co almacén de datos!");
    }

    public void showReadErrorMessage() {
        System.out.println("Erro na lectura de datos!");
    }

    public void showWriteErrorMessage() {
        System.out.println("Erro na escritura dos datos!");
    }

}

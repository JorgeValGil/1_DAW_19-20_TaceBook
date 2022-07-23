package controller;

import view.InitMenuView;
import controller.ProfileController;
import model.Profile;
import persistence.PersistenceException;
import persistence.ProfileDB;
import view.GUIInitMenuView;
import view.TextInitMenuView;

/**
 * Clase que ten o método main() da aplicación
 *
 * @author Jorge Val Gil e Adrián Fernández Pérez
 */
public class InitMenuController {

    //Atributo da clase InitMenuView
    private InitMenuView initMenuView;

    private boolean textMode;

    public InitMenuController(boolean textMode) {
        this.textMode = textMode;
        if (textMode) {
            initMenuView = new TextInitMenuView(this);
        } else {
            initMenuView = new GUIInitMenuView(this);
        }
    }

    /*
    * Método que inicia aplicación, chamando repetidamente
    * ao método showLoginMenu() ata que devolva true
     */
    private void init() {
        while (!initMenuView.showLoginMenu());
    }

    /**
     * Método no que o usuario intenta iniciar na aplicación cun nome de usuario
     * e contrasinal
     *
     * Para isto buscará os datos do perfil na clase ProfileDB e dependendo se o
     * perfil e correcto, abrirá unha sesión ou mostrará un error
     *
     * @param name Nome do perfil
     * @param password Contrasinal do perfil
     */
    public void login(String name, String password) {
        try {
            ProfileController profilecontroller1 = new ProfileController(textMode);
            Profile profile1 = ProfileDB.findByNameAndPassword(name, password, profilecontroller1.getPostsShowed());
            if (profile1 == null) {
                this.initMenuView.showLoginErrorMessage();
            } else {
                profilecontroller1.openSession(profile1);
            }
        } catch (PersistenceException ex) {
            proccessPersistenceException(ex);
        }
    }

    /**
     * Método no que o usuario rexistrase na aplicación chamando ao obxecto
     * vista para mostrar o menú de rexistro do usuario
     */
    public void register() {
        initMenuView.showRegisterMenu();
    }

    /**
     * Método para crear un novo perfil comprobando que non esté xa en uso, logo
     * gardará os datos do perfil na clase ProfileDB
     *
     * @param name Nome do perfil
     * @param password Contrasinal do perfil
     * @param status Estado do perfil
     */
    public void createProfile(String name, String password, String status) {
        try {
            while (ProfileDB.findByName(name, 0) != null) {
                name = initMenuView.showNewNameMenu();
            }
            Profile profile1 = new Profile(name, password, status);
            ProfileDB.save(profile1);
            ProfileController profileController1 = new ProfileController(textMode);
            profileController1.openSession(profile1);
        } catch (PersistenceException ex) {
            proccessPersistenceException(ex);
        }
        
    }

    private void proccessPersistenceException(PersistenceException ex) {
        switch (ex.getCode()) {
            case 0 -> this.initMenuView.showConnectionErrorMessage();
            case 1 -> this.initMenuView.showReadErrorMessage();
            case 2 -> this.initMenuView.showWriteErrorMessage();
        }
    }

    /**
     * Método principal no que creamos un obxecto da clase e chamará ao método
     * init() o cal inicia a aplicación
     *
     * @param args
     */
    public static void main(String[] args) {
        boolean textMode = (args.length == 1 && args[0].equals("text"));
        InitMenuController initMenuController1 = new InitMenuController(textMode);
        initMenuController1.init(); 
    }
}

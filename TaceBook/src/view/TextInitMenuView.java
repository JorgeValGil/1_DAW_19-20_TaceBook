package view;

import controller.InitMenuController;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Clase que encargarase de mostras as opcións do menú principal
 *
 * @author Jorge Val Gil e Adrián Fernández Pérez
 */
public class TextInitMenuView implements InitMenuView {

    //Atributo que referencia ao obxecto controlador
    private InitMenuController initMenuController1;

    /*
    * Constructor da clase
     */
    public TextInitMenuView(InitMenuController initMenuController1) {
        this.initMenuController1 = initMenuController1;
    }

    /**
     * Método que serve para mostrar o menú do inicio da sesión coas diferentes
     * opcións, para iniciar sesión pedirase o nome e contrasinal e chamará ao
     * metodo login(), para rexistrarse chamarase ao metodo register() e por
     * último a opción de saír da aplicación
     *
     * @return
     */
    @Override
    public boolean showLoginMenu() {
        String username;
        String password;
        Scanner sc = new Scanner(System.in);
        System.out.println("---BENVIDO A TACEBOOK---");
        System.out.println("Que desexas facer: ");
        System.out.println("1. Iniciar sesión: ");
        System.out.println("2. Crear un novo perfil: ");
        System.out.println("3. Sair da aplicación: ");
        int menu = readNumber(sc);
        switch (menu) {
            case 1 -> {
                System.out.println("Introduce o nome de usuario: ");
                username = sc.nextLine();
                System.out.println("Introduce a contrasinal: ");
                if (System.console() == null) {
                    password = sc.nextLine();
                } else {
                    password = String.valueOf(System.console().readPassword());
                }
                this.initMenuController1.login(username, password);
            }
            case 2 ->
                this.initMenuController1.register();
            case 3 -> {
                return true;
            }
            default ->
                System.out.println("Debes introducir os numeros 1, 2 ou 3");
        }
        return false;

    }

    /**
     * Método que mostra unha mensaxe de erro cando introducese o nome e
     * contrasinal incorrectos
     */
    @Override
    public void showLoginErrorMessage() {
        System.out.println("Error! Nome de usuario e contrasinal erróneos");
    }

    /**
     * Método que mostra o menú para rexistrarse, no que pedirase un nome para o
     * perfil, unha contrasinal (dúas veces comprobando que coincidan) e un
     * estado. Logo invocarase o método createProfile() do controlador
     */
    @Override
    public void showRegisterMenu() {
        String firstpassword;
        String secondpassword;
        String username;
        String status;
        Scanner sc = new Scanner(System.in);
        System.out.println("---CREACION DE USUARIO---");
        System.out.println("Introduce un nome de usuario: ");
        username = sc.nextLine();
        System.out.println("---CREACION DE CONTRASINAL---");
        do {
            System.out.println("Deberás introducir a contrasinal 2 veces e que ambas coincidan.");
            System.out.println("Introduce a contrasinal: ");
            if (System.console() == null) {
                firstpassword = sc.nextLine();
            } else {
                firstpassword = String.valueOf(System.console().readPassword());
            }
            System.out.println("Introduce de novo a mesma contrasinal:");
            if (System.console() == null) {
                secondpassword = sc.nextLine();
            } else {
                secondpassword = String.valueOf(System.console().readPassword());
            }
            if (!firstpassword.equals(secondpassword)) {
                System.out.println("As contrasinais non son iguais, volvendo ao menú anterior...");
            }
        } while (!firstpassword.equals(secondpassword));
        System.out.println("Introduce o estado: ");
        status = sc.nextLine();
        this.initMenuController1.createProfile(username, firstpassword, status);
    }

    /**
     * Método que mostra unha mensaxe cando introducese un nome de usuario xa en
     * uso pedindo un novo nome para o usuario
     *
     * @return Devolve o novo nome introducido polo usuario
     */
    @Override
    public String showNewNameMenu() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Nome de usuario xa en uso. Introduce un novo: ");
        return sc.nextLine();
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

    @Override
    public void showConnectionErrorMessage() {
        System.out.println("Erro na conexión co almacén de datos!");
    }

    @Override
    public void showReadErrorMessage() {
        System.out.println("Erro na lectura de datos!");
    }

    @Override
    public void showWriteErrorMessage() {
        System.out.println("Erro na escritura dos datos!");
    }
}

package view;

import controller.InitMenuController;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Clase que encargarase de mostras as opcións do menú principal
 *
 * @author Jorge Val Gil e Adrián Fernández Pérez
 */
public class GUIInitMenuView implements InitMenuView {

    //Atributo que referencia ao obxecto controlador
    private InitMenuController initMenuController1;

    /*
    * Constructor da clase
     */
    public GUIInitMenuView(InitMenuController initMenuController1) {
        this.initMenuController1 = initMenuController1;

        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                try {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                    Logger.getLogger(GUIInitMenuView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
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
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        JPanel labels = new JPanel(new GridLayout(0, 1, 2, 2));
        labels.add(new JLabel("Nome de usuario"));
        labels.add(new JLabel("Contrasinal"));
        panel.add(labels, "West");
        JPanel fields = new JPanel(new GridLayout(0, 1, 2, 2));
        JTextField textname = new JTextField();
        fields.add(textname);
        JPasswordField textpasswd = new JPasswordField();
        fields.add(textpasswd);
        panel.add(fields, "Center");
        boolean menu = false;
        Object[] buttons = {"Saír", "Rexistrarse", "Iniciar sesión"};
        int option = JOptionPane.showOptionDialog(null, panel, "Entrar en tacebook", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, buttons, null);
        switch (option) {
            case 0 ->
                menu = true;
            case 1 ->
                this.initMenuController1.register();
            case 2 ->
                this.initMenuController1.login(textname.getText(), new String(textpasswd.getPassword()));
        }
        return menu;
    }

    /**
     * Método que mostra unha mensaxe de erro cando introducese o nome e
     * contrasinal incorrectos
     */
    @Override
    public void showLoginErrorMessage() {
        JOptionPane.showMessageDialog(null, "Nome de usuario e contrasinal incorrectos", "Erro nos datos de acceso", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Método que mostra o menú para rexistrarse, no que pedirase un nome para o
     * perfil, unha contrasinal (dúas veces comprobando que coincidan) e un
     * estado. Logo invocarase o método createProfile() do controlador
     */
    @Override
    public void showRegisterMenu() {
        String password1 = "";
        String password2 = "";
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        JPanel labels = new JPanel(new GridLayout(0, 1, 2, 2));
        labels.add(new JLabel("Nome de usuario:"));
        labels.add(new JLabel("Contrasinal:"));
        labels.add(new JLabel("Repite o contrasinal:"));
        labels.add(new JLabel("Estado:"));
        panel.add(labels, "West");
        JPanel texts = new JPanel(new GridLayout(0, 1, 2, 2));
        JTextField username = new JTextField(10);
        texts.add(username);
        JPasswordField passwd = new JPasswordField(10);
        texts.add(passwd);
        JPasswordField passwd1 = new JPasswordField(10);
        texts.add(passwd1);
        JTextField status = new JTextField(10);
        texts.add(status);
        panel.add(texts, "East");
        Object[] buttons = {"Cancelar", "Aceptar"};
        boolean flag = false;
        while (!flag) {
            int option = JOptionPane.showOptionDialog(null, panel, "Rexistrar conta", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, buttons, null);

            if (option == 1) {
                password1 = new String(passwd.getPassword());
                password2 = new String(passwd1.getPassword());
                if (password1.equals(password2)) {
                    flag = true;
                } else {
                    JOptionPane.showMessageDialog(null, "As contrasinais non coinciden, volve a introducilas!", "Erro nas contrasinais", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                showLoginMenu();
                flag = true;
            }

        }

        this.initMenuController1.createProfile(username.getText(), password1, status.getText());
    }

    /**
     * Método que mostra unha mensaxe cando introducese un nome de usuario xa en
     * uso pedindo un novo nome para o usuario
     *
     * @return Devolve o novo nome introducido polo usuario
     */
    @Override
    public String showNewNameMenu() {
        String newName = JOptionPane.showInputDialog(null, "O nome de usuario xa existe, introduce outro:", "Nome de usuario existente", JOptionPane.INFORMATION_MESSAGE);
        return newName;
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
        JOptionPane.showMessageDialog(null, "Erro na conexion coa base de datos", "Erro de conexión", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void showReadErrorMessage() {
        JOptionPane.showMessageDialog(null, "Erro na lectura de datos!", "Erro de lectura", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void showWriteErrorMessage() {
        JOptionPane.showMessageDialog(null, "Erro na escritura dos datos!", "Erro de escritura", JOptionPane.ERROR_MESSAGE);
    }
}

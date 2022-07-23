/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import controller.ProfileController;
import java.awt.GridLayout;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import model.Comment;
import model.Message;
import model.Post;
import model.Profile;
import persistence.PersistenceException;
import persistence.ProfileDB;

/**
 *
 * @author Jorge Val Gil e Adrián Fernández Pérez
 */
public class GUIProfileView extends javax.swing.JDialog implements ProfileView {
//Numero de publicacións que se mostran na visualización

    private int postsShowed = 10;
    //Referencia ao obxecto controlador
    private ProfileController profileController;
    //Atributo para formatear as datas
    private SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy 'ás' HH:mm:ss");

    /**
     * Creates new form GUIProfileView
     *
     * @param profileController
     */
    public GUIProfileView(java.awt.Frame parent, boolean modal, ProfileController profileController) {
        super(parent, modal);
        initComponents();
        this.profileController = profileController;

    }

    /*
    * Método que se encarga de mostrar toda a información do perfil
    * ownProfile --> Di se estamos vendo o noso perfil ou outro
     */
    private void showProfileInfo(boolean ownProfile, Profile profile) {
        jLabelUserNameProfile.setText("Perfil do usuario: " + profile.getName());
        jLabelStatus.setText("Estado actual: " + profile.getStatus());
        jLabelLastPosts.setText(postsShowed + " últimas publicacións:");
        jButtonBackBio.setVisible(!ownProfile);
        jButtonRemoveFriend.setVisible(ownProfile);
        jButtonSendMessageBottom.setVisible(!ownProfile);
        jButtonShowBiography.setVisible(ownProfile);
        jButtonSendPrivateMessage.setVisible(ownProfile);
        jButtonAcceptRequest.setVisible(ownProfile);
        jButtonDenyRequest.setVisible(ownProfile);
        jButtonNewRequest.setVisible(ownProfile);
        jListFriendRequest.setVisible(ownProfile);
        jLabelFriendRequest.setVisible(ownProfile);
        DefaultTableModel modelposts = (DefaultTableModel) jTablePosts.getModel();
        modelposts.setRowCount(0);
        for (int i = 0; i < this.postsShowed && i < profile.getPosts().size(); i++) {
            Post posted = profile.getPosts().get(i);
            Object[] postsTable = new Object[4];
            postsTable[0] = ("O " + this.formatter.format(posted.getDate()));
            if (posted.getAuthor().getName().equals(this.profileController.getSessionProfile().getName())) {
                postsTable[1] = (" ti escribiches");
            } else {
                postsTable[1] = (posted.getAuthor().getName() + " escribíu");
            }
            postsTable[2] = posted.getText();
            postsTable[3] = posted.getProfileLikes().size();
            modelposts.addRow(postsTable);

        }
        ((DefaultTableModel) jTableComments.getModel()).setRowCount(0);

        DefaultTableModel modelfriends = (DefaultTableModel) this.jTableFriends.getModel();
        modelfriends.setRowCount(0);
        for (int i = 0; i < profile.getFriends().size(); i++) {
            Profile friend = profile.getFriends().get(i);
            Object[] friendsTable = new Object[2];
            friendsTable[0] = friend.getName();
            friendsTable[1] = friend.getStatus();
            modelfriends.addRow(friendsTable);
        }

        if (ownProfile) {
            if (!profile.getMessages().isEmpty()) {
                jLabelPrivateMessages.setText("Mensaxes privadas: ");
                int unreadMessages = 0;
                for (Message messages : profile.getMessages()) {
                    if (!messages.isRead()) {
                        unreadMessages++;
                    }
                }
                if (unreadMessages > 0) {
                    jLabelPrivateMessages.setText("Ti tes " + unreadMessages + " mensaxes sen ler");
                }
                DefaultTableModel modelmessages = (DefaultTableModel) this.jTablePrivateMessage.getModel();
                modelmessages.setRowCount(0);
                for (int j = 0; j < profile.getMessages().size(); j++) {
                    Object[] messageTable = new Object[4];
                    messageTable[0] = Boolean.valueOf(profile.getMessages().get(j).isRead());
                    messageTable[1] = this.formatter.format(profile.getMessages().get(j).getDate());
                    messageTable[2] = profile.getMessages().get(j).getSourceProfile().getName();
                    messageTable[3] = profile.getMessages().get(j).getText();
                    modelmessages.addRow(messageTable);
                }
            }
        }
        requests();
    }

    /**
     * Obtén o numero de publicacións que se mostran na visualización
     *
     * @return Devolve o numero de publicacións que se mostran na visualización
     */
    @Override
    public int getPostsShowed() {
        return postsShowed;
    }

    private void showComments() {
        if (jTablePosts.getSelectedRow() != -1) {
            int rowselected = jTablePosts.getSelectedRow();
            Post postSelected = profileController.getShownProfile().getPosts().get(rowselected);
            DefaultTableModel modelcomments = (DefaultTableModel) this.jTableComments.getModel();
            modelcomments.setRowCount(0);
            for (Comment commented : postSelected.getComments()) {
                Object[] commentsTable = new Object[3];
                commentsTable[0] = commented.getText();
                commentsTable[1] = commented.getSourceProfile().getName();
                commentsTable[2] = this.formatter.format(commented.getDate());
                modelcomments.addRow(commentsTable);
            }
        }
    }

    private void requests() {
        if (!profileController.getShownProfile().getFriendshipRequests().isEmpty()) {
            String[] friendrequest = new String[profileController.getShownProfile().getFriendshipRequests().size()];
            for (int j = 0; j < profileController.getShownProfile().getFriendshipRequests().size(); j++) {
                friendrequest[j] = profileController.getShownProfile().getFriendshipRequests().get(j).getName() + " quere establecer amizade contigo.";
            }
            jListFriendRequest.setListData(friendrequest);
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
    private void writeNewPost(Profile profile) {
        String newpost = JOptionPane.showInputDialog(this, "Introduce o texto da publicación: ", "Nova publicación: ", JOptionPane.QUESTION_MESSAGE);
        this.profileController.newPost(newpost, profile);

    }

    /*
    * Método que pide ao usuario que seleccione unha publicación e que 
    * introduza un texto para crear un comentario con el
     */
    private void commentPost(Profile profile, int rowcomment) {
        Post postComment = profile.getPosts().get(rowcomment);
        String comment = JOptionPane.showInputDialog(this, "Introduce o comentario:", "Novo comentario", JOptionPane.QUESTION_MESSAGE);
        this.profileController.newComment(postComment, comment);

    }

    /*
     * Método que pide ao usuario que seleccione unha publicación e chama ao 
     * controlador para facer like sobre ela
     */
    private void addLike(Profile profile, int rowlike) {
        Post postLiked = profile.getPosts().get(rowlike);
        this.profileController.newLike(postLiked);
    }


    /*
  * Método que se estamos vendo o propio perfil, pide ao usuario seleccionar 
  * unha amizade para ver a súa biografía, e senón volve a mostrar a propia
  * biografía
     */
    private void showBiography(Profile profile, int friend) {

        if (friend == -1) {
            this.profileController.setShownProfile(this.profileController.getSessionProfile());
        } else {
            this.profileController.setShownProfile(profile.getFriends().get(friend));
        }

    }
/**
 * método que borra un amigo
 * @param profile perfil actual
 * @param friend posicion da taboa de amigos
 * @throws PersistenceException 
 */
    private void removeFriend(Profile profile, int friend) throws PersistenceException {
        //creamos unha mensaxe que se enviará ao perfil que borraremos
        String message = "AVISO IMPORTANTE: O usuario " + profile.getName() + " eliminoute da súa lista de amigos.";
        //enviamos a mensaxe ao perfil borrado de amigo
        this.profileController.newMessage(profile.getFriends().get(friend), message);
        //chamamos ao método removefriend da clase profile controller
        this.profileController.removeFriend(profile, profile.getFriends().get(friend));

    }

    /*
  * Método que pide o nome dun perfil e chama ao controlador para enviarlle
  * unha solicitude de amizade
     */
    private void sendFriendshipRequest() {
        String newfriendrequest = JOptionPane.showInputDialog(this, "Introduce o nome do perfil ao que lle queres enviar a solicitude:", "Solicitude de amizade:", JOptionPane.QUESTION_MESSAGE);
        this.profileController.newFriendshipRequest(newfriendrequest);
    }

    /*
  * Método que pide o número dunha solicitude de amizade e chama ao controlador 
  * para aceptala ou rexeitala
     */
    private void proccessFriendshipRequest(Profile profile, int friend, boolean accept) {

        if (accept) {
            this.profileController.acceptFriendshipRequest(profile
                    .getFriendshipRequests().get(friend));
        } else {
            this.profileController.rejectFriendshipRequest(profile
                    .getFriendshipRequests().get(friend));
        }
    }


    /*
  * Método que se estamos vendo o propio perfil, pide ao usuario selecciona un 
  * amigo e o texto da mensaxe e chama ao controlador para enviar unha mensaxe. 
  * Se estamos vendo o perfil dunha amizade, pide o texto para enviarlle unha 
  * mensaxe a ese perfil.
     */
    private void sendPrivateMessage(int friend) {
        Profile destinationProfile;
        if (friend != -1) {
            destinationProfile = profileController.getShownProfile().getFriends().get(friend);
        } else {
            destinationProfile = profileController.getShownProfile();
        }

        String message = JOptionPane.showInputDialog(this, "Introduce a mensaxe:", "Enviar mensaxe privada", JOptionPane.QUESTION_MESSAGE);
        this.profileController.newMessage(destinationProfile, message);
    }

    /*
  * Método que pide ao usuario que seleccione unha mensaxe e a mostra completa, 
  * dando as opcións de respondela, eliminala ou simplemente volver á 
  * biografia marcando a mensaxe como lida
     */
    private void readPrivateMessage(Profile profile, int message) {
        Message messagetoread = profile.getMessages().get(message);
        JPanel labels = new JPanel(new GridLayout(0, 1, 2, 2));
        labels.add(new JLabel("De: " + messagetoread.getSourceProfile().getName()));
        labels.add(new JLabel("Data: " + this.formatter.format(messagetoread.getDate())));
        labels.add(new JLabel("Texto: "));
        labels.add(new JLabel(messagetoread.getText()));
        Object[] buttons = {"Responder", "Eliminar", "Volver"};
        int options = JOptionPane.showOptionDialog(null, labels, "Ler mensaxe", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, buttons, null);
        switch (options) {
            case 0:
                String reply = JOptionPane.showInputDialog(this, "introduce a resposta:", "Resposta mensaxe privada", JOptionPane.QUESTION_MESSAGE);
                this.profileController.replyMessage(messagetoread, reply);
                return;
            case 1:
                this.profileController.deleteMessage(messagetoread);
                return;
        }
        this.profileController.markMessageAsRead(messagetoread);
    }


    /*
  * Método que pide ao usuario que seleccione unha mensaxe e chama ao 
  * controlador para borrala
     */
    private void deletePrivateMessage(Profile profile, int message) {
        this.profileController.deleteMessage(profile.getMessages().get(message));
    }

    /*
    * Método que pide o número de publicacións que se queren visualizar e 
    * chamar ao controlador para recargar o perfil
     */
    private void showOldPosts(Profile profile) {

        String numberposts = JOptionPane.showInputDialog(this, "Introduce o numero de publicacións a visualizar:", "Ver máis publicación", JOptionPane.QUESTION_MESSAGE);

        this.postsShowed = Integer.parseInt(numberposts);
        this.profileController.reloadProfile();
    }

    /*
    * Método que serve para cambiar o estado do perfil do usuario
     */
    private void changeStatus(Profile profile) {

        String status = JOptionPane.showInputDialog(this, "Introduce o teu novo estado: ", "Cambio de estado", JOptionPane.QUESTION_MESSAGE);

        try {
            this.profileController.updateProfileStatus(status);
        } catch (PersistenceException ex) {
            Logger.getLogger(GUIProfileView.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Método que mostra as diferentes funcions que pode facer un usuario dentro
     * do seu perfil
     *
     * @param profile Perfil do usuario
     */
    @Override
    public void showProfileMenu(Profile profile) {
        boolean ownProfile = this.profileController.getSessionProfile().getName().equals(profile.getName());
        showProfileInfo(ownProfile, profile);
        setLocationRelativeTo(null);
        this.setVisible(true);
    }

    /**
     * Método que informa que un perfil non se atopou
     */
    @Override
    public void showProfileNotFoundMessage() {
        JOptionPane.showMessageDialog(null, "Non existe un perfil con ese nome!", "Erro no nome de usuario", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Método que informa de que non se pode facer like sobre unha publicación
     * propia
     */
    @Override
    public void showCannotLikeOwnPostMessage() {
        JOptionPane.showMessageDialog(null, "Non podes dar me gusta sobre as publicacións propias", "Erro!", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Método que informa de que non se pode facer like sobre unha publicación
     * sobre a que xa se fixo like
     */
    @Override
    public void showAlreadyLikedPostMessage() {
        JOptionPane.showMessageDialog(null, "Xa deches me gusta nesta publicación!", "Erro!", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Método que informa de que xa tes amizade con ese perfil
     *
     * @param profileName Nome do perfil
     */
    @Override
    public void showIsAlreadyFriendMessage(String profileName) {
        JOptionPane.showMessageDialog(null, "Xa eres amigo de " + profileName + "!", "Erro", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Método que informa de que ese perfil xa ten unha solicitude de amizade
     * contigo
     *
     * @param profileName Nome do perfil
     */
    @Override
    public void showExistsFrienshipRequestMessage(String profileName) {
        JOptionPane.showMessageDialog(null, "Xa recibiches unha solicitude de amizade de " + profileName + "!", "Erro", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Método que informa de que xa tes unha solicitude de amizade con ese
     * perfil
     *
     * @param profileName Nome do perfil
     */
    @Override
    public void showDuplicateFrienshipRequestMessage(String profileName) {
        JOptionPane.showMessageDialog(null, "Xa mandaches unha solicitude de amizade para " + profileName + "!", "Erro", JOptionPane.WARNING_MESSAGE);

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
        JOptionPane.showMessageDialog(null, "Erro na conexión co almacén de datos!", "Erro", JOptionPane.ERROR_MESSAGE);

    }

    @Override
    public void showReadErrorMessage() {
        JOptionPane.showMessageDialog(null, "Erro na lectura de datos!", "Erro", JOptionPane.ERROR_MESSAGE);
    }

    /**
     *
     */
    @Override
    public void showWriteErrorMessage() {
        JOptionPane.showMessageDialog(null, "Erro na escritura dos datos!", "Erro", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jPanelHead = new javax.swing.JPanel();
        jLabelUserNameProfile = new javax.swing.JLabel();
        jLabelTacebookLogo = new javax.swing.JLabel();
        jLabelStatus = new javax.swing.JLabel();
        jButtonReload = new javax.swing.JButton();
        jPanelBottom = new javax.swing.JPanel();
        jButtonBackBio = new javax.swing.JButton();
        jButtonSendMessageBottom = new javax.swing.JButton();
        jButtonChangeStatus = new javax.swing.JButton();
        jButtonCloseSession = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanelBiografia = new javax.swing.JPanel();
        jLabelLastPosts = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTablePosts = new javax.swing.JTable();
        jPanelcentral = new javax.swing.JPanel();
        jButtonBioNewPost = new javax.swing.JButton();
        jButtonBioComment = new javax.swing.JButton();
        jButtonBioLike = new javax.swing.JButton();
        jButtonBioOldPosts = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableComments = new javax.swing.JTable();
        jLabelComments = new javax.swing.JLabel();
        jPanelFriends = new javax.swing.JPanel();
        jLabelFriendList = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTableFriends = new javax.swing.JTable();
        jPanelFriendsCenter = new javax.swing.JPanel();
        jButtonShowBiography = new javax.swing.JButton();
        jButtonSendPrivateMessage = new javax.swing.JButton();
        jButtonRemoveFriend = new javax.swing.JButton();
        jLabelFriendRequest = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jListFriendRequest = new javax.swing.JList<>();
        jPanel2 = new javax.swing.JPanel();
        jButtonAcceptRequest = new javax.swing.JButton();
        jButtonDenyRequest = new javax.swing.JButton();
        jButtonNewRequest = new javax.swing.JButton();
        jPanelPrivateMessages = new javax.swing.JPanel();
        jLabelPrivateMessages = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTablePrivateMessage = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jButtonReadMessage = new javax.swing.JButton();
        jButtonDeleteMessage = new javax.swing.JButton();
        jPanelSettings = new javax.swing.JPanel();
        jPanelChangePass = new javax.swing.JPanel();
        jLabelTittle = new javax.swing.JLabel();
        jLabelActualPassword = new javax.swing.JLabel();
        jLabelNewPass1 = new javax.swing.JLabel();
        jLabelNewPass2 = new javax.swing.JLabel();
        jPasswordFieldActual = new javax.swing.JPasswordField();
        jPasswordFieldNew1 = new javax.swing.JPasswordField();
        jPasswordFieldNew2 = new javax.swing.JPasswordField();
        jButtonChangePassword = new javax.swing.JButton();

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Tacebook");

        jPanelHead.setPreferredSize(new java.awt.Dimension(700, 50));

        jLabelUserNameProfile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/User_icon.png"))); // NOI18N
        jLabelUserNameProfile.setText("Perfil do Usuario:");

        jLabelTacebookLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/logo-tacebook.png"))); // NOI18N

        jLabelStatus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/status.png"))); // NOI18N
        jLabelStatus.setText("Estado actual:");

        jButtonReload.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/reload-icon.png"))); // NOI18N
        jButtonReload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonReloadActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelHeadLayout = new javax.swing.GroupLayout(jPanelHead);
        jPanelHead.setLayout(jPanelHeadLayout);
        jPanelHeadLayout.setHorizontalGroup(
            jPanelHeadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelHeadLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jLabelUserNameProfile)
                .addGap(106, 106, 106)
                .addComponent(jLabelTacebookLogo)
                .addGap(33, 33, 33)
                .addComponent(jLabelStatus)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonReload)
                .addContainerGap())
        );
        jPanelHeadLayout.setVerticalGroup(
            jPanelHeadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelHeadLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanelHeadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelHeadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabelStatus)
                        .addComponent(jButtonReload))
                    .addComponent(jLabelUserNameProfile)
                    .addComponent(jLabelTacebookLogo))
                .addGap(4, 4, 4))
        );

        jButtonBackBio.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/back_arrow.png"))); // NOI18N
        jButtonBackBio.setText("Volver a miña biografía");
        jButtonBackBio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBackBioActionPerformed(evt);
            }
        });
        jPanelBottom.add(jButtonBackBio);

        jButtonSendMessageBottom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/new_message.png"))); // NOI18N
        jButtonSendMessageBottom.setText("Enviar mensaxe privada");
        jButtonSendMessageBottom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSendMessageBottomActionPerformed(evt);
            }
        });
        jPanelBottom.add(jButtonSendMessageBottom);

        jButtonChangeStatus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/status.png"))); // NOI18N
        jButtonChangeStatus.setLabel("Cambiar Estado");
        jButtonChangeStatus.setPreferredSize(new java.awt.Dimension(154, 40));
        jButtonChangeStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonChangeStatusActionPerformed(evt);
            }
        });
        jPanelBottom.add(jButtonChangeStatus);

        jButtonCloseSession.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/exit_session.png"))); // NOI18N
        jButtonCloseSession.setText("Pechar Sesión");
        jButtonCloseSession.setPreferredSize(new java.awt.Dimension(147, 40));
        jButtonCloseSession.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCloseSessionActionPerformed(evt);
            }
        });
        jPanelBottom.add(jButtonCloseSession);

        jLabelLastPosts.setText("10 últimas publicacións:");

        jTablePosts.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Data", "Autor", "Texto", "Me gustas"
            }
        ));
        jTablePosts.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTablePostsMouseClicked(evt);
            }
        });
        jTablePosts.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTablePostsKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTablePostsKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(jTablePosts);

        jButtonBioNewPost.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/new_post.png"))); // NOI18N
        jButtonBioNewPost.setText("Nova Publicación");
        jButtonBioNewPost.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBioNewPostActionPerformed(evt);
            }
        });

        jButtonBioComment.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/comment.png"))); // NOI18N
        jButtonBioComment.setText("Comentar");
        jButtonBioComment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBioCommentActionPerformed(evt);
            }
        });

        jButtonBioLike.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/like.png"))); // NOI18N
        jButtonBioLike.setText("Gústame");
        jButtonBioLike.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBioLikeActionPerformed(evt);
            }
        });

        jButtonBioOldPosts.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/old_posts.png"))); // NOI18N
        jButtonBioOldPosts.setText("Ver anteriores publicacións");
        jButtonBioOldPosts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBioOldPostsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelcentralLayout = new javax.swing.GroupLayout(jPanelcentral);
        jPanelcentral.setLayout(jPanelcentralLayout);
        jPanelcentralLayout.setHorizontalGroup(
            jPanelcentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelcentralLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jButtonBioNewPost)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonBioComment)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonBioLike)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonBioOldPosts)
                .addContainerGap(36, Short.MAX_VALUE))
        );
        jPanelcentralLayout.setVerticalGroup(
            jPanelcentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelcentralLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanelcentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonBioNewPost)
                    .addComponent(jButtonBioComment)
                    .addComponent(jButtonBioLike)
                    .addComponent(jButtonBioOldPosts))
                .addContainerGap())
        );

        jTableComments.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Texto", "De", "Data"
            }
        ));
        jScrollPane2.setViewportView(jTableComments);

        jLabelComments.setText("Comentarios:");

        javax.swing.GroupLayout jPanelBiografiaLayout = new javax.swing.GroupLayout(jPanelBiografia);
        jPanelBiografia.setLayout(jPanelBiografiaLayout);
        jPanelBiografiaLayout.setHorizontalGroup(
            jPanelBiografiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBiografiaLayout.createSequentialGroup()
                .addComponent(jPanelcentral, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 31, Short.MAX_VALUE))
            .addGroup(jPanelBiografiaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelBiografiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanelBiografiaLayout.createSequentialGroup()
                        .addGroup(jPanelBiografiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelLastPosts, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelComments, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelBiografiaLayout.setVerticalGroup(
            jPanelBiografiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBiografiaLayout.createSequentialGroup()
                .addComponent(jLabelLastPosts)
                .addGap(2, 2, 2)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelcentral, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelComments)
                .addGap(25, 25, 25)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Biografia", new javax.swing.ImageIcon(getClass().getResource("/img/new_post.png")), jPanelBiografia); // NOI18N

        jLabelFriendList.setText("Lista de amig@s:");

        jTableFriends.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nome", "Estado"
            }
        ));
        jScrollPane4.setViewportView(jTableFriends);

        jButtonShowBiography.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/new_post.png"))); // NOI18N
        jButtonShowBiography.setText("Ver biografía");
        jButtonShowBiography.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonShowBiographyActionPerformed(evt);
            }
        });

        jButtonSendPrivateMessage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/new_message.png"))); // NOI18N
        jButtonSendPrivateMessage.setText("Enviar mensaxe privada");
        jButtonSendPrivateMessage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSendPrivateMessageActionPerformed(evt);
            }
        });

        jButtonRemoveFriend.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/remove_friend.png"))); // NOI18N
        jButtonRemoveFriend.setText("Eliminar amig@");
        jButtonRemoveFriend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRemoveFriendActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelFriendsCenterLayout = new javax.swing.GroupLayout(jPanelFriendsCenter);
        jPanelFriendsCenter.setLayout(jPanelFriendsCenterLayout);
        jPanelFriendsCenterLayout.setHorizontalGroup(
            jPanelFriendsCenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFriendsCenterLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(jButtonShowBiography)
                .addGap(84, 84, 84)
                .addComponent(jButtonSendPrivateMessage)
                .addGap(61, 61, 61)
                .addComponent(jButtonRemoveFriend)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelFriendsCenterLayout.setVerticalGroup(
            jPanelFriendsCenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFriendsCenterLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelFriendsCenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonShowBiography)
                    .addComponent(jButtonSendPrivateMessage)
                    .addComponent(jButtonRemoveFriend))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabelFriendRequest.setText("Tes solicitudes de amizade dos seguintes perfís:");

        jScrollPane5.setViewportView(jListFriendRequest);

        jButtonAcceptRequest.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/ok.png"))); // NOI18N
        jButtonAcceptRequest.setText("Aceptar Solicitude");
        jButtonAcceptRequest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAcceptRequestActionPerformed(evt);
            }
        });

        jButtonDenyRequest.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/exit_session.png"))); // NOI18N
        jButtonDenyRequest.setText("Rexeitar solicitude");
        jButtonDenyRequest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDenyRequestActionPerformed(evt);
            }
        });

        jButtonNewRequest.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/handshake.png"))); // NOI18N
        jButtonNewRequest.setText("Nova solicitude de amizade");
        jButtonNewRequest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNewRequestActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(79, Short.MAX_VALUE)
                .addComponent(jButtonAcceptRequest)
                .addGap(18, 18, 18)
                .addComponent(jButtonDenyRequest)
                .addGap(18, 18, 18)
                .addComponent(jButtonNewRequest)
                .addGap(48, 48, 48))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonAcceptRequest)
                    .addComponent(jButtonDenyRequest)
                    .addComponent(jButtonNewRequest))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanelFriendsLayout = new javax.swing.GroupLayout(jPanelFriends);
        jPanelFriends.setLayout(jPanelFriendsLayout);
        jPanelFriendsLayout.setHorizontalGroup(
            jPanelFriendsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFriendsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelFriendsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4)
                    .addComponent(jScrollPane5)
                    .addComponent(jPanelFriendsCenter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanelFriendsLayout.createSequentialGroup()
                        .addGroup(jPanelFriendsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelFriendList, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelFriendRequest, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelFriendsLayout.setVerticalGroup(
            jPanelFriendsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFriendsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelFriendList)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanelFriendsCenter, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelFriendRequest)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(73, 73, 73))
        );

        jTabbedPane1.addTab("Amig@s", new javax.swing.ImageIcon(getClass().getResource("/img/friends.png")), jPanelFriends); // NOI18N

        jLabelPrivateMessages.setText("Mensaxes privadas:");

        jTablePrivateMessage.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Lida", "Data", "De", "Texto"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane3.setViewportView(jTablePrivateMessage);

        jButtonReadMessage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/read_message.png"))); // NOI18N
        jButtonReadMessage.setText("Ler Mensaxe");
        jButtonReadMessage.setPreferredSize(new java.awt.Dimension(138, 40));
        jButtonReadMessage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonReadMessageActionPerformed(evt);
            }
        });

        jButtonDeleteMessage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/delete.png"))); // NOI18N
        jButtonDeleteMessage.setText("Eliminar mensaxe");
        jButtonDeleteMessage.setPreferredSize(new java.awt.Dimension(166, 40));
        jButtonDeleteMessage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteMessageActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(211, Short.MAX_VALUE)
                .addComponent(jButtonReadMessage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48)
                .addComponent(jButtonDeleteMessage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(156, 156, 156))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonDeleteMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonReadMessage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanelPrivateMessagesLayout = new javax.swing.GroupLayout(jPanelPrivateMessages);
        jPanelPrivateMessages.setLayout(jPanelPrivateMessagesLayout);
        jPanelPrivateMessagesLayout.setHorizontalGroup(
            jPanelPrivateMessagesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPrivateMessagesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelPrivateMessagesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanelPrivateMessagesLayout.createSequentialGroup()
                        .addComponent(jLabelPrivateMessages, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelPrivateMessagesLayout.setVerticalGroup(
            jPanelPrivateMessagesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPrivateMessagesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelPrivateMessages)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 343, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Mensaxes Privadas", new javax.swing.ImageIcon(getClass().getResource("/img/private_message.png")), jPanelPrivateMessages); // NOI18N

        jLabelTittle.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabelTittle.setText("Cambio de Contrasinal");

        jLabelActualPassword.setText("Introduce a túa contrasinal actual:");

        jLabelNewPass1.setText("Introduce a túa nova contrasinal:");

        jLabelNewPass2.setText("Volve a introducir a nova contrasinal:");

        jPasswordFieldNew2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPasswordFieldNew2ActionPerformed(evt);
            }
        });

        jButtonChangePassword.setText("Cambio de Contrasinal");
        jButtonChangePassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonChangePasswordActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelChangePassLayout = new javax.swing.GroupLayout(jPanelChangePass);
        jPanelChangePass.setLayout(jPanelChangePassLayout);
        jPanelChangePassLayout.setHorizontalGroup(
            jPanelChangePassLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelChangePassLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelChangePassLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelTittle)
                    .addGroup(jPanelChangePassLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanelChangePassLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelActualPassword)
                            .addComponent(jLabelNewPass1)
                            .addComponent(jLabelNewPass2))
                        .addGap(40, 40, 40)
                        .addGroup(jPanelChangePassLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPasswordFieldNew2, javax.swing.GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE)
                            .addComponent(jPasswordFieldNew1)
                            .addComponent(jPasswordFieldActual))))
                .addGap(357, 357, 357))
            .addGroup(jPanelChangePassLayout.createSequentialGroup()
                .addGap(146, 146, 146)
                .addComponent(jButtonChangePassword)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelChangePassLayout.setVerticalGroup(
            jPanelChangePassLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelChangePassLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelTittle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelChangePassLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelActualPassword)
                    .addComponent(jPasswordFieldActual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21)
                .addGroup(jPanelChangePassLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelNewPass1)
                    .addComponent(jPasswordFieldNew1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanelChangePassLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelNewPass2)
                    .addComponent(jPasswordFieldNew2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jButtonChangePassword)
                .addContainerGap(35, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanelSettingsLayout = new javax.swing.GroupLayout(jPanelSettings);
        jPanelSettings.setLayout(jPanelSettingsLayout);
        jPanelSettingsLayout.setHorizontalGroup(
            jPanelSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSettingsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelChangePass, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelSettingsLayout.setVerticalGroup(
            jPanelSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSettingsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelChangePass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(210, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Configuración", new javax.swing.ImageIcon(getClass().getResource("/img/Setting-icon.png")), jPanelSettings); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
            .addComponent(jPanelHead, javax.swing.GroupLayout.DEFAULT_SIZE, 733, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanelBottom, javax.swing.GroupLayout.PREFERRED_SIZE, 721, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanelHead, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 483, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(70, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(549, Short.MAX_VALUE)
                    .addComponent(jPanelBottom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonChangeStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonChangeStatusActionPerformed
        changeStatus(profileController.getSessionProfile());
    }//GEN-LAST:event_jButtonChangeStatusActionPerformed

    private void jButtonSendPrivateMessageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSendPrivateMessageActionPerformed
        if (jTableFriends.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un amig@!", "Enviar mensaxe", JOptionPane.WARNING_MESSAGE);
        } else {
            sendPrivateMessage(jTableFriends.getSelectedRow());
        }
    }//GEN-LAST:event_jButtonSendPrivateMessageActionPerformed

    private void jButtonBioNewPostActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBioNewPostActionPerformed
        writeNewPost(profileController.getShownProfile());
    }//GEN-LAST:event_jButtonBioNewPostActionPerformed

    private void jButtonBioOldPostsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBioOldPostsActionPerformed
        showOldPosts(profileController.getShownProfile());
    }//GEN-LAST:event_jButtonBioOldPostsActionPerformed

    private void jButtonBioLikeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBioLikeActionPerformed
        if (jTablePosts.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona unha publicación!", "Erro ao facer me gusta", JOptionPane.WARNING_MESSAGE);
        } else {
            addLike(profileController.getShownProfile(), jTablePosts.getSelectedRow());
        }
    }//GEN-LAST:event_jButtonBioLikeActionPerformed

    private void jButtonCloseSessionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCloseSessionActionPerformed
        dispose();
    }//GEN-LAST:event_jButtonCloseSessionActionPerformed

    private void jButtonBioCommentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBioCommentActionPerformed
        if (jTablePosts.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona unha publicación!", "Erro ao comentar", JOptionPane.WARNING_MESSAGE);
        } else {
            commentPost(profileController.getShownProfile(), jTablePosts.getSelectedRow());
        }
    }//GEN-LAST:event_jButtonBioCommentActionPerformed

    private void jTablePostsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTablePostsMouseClicked
        showComments();
    }//GEN-LAST:event_jTablePostsMouseClicked

    private void jTablePostsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTablePostsKeyPressed

    }//GEN-LAST:event_jTablePostsKeyPressed

    private void jTablePostsKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTablePostsKeyReleased
        showComments();
    }//GEN-LAST:event_jTablePostsKeyReleased

    private void jButtonNewRequestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNewRequestActionPerformed
        sendFriendshipRequest();
    }//GEN-LAST:event_jButtonNewRequestActionPerformed

    private void jButtonAcceptRequestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAcceptRequestActionPerformed
        if (jListFriendRequest.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Debes seleccionar un solictude para aceptala!", "Aceptar solicitude", JOptionPane.WARNING_MESSAGE);
        } else {
            proccessFriendshipRequest(profileController.getShownProfile(), jListFriendRequest.getSelectedIndex(), true);
            requests();
        }

    }//GEN-LAST:event_jButtonAcceptRequestActionPerformed

    private void jButtonDenyRequestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDenyRequestActionPerformed
        if (jListFriendRequest.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Debes seleccionar un solictude para rexeitala!", "Rexeitar solicitude", JOptionPane.WARNING_MESSAGE);
        } else {
            proccessFriendshipRequest(profileController.getShownProfile(), jListFriendRequest.getSelectedIndex(), false);
            requests();
        }
    }//GEN-LAST:event_jButtonDenyRequestActionPerformed

    private void jButtonShowBiographyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonShowBiographyActionPerformed
        if (jTableFriends.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un amig@!", "Mostrar biografía", JOptionPane.WARNING_MESSAGE);
        } else {
            showBiography(profileController.getShownProfile(), jTableFriends.getSelectedRow());
        }
    }//GEN-LAST:event_jButtonShowBiographyActionPerformed

    private void jButtonReadMessageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonReadMessageActionPerformed
        if (jTablePrivateMessage.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un mensaxe!", "Ler mensaxe", JOptionPane.WARNING_MESSAGE);
        } else {
            readPrivateMessage(profileController.getShownProfile(), jTablePrivateMessage.getSelectedRow());
        }
    }//GEN-LAST:event_jButtonReadMessageActionPerformed

    private void jButtonDeleteMessageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteMessageActionPerformed
        if (jTablePrivateMessage.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un mensaxe!", "Borrar mensaxe", JOptionPane.WARNING_MESSAGE);
        } else {
            deletePrivateMessage(profileController.getShownProfile(), jTablePrivateMessage.getSelectedRow());
        }
    }//GEN-LAST:event_jButtonDeleteMessageActionPerformed

    private void jButtonBackBioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBackBioActionPerformed
        showBiography(profileController.getShownProfile(), -1);
    }//GEN-LAST:event_jButtonBackBioActionPerformed

    private void jButtonSendMessageBottomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSendMessageBottomActionPerformed
        sendPrivateMessage(-1);
    }//GEN-LAST:event_jButtonSendMessageBottomActionPerformed

    private void jPasswordFieldNew2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPasswordFieldNew2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jPasswordFieldNew2ActionPerformed

    private void jButtonChangePasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonChangePasswordActionPerformed
//Boton de cambio de contrasinal

        try {
            //obtemos os datos dos jPasswordfield
            String actualpass = new String(jPasswordFieldActual.getPassword());
            String pass1 = new String(jPasswordFieldNew1.getPassword());
            String pass2 = new String(jPasswordFieldNew2.getPassword());
            //obtemos a contrasinal
            String getpass = profileController.getShownProfile().getPassword();
            //ciframos a contrasinal do jpasswordfield pra comparala
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(actualpass.getBytes());
            String passencrypt = new String(messageDigest.digest());

            //se as contraseñas novas son iguais
            if (pass1.equals(pass2)) {
                //e se a contraseña actual coa que escribimos encriptada son igual
                if (getpass.equals(passencrypt)) {
                    MessageDigest newpass = MessageDigest.getInstance("SHA-256");
                    newpass.update(pass1.getBytes());
                    //chamamos ao método changepassword da clase profilecontroller e pasámoslle a nova contrasinal cifrada
                    profileController.changePassword(new String(newpass.digest()));
                    //mostramos un joptionpane de confirmacion
                    JOptionPane.showMessageDialog(this, "Contrasinal cambiado correctamente!", "Cambio de contrasinal", JOptionPane.INFORMATION_MESSAGE);
                   //valeiramos os jpasswordfield
                    jPasswordFieldActual.setText("");
                    jPasswordFieldNew1.setText("");
                    jPasswordFieldNew2.setText("");
                } else {
                    //se as contraseñas cifradas non son correctas mostramos un joptionpane de aviso
                    JOptionPane.showMessageDialog(this, "Os datos son incorrectos!", "Erro no cambio de contrasinal", JOptionPane.WARNING_MESSAGE);
                }

            } else {
                //se as contraseñas novas non son iguais mostramos un joptionpane de aviso
                JOptionPane.showMessageDialog(this, "A contrasinal nova non coincide!", "Erro no cambio de contrasinal", JOptionPane.WARNING_MESSAGE);
            }
        } catch (NoSuchAlgorithmException | PersistenceException ex) {
            Logger.getLogger(GUIProfileView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButtonChangePasswordActionPerformed

    private void jButtonReloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonReloadActionPerformed
        //Boton de que refresca os datos
        
        profileController.reloadProfile();
    }//GEN-LAST:event_jButtonReloadActionPerformed

    private void jButtonRemoveFriendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRemoveFriendActionPerformed
//botón que borra un amig@

        if (jTableFriends.getSelectedRow() == -1) {
            //se non ten ningún amigo seleccionado mostra un joption pane de aviso
            JOptionPane.showMessageDialog(this, "Selecciona un amig@ para poder borralo!", "Mostrar biografía", JOptionPane.WARNING_MESSAGE);
        } else {
            try {
                //chama ao método remove friend
                removeFriend(profileController.getShownProfile(), jTableFriends.getSelectedRow());
            } catch (PersistenceException ex) {
                Logger.getLogger(GUIProfileView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jButtonRemoveFriendActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAcceptRequest;
    private javax.swing.JButton jButtonBackBio;
    private javax.swing.JButton jButtonBioComment;
    private javax.swing.JButton jButtonBioLike;
    private javax.swing.JButton jButtonBioNewPost;
    private javax.swing.JButton jButtonBioOldPosts;
    private javax.swing.JButton jButtonChangePassword;
    private javax.swing.JButton jButtonChangeStatus;
    private javax.swing.JButton jButtonCloseSession;
    private javax.swing.JButton jButtonDeleteMessage;
    private javax.swing.JButton jButtonDenyRequest;
    private javax.swing.JButton jButtonNewRequest;
    private javax.swing.JButton jButtonReadMessage;
    private javax.swing.JButton jButtonReload;
    private javax.swing.JButton jButtonRemoveFriend;
    private javax.swing.JButton jButtonSendMessageBottom;
    private javax.swing.JButton jButtonSendPrivateMessage;
    private javax.swing.JButton jButtonShowBiography;
    private javax.swing.JLabel jLabelActualPassword;
    private javax.swing.JLabel jLabelComments;
    private javax.swing.JLabel jLabelFriendList;
    private javax.swing.JLabel jLabelFriendRequest;
    private javax.swing.JLabel jLabelLastPosts;
    private javax.swing.JLabel jLabelNewPass1;
    private javax.swing.JLabel jLabelNewPass2;
    private javax.swing.JLabel jLabelPrivateMessages;
    private javax.swing.JLabel jLabelStatus;
    private javax.swing.JLabel jLabelTacebookLogo;
    private javax.swing.JLabel jLabelTittle;
    private javax.swing.JLabel jLabelUserNameProfile;
    private javax.swing.JList<String> jListFriendRequest;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanelBiografia;
    private javax.swing.JPanel jPanelBottom;
    private javax.swing.JPanel jPanelChangePass;
    private javax.swing.JPanel jPanelFriends;
    private javax.swing.JPanel jPanelFriendsCenter;
    private javax.swing.JPanel jPanelHead;
    private javax.swing.JPanel jPanelPrivateMessages;
    private javax.swing.JPanel jPanelSettings;
    private javax.swing.JPanel jPanelcentral;
    private javax.swing.JPasswordField jPasswordFieldActual;
    private javax.swing.JPasswordField jPasswordFieldNew1;
    private javax.swing.JPasswordField jPasswordFieldNew2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTableComments;
    private javax.swing.JTable jTableFriends;
    private javax.swing.JTable jTablePosts;
    private javax.swing.JTable jTablePrivateMessage;
    // End of variables declaration//GEN-END:variables

}

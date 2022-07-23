/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import model.Profile;

/**
 *
 * @author Jorge Val Gil e Adrián Fernández Pérez
 */
public interface ProfileView {

    public int getPostsShowed();

    public void showProfileMenu(Profile profile);

    public void showProfileNotFoundMessage();

    public void showCannotLikeOwnPostMessage();

    public void showAlreadyLikedPostMessage();

    public void showIsAlreadyFriendMessage(String profileName);

    public void showExistsFrienshipRequestMessage(String profileName);

    public void showDuplicateFrienshipRequestMessage(String profileName);

    public void showConnectionErrorMessage();

    public void showReadErrorMessage();

    public void showWriteErrorMessage();
}

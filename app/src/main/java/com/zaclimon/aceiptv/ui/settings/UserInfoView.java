package com.zaclimon.aceiptv.ui.settings;

import java.util.Date;

/**
 * Interface used to react to actions after retrieving a view.
 *
 * Created by isaac on 17-07-03.
 */

public interface UserInfoView {

    /**
     * Gets the required API endpoint in order to fetch a user's information
     * @return the URL pointing to the user information
     */
    String getUserInfoApiEndpoint();

    /**
     * Called upon successful connection and information retrieved
     * @param status the user's account status
     * @param expirationDate the user's account expiration date
     * @param isTrial whether the user's account type is a trial one
     * @param maxConnections the maximum number of connections possible on the user's account
     */
    void onConnectionSuccess(String status, Date expirationDate, boolean isTrial, int maxConnections);

    /**
     * Called upon connection failure when retrieving the user's account information
     */
    void onConnectionFailed();
}

/*
 * Created on Jan 19, 2015
 * 
 * All sources, binaries and HTML pages (C) copyright 2014 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 *
 */
package com.nextlabs.qa.keywords.pdp.beans;

import com.bluejungle.destiny.agent.pdpapi.*;

/**
 * <p>
 * PDPRequest, modified from Rest API Beans
 * </p>
 *
 */
public class PDPRequest {
    public static final String KEY_USER_NAME = "user";
    public static final String KEY_HOST_NAME = "host";
    public static final String KEY_APPLICATION_NAME = "application";
    public static final String KEY_RESOURCE_NAME_FROM = "from";
    public static final String KEY_RESOURCE_NAME_TO = "to";
    public static final String KEY_RECIPIENT_NAME = "recipient";
    public static final String KEY_POD_NAME = "policies";
    public static final String KEY_ENVIRONMENT = "environment";

    public static final String ATTR_RECIPIENT_EMAIL = "email";
    public static final String ATTR_USER_ID = "id";
    public static final String ATTR_USER_NAME = "name";
    public static final String ATTR_APPLICATION_NAME = "name";
    public static final String ATTR_HOST_IP = "inet_address";
    public static final String ATTR_HOST_HOSTNAME = "name";
    public static final String ATTR_APPLICATION_PID = "pid";
    public static final String ATTR_RESOURCE_ID = "ce::id";
    public static final String ATTR_RESOURCE_TYPE = "ce::destinytype";
    public static final String ATTR_POD_PQL = "pql";
    public static final String ATTR_POD_IGNOREDEFAULT = "ignoredefault";

    private String action = null;
    private IPDPUser user = null;
    private IPDPHost host = null;
    private IPDPApplication application = null;
    private IPDPResource[] resourceArr = null;
    private IPDPNamedAttributes[] additionalData = null;

    /**
     * <p>
     * Getter method for action
     * </p>
     *
     * @return the action
     */
    public String getAction() {
        return action;
    }

    /**
     * <p>
     * Setter method for action
     * </p>
     *
     * @param action
     *            the action to set
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * <p>
     * Getter method for user
     * </p>
     *
     * @return the user
     */
    public IPDPUser getUser() {
        return user;
    }

    /**
     * <p>
     * Setter method for user
     * </p>
     *
     * @param user
     *            the user to set
     */
    public void setUser(IPDPUser user) {
        this.user = user;
    }

    /**
     * <p>
     * Getter method for host
     * </p>
     *
     * @return the host
     */
    public IPDPHost getHost() {
        return host;
    }

    /**
     * <p>
     * Setter method for host
     * </p>
     *
     * @param host
     *            the host to set
     */
    public void setHost(IPDPHost host) {
        this.host = host;
    }

    /**
     * <p>
     * Getter method for application
     * </p>
     *
     * @return the application
     */
    public IPDPApplication getApplication() {
        return application;
    }

    /**
     * <p>
     * Setter method for application
     * </p>
     *
     * @param application
     *            the application to set
     */
    public void setApplication(IPDPApplication application) {
        this.application = application;
    }

    /**
     * <p>
     * Getter method for resourceArr
     * </p>
     *
     * @return the resourceArr
     */
    public IPDPResource[] getResourceArr() {
        return resourceArr;
    }

    /**
     * <p>
     * Setter method for resourceArr
     * </p>
     *
     * @param resourceArr
     *            the resourceArr to set
     */
    public void setResourceArr(IPDPResource[] resourceArr) {
        this.resourceArr = resourceArr;
    }

    /**
     * <p>
     * Getter method for additionalData
     * </p>
     *
     * @return the additionalData
     */
    public IPDPNamedAttributes[] getAdditionalData() {
        return additionalData;
    }

    /**
     * <p>
     * Setter method for additionalData
     * </p>
     *
     * @param additionalData
     *            the additionalData to set
     */
    public void setAdditionalData(IPDPNamedAttributes[] additionalData) {
        this.additionalData = additionalData;
    }

}

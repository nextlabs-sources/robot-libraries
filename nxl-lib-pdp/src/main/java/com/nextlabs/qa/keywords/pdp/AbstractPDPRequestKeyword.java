/*
 * Copyright (c) 2015. Nextlabs. All Rights Reserved.
 */

package com.nextlabs.qa.keywords.pdp;

import org.jspringbot.Keyword;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractPDPRequestKeyword implements Keyword {

    @Autowired
    protected PDPRequestHelper pdpRequestHelper;

}

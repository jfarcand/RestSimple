/*******************************************************************************
 * Copyright (c) 2010-2011 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * The Eclipse Public License is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 * The Apache License v2.0 is available at
 *   http://www.apache.org/licenses/LICENSE-2.0.html
 * You may elect to redistribute this code under either of these licenses.
 *******************************************************************************/
package org.sonatype.restsimple.test.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.restsimple.WebDriver;
import org.sonatype.restsimple.api.Action;
import org.sonatype.restsimple.api.DefaultServiceDefinition;
import org.sonatype.restsimple.api.DeleteServiceHandler;
import org.sonatype.restsimple.api.GetServiceHandler;
import org.sonatype.restsimple.api.MediaType;
import org.sonatype.restsimple.api.PostServiceHandler;
import org.sonatype.restsimple.api.ServiceDefinition;
import org.sonatype.restsimple.common.test.petstore.Pet;
import org.sonatype.restsimple.common.test.petstore.PetstoreAction;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public class BaseTest {

    protected static final Logger logger = LoggerFactory.getLogger(WebTest.class);

    protected final static MediaType JSON = new MediaType(PetstoreAction.APPLICATION, PetstoreAction.JSON);

    protected String targetUrl;

    protected String acceptHeader;

    protected WebDriver webDriver;

    protected ServiceDefinition serviceDefinition;

    @BeforeClass(alwaysRun = true)
    public void setUpGlobal() throws Exception {

        acceptHeader = PetstoreAction.APPLICATION + "/" + PetstoreAction.JSON;

        Action action = new PetstoreAction();
        serviceDefinition = new DefaultServiceDefinition();
        serviceDefinition
                .withHandler(new GetServiceHandler("getPet", action).consumeWith(JSON, Pet.class).producing(JSON))
                .withHandler(new GetServiceHandler("getPetString", action).consumeWith(JSON, Pet.class).producing(JSON))
                .withHandler(new DeleteServiceHandler("deletePet", action).consumeWith(JSON, Pet.class).producing(JSON))
                .withHandler(new PostServiceHandler("addPet", action).consumeWith(JSON, Pet.class).producing(JSON));

        webDriver = WebDriver.getDriver().serviceDefinition(serviceDefinition);
        targetUrl = webDriver.getUri();
        logger.info("Local HTTP server started successfully");
    }
    
    @AfterClass(alwaysRun = true)
    public void tearDown() throws Exception {
        webDriver.shutdown();
    }

}
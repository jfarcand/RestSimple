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
package org.sonatype.restsimple.acceptance;

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
import org.sonatype.restsimple.client.WebClient;
import org.sonatype.restsimple.client.WebException;
import org.sonatype.restsimple.client.WebAHCClient;
import org.sonatype.restsimple.common.test.petstore.Pet;
import org.sonatype.restsimple.common.test.petstore.PetstoreAction;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.FileAssert.fail;

public class PetstoreTest {

    private static final Logger logger = LoggerFactory.getLogger(PetstoreTest.class);

    private final static MediaType JSON = new MediaType(PetstoreAction.APPLICATION, PetstoreAction.JSON);

    public String targetUrl;

    public String acceptHeader;

    private WebDriver webDriver;

    private ServiceDefinition serviceDefinition;

    @BeforeClass(alwaysRun = true)
    public void setUpGlobal() throws Exception {

        acceptHeader = PetstoreAction.APPLICATION + "/" + PetstoreAction.JSON;

        Action action = new PetstoreAction();
        serviceDefinition = new DefaultServiceDefinition();
        serviceDefinition
                .withHandler(new GetServiceHandler("/getPet/:pet", action).consumeWith(JSON, Pet.class).producing(JSON))
                .withHandler(new DeleteServiceHandler("/deletePet/:pet", action).consumeWith(JSON, Pet.class).producing(JSON))
                .withHandler(new PostServiceHandler("/addPet/:pet", action).consumeWith(JSON, Pet.class).producing(JSON));

        webDriver = WebDriver.getDriver().serviceDefinition(serviceDefinition);
        targetUrl = webDriver.getUri();
        logger.info("Local HTTP server started successfully");
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() throws Exception {
        webDriver.shutdown();
    }

    @Test(timeOut = 20000)
    public void testPost() throws Throwable {
        logger.info("running test: testPut");

        WebClient webClient = new WebAHCClient(serviceDefinition);
        Map<String, String> m = new HashMap<String, String>();
        m.put("Content-Type", acceptHeader);

        Pet pet = (Pet) webClient.clientOf(targetUrl + "/addPet/myPet").headers(m).post("{\"name\":\"pouetpouet\"}");
        assertNotNull(pet);

        pet = webClient.clientOf(targetUrl + "/getPet/myPet").headers(m).get(Pet.class);

        assertNotNull(pet);
    }

    @Test(timeOut = 20000)
    public void testDelete() throws Throwable {
        logger.info("running test: testPut");

        WebClient webClient = new WebAHCClient(serviceDefinition);
        Map<String, String> m = new HashMap<String, String>();
        m.put("Content-Type", acceptHeader);

        Pet pet = (Pet) webClient.clientOf(targetUrl + "/addPet/myPet").headers(m).post("{\"name\":\"pouetpouet\"}");
        assertNotNull(pet);

        pet = (Pet) webClient.clientOf(targetUrl + "/deletePet/myPet").headers(m).delete();
        assertNotNull(pet);

        try {
            pet = (Pet) webClient.clientOf(targetUrl + "/getPet/myPet").headers(m).get();
            fail("No exception");
        } catch (WebException ex) {
            assertEquals(ex.getClass(), WebException.class);
        }
    }

    @Test(timeOut = 20000)
    public void testPostWithType() throws Throwable {
        logger.info("running test: testPut");

        WebClient webClient = new WebAHCClient(serviceDefinition);
        Map<String, String> m = new HashMap<String, String>();
        m.put("Content-Type", acceptHeader);

        Pet pet = webClient.clientOf(targetUrl + "/addPet/myPet").headers(m).post("{\"name\":\"pouetpouet\"}", Pet.class);
        assertNotNull(pet);

        pet = webClient.clientOf(targetUrl + "/getPet/myPet").headers(m).get(Pet.class);

        assertNotNull(pet);
    }

    @Test(timeOut = 20000)
    public void testPostWithoutSD() throws Throwable {
        logger.info("running test: testPut");

        WebClient webClient = new WebAHCClient();
        Map<String, String> m = new HashMap<String, String>();
        m.put("Content-Type", acceptHeader);
        m.put("Accept", acceptHeader);

        Pet pet = webClient.clientOf(targetUrl + "/addPet/myPet").headers(m).post("{\"name\":\"pouetpouet\"}", Pet.class);
        assertNotNull(pet);

        pet = webClient.clientOf(targetUrl + "/getPet/myPet").headers(m).get(Pet.class);

        assertNotNull(pet);
    }
}

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
import org.sonatype.restsimple.api.PutServiceHandler;
import org.sonatype.restsimple.api.ServiceDefinition;
import org.sonatype.restsimple.client.Web;
import org.sonatype.restsimple.client.WebException;
import org.sonatype.restsimple.common.test.addressbook.AddressBookAction;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.FileAssert.fail;

public class AddressBookTest {

    private static final Logger logger = LoggerFactory.getLogger(AddressBookTest.class);

    private WebDriver webDriver;

    private ServiceDefinition serviceDefinition;

    public String targetUrl;

    public String acceptHeader;

    private Web web;

    @BeforeClass(alwaysRun = true)
    public void setUpGlobal() throws Exception {

        acceptHeader = AddressBookAction.APPLICATION + "/" + AddressBookAction.JSON;

        Action action = new AddressBookAction();
        PostServiceHandler postServiceHandler = new PostServiceHandler("updateAddressBook", action);
        postServiceHandler.addFormParam("update");
        postServiceHandler.addFormParam("update2");

        serviceDefinition = new DefaultServiceDefinition();
        serviceDefinition
                .withPath("")
                .producing(new MediaType(AddressBookAction.APPLICATION, AddressBookAction.JSON))
                .producing(new MediaType(AddressBookAction.APPLICATION, AddressBookAction.XML))
                .consuming(new MediaType(AddressBookAction.APPLICATION, AddressBookAction.JSON))
                .withHandler(new PutServiceHandler("createAddressBook", action))
                .withHandler(new GetServiceHandler("getAddressBook", action))
                .withHandler(postServiceHandler)
                .withHandler(new DeleteServiceHandler("deleteAddressBook", action));

        webDriver = WebDriver.getDriver().serviceDefinition(serviceDefinition);
        targetUrl = webDriver.getUri();
        web = new Web(serviceDefinition);

        logger.info("Local HTTP server started successfully");
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() throws Exception {
        webDriver.shutdown();
    }

    @Test(timeOut = 20000)
    public void testPut() throws Throwable {
        Map<String, String> m = new HashMap<String, String>();

        String s = web.clientOf(targetUrl + "/createAddressBook/myBook").headers(m).put("doubleviedeveronique", String.class);
        assertNotNull(s);

    }

    @Test(timeOut = 20000)
    public void testPost() throws Throwable {
        logger.info("running test: testPost");
        Map<String, String> m = new HashMap<String, String>();
        String s = web.clientOf(targetUrl + "/createAddressBook/myBook").headers(m).put("doubleviedeveronique", String.class);
        assertNotNull(s);

        m = new HashMap<String, String>();
        m.put("update", "foo");
        s = web.clientOf(targetUrl + "/updateAddressBook/myBook").headers(m).post(m, String.class);

        assertNotNull(s);

    }

    @Test(timeOut = 20000)
    public void testGet() throws Throwable {
        logger.info("running test: testGet");
        Map<String, String> m = new HashMap<String, String>();
        String s = web.clientOf(targetUrl + "/createAddressBook/myBook").headers(m).put("myBook", String.class);
        assertNotNull(s);

        m = new HashMap<String, String>();
        m.put("update", "foo");
        s = web.clientOf(targetUrl + "/updateAddressBook/myBook").post(m, String.class);

        assertNotNull(s);

        s = web.clientOf(targetUrl + "/getAddressBook/myBook").headers(m).get(String.class);

        assertNotNull(s);
        assertEquals(s, "{\"entries\":\"foo - \"}");
    }

    @Test(timeOut = 20000)
    public void testDelete() throws Throwable {
        logger.info("running test: testDelete");
        Map<String, String> m = new HashMap<String, String>();
        String s = web.clientOf(targetUrl + "/createAddressBook/myBook").headers(m).put("myBook", String.class);
        assertNotNull(s);

        m = new HashMap<String, String>();
        m.put("update", "foo");
        s = web.clientOf(targetUrl + "/updateAddressBook/myBook").post(m, String.class);

        assertNotNull(s);

        s = web.clientOf(targetUrl + "/getAddressBook/myBook").headers(m).get(String.class);

        assertNotNull(s);
        assertEquals(s, "{\"entries\":\"foo - \"}");

        s = web.clientOf(targetUrl + "/deleteAddressBook/myBook").headers(m).delete(String.class);
        assertNotNull(s);

        try {
            s = web.clientOf(targetUrl + "/getAddressBook/myBook").headers(m).get(String.class);
            fail("No exception");
        } catch (WebException ex) {
            assertEquals(ex.getClass(), WebException.class);
        }

    }

}
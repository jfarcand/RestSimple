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
package org.sonatype.restsimple.tests.creator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.restsimple.WebDriver;
import org.sonatype.restsimple.annotation.Consumes;
import org.sonatype.restsimple.annotation.Delete;
import org.sonatype.restsimple.annotation.Get;
import org.sonatype.restsimple.annotation.Path;
import org.sonatype.restsimple.annotation.PathParam;
import org.sonatype.restsimple.annotation.Post;
import org.sonatype.restsimple.annotation.Produces;
import org.sonatype.restsimple.annotation.Put;
import org.sonatype.restsimple.api.ServiceDefinition;
import org.sonatype.restsimple.client.WebException;
import org.sonatype.restsimple.client.WebProxy;
import org.sonatype.restsimple.creator.MethodServiceDefinitionBuilder;
import org.sonatype.restsimple.creator.ServiceDefinitionCreatorConfig;
import org.sonatype.restsimple.tests.creator.model.Person;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.Collection;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

public class ServiceDefinitionCreatorTest {

    private MethodServiceDefinitionBuilder serviceDefinitionBuilder = new MethodServiceDefinitionBuilder();

    private static final Logger logger = LoggerFactory.getLogger(ServiceDefinitionCreatorTest.class);

    public String targetUrl;
    private WebDriver webDriver;
    private ServiceDefinition serviceDefinition;

    @BeforeClass(alwaysRun = true)
    public void setUpGlobal() throws Exception {
        serviceDefinition = serviceDefinitionBuilder.type(AddressBook.class).build();
        webDriver = WebDriver.getDriver().serviceDefinition(serviceDefinition);
        targetUrl = webDriver.getUri();
        logger.info("Local HTTP server started successfully");
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() throws Exception {
        webDriver.shutdown();
    }

    public interface AddressBookClient {

        @Path("/" + ServiceDefinitionCreatorConfig.CREATE)
        @Post
        @Produces("application/json")
        @Consumes("application/json")
        public Person createPerson(Person person);

        @Path("/" + ServiceDefinitionCreatorConfig.READ)
        @Get
        @Produces("text/plain")
        @Consumes("application/json")
        public Person readPerson(@PathParam("id") String id);

        @Path("/" + ServiceDefinitionCreatorConfig.CREATES)
        @Get
        @Produces("text/plain")
        @Consumes("application/json")
        public Collection<Person> readPeoples(@PathParam("allUsers") String user);

        @Path("/" + ServiceDefinitionCreatorConfig.CREATES)
        @Put
        @Produces("application/json")                                             
        @Consumes("application/json")
        public Person updatePerson(@PathParam("id") String id, Person person);

        @Path("/" + ServiceDefinitionCreatorConfig.DELETE)
        @Delete
        @Produces("text/plain")
        @Consumes("application/json")
        public Person deletePerson(@PathParam("id") String id);


    }

    @Test (enabled = true)
    public void testServiceDefinitionCreator()
            throws Exception {
        System.out.println(serviceDefinition);
        assertNotNull(serviceDefinition);

        AddressBookClient client = WebProxy.createProxy(AddressBookClient.class, URI.create(targetUrl));
        Person person = client.createPerson(new Person("me", "jfarcand@apache.org", "jf", "arcand"));

        assertNotNull(person);
        assertEquals(person.getFirstName(), "jf");

        person = client.readPerson("me");

        assertNotNull(person);
        assertEquals(person.getFirstName(), "jf");

        person = client.deletePerson("me");

        assertNotNull(person);
        assertEquals(person.getFirstName(), "jf");

        try {
            person = client.readPerson("me");
        } catch (WebException ex) {
            assertEquals(ex.getStatusCode(), 404);
        }

    }
}


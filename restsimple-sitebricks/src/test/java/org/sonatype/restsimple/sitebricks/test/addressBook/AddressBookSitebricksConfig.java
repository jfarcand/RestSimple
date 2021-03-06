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
package org.sonatype.restsimple.sitebricks.test.addressBook;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import org.sonatype.restsimple.api.Action;
import org.sonatype.restsimple.api.DeleteServiceHandler;
import org.sonatype.restsimple.api.GetServiceHandler;
import org.sonatype.restsimple.api.MediaType;
import org.sonatype.restsimple.api.PostServiceHandler;
import org.sonatype.restsimple.api.PutServiceHandler;
import org.sonatype.restsimple.api.ServiceDefinition;
import org.sonatype.restsimple.common.test.addressbook.AddressBook;
import org.sonatype.restsimple.common.test.addressbook.AddressBookAction;
import org.sonatype.restsimple.sitebricks.guice.RestSimpleSitebricksModule;

import java.util.ArrayList;
import java.util.List;

public class AddressBookSitebricksConfig extends GuiceServletContextListener {

    private final static MediaType JSON = new MediaType(AddressBookAction.APPLICATION, AddressBookAction.JSON);

    @Override
    protected Injector getInjector() {
        return Guice.createInjector(new RestSimpleSitebricksModule() {

            @Override
            public List<ServiceDefinition> defineServices(Injector injector) {

                List<ServiceDefinition> list = new ArrayList<ServiceDefinition>();

                Action action = new AddressBookAction();
                PostServiceHandler postServiceHandler = new PostServiceHandler("/updateAddressBook/:ad", action);
                postServiceHandler.addFormParam("update");
                postServiceHandler.addFormParam("update2");

                ServiceDefinition serviceDefinition = injector.getInstance(ServiceDefinition.class);
                serviceDefinition
                        .withPath("/")
                        .producing(JSON)
                        .consuming(JSON)
                        .withHandler( new PutServiceHandler( "/createAddressBook/:ad", action).consumeWith( JSON, AddressBook.class))
                        .get("/getAddressBook/:ad", action)
                        .withHandler(postServiceHandler)
                        .delete("/deleteAddressBook/:ad", action);
                list.add(serviceDefinition);

                serviceDefinition = injector.getInstance(ServiceDefinition.class);
                serviceDefinition
                        .withPath("/foo")
                        .withHandler(new PutServiceHandler("/createAddressBook/:ad", action).producing(JSON))
                        .withHandler(new GetServiceHandler("/getAddressBook/:ad", action).producing(JSON))
                        .withHandler(postServiceHandler.producing(JSON))
                        .withHandler(new DeleteServiceHandler("/deleteAddressBook/:ad", action).producing(JSON));
                list.add(serviceDefinition);

                return list;
            }
        });
    }
}

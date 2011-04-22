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
package org.sonatype.restsimple.api;

import java.util.Map;

/**
 * A Web client for RestSimple. 
 */
public interface WebClient {
    WebClient headers(Map<String, String> headers);

    WebClient queryString(Map<String, String> queryString);

    WebClient matrixParams(Map<String, String> matrixParams);

    WebClient clientOf(String uri);

    <T> T post(Map<String, String> formParams, Class<T> t);

    <T> T post(Object o, Class<T> t);

    <T> T post(Object o, Class<?> requestEntity, Class<T> responseEntity);

    Object post(Object o);

    Object delete(Object o);

    <T> T delete(Class<T> t);

    Object delete();

    <T> T delete(Object o, Class<T> t);

    <T> T delete(Object o, Class<?> requestEntity, Class<T> responseEntity);

    <T> T get(Class<T> t);

    Object get();

    <T> T put(Object o, Class<T> t);

    <T> T put(Object o, Class<?> requestEntity, Class<T> responseEntity);

    Object put(Object o);

    WebClient supportedContentType(MediaType mediaType);

    public static enum TYPE {
        POST, PUT, DELETE, GET
    }
}

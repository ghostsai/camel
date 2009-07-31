/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.restlet;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.Test;

/**
 *
 * @version $Revision$
 */
public class RestletFaultTest extends CamelTestSupport {

    @Override
    protected RouteBuilder createRouteBuilder() {

        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("restlet:http://localhost:9080/users/{username}?restletMethod=POST").process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        exchange.getOut().setFault(true);
                        exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, "404");
                        exchange.getOut().setHeader(Exchange.CONTENT_TYPE, "text/plain");
                        exchange.getOut().setBody("Application fault");
                    }        
                });
            }
        };
    }
    
    @Test
    public void testFaultResponse() throws Exception {
        HttpMethod method = new PostMethod("http://localhost:9080/users/homer");
        try {
            HttpClient client = new HttpClient();
            assertEquals(404, client.executeMethod(method));
            assertTrue(method.getResponseHeader("Content-Type").getValue()
                    .startsWith("text/plain"));
        } finally {
            method.releaseConnection();
        }
    }
}

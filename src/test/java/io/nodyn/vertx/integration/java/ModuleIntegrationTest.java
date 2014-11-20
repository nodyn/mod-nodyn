package io.nodyn.vertx.integration.java;
/*
 * Copyright 2013 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */

import org.junit.Test;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.testtools.TestVerticle;

import static org.vertx.testtools.VertxAssert.*;

public class ModuleIntegrationTest extends TestVerticle {

    @Test
    public void testNodynVertxIntegration() {
        vertx.eventBus().sendWithTimeout( "sample.app", 
            "Hello!", 10000, 
            new HelloHandler() );
    }

    @Override
    public void start() {
        initialize();
        JsonObject config = new JsonObject();
        config.putString("main", "./src/test/resources/sample_app.js");
        container.deployModule(System.getProperty("vertx.modulename"), config, new AsyncResultHandler<String>() {
            @Override
            public void handle(AsyncResult<String> asyncResult) {
                // Deployment is asynchronous and this this handler will be called when it's complete (or failed)
                assertTrue(asyncResult.succeeded());
                assertNotNull("deploymentID should not be null", asyncResult.result());
                // If deployed correctly then start the tests!
                startTests();
            }
        });
    }

    private static class HelloHandler implements Handler<AsyncResult<Message<String>>> {
        @Override
        public void handle(AsyncResult<Message<String>> messageAsyncResult) {
            if (messageAsyncResult.failed()) return;
            Message<String> event = messageAsyncResult.result();
            System.err.println("ok: " + event.body());
            assertTrue( event.body().equals( "Howdy!" ) );
            testComplete();
        }
    }
}

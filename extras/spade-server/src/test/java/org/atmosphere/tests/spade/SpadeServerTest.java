/*
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://jersey.dev.java.net/CDDL+GPL.html
 * or jersey/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at jersey/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 *
 * Contributor(s):
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.atmosphere.tests.spade;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import org.atmosphere.grizzly.AtmosphereSpadeServer;
import org.atmosphere.util.LoggerUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

public class SpadeServerTest {

    protected AtmosphereSpadeServer spade;

    static {
        LoggerUtils.getLogger().setLevel(Level.ALL);
    }

    protected final static String ROOT = "/*";
    public String urlTarget;
    public int port;


    public static class TestHelper {

        public static int getEnvVariable(final String varName, int defaultValue) {
            if (null == varName) {
                return defaultValue;
            }
            String varValue = System.getenv(varName);
            if (null != varValue) {
                try {
                    return Integer.parseInt(varValue);
                } catch (NumberFormatException e) {
                    // will return default value bellow
                }
            }
            return defaultValue;
        }
    }

    @BeforeMethod(alwaysRun = true)
    public void setUpGlobal() throws Exception {
        port = TestHelper.getEnvVariable("ATMOSPHERE_HTTP_PORT", 9999);
        urlTarget = "http://127.0.0.1:" + port + "/invoke";
        startServer();
    }

    @AfterMethod(alwaysRun = true)
    public void unsetAtmosphereHandler() throws Exception {
        stopServer();
    }

    @Test(timeOut = 20000)
    public void testSuspendTimeout() {
        System.out.println("Running testSuspendTimeout");
        AsyncHttpClient c = new AsyncHttpClient();
        try {
            long t1 = System.currentTimeMillis();
            Response r = c.prepareGet(urlTarget).execute().get(10, TimeUnit.SECONDS);
            assertNotNull(r);
            assertEquals(r.getStatusCode(), 200);
            String resume = r.getResponseBody();
            assertEquals(resume, "resume");
            long current = System.currentTimeMillis() - t1;
            assertTrue(current > 5000 && current < 10000);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        c.close();

    }

    public void startServer() throws Exception {
        spade = AtmosphereSpadeServer.build("http://127.0.0.1:" + port + "/",PubSubTest.class.getPackage().getName());
        spade.start();
    }

    public void stopServer() throws Exception {
        spade.stop();
    }

}
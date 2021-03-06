/*
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2007-2008 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
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
 *
 */
package org.atmosphere.cpr;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Simple {@link Future} that can be used when awiting for a {@link Broadcaster} to finish
 * it's broadcast operations to {@link AtmosphereHandler}
 *
 * @author Jeanfrancois Arcand
 */
public class BroadcasterFuture<E> implements Future {

    private final CountDownLatch latch;

    private boolean isCancelled = false;

    private boolean isDone = false;

    private final E msg;

    public BroadcasterFuture(E msg) {
        this.msg = msg;
        latch = new CountDownLatch(1);
    }

    /**
     * Cancel
     *
     * @param b
     * @return
     */
    public boolean cancel(boolean b) {
        if (latch.getCount() == 1) {
            latch.countDown();
            isCancelled = true;
        }
        return isCancelled;
    }

    /**
     * True is cancelled.
     *
     * @return
     */
    public boolean isCancelled() {
        return isCancelled;
    }

    /**
     * True when done.
     */
    public boolean isDone() {
        isDone = true;
        return isDone;
    }

    /**
     * Invoked when a {@link Broadcaster} completed it broadcast operation.
     */
    public void done() {
        isDone = true;
        latch.countDown();
    }

    /**
     * Block until a {@link Broadcaster} completed it broadcast operation.
     *
     * @return The filtered message.
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public E get() throws InterruptedException, ExecutionException {
        latch.await();
        return msg;
    }

    /**
     * Block until a {@link Broadcaster} completed it broadcast operation.
     *
     * @param l  The time to wait.
     * @param tu {@link TimeUnit}
     * @return The filtered message.
     */
    public E get(long l, TimeUnit tu) throws InterruptedException, ExecutionException, TimeoutException {
        latch.await(l, tu);
        return msg;
    }
}

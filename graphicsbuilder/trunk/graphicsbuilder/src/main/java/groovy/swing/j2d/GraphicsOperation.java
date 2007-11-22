/*
 * Copyright 2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 */

package groovy.swing.j2d;

import java.awt.Shape;
import java.beans.PropertyChangeListener;

/**
 * A GraphicsOperation abstracts an operation on a Graphics2D instance.
 *
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public interface GraphicsOperation {
    /**
     * Executes the operation.
     * @param context the contextual information neede to run the operation.
     */
    void execute( GraphicsContext context );

    /**
     * Returns the clip defined by this operation.
     * @param context the contextual information neede to run the operation.
     *
     * @return a Shape instance, it may return null if this operation doesn't
     *         have a clip
     */
    Shape getClip( GraphicsContext context );

    /**
     * Returns the name of this operation.
     *
     * @return the name of this operation
     */
    String getName();

    /**
     * Returns true if a parameter has changed value.<br>
     * This is useful for binding and animation as well as keeping a local cache
     * of values to speed up execution if no value has changed.
     */
    boolean isDirty();

    /**
     * Returns any optional parameters this operation may have.<br>
     * Optional parameters do not need to be verified.
     */
    String[] getOptionalParameters();

    /**
     * Returns all parameters this operation has.<br>
     * Parameters must be verified.
     */
    String[] getParameters();

    /**
     * Returns the value of the specified parameter.<br>
     *
     * @param name the name of the parameter
     * @return the value of the specified parameter
     */
    Object getParameterValue( String name );

    /**
     * Returns true if specified parameter has a value != null.<br>
     *
     * @param name the name of the parameter
     * @return true if value is not null
     */
    boolean parameterHasValue( String name );

    /**
     * Sets the value of the specified parameter.
     *
     * @param name the name of the parameter
     * @param value the value of the specified parameter
     */
    void setParameterValue( String name, Object value );

    /**
     * Verifies that all parameters have a value.
     *
     * @throws IllegalStateException if any parameter has not a value.
     */
    void verify();

    // Observable properties support

    /**
     * Add a PropertyChangeListener to the listener list. The listener is
     * registered for all properties. The same listener object may be added more
     * than once, and will be called as many times as it is added. If
     * <code>listener</code> is null, no exception is thrown and no action is
     * taken.
     *
     * @param listener The PropertyChangeListener to be added
     */
    void addPropertyChangeListener( PropertyChangeListener listener );

    /**
     * Remove a PropertyChangeListener from the listener list. This removes a
     * PropertyChangeListener that was registered for all properties. If
     * <code>listener</code> was added more than once to the same event
     * source, it will be notified one less time after being removed. If
     * <code>listener</code> is null, or was never added, no exception is
     * thrown and no action is taken.
     *
     * @param listener The PropertyChangeListener to be removed
     */
    void removePropertyChangeListener( PropertyChangeListener listener );
}
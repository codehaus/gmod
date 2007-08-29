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

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.ImageObserver;

/**
 * A GraphicsOperation abstracts an operation on a Graphics2D instance.
 * 
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public interface GraphicsOperation {
    /**
     * Executes the operation.
     * 
     * @param g the Graphics2D instance used to draw over
     * @param observer an ImageObserver needed when drawing image related
     *        operations
     */
    void execute( Graphics2D g, ImageObserver observer );

    /**
     * Returns the clip defined by this operation.
     * 
     * @param g the Graphics2D instance used to draw over
     * @param observer an ImageObserver needed when drawing image related
     *        operations
     * @return a Shape instance, it may return null if this operation doesn't
     *         have a clip
     */
    Shape getClip( Graphics2D g, ImageObserver observer );

    /**
     * Returns the name of this operation.
     * 
     * @return the name of this operation
     */
    String getName();

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
}
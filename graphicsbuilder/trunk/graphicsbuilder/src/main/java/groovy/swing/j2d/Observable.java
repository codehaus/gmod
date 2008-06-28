/*
 * Copyright 2007-2008 the original author or authors.
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

import java.beans.PropertyChangeListener;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public interface Observable {
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
    * <code>listener</code> was added more than once to the same event source,
    * it will be notified one less time after being removed. If
    * <code>listener</code> is null, or was never added, no exception is
    * thrown and no action is taken.
    *
    * @param listener The PropertyChangeListener to be removed
    */
   void removePropertyChangeListener( PropertyChangeListener listener );

    /**
     * Returns an array of all the listeners that were added to the
     * PropertyChangeSupport object with addPropertyChangeListener().
     *
     * @return all of the <code>PropertyChangeListeners</code> added or an
     *         empty array if no listeners have been added
     */
    PropertyChangeListener[] getPropertyChangeListeners();
}

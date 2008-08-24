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
 * limitations under the License.
 */

package org.kordamp.groovy.swing.jide.factory

import groovy.swing.factory.BindFactory
import groovy.swing.factory.ComponentFactory

import org.codehaus.groovy.runtime.InvokerHelper
import org.codehaus.groovy.binding.*

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
abstract class AbstractJideComponentFactory extends ComponentFactory {
    public AbstractJideComponentFactory( Class beanClass ) {
       super( beanClass )
    }

    public AbstractJideComponentFactory( Class beanClass, boolean leaf ) {
       super( beanClass, leaf )
    }

    protected void setWidgetAttributes( FactoryBuilderSupport builder, Object widget, Map attributes,
          boolean skipMissingProperty ) {
       // TODO revisit binding code
       Iterator iter = attributes.entrySet().iterator()
       while (iter.hasNext()) {
           def entry = iter.next()
           String property = entry.key.toString()
           if( !widget.metaClass.hasProperty(widget,property) && skipMissingProperty ){
              continue
           }
           Object value = entry.value
           if (value instanceof FullBinding) {
               FullBinding fb = (FullBinding) value
               PropertyBinding ptb = new PropertyBinding(node, property)
               fb.setTargetBinding(ptb)
               try {
                   fb.update()
               } catch (Exception e) {
                   // just eat it?
               }
               try {
                   fb.rebind()
               } catch (Exception e) {
                   // just eat it?
               }
               // this is why we cannot use entrySet().each { }
           }else{
               InvokerHelper.setProperty( widget, property, value )
           }
           iter.remove()
       }
    }

    protected void handleWidgetDelegate( FactoryBuilderSupport builder, Object widgetDelegate, String name ) {
       BindFactory.bindingAttributeDelegate( builder, widgetDelegate, attributes )
       builder.setNodeAttributes( widgetDelegate, attributes )
    }
}
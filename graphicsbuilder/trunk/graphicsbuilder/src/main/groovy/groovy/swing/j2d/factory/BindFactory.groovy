/*
 * Copyright 2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package groovy.swing.j2d.factory;

import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;
import org.codehaus.groovy.binding.ClosureSourceBinding;
import org.codehaus.groovy.binding.EventTriggerBinding;
import org.codehaus.groovy.binding.FullBinding;
import org.codehaus.groovy.binding.PropertyBinding;
import org.codehaus.groovy.binding.TargetBinding;
import org.codehaus.groovy.binding.TriggerBinding;

/**
 * @author <a href="mailto:shemnon@yahoo.com">Danno Ferrin</a>
 */
public class BindFactory extends AbstractFactory {
    /**
     * Accepted Properties...
     *
     * group?
     * source ((sourceProperty) | (sourceEvent sourceValue))
     * (target targetProperty)? (? use default javabeans property if targetProperty is not present?)
     *
     *
     * @param builder
     * @param name
     * @param value
     * @param properties
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map properties) throws InstantiationException, IllegalAccessException {
        if (value != null) {
            throw new RuntimeException(name + " elements do not accept a value argument.");
        }
        Object source = properties.remove("source");
        Object target = properties.remove("target");

        TargetBinding tb = null;
        if (target != null) {
            String targetProperty = (String) properties.remove("targetProperty");
            tb = new PropertyBinding(target, targetProperty);
        }
        FullBinding fb;

        if (properties.containsKey("sourceProperty")) {
            String property = (String) properties.remove("sourceProperty");
            PropertyBinding psb = new PropertyBinding(source, property);
            TriggerBinding trigger = psb;
            Class currentClass = source.getClass();
            fb = trigger.createBinding(psb, tb);
        } else if (properties.containsKey("sourceEvent") && properties.containsKey("sourceValue")) {
            Closure queryValue = (Closure) properties.remove("sourceValue");
            ClosureSourceBinding psb = new ClosureSourceBinding(queryValue);
            String trigger = (String) properties.remove("sourceEvent");
            EventTriggerBinding etb = new EventTriggerBinding(source, trigger);
            fb = etb.createBinding(psb, tb);
        } else {
            throw new RuntimeException(name + " does not have suffient properties to initialize");
        }

        if (target != null) {
            fb.bind();
            fb.update();
        }
        return fb;
    }

    public boolean isLeaf() {
        return true
    }
}
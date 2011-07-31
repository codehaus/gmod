/*
* Copyright 2011 the original author or authors.
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

package groovyx.javafx.factory
import groovyx.javafx.SceneGraphBuilder;
import groovyx.javafx.factory.binding.*
import java.util.Map.Entry;
import org.codehaus.groovyfx.javafx.binding.*;
import javafx.beans.property.Property;

/**
 *
 * @author jimclarke
 */
class BindFactory extends AbstractFactory {
    public static final String CONTEXT_DATA_KEY = "BindFactoryData";
    final Map<String, TriggerBinding> syntheticBindings;
    public BindFactory() {
        syntheticBindings = new HashMap();

    }
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        Object source = attributes.remove("source");
        Object target = attributes.remove("target");
        Map bindContext = builder.context.get(CONTEXT_DATA_KEY) ?: [:]
        if (bindContext.isEmpty()) {
            builder.context.put(CONTEXT_DATA_KEY, bindContext)
        }

        TargetBinding tb = null;
        if (target != null) {
            String targetProperty = attributes.remove("targetProperty") ?: value
            tb = new PropertyBinding(target, targetProperty)
            if (source == null) {                // if we have a target but no source assume the build context is the source and return
                def result
                if (attributes.remove("mutual")) {
                    result = new MutualPropertyBinding(null, null, tb, this.&getTriggerBinding)
                } else {
                    result = tb
                }
                def newAttributes = [:]
                newAttributes.putAll(attributes)
                bindContext.put(result, newAttributes)
                attributes.clear()
                return result
            }
        }
        FullBinding fb
        boolean sea = attributes.containsKey("sourceEvent")
        boolean sva = attributes.containsKey("sourceValue")
        boolean spa = attributes.containsKey("sourceProperty") || value

        if (sea && sva && !spa) {
            // entirely event triggered binding
            Closure queryValue = (Closure) attributes.remove("sourceValue")
            ClosureSourceBinding csb = new ClosureSourceBinding(queryValue)
            String trigger = (String) attributes.remove("sourceEvent")
            EventTriggerBinding etb = new EventTriggerBinding(source, trigger)
            fb = etb.createBinding(csb, tb)
        } else if (spa && !(sea && sva)) {
            // partially property driven binding
            String property = attributes.remove("sourceProperty") ?: value
            SourceBinding pb;
            if(value instanceof Property) {
                pb = new PropertyBinding((Property)value);
            }else {
                pb = new PropertyBinding(source, property)
            }

            TriggerBinding trigger
            if (sea) {
                // source trigger comes from an event
                String triggerName = (String) attributes.remove("sourceEvent")
                trigger = new EventTriggerBinding(source, triggerName)
            } else {
                // source trigger comes from a property change
                // this method will also check for synthetic properties
                trigger = getTriggerBinding(pb)
            }

            SourceBinding sb;
            if (sva) {
                // source value comes from a value closure
                Closure queryValue = (Closure) attributes.remove("sourceValue")
                sb = new ClosureSourceBinding(queryValue)
            } else {
                // soruce value is the property value
                sb = pb
            }
            // check for a mutual binding (bi-directional)
            if (attributes.remove("mutual")) {
                fb = new MutualPropertyBinding(trigger, sb, tb, this.&getTriggerBinding)
            } else {
                fb = trigger.createBinding(sb, tb)
            }
        } else if (!(sea || sva || spa)) {
            // if no sourcing is defined then assume we are a closure binding and return
            def newAttributes = [:]
            newAttributes.putAll(attributes)
            bindContext.put(tb, newAttributes)
            attributes.clear()
            return new ClosureTriggerBinding(syntheticBindings)
        } else {
            throw new RuntimeException("Both sourceEvent: and sourceValue: cannot be specified along with sourceProperty: or a value argument")
        }

        if (attributes.containsKey("value")) {
            bindContext.put(fb, [value:attributes.remove("value")])
        }
        Object o = attributes.remove("bind")
        if (    ((o == null) && !attributes.containsKey('group'))
            || ((o instanceof Boolean) && ((Boolean)o).booleanValue()))
        {
            fb.bind()
        }

        if ((attributes.group instanceof AggregateBinding) && (fb instanceof BindingUpdatable)) {
            attributes.remove('group').addBinding(fb)
        }

        builder.addDisposalClosure(fb.&unbind)
        return fb
    }
    public void onNodeCompleted(FactoryBuilderSupport builder, Object parent, Object node) {
        super.onNodeCompleted(builder, parent, node);

        if (node instanceof FullBinding && node.sourceBinding && node.targetBinding) {
            try {
                node.update()
            } catch (Exception ignored) {
                // don't throw out to top
            }
            try {
                node.rebind()
            } catch (Exception ignored) {
                // don't throw out to top
            }
        }
    }

    public boolean isLeaf() {
        return false;
    }
    public boolean isHandlesNodeChildren() {
        return true;
    }

    public boolean onNodeChildren(FactoryBuilderSupport builder, Object node, Closure childContent) {
        if ((node instanceof FullBinding) && (node.converter == null)) {
            node.converter = childContent
            return false
        } else if (node instanceof ClosureTriggerBinding) {
            node.closure = childContent
            return false;
        } else if (node instanceof TriggerBinding) {
            def bindAttrs = builder.context.get(CONTEXT_DATA_KEY)[node] ?: [:]
            if (!bindAttrs.containsKey("converter")) {
                bindAttrs["converter"] = childContent
                return false;
            }
        }

        throw new RuntimeException("Binding nodes do not accept child content when a converter is already specified")
    }
    public TriggerBinding getTriggerBinding(PropertyBinding psb) {
        String property = psb.propertyName
        Class currentClass = psb.bean.getClass()
        while (currentClass != null) {
            // should we check interfaces as well?  if so at what level?
            def trigger = (TriggerBinding) syntheticBindings.get("$currentClass.name#$property" as String)
            if (trigger != null) {
                return trigger
            }
            currentClass = currentClass.getSuperclass()
        }
        //TODO inspect the bean info and throw an error if the property is not obserbable and not bind:false?
        return psb
    }
    public bindingAttributeDelegate(FactoryBuilderSupport builder, def node, def attributes) {
        Iterator iter = attributes.entrySet().iterator()
        Map bindContext = builder.context.get(CONTEXT_DATA_KEY) ?: [:]

        while (iter.hasNext()) {
            Entry entry = (Entry) iter.next()
            String property = entry.key.toString()
            Object value = entry.value

            def bindAttrs = bindContext.get(value) ?: [:]
            def idAttr = builder.getAt(SceneGraphBuilder.DELEGATE_PROPERTY_OBJECT_ID) ?: SceneGraphBuilder.DEFAULT_DELEGATE_PROPERTY_OBJECT_ID
            def id = bindAttrs.remove(idAttr)
            if (bindAttrs.containsKey("value")) {
                node."$property" = bindAttrs.remove("value")
            }

            FullBinding fb
            if (value instanceof MutualPropertyBinding) {
                fb = (FullBinding) value
                PropertyBinding psb = new PropertyBinding(node, property)
                if (fb.sourceBinding == null) {
                    fb.sourceBinding = psb
                    finishContextualBinding(fb, builder, bindAttrs, id)
                } else if (fb.targetBinding == null) {
                    fb.targetBinding = psb
                }
            } else if (value instanceof FullBinding) {
                fb = (FullBinding) value
                fb.targetBinding = new PropertyBinding(node, property)
            } else  if (value instanceof TargetBinding) {
                PropertyBinding psb = new PropertyBinding(node, property)
                fb = getTriggerBinding(psb).createBinding(psb, value)
                finishContextualBinding(fb, builder, bindAttrs, id)
            } else if (value instanceof ClosureTriggerBinding) {
                PropertyBinding psb = new PropertyBinding(node, property)
                fb = value.createBinding(value, psb);
                finishContextualBinding(fb, builder, bindAttrs, id)
            } else {
                continue
            }
            try {
                fb.update()
            } catch (Exception e) {
                // just eat it?
            }
            try {
                fb.rebind()
                if(FXHelper.fxAttribute(node, property, value)) {
                    iter.remove();
                }
            } catch (Exception e) {
                // just eat it?
                // this is why we cannot use entrySet().each { }
                iter.remove()
            }

        }
        
    }

    private def finishContextualBinding(FullBinding fb, FactoryBuilderSupport builder, bindAttrs, id) {

        Object bindValue = bindAttrs.remove("bind")
        bindAttrs.each {k, v -> fb."$k" = v}

        if ((bindValue == null)
            || ((bindValue instanceof Boolean) && ((Boolean) bindValue).booleanValue())) {
            fb.bind()
        }

        builder.addDisposalClosure(fb.&unbind)

        // replaces ourselves in the variables
        // id: is lost to us by now, so we just assume that any storage of us is a goner as well
        //builder.getVariables().each{ Map.Entry me -> if (value.is(me.value)) me.setValue fb}
        if (id) builder.setVariable(id, fb)
    }

}


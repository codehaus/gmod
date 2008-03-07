/**
 * 
 */
package org.lpny.groovyrestlet.builder.factory;

import groovy.lang.Closure;
import groovy.util.FactoryBuilderSupport;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.data.Protocol;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

/**
 * @author keke
 * @version
 * @since
 * @revision $Revision$
 */
public class FactoryUtils {
    @SuppressWarnings("unchecked")
    protected static Object createFromSpringContext(
            final ApplicationContext springContext, final Map context)
            throws InstantiationException, IllegalAccessException {
        if (context.containsKey(AbstractFactory.OF_BEAN)) {
            if (springContext == null) {
                throw new RuntimeException(
                        "No Spring Context was specified, \"ofBean\" is not supported! ");
            }
            final String beanName = (String) context
                    .get(AbstractFactory.OF_BEAN);
            if (SpringFinder.LOG.isDebugEnabled()) {
                SpringFinder.LOG.debug("To create instance by beanName {}",
                        beanName);
            }
            return springContext.getBean(beanName);
        }
        // if user defines ofClass attribute, factory will first try to
        // check
        // whether user specifies springContext, if so factory will try
        // to
        // create bean using spring autowire bean factory
        // otherwise will use class.newInstance
        if (context.containsKey(AbstractFactory.OF_CLASS)) {
            final Object ofClazz = context.get(AbstractFactory.OF_CLASS);
            Class<?> beanClass;
            if (ofClazz.getClass().equals(String.class)) {
                try {
                    beanClass = Class.forName((String) ofClazz);
                } catch (final ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            } else if (ofClazz.getClass().equals(Class.class)) {
                beanClass = (Class<?>) ofClazz;
            } else {
                throw new IllegalArgumentException("Illegal ofClass type "
                        + ofClazz);
            }

            return springContext == null ? FactoryUtils
                    .createInstance(beanClass, (Object[]) context
                            .get(AbstractFactory.CONS_ARG)) : springContext
                    .getAutowireCapableBeanFactory().createBean(beanClass,
                            AutowireCapableBeanFactory.AUTOWIRE_AUTODETECT,
                            false);
        }
        return null;
    }

    /**
     * @param value
     * @param attributes
     * @return
     */
    @SuppressWarnings("unchecked")
    protected static List<Protocol> extractProtocols(final Object value,
            final Map attributes) {
        final List<Protocol> protocols = new ArrayList<Protocol>();
        Protocol protocol = (Protocol) attributes
                .remove(ClientFactory.PROTOCOL);
        if (protocol == null) {
            if (value != null && value instanceof Protocol) {
                protocol = (Protocol) value;
            }
        }
        if (protocol != null) {
            protocols.add(protocol);
        }
        final List<Protocol> list = (List<Protocol>) attributes
                .remove(ClientFactory.PROTOCOLS);
        if (list != null) {
            protocols.addAll(list);
        }
        return protocols;
    }

    protected static Context getParentRestletContext(
            final FactoryBuilderSupport builder) {
        Context context = null;
        // final Object parentNode = builder.getParentNode();
        final Object parentNode = builder.getCurrent();
        if (parentNode instanceof Restlet) {
            context = ((Restlet) parentNode).getContext();
        }
        return context;
    }

    @SuppressWarnings("unchecked")
    protected static boolean isAutoAttachEnabled(final Map context) {
        if (context.containsKey(AbstractFactory.AUTO_ATTACH)
                && !((Boolean) context.get(AbstractFactory.AUTO_ATTACH))
                        .booleanValue()) {
            return false;
        }
        return true;
    }

    protected static Object[] packArgs(final Object self,
            final Closure closure, Object... args) {
        if (args == null) {
            args = new Object[0];
        }
        if (args.length == closure.getParameterTypes().length) {
            return args;
        } else {
            final List<Object> newArgs = new ArrayList<Object>(Arrays
                    .asList(args));
            newArgs.add(self);
            return newArgs.toArray();
        }
    }

    static Object createInstance(final Class<?> beanClass, final Object[] args)
            throws SecurityException, IllegalArgumentException,
            InstantiationException, IllegalAccessException {
        Class<?>[] types = null;
        if (args != null) {
            types = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++) {
                types[i] = args[i].getClass();
            }
            try {
                final Constructor<?> cons = beanClass.getConstructor(types);
                return cons.newInstance(args);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            return beanClass.newInstance();
        }

    }

}

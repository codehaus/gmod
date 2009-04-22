/*
 * Copyright 2009 the original author or authors.
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
package groovy.gaelyk.servlet;

import groovy.servlet.ServletBinding;
import groovy.servlet.TemplateServlet;

import java.util.Map;

/**
 * @author Marcel Overdijk
 *
 * @see groovy.servlet.TemplateServlet
 */
public class GaelykTemplateServlet extends TemplateServlet {

    @Override
    // protected void setVariables(Binding binding) {
    protected void setVariables(ServletBinding binding) {
        GaelykBindingEnhancer enhancer = new GaelykBindingEnhancer(binding);
        enhancer.bind();
    }
}

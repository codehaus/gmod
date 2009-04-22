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

import groovy.lang.Binding;

import java.io.IOException;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.groovy.runtime.MethodClosure;

import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.mail.MailServiceFactory;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * @author Marcel Overdijk
 */
public class GaelykBindingEnhancer {

    Binding binding;
    
    public GaelykBindingEnhancer(Binding binding) {
        this.binding = binding;
    }
    
    public void bind() {
        
        // bind google app engine services
        binding.setVariable("datastoreService", DatastoreServiceFactory.getDatastoreService());
        binding.setVariable("memcacheService", MemcacheServiceFactory.getMemcacheService());
        binding.setVariable("urlFetchService", URLFetchServiceFactory.getURLFetchService());
        binding.setVariable("mailService", MailServiceFactory.getMailService());
        binding.setVariable("imagesService", ImagesServiceFactory.getImagesService());
        
        // bind user service and current user
        UserService userService = UserServiceFactory.getUserService();
        binding.setVariable("userService", userService);
        binding.setVariable("user", userService.getCurrentUser());
        
        // bind forward method
        MethodClosure c = new MethodClosure(this, "forward");
        binding.setVariable("forward", c);
        
        // bind include method
        c = new MethodClosure(this, "include");
        binding.setVariable("include", c);
        
        // bind redirect method
        c = new MethodClosure(this, "redirect");
        binding.setVariable("redirect", c);
    }

    public void forward(String path) throws ServletException, IOException {
        HttpServletRequest request = getHttpServletRequest();
        HttpServletResponse response = getHttpServletResponse();
        RequestDispatcher dispatcher = request.getRequestDispatcher(path);
        dispatcher.forward(request, response);
    } 
    
    public void include(String path) throws ServletException, IOException {
        HttpServletRequest request = getHttpServletRequest();
        HttpServletResponse response = getHttpServletResponse();
        RequestDispatcher dispatcher = request.getRequestDispatcher(path);
        dispatcher.include(request, response);
    }

    public void redirect(String location) throws IOException {
        HttpServletResponse response = getHttpServletResponse();
        response.sendRedirect(location);
    }
    
    private HttpServletRequest getHttpServletRequest() {
        return (HttpServletRequest) binding.getVariable("request");
    }
    
    private HttpServletResponse getHttpServletResponse() {
        return (HttpServletResponse) binding.getVariable("response");
    }
}

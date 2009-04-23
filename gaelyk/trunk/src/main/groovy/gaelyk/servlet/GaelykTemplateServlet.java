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

import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.mail.MailServiceFactory;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * @author Marcel Overdijk
 *
 * @see groovy.servlet.TemplateServlet
 */
public class GaelykTemplateServlet extends TemplateServlet {

    @Override
    protected void setVariables(ServletBinding binding) {
        binding.setVariable("datastoreService", DatastoreServiceFactory.getDatastoreService());
        binding.setVariable("memcacheService", MemcacheServiceFactory.getMemcacheService());
        binding.setVariable("urlFetchService", URLFetchServiceFactory.getURLFetchService());
        binding.setVariable("mailService", MailServiceFactory.getMailService());
        binding.setVariable("imagesService", ImagesServiceFactory.getImagesService());
        UserService userService = UserServiceFactory.getUserService();
        binding.setVariable("userService", userService);
        binding.setVariable("user", userService.getCurrentUser());   
    }
}

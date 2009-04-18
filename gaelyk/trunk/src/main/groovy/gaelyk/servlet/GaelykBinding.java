package groovy.gaelyk.servlet;

import groovy.servlet.ServletBinding;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.mail.MailService;
import com.google.appengine.api.mail.MailServiceFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class GaelykBinding extends ServletBinding {

    private DatastoreService datastoreService; 
    
    private MemcacheService memcacheService;
    
    private URLFetchService urlFetchService;
    
    private MailService mailService;
    
    private ImagesService imagesService;
    
    private UserService userService;

    private User user;    
    
    public GaelykBinding(HttpServletRequest request, HttpServletResponse response, ServletContext context) {
        super(request, response, context);
    }
    
    public void setVariable(String name, Object value) {
        if ("datastoreService".equals(name)) {
            throw new IllegalArgumentException("Can't bind variable to key named '" + name + "'.");
        }
        if ("memcacheService".equals(name)) {
            throw new IllegalArgumentException("Can't bind variable to key named '" + name + "'.");
        }
        if ("urlFetchService".equals(name)) {
            throw new IllegalArgumentException("Can't bind variable to key named '" + name + "'.");
        }        
        if ("mailService".equals(name)) {
            throw new IllegalArgumentException("Can't bind variable to key named '" + name + "'.");
        }
        if ("imagesService".equals(name)) {
            throw new IllegalArgumentException("Can't bind variable to key named '" + name + "'.");
        }
        if ("userService".equals(name)) {
            throw new IllegalArgumentException("Can't bind variable to key named '" + name + "'.");
        }
        if ("user".equals(name)) {
            throw new IllegalArgumentException("Can't bind variable to key named '" + name + "'.");
        }        
        super.setVariable(name, value);
    }

    public Object getVariable(String name) {
        if ("datastoreService".equals(name)) {
            return getDatastoreService();
        }
        if ("memcacheService".equals(name)) {
            return getMemcacheService();
        }
        if ("urlFetchService".equals(name)) {
            return getURLFetchService();
        }
        if ("mailService".equals(name)) {
            return getMailService();
        }
        if ("imagesService".equals(name)) {
            return getImagesService();
        }
        if ("userService".equals(name)) {
            return getUserService();
        }
        if ("user".equals(name)) {
            return getUser();
        }        
        return super.getVariable(name);
    }
    
    public DatastoreService getDatastoreService() {
        if (datastoreService == null) {
            datastoreService = DatastoreServiceFactory.getDatastoreService();
        }
        return datastoreService;
    }    
    
    public MemcacheService getMemcacheService() {
        if (memcacheService == null) {
            memcacheService = MemcacheServiceFactory.getMemcacheService();
        }
        return memcacheService;
    }
    
    public URLFetchService getURLFetchService() {
        if (urlFetchService == null) {
            urlFetchService = URLFetchServiceFactory.getURLFetchService();
        }
        return urlFetchService;
    }
    
    public MailService getMailService() {
        if (mailService == null) {
            mailService = MailServiceFactory.getMailService();
        }
        return mailService;
    }
    
    public ImagesService getImagesService() {
        if (imagesService == null) {
            imagesService = ImagesServiceFactory.getImagesService();
        }
        return imagesService;
    }
    
    public UserService getUserService() {
        if (userService == null) {
            userService = UserServiceFactory.getUserService();
        }
        return userService;
    }
    
    public User getUser() {
        if (user == null) {
            user = getUserService().getCurrentUser();
        }
        return user;
    }
}

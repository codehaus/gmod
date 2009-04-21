package groovy.gaelyk.servlet;

import groovy.lang.Binding;

import java.io.IOException;

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

public class GaelykBinding extends Binding {

    public GaelykBinding() {
        setVariable("datastoreService", DatastoreServiceFactory.getDatastoreService());
        setVariable("memcacheService", MemcacheServiceFactory.getMemcacheService());
        setVariable("urlFetchService", URLFetchServiceFactory.getURLFetchService());
        setVariable("mailService", MailServiceFactory.getMailService());
        setVariable("imagesService", ImagesServiceFactory.getImagesService());
        UserService userService = UserServiceFactory.getUserService();
        setVariable("userService", userService);
        setVariable("user", userService.getCurrentUser());
        
        MethodClosure c = new MethodClosure(this, "render");
        setVariable("render", c);
        
        c = new MethodClosure(this, "hello");
        setVariable("hello", c);
    }
    
    public void render(String template) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) getVariable("request");
        HttpServletResponse response = (HttpServletResponse) getVariable("response");
        request.getRequestDispatcher(template).forward(request, response);
    }
    
    public String hello() {
        HttpServletRequest request = (HttpServletRequest) getVariable("request");
        return "hello (" + request + ")!";
    }
}

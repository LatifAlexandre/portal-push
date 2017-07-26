package org.vaadin.artur.portalpush;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletService;

public class PushServletAlex extends VaadinServlet {
    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
    	System.out.println("PushServlet.init()");
        super.init(servletConfig);
    }

    @Override
    protected VaadinServletService createServletService(
            DeploymentConfiguration deploymentConfiguration)
            throws ServiceException {
    	System.out.println("PushServlet.createServletService()");
        PushServletService service = new PushServletService(this,
                deploymentConfiguration);
        service.init();
        return service;
    }
}

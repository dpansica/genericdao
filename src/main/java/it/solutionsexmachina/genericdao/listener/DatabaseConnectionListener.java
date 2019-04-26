package it.solutionsexmachina.genericdao.listener;

import org.apache.commons.io.FileUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.File;

@WebListener
public class DatabaseConnectionListener implements ServletContextListener
{

    private static ServletContext servletContext;

    public static ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public void contextInitialized(ServletContextEvent e)
    {
        try
        {
            servletContext = e.getServletContext();

            System.setProperty("objectdb.home", System.getProperty("user.home") + "/"+servletContext.getContextPath()+"/objectdb");

            File confFile = new File(System.getProperty("objectdb.home") + "/" + "objectdb.conf");

            if (!confFile.exists()) {
                FileUtils.copyInputStreamToFile(this.getClass().getClassLoader().getResourceAsStream("objectdb.conf"), confFile);
            }


        }
        catch (Throwable exc)
        {
            exc.printStackTrace();
        }

    }


    @Override
    public void contextDestroyed(ServletContextEvent e)
    {

    }

}

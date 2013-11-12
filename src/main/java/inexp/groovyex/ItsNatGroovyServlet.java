
package inexp.groovyex;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.servlet.ServletCategory;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import java.io.File;
import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import org.codehaus.groovy.runtime.GroovyCategorySupport;
import org.itsnat.core.http.HttpServletWrapper;


/**
 * Inspired on:
 * https://github.com/groovy/groovy-core/blob/master/subprojects/groovy-servlet/src/main/java/groovy/servlet/GroovyServlet.java
 * 
 * @author jmarranz
 */
public class ItsNatGroovyServlet extends HttpServletWrapper
{  
    protected GroovyScriptEngine gse;
    
    public ItsNatGroovyServlet()
    {
    }

    public GroovyScriptEngine getGroovyScriptEngine()
    {
        return gse;
    }
    
    public String getScriptRootPath(ServletConfig config) throws ServletException
    {
        String scriptRootPath = config.getInitParameter("scriptRootPath");
        if (scriptRootPath == null) throw new ServletException("Missing servlet init param scriptRootPath");
        return getServletContext().getRealPath("/") + "/WEB-INF/" + scriptRootPath + "/";    
    }
    
    public String getInitScript(ServletConfig config) throws ServletException
    {
        String initScript = config.getInitParameter("initScript");
        if (initScript == null) throw new ServletException("Missing servlet init param initScript");
        return initScript;
    }    

    @Override
    public void init(ServletConfig config) throws ServletException 
    {
        super.init(config);

        // Set up the scripting engine

        String pathPrefix = getScriptRootPath(config);
        
        try
        {
            this.gse = new GroovyScriptEngine(new String[]{pathPrefix}); 
        }
        catch(IOException ex) { throw new RuntimeException(ex); }
        
        //gse.getConfig().setMinimumRecompilationInterval(0); 
        
        //System.out.println("MinimumRecompilationInterval " + gse.getConfig().getMinimumRecompilationInterval());  
        
        getServletContext().log("Groovy servlet initialized on " + gse + ".");
      
        String initScript = getInitScript(config);         
        
        File initFile = new File(pathPrefix + initScript);
        if (!initFile.exists())
            throw new ServletException(initFile.getAbsolutePath() + " does not exist");
        
        final Binding binding = new Binding();
        binding.setVariable("itsNatServlet", itsNatServlet);
        binding.setVariable("servlet",     this);  
        binding.setVariable("config",      config);        
        binding.setVariable("context",     getServletContext());
        binding.setVariable("application", getServletContext());        
        
        execGroovyScript(initScript,binding);      
    }    
    
    protected void execGroovyScript(final String filePath,final Binding binding)
    {        
        Closure<Object> closure = new Closure<Object>(gse)
        {
            @Override
            public Object call() {
                try {
                    return ((GroovyScriptEngine)getDelegate()).run(filePath, binding);
                }
                catch (ResourceException e) { throw new RuntimeException(e); }
                catch (ScriptException e) { throw new RuntimeException(e); }           
            }
        };
        GroovyCategorySupport.use(ServletCategory.class, closure);      
    }
 
}



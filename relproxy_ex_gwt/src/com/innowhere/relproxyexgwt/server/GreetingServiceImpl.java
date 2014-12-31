package com.innowhere.relproxyexgwt.server;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.innowhere.relproxy.RelProxyOnReloadListener;
import com.innowhere.relproxy.jproxy.JProxy;
import com.innowhere.relproxy.jproxy.JProxyCompilerListener;
import com.innowhere.relproxy.jproxy.JProxyConfig;
import com.innowhere.relproxy.jproxy.JProxyDiagnosticsListener;
import com.innowhere.relproxy.jproxy.JProxyInputSourceFileExcludedListener;
import com.innowhere.relproxyexgwt.client.GreetingService;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
		GreetingService {

	protected GreetingServiceDelegate delegate;
	
	public void init(ServletConfig config) throws ServletException {

		super.init(config);
				
		ServletContext context = config.getServletContext();
		
	    String inputPath = context.getRealPath("/") + "/../src/";
	    
	    JProxyInputSourceFileExcludedListener excludedListener = new JProxyInputSourceFileExcludedListener()
	    {
			@Override
			public boolean isExcluded(File file, File rootFolder) {

				String absPath = file.getAbsolutePath();				
				if (file.isDirectory())
				{
					return absPath.endsWith(File.separatorChar + "client") ||
						   absPath.endsWith(File.separatorChar + "shared");					
				}
				else
				{
					return absPath.endsWith(GreetingServiceDelegate.class.getSimpleName() + ".java") || 
						   absPath.endsWith(GreetingServiceImpl.class.getSimpleName() + ".java");
				}
			}	    	
	    };
	    
	    String classFolder = null; // Optional: context.getRealPath("/") + "/WEB-INF/classes";
	    Iterable<String> compilationOptions = Arrays.asList(new String[]{"-source","1.6","-target","1.6"});
	    long scanPeriod = 200;

	    RelProxyOnReloadListener proxyListener = new RelProxyOnReloadListener() {
	        public void onReload(Object objOld, Object objNew, Object proxy, Method method, Object[] args) {
	            System.out.println("Reloaded " + objNew + " Calling method: " + method);
	        }
	    };
	    
        JProxyCompilerListener compilerListener = new JProxyCompilerListener(){
            @Override
            public void beforeCompile(File file)
            {
                System.out.println("Before compile: " + file);
            }

            @Override
            public void afterCompile(File file)
            {
                System.out.println("After compile: " + file);
            } 
        }; 
        
	    JProxyDiagnosticsListener diagnosticsListener = new JProxyDiagnosticsListener()
	    {
	        public void onDiagnostics(DiagnosticCollector<javax.tools.JavaFileObject> diagnostics)
	        {
	            List<Diagnostic<? extends JavaFileObject>> diagList = diagnostics.getDiagnostics();
	            int i = 1;
	            for (Diagnostic<? extends JavaFileObject> diagnostic : diagList)
	            {
	               System.err.println("Diagnostic " + i);
	               System.err.println("  code: " + diagnostic.getCode());
	               System.err.println("  kind: " + diagnostic.getKind());
	               System.err.println("  line number: " + diagnostic.getLineNumber());
	               System.err.println("  column number: " + diagnostic.getColumnNumber());
	               System.err.println("  start position: " + diagnostic.getStartPosition());
	               System.err.println("  position: " + diagnostic.getPosition());
	               System.err.println("  end position: " + diagnostic.getEndPosition());
	               System.err.println("  source: " + diagnostic.getSource());
	               System.err.println("  message: " + diagnostic.getMessage(null));
	               i++;
	            }
	        }
	    };

	    JProxyConfig jpConfig = JProxy.createJProxyConfig();
	    jpConfig.setEnabled(true)
	            .setRelProxyOnReloadListener(proxyListener)
	            .setInputPath(inputPath)
	            .setJProxyInputSourceFileExcludedListener(excludedListener)
	            .setScanPeriod(scanPeriod)
	            .setClassFolder(classFolder)
	            .setCompilationOptions(compilationOptions)
	            .setJProxyCompilerListener(compilerListener)
	            .setJProxyDiagnosticsListener(diagnosticsListener);

	    JProxy.init(jpConfig);
		
	    this.delegate = JProxy.create(new GreetingServiceDelegateImpl(this), GreetingServiceDelegate.class);

		
	}   // init
	
	
	
	public String greetServer(String input) throws IllegalArgumentException 
	{
		
		try
		{
			return delegate.greetServer(input);
		}
		catch(IllegalArgumentException ex)
		{
			ex.printStackTrace();
			throw ex;
		}		
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}

	public HttpServletRequest getThreadLocalRequestPublic()
	{
		return getThreadLocalRequest();
	}
}

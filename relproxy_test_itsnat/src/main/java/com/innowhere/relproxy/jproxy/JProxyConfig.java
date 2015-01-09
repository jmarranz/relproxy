
package com.innowhere.relproxy.jproxy;

import com.innowhere.relproxy.RelProxyOnReloadListener;

/**
 * Interface implemented by the configuration object needed to initialize <code>JProxy</code> and {@link JProxyScriptEngineFactory}.
 * 
 * @see JProxy#init(JProxyConfig)  
 * @see JProxyScriptEngineFactory#create(JProxyConfig) 
 * @author Jose Maria Arranz Santamaria
 */
public interface JProxyConfig
{
    /**
     * Sets whether automatic detection of source code changes is enabled.
     * 
     * <p>If set to false other configuration parameters are ignored, there is no automatic source code change detection/reload and original objects are returned
     * instead of proxies, performance penalty is zero. Setting to false is recommended in production whether source code change detection/reload is not required.</p>
     * 
     * @param enabled whether automatic source code change detection and reload is enabled. By default is true.
     * @return this object for flow API use.
     */
    public JProxyConfig setEnabled(boolean enabled);

    /**
     * Sets the class reload listener.
     * 
     * @param relListener the class reload listener. By default is null.
     * @return this object for flow API use.
     */    
    public JProxyConfig setRelProxyOnReloadListener(RelProxyOnReloadListener relListener);

    /**
     * Defines the folder root to locate source code Java files.
     * 
     * <p>Structure of the source tree must be the same as a JavaSE application, the only difference is shell scripts, shell scripts must be
     * located on the top level of the source tree (default package) and file extension is not required .</p> 
     * 
     * <p>Setting some input path is required.</p>
     * 
     * @param inputPath the folder root to locate source code Java files.
     * @return this object for flow API use.
     * @see #setInputPaths(java.lang.String[])       
     */
    public JProxyConfig setInputPath(String inputPath);
  
    /**
     * Defines the folder roots to locate source code Java files.
     * 
     * <p>Structure of the source tree must be the same as a JavaSE application, the only difference is shell scripts, shell scripts must be
     * located on the top level of the source tree (default package).</p> 
     * 
     * <p>Setting some input path is required.</p>
     * 
     * @param inputPaths the folder roots to locate source code Java files.
     * @return this object for flow API use.
     * @see #setJProxyInputSourceFileExcludedListener(JProxyInputSourceFileExcludedListener)       
     */
    public JProxyConfig setInputPaths(String[] inputPaths);    
    
    /**
     * Registers the listener implementing excluding rules to filter source files not to be part of the hot reloading system in spite of included in input paths.
     * 
     * @param listener the listener. By default is null.
     * @return this object for flow API use.
     */        
    public JProxyConfig setJProxyInputSourceFileExcludedListener(JProxyInputSourceFileExcludedListener listener);    
    
    /**
     * Registers the listener for monitoring files being compiled.
     * 
     * @param listener the listener. By default is null.
     * @return this object for flow API use.
     */        
    public JProxyConfig setJProxyCompilerListener(JProxyCompilerListener  listener);     
    
        
    /**
     * Sets the folder where to save .class files result of recompiling source code changed.
     * 
     * <p>This setting is optional and the folder must be included in Java classpath because the objective is to avoid recompiling.</p>
     * 
     * <p>Be careful when executing several Java scripts in the same time and source code has been changed, some file write collisions may happen.</p>
     * 
     * @param classFolder the folder where to save .class files. By default is null (not defined, .class files are not saved).
     * @return this object for flow API use.
     */
    public JProxyConfig setClassFolder(String classFolder);

    /**
     * Sets the delay between source code change checking.
     * 
     * <p>If this value is set to 0 or negative, no periodic source code change detection is executed and only compilation on the fly happens in load time,
     * this is valid for one shot scripts but it has no sense when using proxies.
     * 
     * @param scanPeriod the delay between source code change checking.
     * @return this object for flow API use.
     */
    public JProxyConfig setScanPeriod(long scanPeriod);
    
    /**
     * Sets the compilation options to be provided to the compiler built-in in JDK like <code>JavaCompiler.getTask()</code> method and the same you would provide to javac.
     * 
     * <p>Example of compilation options:</p>
     * <p><code>Iterable<String> compilationOptions = Arrays.asList(new String[]{"-source","1.6","-target","1.6"});</code></p>
     * 
     * @param compilationOptions compilation options passed to the internal compiler. By default is null (default compiler settings).
     * @return this object for flow API use.
     */
    public JProxyConfig setCompilationOptions(Iterable<String> compilationOptions);

    /**
     * Sets the diagnostic listener to capture compilation errors and warnings thrown by the internal compiler. 
     * 
     * <p>The following is an example similar to the default behavior when this listener is not specified:</p>
     * 
     <pre>
        JProxyDiagnosticsListener diagnosticsListener = new JProxyDiagnosticsListener()
        {
            {@code @}Override
            public void onDiagnostics(DiagnosticCollector{@code <}JavaFileObject> diagnostics)
            {
                List{@code <}Diagnostic{@code <}? extends JavaFileObject>> diagList = diagnostics.getDiagnostics();                
                int i = 1;
                for (Diagnostic diagnostic : diagList)
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
     </pre>
     * 
     * @param diagnosticsListener the diagnostic listener to capture compilation errors and warnings. By default is null, an internal listener is used logging to System.err.
     * @return this object for flow API use.
     */
    public JProxyConfig setJProxyDiagnosticsListener(JProxyDiagnosticsListener diagnosticsListener);
}

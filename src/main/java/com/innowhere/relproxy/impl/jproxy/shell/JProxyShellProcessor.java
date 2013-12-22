package com.innowhere.relproxy.impl.jproxy.shell;

import com.innowhere.relproxy.RelProxyException;
import com.innowhere.relproxy.impl.jproxy.JProxyShellInteractiveImpl;
import com.innowhere.relproxy.impl.jproxy.clsmgr.ClassDescriptorSourceScript;
import com.innowhere.relproxy.impl.jproxy.clsmgr.JProxyEngine;
import com.innowhere.relproxy.impl.jproxy.clsmgr.SourceScriptInMemory;
import com.innowhere.relproxy.impl.jproxy.clsmgr.comp.JProxyCompilationException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

/**
 *
 * @author jmarranz
 */
public class JProxyShellProcessor
{
    protected JProxyShellInteractiveImpl parent;
    protected Charset encoding = Charset.defaultCharset();
    protected ArrayList<String> codeBuffer = new ArrayList<String>(20);
    protected WindowUnicodeKeyboard keyboard = new WindowUnicodeKeyboard(encoding);
    protected Command lastCommand;
    
    public JProxyShellProcessor(JProxyShellInteractiveImpl parent)
    {
        this.parent = parent;
    }
    
    public WindowUnicodeKeyboard getWindowUnicodeKeyboard()
    {
        return keyboard;
    }
    
    public ArrayList<String> getCodeBuffer()
    {
        return codeBuffer;
    }
    
    public void test(ClassDescriptorSourceScript scriptClass,SourceScriptInMemory sourceScript)
    {
        try { Thread.sleep(2); } catch (InterruptedException ex){  }
        execute("System.out.println(\"Hello World\");",scriptClass,sourceScript); //  "Object o = null; o.equals(null);"    
    }
    
    public void loop(ClassDescriptorSourceScript scriptClass,SourceScriptInMemory sourceScript)
    {
        Scanner sc = new Scanner(System.in,encoding.name());  // No encuentro nada interesante en http://docs.oracle.com/javase/6/docs/api/java/io/Console.html  
        System.out.print(">");        
        while(true)
        {
            String line = sc.nextLine();
            Command command = Command.createCommand(this,line);
            if (command != null)
            {
                boolean success = command.run(scriptClass, sourceScript);
             
                System.out.print(">"); 
                
                if (success)
                {
                    command.runPostCommand(); // Lo normal es que no haga nada
                    this.lastCommand = command;                    
                }
            }
            else
            {
                codeBuffer.add(line);            
                System.out.print(">");
                
                this.lastCommand = null;                
            }
        }
    }    
    
    public void execute(String code,ClassDescriptorSourceScript scriptClass,SourceScriptInMemory sourceScript)
    {
        sourceScript.setScriptCode(code);
        // Recuerda que cada vez que se obtiene el timestamp se llama a System.currentTimeMillis(), es imposible que el usuario haga algo en menos de 1ms
        
        JProxyEngine engine = parent.getJProxyEngine();
        
        ClassDescriptorSourceScript scriptClass2 = null;
        try
        {
            scriptClass2 = engine.detectChangesInSources();
        }
        catch(JProxyCompilationException ex) 
        {
            System.out.println("Compilation error");
            return;
        }
        
        if (scriptClass2 != scriptClass)
            throw new RelProxyException("Internal Error");
        
        try
        {            
            scriptClass.callMainMethod(new LinkedList<String>());    
        }
        catch(Throwable ex)
        {
            ex.printStackTrace(System.out);
        }
    }      
}

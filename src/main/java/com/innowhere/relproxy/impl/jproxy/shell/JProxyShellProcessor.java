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
    public static int LINE_OFFSET = 2; // El índice en codeBuffer + este valor = al valor de la línea que se muestra al usuario, hay que tener en cuenta que contamos desde uno y la primera línea es siempre vacía
    
    protected JProxyShellInteractiveImpl parent;
    protected Charset encoding = Charset.defaultCharset();
    protected ArrayList<String> codeBuffer = new ArrayList<String>(20);
    protected WindowUnicodeKeyboard keyboard = new WindowUnicodeKeyboard(encoding);
    protected int lastLine = -1; // Indice respecto a codeBuffer
    protected int lineEditing = -1;  // Indice respecto a codeBuffer
    
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
    
    public int getLastLine()
    {
        return lastLine;
    }
    
    public void setLineEditing(int lineEditing)
    {
        this.lineEditing = lineEditing;
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
                if (command instanceof CommandError) // Era un comando pero con params erróneos
                {
                    // Nada que hacer
                    System.out.print(">");                     
                }
                else
                {
                    boolean success = command.run(scriptClass, sourceScript);

                    System.out.print(">"); 

                    if (success)
                    {
                        command.runPostCommand(); // Lo normal es que no haga nada                  
                    }
                }
            }
            else
            {
                if (lineEditing != -1)
                {
                    codeBuffer.set(lineEditing, line);
                    this.lastLine = lineEditing; 
                    this.lineEditing = -1;
                }
                else
                {
                    codeBuffer.add(line);            
                    this.lastLine = codeBuffer.size() - 1;
                }
                
                System.out.print(">");              
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

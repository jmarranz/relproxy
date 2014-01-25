package com.innowhere.relproxy.impl.jproxy.shell.inter;

import com.innowhere.relproxy.impl.jproxy.shell.inter.WindowUnicodeKeyboard;
import com.innowhere.relproxy.impl.jproxy.shell.inter.CommandError;
import com.innowhere.relproxy.impl.jproxy.shell.inter.Command;
import com.innowhere.relproxy.RelProxy;
import com.innowhere.relproxy.RelProxyException;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.ClassDescriptorSourceScript;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.JProxyEngine;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.SourceScriptInMemory;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.comp.JProxyCompilationException;
import com.innowhere.relproxy.impl.jproxy.shell.JProxyShellInteractiveImpl;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
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
    protected long codeBufferModTimestamp = 0;    
    protected WindowUnicodeKeyboard keyboard = new WindowUnicodeKeyboard(encoding);
    protected int lastLine = -1; // Indice respecto a codeBuffer
    protected int lineEditing = -1;  // Indice respecto a codeBuffer
    protected long lastCodeExecutedTimestamp = 0;    
    
    public JProxyShellProcessor(JProxyShellInteractiveImpl parent)
    {
        this.parent = parent;
    }
    
    public WindowUnicodeKeyboard getWindowUnicodeKeyboard()
    {
        return keyboard;
    }
   
    public Charset getEncoding()
    {
        return encoding;
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
        System.out.println("RelProxy Java Shell v" + RelProxy.getVersion());
        System.out.println("Write help for help");
        
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
                    setCodeBuffer(lineEditing, line); 
                    this.lineEditing = -1;
                }
                else
                {
                    addCodeBuffer(line);            
                }
                
                System.out.print(">");              
            }
        }
    }    
    
    public List<String> getCodeBuffer()
    {
        return Collections.unmodifiableList(codeBuffer);
    }    
    
    public void setCodeBuffer(int index,String line)
    {
        codeBuffer.set(index, line);        
        this.codeBufferModTimestamp = System.currentTimeMillis();
        this.lastLine = index;        
    }
    
    public void setCodeBuffer(LinkedList<String> codeBuffer)
    {
        codeBuffer.clear();
        this.codeBuffer.addAll(codeBuffer);        
        this.codeBufferModTimestamp = System.currentTimeMillis();
        this.lastLine = codeBuffer.size() - 1;        
    }        
    
    public void insertCodeBuffer(int index,String line)
    {
        codeBuffer.add(index, line);
        this.codeBufferModTimestamp = System.currentTimeMillis();
        this.lastLine = index;        
    }    
    
    public void addCodeBuffer(String line)
    {
        codeBuffer.add(line);        
        this.codeBufferModTimestamp = System.currentTimeMillis();
        this.lastLine = codeBuffer.size() - 1;        
    }    
    
    public void removeCodeBuffer(int index)
    {
        codeBuffer.remove(index);
        this.codeBufferModTimestamp = System.currentTimeMillis();
        this.lastLine = -1;  // La hemos eliminado, no existe ya
    }        
    
    public void clearCodeBuffer()
    {
        codeBuffer.clear();        
        this.codeBufferModTimestamp = System.currentTimeMillis();
        this.lastLine = - 1;        
    }        
    
    public void execute(ClassDescriptorSourceScript scriptClass,SourceScriptInMemory sourceScript)
    {    
        StringBuilder code = new StringBuilder();
        for(String line : codeBuffer)
        {
            code.append(line);
            code.append("\n");                
        }    
        execute(code.toString(),scriptClass,sourceScript);
    }
    
    private void execute(String code,ClassDescriptorSourceScript scriptClass,SourceScriptInMemory sourceScript)
    {
        if (codeBufferModTimestamp > lastCodeExecutedTimestamp)
        {
            this.lastCodeExecutedTimestamp = System.currentTimeMillis();
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
        }
        
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

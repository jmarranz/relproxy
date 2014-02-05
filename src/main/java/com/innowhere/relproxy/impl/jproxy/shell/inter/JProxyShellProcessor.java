package com.innowhere.relproxy.impl.jproxy.shell.inter;

import com.innowhere.relproxy.RelProxy;
import com.innowhere.relproxy.RelProxyException;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.ClassDescriptorSourceScript;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.JProxyEngine;
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
    protected WindowUnicodeKeyboard keyboard = new WindowUnicodeKeyboard(encoding);
    protected int lastLine = -1; // Indice respecto a codeBuffer
    protected int lineEditing = -1;  // Indice respecto a codeBuffer
    protected long codeBufferModTimestamp = 0;     
    protected long lastCodeCompiledTimestamp = 0;    
    
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
    
    public void test()
    {
        try { Thread.sleep(2); } catch (InterruptedException ex){  }
        execute("System.out.println(\"Hello World\");"); //  "Object o = null; o.equals(null);"    
    }
    
    public void loop()
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
                    boolean success = command.run();

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
    
    public void executeCodeBuffer()
    {    
        StringBuilder code = new StringBuilder();
        for(String line : codeBuffer)
        {
            code.append(line);
            code.append("\n");                
        }    
        execute(code.toString());
    }
    
    private void execute(String code)
    {
        // Este código no es thread safe ni falta que hace.
        
        ClassDescriptorSourceScript classDescSourceScript = parent.getClassDescriptorSourceScript();
        
        if (codeBufferModTimestamp > lastCodeCompiledTimestamp)  // Incluimos el = por si acaso va todo muy seguido
        {
            parent.getSourceScriptInMemory().setScriptCode(code,codeBufferModTimestamp);
            // Recuerda que cada vez que se obtiene el timestamp se llama a System.currentTimeMillis(), es imposible que el usuario haga algo en menos de 1ms

            JProxyEngine engine = parent.getJProxyEngine();

            ClassDescriptorSourceScript classDescSourceScript2 = null;
            try
            {
                classDescSourceScript2 = engine.detectChangesInSources();
            }
            catch(JProxyCompilationException ex) 
            {
                System.out.println("Compilation error");
                return;
            }

            if (classDescSourceScript2 != classDescSourceScript)
                throw new RelProxyException("Internal Error");
            
            this.lastCodeCompiledTimestamp = System.currentTimeMillis();      
            if (lastCodeCompiledTimestamp == codeBufferModTimestamp) // Demasiado rápido compilando
            {
                // El ser humano es muy raro y es posible que a alguien se le ocurra usar el shell de forma automatizada y se genere un siguiente cambio en el 
                // código fuente tan rápido que no cambie el ms, así nos aseguramos con total rotundidad que la modificación posterior de código fuente su timestamp es MAYOR que el de compilación último
                try { Thread.sleep(1); } catch (InterruptedException ex) { throw new RelProxyException(ex);  }
            }
        }
        
        try
        {
            classDescSourceScript.callMainMethod(new LinkedList<String>());    
        }
        catch(Throwable ex)
        {
            ex.printStackTrace(System.out);
        }
    }      
}


package com.innowhere.relproxy.impl.jproxy.shell.inter;

import com.innowhere.relproxy.RelProxyException;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.ClassDescriptorSourceScript;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.SourceScriptInMemory;

/**
 *
 * @author jmarranz
 */
public class CommandOther extends Command
{
    public CommandOther(JProxyShellProcessor parent,String name)
    {
        super(parent,name);       
    }    
    
    @Override
    public boolean run()
    {
        if (name.equals("clear"))
        {
            commandClear();
        }     
        else if (name.equals("display"))
        {
            commandDisplay();
        }            
        else if (name.equals("exec"))
        {
            commandExec();
        }
        else if (name.equals("exit"))
        {
            commandExit();
        }
        else if (name.equals("help"))
        {
            commandHelp();
        }                     
        else if (name.equals("quit"))
        {
            commandExit();
        }        
        else throw new RelProxyException("Internal Error");
        
        return true;
    }    
    
    @Override
    public void runPostCommand()
    {
    }
    
    private void commandClear()
    {
        parent.clearCodeBuffer();
    }
    
    private void commandExit()
    {
        System.exit(0);
    }    
    
    private void commandDisplay()
    {
        System.out.println("001>"); // La primera línea es siempre vacía porque en ella es donde ponemos el "public class /_jproxyShellInMemoryClass_ { " que el usuario ignora, así al dar error el número de línea será correcto respecto al "display"
        
        int i = 2;
        for(String line : parent.getCodeBuffer())
        {
            for(int j = 0; j < 3 - String.valueOf(i).length(); j++) System.out.print("0"); 
            System.out.print(i + ">");
            System.out.print(line);                
            System.out.println(); 
            i++;
        }    
    }
    
    private void commandExec()
    {
        parent.executeCodeBuffer();        
    }    
    
    private void commandHelp()
    {
        System.out.println("Everything you write in the prompt is added to a code buffer, code buffer is compiled on the fly and executed by exec command, unless a command is detected");
        System.out.println("");        
        System.out.println("Available commands:");
        System.out.println(" clear"); 
        System.out.println("         Clears the buffer");            
        System.out.println(" display");         
        System.out.println("         Shows the buffer content");        
        System.out.println(" edit last | <number>");        
        System.out.println("         Edits the last introduced line code or the specified line number");        
        System.out.println(" exec");         
        System.out.println("         Compile and execute the buffer content");        
        System.out.println(" exit");          
        System.out.println("         Exits shell");         
        System.out.println(" help");        
        System.out.println("         This command"); 
        System.out.println(" insert last | <number>");        
        System.out.println("         Insert the next line of code before the last introduced line or the specified line number");        
        System.out.println(" load <path> | <url>");        
        System.out.println("         Load a file or URL into the buffer");         
        System.out.println(" quit");        
        System.out.println("         Same as exit"); 
        System.out.println(" save <path>");        
        System.out.println("         Save the current buffer to a file");         
    }    
}

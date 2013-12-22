
package com.innowhere.relproxy.impl.jproxy.shell;

import com.innowhere.relproxy.impl.jproxy.clsmgr.ClassDescriptorSourceScript;
import com.innowhere.relproxy.impl.jproxy.clsmgr.SourceScriptInMemory;

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
    public boolean run(ClassDescriptorSourceScript scriptClass,SourceScriptInMemory sourceScript)
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
            commandExec(scriptClass,sourceScript);
        }
        else if (name.equals("exit"))
        {
            commandExit();
        }
        else if (name.equals("quit"))
        {
            commandExit();
        }        
        
        return true;
    }    
    
    @Override
    public void runPostCommand()
    {
    }
    
    private void commandClear()
    {
        parent.getCodeBuffer().clear();
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
    
    private void commandExec(ClassDescriptorSourceScript scriptClass,SourceScriptInMemory sourceScript)
    {
        StringBuilder code = new StringBuilder();
        for(String line : parent.getCodeBuffer())
        {
            code.append(line);
            code.append("\n");                
        }

        parent.execute(code.toString(),scriptClass,sourceScript);        
    }    
}
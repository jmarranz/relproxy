package com.innowhere.relproxy.impl.jproxy.shell;

import com.innowhere.relproxy.impl.jproxy.clsmgr.ClassDescriptorSourceScript;
import com.innowhere.relproxy.impl.jproxy.clsmgr.SourceScriptInMemory;

/**
 *
 * @author jmarranz
 */
public class CommandEdit extends Command
{
    public static final String NAME = "edit";
    protected String codeLine;
    protected int line;        
    
    public CommandEdit(JProxyShellProcessor parent,int line,String codeLine)
    {
        super(parent,NAME);
        this.line = line;
        this.codeLine = codeLine;        
    }    
    
    public static CommandEdit createCommandEdit(JProxyShellProcessor parent,String cmd)
    {
        int pos = cmd.indexOf("edit ");
        if (pos != 0)
        {
            System.out.println("Command error: parameter \"last\" or a line number is required");
            return null;
        }
        pos = cmd.indexOf(' ');        
        String param = cmd.substring(pos + 1);
        param = param.trim();
        int line;
        if (param.equals("last"))
        {
            int lastLine = parent.getLastLine();
            if (lastLine == -1)
            {
                System.out.println("Command error: no line code has been introduced yet");
                return null;
            }
            line = lastLine;
        }
        else
        {
            try
            {
                line = Integer.parseInt(param); 
            }
            catch(NumberFormatException ex)
            {
                return null;
            }           
            // Ojo es el valor dado por el usuario (empezando en 1 y con línea vacía)
            if (line <= 0)
            {
                System.out.println("Command error: line value cannot be 0 or negative");
                return null;
            }            
            else if (line == 1)
            {
                System.out.println("Command error: line 1 is ever empty and cannot be edited");
                return null;
            }
            line -= JProxyShellProcessor.LINE_OFFSET;  
                       
            if (line >= parent.getCodeBuffer().size())
            {
                System.out.println("Command error: line number out of range");
                return null;
            }                
        }
        
        String codeLine = parent.getCodeBuffer().get(line);    
        return new CommandEdit(parent,line,codeLine);    
    }
    
    @Override
    public boolean run(ClassDescriptorSourceScript scriptClass, SourceScriptInMemory sourceScript)
    {
        return true;
    }    
    
    @Override
    public void runPostCommand()
    {
        parent.getWindowUnicodeKeyboard().type(codeLine);
        parent.setLineEditing(line);
    }
}

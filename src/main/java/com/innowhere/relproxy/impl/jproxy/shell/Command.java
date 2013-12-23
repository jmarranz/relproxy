package com.innowhere.relproxy.impl.jproxy.shell;

import com.innowhere.relproxy.impl.jproxy.clsmgr.ClassDescriptorSourceScript;
import com.innowhere.relproxy.impl.jproxy.clsmgr.SourceScriptInMemory;

/**
 *
 * @author jmarranz
 */
public abstract class Command
{
    protected JProxyShellProcessor parent;    
    protected String name;
    
    public Command(JProxyShellProcessor parent,String name)
    {
        this.parent = parent;
        this.name = name;
    }
    
    public static Command createCommand(JProxyShellProcessor parent,String cmd)
    {
        cmd = cmd.trim();
        if (cmd.equals("clear"))
        {
            return new CommandOther(parent,cmd);
        }     
        else if (cmd.equals("display"))
        {
            return new CommandOther(parent,cmd);
        }       
        else if (cmd.startsWith("edit"))
        {            
            CommandEdit command = CommandEdit.createCommandEdit(parent,cmd);
            if (command != null)
                return command;
            else
                return new CommandError(parent);            
        }               
        else if (cmd.equals("exec"))
        {
            return new CommandOther(parent,cmd);
        }
        else if (cmd.equals("exit"))
        {
            return new CommandOther(parent,cmd);
        }
        else if (cmd.equals("quit"))
        {
            return new CommandOther(parent,cmd);
        }        
        return null; // No es un comando
    }    
    
    public abstract boolean run(ClassDescriptorSourceScript scriptClass,SourceScriptInMemory sourceScript);    
    
    public abstract void runPostCommand();    
}

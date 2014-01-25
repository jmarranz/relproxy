package com.innowhere.relproxy.impl.jproxy.shell.inter;

import com.innowhere.relproxy.impl.jproxy.core.clsmgr.ClassDescriptorSourceScript;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.SourceScriptInMemory;

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
        else if (cmd.startsWith("delete"))
        {            
            CommandDelete command = CommandDelete.createCommandDelete(parent,cmd);
            if (command != null)
                return command;
            else
                return new CommandError(parent);            
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
        else if (cmd.equals("help"))
        {
            return new CommandOther(parent,cmd);
        }        
        else if (cmd.startsWith("insert"))
        {            
            CommandInsert command = CommandInsert.createCommandInsert(parent,cmd);
            if (command != null)
                return command;
            else
                return new CommandError(parent);            
        }         
        else if (cmd.startsWith("load"))
        {            
            CommandLoad command = CommandLoad.createCommandLoad(parent,cmd);
            if (command != null)
                return command;
            else
                return new CommandError(parent);            
        }                
        else if (cmd.equals("quit"))
        {
            return new CommandOther(parent,cmd);
        }     
        else if (cmd.startsWith("save"))
        {            
            CommandSave command = CommandSave.createCommandSave(parent,cmd);
            if (command != null)
                return command;
            else
                return new CommandError(parent);            
        }      
        
        return null; // No es un comando
    }    
    
    protected static String getParameter(String cmdName,String cmd)
    {
        int pos = cmd.indexOf(cmdName + " ");
        if (pos != 0)
            return null;
        pos = cmd.indexOf(' ');        
        String param = cmd.substring(pos + 1);
        param = param.trim();    
        return param;
    }
    
    public abstract boolean run(ClassDescriptorSourceScript scriptClass,SourceScriptInMemory sourceScript);    
    
    public abstract void runPostCommand();    
}

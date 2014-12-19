package com.innowhere.relproxy.impl.jproxy.shell.inter;

import com.innowhere.relproxy.impl.jproxy.core.clsmgr.ClassDescriptorSourceScript;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.SourceScriptRootInMemory;
import static com.innowhere.relproxy.impl.jproxy.shell.inter.Command.getParameter;
import static com.innowhere.relproxy.impl.jproxy.shell.inter.CommandCodeChangerBase.ERROR_LAST_REQUIRED;
import static com.innowhere.relproxy.impl.jproxy.shell.inter.CommandCodeChangerBase.ERROR_LINE_1_NOT_VALID;
import static com.innowhere.relproxy.impl.jproxy.shell.inter.CommandCodeChangerBase.ERROR_NOT_A_NUMBER;
import static com.innowhere.relproxy.impl.jproxy.shell.inter.CommandCodeChangerBase.ERROR_NO_LAST_LINE;
import static com.innowhere.relproxy.impl.jproxy.shell.inter.CommandCodeChangerBase.ERROR_OUT_OF_RANGE;
import static com.innowhere.relproxy.impl.jproxy.shell.inter.CommandCodeChangerBase.ERROR_VALUE_NOT_0_OR_NEGATIVE;
import static com.innowhere.relproxy.impl.jproxy.shell.inter.CommandCodeChangerBase.getLineFromParam;
import static com.innowhere.relproxy.impl.jproxy.shell.inter.CommandDelete.NAME;


/**
 *
 * @author jmarranz
 */
public class CommandEdit extends CommandCodeChangerBase
{
    public static final String NAME = "edit";
    protected String codeLine;       
    
    public CommandEdit(JProxyShellProcessor parent,int line,String codeLine)
    {
        super(parent,NAME,line);
        this.codeLine = codeLine;        
    }    
    
    public static CommandEdit createCommandEdit(JProxyShellProcessor parent,String cmd)
    {
        int line = getLineFromParam(parent,NAME,cmd);
        if (line < 0)
        {
            switch(line)
            {
                case ERROR_LAST_REQUIRED: 
                    System.out.println("Command error: parameter \"last\" or a line number is required");
                    break;
                case ERROR_NO_LAST_LINE:
                    System.out.println("Command error: no new or edited line code has been introduced");              
                    break;
                case ERROR_NOT_A_NUMBER:
                    System.out.println("Command error: line value is not a number");                    
                    break;  
                case ERROR_VALUE_NOT_0_OR_NEGATIVE:
                    System.out.println("Command error: line value cannot be 0 or negative");                   
                    break;  
                case ERROR_LINE_1_NOT_VALID:
                    System.out.println("Command error: line 1 is ever empty and cannot be edited");                   
                    break;    
                case ERROR_OUT_OF_RANGE:
                    System.out.println("Command error: line number out of range");                  
                    break; 
                default:
                    // Para que se calle el FindBugs
            }
            return null;
        }        
        
        String codeLine = parent.getCodeBuffer().get(line);    
        return new CommandEdit(parent,line,codeLine);    
    }
    
    @Override
    public void runPostCommand()
    {
        parent.getKeyboard().type(codeLine);
        parent.setLineEditing(line);
    }
}

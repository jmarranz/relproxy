package com.innowhere.relproxy.impl.jproxy.shell.inter;


/**
 *
 * @author jmarranz
 */
public class CommandInsert extends CommandCodeChangerBase
{
    public static final String NAME = "insert";        
    
    public CommandInsert(JProxyShellProcessor parent,int line)
    {
        super(parent,NAME,line);        
    }    
    
    public static CommandInsert createCommandInsert(JProxyShellProcessor parent,String cmd)
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
                    System.out.println("Command error: line 1 is ever empty and no code can be inserted before");                   
                    break;    
                case ERROR_OUT_OF_RANGE:
                    System.out.println("Command error: line number out of range");                  
                    break; 
                default:
                     // Para que se calle el FindBugs
            }
            return null;
        }                
            
        return new CommandInsert(parent,line);    
    }
    
    @Override
    public void runPostCommand()
    {
        parent.insertCodeBuffer(line,"");
        parent.setLineEditing(line);
    }
}

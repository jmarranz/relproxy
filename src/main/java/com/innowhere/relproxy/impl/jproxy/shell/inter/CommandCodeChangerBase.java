package com.innowhere.relproxy.impl.jproxy.shell.inter;

import com.innowhere.relproxy.impl.jproxy.core.clsmgr.ClassDescriptorSourceScript;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.SourceScriptRootInMemory;
import static com.innowhere.relproxy.impl.jproxy.shell.inter.Command.getParameter;


/**
 *
 * @author jmarranz
 */
public abstract class CommandCodeChangerBase extends Command
{
    public static final int ERROR_LAST_REQUIRED = -1;
    public static final int ERROR_NO_LAST_LINE = -2;    
    public static final int ERROR_NOT_A_NUMBER = -3;    
    public static final int ERROR_VALUE_NOT_0_OR_NEGATIVE = -4;        
    public static final int ERROR_LINE_1_NOT_VALID = -5;    
    public static final int ERROR_OUT_OF_RANGE = -6;    
    
    
    protected int line;        
    
    public CommandCodeChangerBase(JProxyShellProcessor parent,String name,int line)
    {
        super(parent,name);
        this.line = line;        
    }    
    
    public static int getLineFromParam(JProxyShellProcessor parent,String name,String cmd)
    {
        String param = getParameter(name,cmd);
        if (param == null)
        {
            return ERROR_LAST_REQUIRED;
        }        

        int line;
        if (param.equals("last"))
        {
            int lastLine = parent.getLastLine();
            if (lastLine == -1)
            {
                return ERROR_NO_LAST_LINE;
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
                return ERROR_NOT_A_NUMBER;
            }
            // Ojo es el valor dado por el usuario (empezando en 1 y con línea vacía)
            if (line <= 0)
            {
                return ERROR_VALUE_NOT_0_OR_NEGATIVE;
            }            
            else if (line == 1)
            {
                return ERROR_LINE_1_NOT_VALID;
            }
            line -= JProxyShellProcessor.LINE_OFFSET;  

            if (line >= parent.getCodeBuffer().size())
            {
                return ERROR_OUT_OF_RANGE;
            }
        }    
        return line;
    }
    
    
    @Override
    public boolean run()
    {
        return true;
    }    

}

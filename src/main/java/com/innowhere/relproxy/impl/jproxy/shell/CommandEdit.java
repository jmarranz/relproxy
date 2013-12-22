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
    protected String text;
            
    public CommandEdit(JProxyShellProcessor parent,String text)
    {
        super(parent,NAME);
        this.text = text;        
        
        // Recuerda que la línea 1 no se puede editar (siempre vacía)
    }    
    
    @Override
    public boolean run(ClassDescriptorSourceScript scriptClass, SourceScriptInMemory sourceScript)
    {
        return true;
    }    
    
    @Override
    public void runPostCommand()
    {
        parent.getWindowUnicodeKeyboard().type(text);
    }
}

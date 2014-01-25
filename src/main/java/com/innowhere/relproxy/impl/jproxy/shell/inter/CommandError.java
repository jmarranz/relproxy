package com.innowhere.relproxy.impl.jproxy.shell.inter;

import com.innowhere.relproxy.impl.jproxy.core.clsmgr.ClassDescriptorSourceScript;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.SourceScriptInMemory;

/**
 *
 * @author jmarranz
 */
public class CommandError extends Command
{
    public CommandError(JProxyShellProcessor parent)
    {
        super(parent, "ERROR");
    }

    @Override
    public boolean run(ClassDescriptorSourceScript scriptClass, SourceScriptInMemory sourceScript)
    {
        return false;
    }

    @Override
    public void runPostCommand()
    {
    }
    
}

package com.innowhere.relproxy.impl.jproxy.shell;

import com.innowhere.relproxy.impl.jproxy.clsmgr.ClassDescriptorSourceScript;
import com.innowhere.relproxy.impl.jproxy.clsmgr.SourceScriptInMemory;

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

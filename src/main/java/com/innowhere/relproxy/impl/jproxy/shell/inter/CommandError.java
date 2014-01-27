package com.innowhere.relproxy.impl.jproxy.shell.inter;

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
    public boolean run()
    {
        return false;
    }

    @Override
    public void runPostCommand()
    {
    }
    
}

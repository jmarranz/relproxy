package com.innowhere.relproxy.impl.jproxy.core.clsmgr;

/**
 *
 * @author jmarranz
 */
public abstract class SourceScriptRoot extends SourceUnit
{
    public SourceScriptRoot(String className)
    {
        super(className);
    }
    
    public abstract String getScriptCode(String encoding,boolean[] hasHashBang);   
}

package com.innowhere.relproxy.impl.jproxy.clsmgr.comp;

/**
 * http://www.javablogging.com/dynamic-in-memory-compilation/
 * 
 * @author jmarranz
 */
public class JavaFileObjectInputSourceInMemory extends JavaFileObjectInputSourceBase 
{
    protected String source;
    
    public JavaFileObjectInputSourceInMemory(String name,String source,String encoding) 
    {
        super(name,encoding);
        this.source = source;
    }

    @Override
    protected String getSource()
    {
        return source;
    }         
}

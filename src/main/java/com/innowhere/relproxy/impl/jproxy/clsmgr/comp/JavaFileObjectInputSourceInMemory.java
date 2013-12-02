package com.innowhere.relproxy.impl.jproxy.clsmgr.comp;

/**
 * http://www.javablogging.com/dynamic-in-memory-compilation/
 * 
 * @author jmarranz
 */
public class JavaFileObjectInputSourceInMemory extends JavaFileObjectInputSourceBase 
{
    protected String source;
    protected long timestamp;
    
    public JavaFileObjectInputSourceInMemory(String name,String source,String encoding,long timestamp) 
    {
        super(name,encoding);
        this.source = source;
        this.timestamp = timestamp;
    }

    @Override
    protected String getSource()
    {
        return source;
    }         
    
    @Override    
    public long getLastModified() 
    {
        return timestamp;
    }        
}

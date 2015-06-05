package com.innowhere.relproxy.impl.jproxy.core.clsmgr.cldesc;

/**
 *
 * @author jmarranz
 */
public class ClassDescriptorInner extends ClassDescriptor
{
    protected final ClassDescriptorSourceUnit parent;
    
    public ClassDescriptorInner(String className,ClassDescriptorSourceUnit parent) 
    {
        super(className);
        this.parent = parent;
    }      
    
    @Override
    public boolean isInnerClass()
    {
        return true;
    }     
    
    public ClassDescriptorSourceUnit getClassDescriptorSourceUnit()
    {
        return parent;
    }
}

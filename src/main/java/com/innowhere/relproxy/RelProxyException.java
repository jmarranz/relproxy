package com.innowhere.relproxy;

/**
 *
 * @author jmarranz
 */
public class RelProxyException extends RuntimeException
{
    public RelProxyException(String message, Throwable cause) 
    {
        super(message, cause);
    }
    
    public RelProxyException(String message) 
    {
        super(message);
    }   
    
    public RelProxyException(Throwable cause) 
    {
        super(cause);
    }    
}

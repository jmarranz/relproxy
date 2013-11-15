package com.innowhere.relproxy;

/**
 *
 * @author jmarranz
 */
public class ProxyException extends RuntimeException
{
    public ProxyException(String message, Throwable cause) 
    {
        super(message, cause);
    }
    
    public ProxyException(String message) 
    {
        super(message);
    }   
    
    public ProxyException(Throwable cause) 
    {
        super(cause);
    }    
}

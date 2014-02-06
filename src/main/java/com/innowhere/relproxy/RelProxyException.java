package com.innowhere.relproxy;

/**
 *
 * @author Jose Maria Arranz Santamaria
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

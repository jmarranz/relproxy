package com.innowhere.relproxy;

/**
 * Internal checked exceptions thrown by RelProxy and library specific errors are wrapped into this exception class.
 * 
 * @author Jose Maria Arranz Santamaria
 */
public class RelProxyException extends RuntimeException
{
    /**
     * Constructs a new exception with the specified message and cause.
     * 
     * <p>Parameters are passed to the super constructor.</p>
     * 
     * @param message the detail message
     * @param cause the cause
     */
    public RelProxyException(String message, Throwable cause) 
    {
        super(message, cause);
    }
    
    /**
     * Constructs a new exception with the specified message.
     * 
     * <p>Parameter is passed to the super constructor.</p>
     * 
     * @param message the detail message
     */
    public RelProxyException(String message) 
    {
        super(message);
    }   
     
    /**
     * Constructs a new exception with the specified cause.
     * 
     * <p>Parameter is passed to the super constructor.</p>
     * 
     * @param cause the cause
     */
    public RelProxyException(Throwable cause) 
    {
        super(cause);
    }    
}

package com.innowhere.relproxy.impl.jproxy.shell.inter;

import java.nio.charset.Charset;

/**
 *
 * @author jmarranz
 */
public abstract class Keyboard
{
    public abstract void type(CharSequence characters);
       
    public static Keyboard create(Charset cs)
    {
        return KeyboardUsingClipboard.create(cs);
        //return KeyboardNotUsingClipboard.create(cs);        
    }        
    
    public static void test(String arg)
    {
    	Charset charset = Charset.defaultCharset();
    	Keyboard kbd = create(charset);
    	kbd.type(arg);
    }        
}

package com.innowhere.relproxy.impl.jproxy.shell.inter;

import static java.awt.event.KeyEvent.VK_CONTROL;
import static java.awt.event.KeyEvent.VK_SHIFT;
import static java.awt.event.KeyEvent.VK_U;
import java.nio.charset.Charset;

/**
 * 
 * http://superuser.com/questions/59418/how-to-type-special-characters-in-linux
 * 
 * @author jmarranz
 */
public class LinuxUnicodeKeyboard extends Keyboard
{
    protected Charset cs;
    
    public LinuxUnicodeKeyboard(Charset cs)
    {
        this.cs = cs;
    }
    
    @Override
    public boolean type(char character) 
    {
        if (super.type(character))
            return true;

        int bi = getUnicodeInt(cs,character);

        String unicodeDigits = Integer.toString(bi,16); // En hexadecimal

        robot.keyPress(VK_CONTROL);        
        robot.keyPress(VK_SHIFT);
        
        doType(VK_U); // 'u' indica que después viene un valor unicode hexadecimal
            
        // Pero dejamos pulsadas CTRL y SHIFT mientras 
        try
        {
            for (int i = 0; i < unicodeDigits.length(); i++) {
                type(unicodeDigits.charAt(i)); 
            }
        }
        finally
        {
            robot.keyRelease(VK_CONTROL);             
            robot.keyRelease(VK_SHIFT);            
        }
        
        return true;
    }

}
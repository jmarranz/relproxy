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
public class LinuxUnicodeKeyboard extends KeyboardNotUsingClipboard
{  
    public LinuxUnicodeKeyboard(Charset cs)
    {
        super(cs);
    }
    
    @Override
    public boolean isUseCodePoint()
    {
        return true;
    }
    
    @Override
    public boolean type(char character) 
    {
        if (super.type(character))
            return true;

        String unicodeDigits = getUnicodeDigits(character,16); // En hexadecimal

        robot.keyPress(VK_CONTROL);        
        robot.keyPress(VK_SHIFT);
        
        doType(VK_U); // 'u' indica que después viene un valor unicode hexadecimal
            
        // Pero dejamos pulsadas CTRL y SHIFT mientras 
        // Ejemplo: 266A es una nota de solfeo
        try
        {        
            for (int i = 0; i < unicodeDigits.length(); i++)
            {
                char c = unicodeDigits.charAt(i);
                if (Character.isDigit(c))
                    typeNumPad(Integer.parseInt(unicodeDigits.substring(i, i + 1)));
                else
                    type(c); 
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
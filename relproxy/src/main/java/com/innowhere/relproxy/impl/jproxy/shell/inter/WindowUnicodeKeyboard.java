package com.innowhere.relproxy.impl.jproxy.shell.inter;

import static java.awt.event.KeyEvent.VK_ALT;
import java.nio.charset.Charset;

/**
 * http://stackoverflow.com/questions/1248510/convert-string-to-keyevents
 * 
 * @author jmarranz
 */
public class WindowUnicodeKeyboard extends KeyboardNotUsingClipboard
{   
    public WindowUnicodeKeyboard(Charset cs)
    {
        super(cs);
    }
    
    @Override
    public boolean isUseCodePoint()
    {
        return false;
    }    
    
    @Override
    public boolean type(char character) 
    {
        if (super.type(character))
            return true;

        // En Windows usar mintty porque usando la consola de MSYS por sí misma, que es realmente la de Windows, hay problemas con el set de caracteres, pues sería Cp1252 para Java pero Cp850 para la consola y salen mal por tanto los caracteres no ASCII
        
        
        String unicodeDigits = getUnicodeDigits(character,10); // En DECIMAL   
        
        robot.keyPress(VK_ALT);
        try
        {
            for (int i = 0; i < unicodeDigits.length(); i++) {
                typeNumPad(Integer.parseInt(unicodeDigits.substring(i, i + 1)));
            }
        }
        finally
        {
            robot.keyRelease(VK_ALT);            
        }        
        
        return true;
    }


}
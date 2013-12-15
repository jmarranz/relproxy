package com.innowhere.relproxy.impl.jproxy.clsmgr;

import com.innowhere.relproxy.RelProxyException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;

/**
 *
 * @author jmarranz
 */
public class ClassDescriptorSourceFileScript extends ClassDescriptorSourceFile
{
    protected String source;
    protected boolean completeClass;    
    
    public ClassDescriptorSourceFileScript(JProxyEngine engine,String className,SourceScript sourceFile,long timestamp)
    {
        super(engine,className, sourceFile, timestamp);
                
        generateSourceCode();
    }
    
    public SourceScript getSourceFileScript()
    {
        return (SourceScript)sourceFile;
    }
    
    private void generateSourceCode()
    {
        String codeBody = getSourceFileScript().getCodeBody(getEncoding());         
        
        StringBuilder code = new StringBuilder();        
        this.completeClass = isCompleteClass(codeBody);
        
        if (completeClass)
        {
            code.append(codeBody);  
        }
        else
        {
            code.append("public class " + className + " { public static void main(String[] args) {\n"); // Lo ponemos todo en una línea para que en caso de error la línea de error coincida con el script original pues hemos quitado la primera línea #!
            code.append(codeBody);        
            code.append("  }\n");        
            code.append("}\n");         
        }
        this.source = code.toString();        
    }
    
    private boolean isCompleteClass(String codeBody)
    {
        // Buscamos si hay un " class ..." o un "import..." al comienzo para soportar la definición de una clase completa como script
        int posEnd = codeBody.indexOf('{'); // Buscamos el { donde comenzaría la declaración de la clase o bien un primer bloque de sentencias ... para evitar buscar en todo el archivo
        if (posEnd == -1) return false; 
        
        codeBody = codeBody.substring(0, posEnd); // Acotamos el código fuente a un trozo más pequeño
        
        int pos = codeBody.indexOf("class");        
        if (pos == -1) return false; 
        // Hay al menos un "class", ojo que puede ser parte de una variable o dentro de un comentario
        
        pos = getFirstPosIgnoringCommentsAndSeparators(codeBody);
        if (pos == -1) return false;
        
        // Lo primero que nos tenemos encontrar es un import o una declaración de class
        int pos2 = codeBody.indexOf("import",pos);
        if (pos2 == pos)
            return true; // Si hay un import hay declaración de clase
        

        // Vemos si es un "public class..." o similar
        int posClass = codeBody.indexOf("class", pos);
        String visibility = codeBody.substring(pos, posClass);
        visibility = visibility.trim(); // No consideramos \n hay que ser retorcido poner un \n entre el public y el class por ejemplo
        if (visibility.isEmpty()) return true; // No hay visibilidad, que no compile no es cosa nuestra
        return ("private".equals(visibility) || "public".equals(visibility) || "protected".equals(visibility));  
    }
    
    private int getFirstPosIgnoringCommentsAndSeparators(String code)
    {
        int i = -1;
        for(i = 0; i < code.length(); i++)
        {
            char c = code.charAt(i);
            if (c == ' ' || c == '\n') continue;
            else if (c == '/' && i + 1 < code.length())
            {
                char c2 = code.charAt(i + 1);
                if (c2 == '/')
                {
                    i = getFirstPosIgnoringOneLineComment(code,i);
                    if (i == -1) return -1; // Comentario mal formado
                }
                else if (c2 == '*')
                {
                    i = getFirstPosIgnoringMultiLineComment(code,i);
                    if (i == -1) return -1; // Comentario mal formado                    
                }
            }
            else break;
        }
        return i;
    }
    
    private int getFirstPosIgnoringOneLineComment(String code,int start)
    {
        return code.indexOf('\n',start);
    }    
    
    private int getFirstPosIgnoringMultiLineComment(String code,int start)
    {
        return code.indexOf("*/", start);
    }        
    
    @Override
    public void updateTimestamp(long timestamp)
    {
        long oldTimestamp = this.timestamp;
        if (oldTimestamp != timestamp)
            generateSourceCode();   
        super.updateTimestamp(timestamp);
    }
    
    public String getSourceCode()
    {
        return source;
    }
    
    public void callMainMethod(LinkedList<String> argsToScript)
    {       
        try
        {
            Class scriptClass = getLastLoadedClass();            
            Method method = scriptClass.getDeclaredMethod("main",new Class[]{ String[].class });
            String[] argsToScriptArr = argsToScript.size() > 0 ? argsToScript.toArray(new String[argsToScript.size()]) : new String[0];
            method.invoke(null, new Object[]{ argsToScriptArr });
        }
        catch (IllegalAccessException ex) { throw new RelProxyException(ex); }
        catch (NoSuchMethodException ex) { throw new RelProxyException(ex); }
        catch (SecurityException ex) { throw new RelProxyException(ex); }
        catch (IllegalArgumentException ex) { throw new RelProxyException(ex); }
        catch (InvocationTargetException ex) { throw new RelProxyException(ex); }     
    }     
             
}

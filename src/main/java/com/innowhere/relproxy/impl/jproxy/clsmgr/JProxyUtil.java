package com.innowhere.relproxy.impl.jproxy.clsmgr;

import com.innowhere.relproxy.ProxyException;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 * @author jmarranz
 */
public class JProxyUtil 
{    
    public static String getFileExtension(File file)
    {
        String path = file.getAbsolutePath();
        int pos = path.lastIndexOf('.');        
        if (pos != -1) 
            return path.substring(pos + 1);    
        return "";
    }    
    
    public static File getParentDir(String absFilePath)
    {
        File file = new File(absFilePath);
        if (!file.isAbsolute()) return null;
        absFilePath = file.getAbsolutePath(); // Para normalizar separadores por si acaso, pues tenemos que buscar el último separador
        int pos = absFilePath.lastIndexOf(File.separatorChar);
        if (pos == -1)
            return null; // no nos esperamos esto
        return new File(absFilePath.substring(0,pos)); // Sin el terminador
    }
    
    public static byte[] readURL(URL url)
    {
        URLConnection urlCon;
        try 
        { 
            urlCon = url.openConnection(); 
            return readInputStream(urlCon.getInputStream());           
        } 
        catch (IOException ex) { throw new ProxyException(ex); }       
    }
   
    public static byte[] readFile(File file)
    {	
        FileInputStream fis = null;
        try 
        {
            fis = new FileInputStream(file);    			
        }
        catch (FileNotFoundException ex) 
        {
            throw new ProxyException(ex);
        }			

        return readInputStream(fis);
    }		

    public static byte[] readInputStream(InputStream is)
    {
        return readInputStream(is,50); // 50Kb => unas 100 lecturas 5 Mb 
    }

    public static byte[] readInputStream(InputStream is,int bufferSizeKb)
    {	
        ByteArrayOutputStream out = new ByteArrayOutputStream();		
        try 
        {	
            byte[] buffer = new byte[bufferSizeKb*1024]; 

            int size;
            while( (size = is.read(buffer)) != -1 )
            {
                    out.write(buffer, 0, size);
            }             			
        }   		
        catch (IOException ex) 
        {
            throw new ProxyException(ex);
        }	       
        finally
        {
            try { is.close(); } catch (IOException ex2) { throw new ProxyException(ex2); }			
        }

        return out.toByteArray();
    }	    
    
    public static void saveFile(String path,byte[] content)
    {	
        FileOutputStream out = null;		
        try 
        {	
            out = new FileOutputStream(path);	
            out.write(content, 0, content.length);			
        }   		
        catch (IOException ex) 
        {
            throw new ProxyException(ex);
        }	       
        finally
        {
            if (out != null) try { out.close(); } catch (IOException ex2) { throw new ProxyException(ex2); }			
        }
    }    

    public static String readTextFile(File file,String encoding)
    {
        BufferedReader br = null;
        try 
        {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file),encoding));   // FileReader no permite especificar el encoding y total no hace nada que no haga InputStreamReader       
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) 
            {
                sb.append(line);
                sb.append('\n');
                line = br.readLine();
            }
            return sb.toString();
        }
        catch(IOException ex)
        {
            throw new ProxyException(ex);
        }
        finally 
        {
            if (br != null) try { br.close(); } catch (IOException ex) { throw new ProxyException(ex);  }
        }
    }    
}

package com.innowhere.relproxy.impl.jproxy;

import com.innowhere.relproxy.RelProxyException;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 * @author jmarranz
 */
public class JProxyUtil 
{   
    public static String getCanonicalPath(File file)
    {
        try
        {
            return file.getCanonicalPath();
        }
        catch (IOException ex)
        {
            throw new RelProxyException(ex);
        }
    }
        
    public static String getFileExtension(File file)
    {
        String path = file.getAbsolutePath();
        int pos = path.lastIndexOf('.');        
        if (pos != -1) 
            return path.substring(pos + 1);    
        return "";
    }    
    
    public static File getParentDir(File file)
    {
        return file.getParentFile();
    }
    
    public static byte[] readURL(URL url)
    {
        URLConnection urlCon;
        try 
        { 
            urlCon = url.openConnection(); 
            return readInputStream(urlCon.getInputStream());           
        } 
        catch (IOException ex) { throw new RelProxyException(ex); }       
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
            throw new RelProxyException(ex);
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
            throw new RelProxyException(ex);
        }	       
        finally
        {
            try { is.close(); } catch (IOException ex2) { throw new RelProxyException(ex2); }			
        }

        return out.toByteArray();
    }	    
    
    public static void saveFile(File file,byte[] content)
    {	
        File parent = getParentDir(file);
        if (parent != null) parent.mkdirs();
        FileOutputStream out = null;		
        try 
        {	
            out = new FileOutputStream(file);	
            out.write(content, 0, content.length);			
        }   		
        catch (IOException ex) 
        {
            throw new RelProxyException(ex);
        }	       
        finally
        {
            if (out != null) try { out.close(); } catch (IOException ex2) { throw new RelProxyException(ex2); }			
        }
    }    

    public static String readTextFile(File file,String encoding)
    {
        Reader reader = null;
        try 
        {
            reader = new InputStreamReader(new FileInputStream(file),encoding);   // FileReader no permite especificar el encoding y total no hace nada que no haga InputStreamReader       
        }
        catch(IOException ex) { throw new RelProxyException(ex);  }
        
        return readTextFile(reader);
    }    
    
    public static String readTextFile(Reader reader)
    {
        BufferedReader br = null;
        try 
        {
            br = new BufferedReader(reader);   // FileReader no permite especificar el encoding y total no hace nada que no haga InputStreamReader       
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
            throw new RelProxyException(ex);
        }
        finally 
        {
            if (br != null) try { br.close(); } catch (IOException ex) { throw new RelProxyException(ex);  }
        }
    }     
}

package com.innowhere.relproxy.impl.jproxy.screngine;

import com.innowhere.relproxy.impl.jproxy.JProxyConfigImpl;
import com.innowhere.relproxy.jproxy.JProxyConfig;
import com.innowhere.relproxy.jproxy.JProxyScriptEngineFactory;
import java.util.List;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;

/**
 *
 * @author jmarranz
 */
public class JProxyScriptEngineFactoryImpl extends JProxyScriptEngineFactory
{
    protected JProxyConfigImpl config;
    
    public JProxyScriptEngineFactoryImpl(JProxyConfigImpl config)
    {
        this.config = config;
    }
    
    public static JProxyScriptEngineFactory create(JProxyConfig config)
    {
        return new JProxyScriptEngineFactoryImpl((JProxyConfigImpl)config);
    }    
    
    @Override
    public String getEngineName()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getEngineVersion()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<String> getExtensions()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<String> getMimeTypes()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<String> getNames()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getLanguageName()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getLanguageVersion()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getParameter(String key)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getMethodCallSyntax(String obj, String m, String... args)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getOutputStatement(String toDisplay)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getProgram(String... statements)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ScriptEngine getScriptEngine()
    {
        return new JProxyScriptEngineImpl(this,config);
    }    
}

package com.innowhere.relproxy.impl.jproxy.core.clsmgr;

import java.io.File;

/**
 *
 * @author jmarranz
 */
public abstract class SourceScript extends SourceUnit
{
    public abstract String getScriptCode(String encoding,boolean[] hasHashBang);
    public abstract String getClassNameFromSourceFileScriptAbsPath(File rootPathOfSourcesFile);    
}

package org.grits.toolbox.glytoucan.registry.client;

public class AppArguments
{
    private String m_glyTouCanUserId = null;
    private String m_glyTouCanApiKey = null;
    private String m_inputFolder = null;
    private String m_outputFolder = null;
    private String m_inputFile = null;

    public String getGlyTouCanUserId()
    {
        return this.m_glyTouCanUserId;
    }

    public void setGlyTouCanUserId(String a_glyTouCanUserId)
    {
        this.m_glyTouCanUserId = a_glyTouCanUserId;
    }

    public String getGlyTouCanApiKey()
    {
        return this.m_glyTouCanApiKey;
    }

    public void setGlyTouCanApiKey(String a_glyTouCanApiKey)
    {
        this.m_glyTouCanApiKey = a_glyTouCanApiKey;
    }

    public String getInputFolder()
    {
        return this.m_inputFolder;
    }

    public void setInputFolder(String a_inputFolder)
    {
        this.m_inputFolder = a_inputFolder;
    }

    public String getOutputFolder()
    {
        return this.m_outputFolder;
    }

    public void setOutputFolder(String a_outputFolder)
    {
        this.m_outputFolder = a_outputFolder;
    }

    public String getInputFile()
    {
        return m_inputFile;
    }

    public void setInputFile(String a_inputFile)
    {
        this.m_inputFile = a_inputFile;
    }

}

package org.grits.toolbox.glytoucan.registry.client.om;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GlycanFile
{
    private String m_fileName = null;
    private List<GlycanInformation> m_glycans = new ArrayList<>();

    @JsonProperty("filename")
    public String getFileName()
    {
        return this.m_fileName;
    }

    public void setFileName(String a_fileName)
    {
        this.m_fileName = a_fileName;
    }

    @JsonProperty("glycans")
    public List<GlycanInformation> getGlycans()
    {
        return this.m_glycans;
    }

    public void setGlycans(List<GlycanInformation> a_glycans)
    {
        this.m_glycans = a_glycans;
    }
}

package org.grits.toolbox.glytoucan.registry.client.om;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GlycanInformation
{
    private String m_glyTouCanId = null;
    private String m_gws = null;
    private String m_gwsOrdered = null;
    private String m_glycoCt = null;
    private String m_glycoCtRecoded = null;
    private String m_wurcs = null;
    private String m_wurcsAfterRegistration = null;
    private String m_glycoCtAfterRegistration = null;
    private String m_gwsAfterRegistration = null;
    private String m_gwsOrderedAfterRegistration = null;
    private boolean m_failed = false;
    private String m_error = null;
    private List<String> m_warnings = new ArrayList<>();
    private List<String> m_errorInfo = new ArrayList<>();
    private ImageInformation m_imageGWS = null;
    private ImageInformation m_imageGlycoCT = null;
    private ImageInformation m_imageWURCS = null;
    private ImageInformation m_imageWURCSAfterRegistration = null;
    private ImageInformation m_imageGlycoCTAfterRegistration = null;
    private ImageInformation m_imageGWSAfterRegistration = null;

    @JsonProperty("failed")
    public boolean isFailed()
    {
        return this.m_failed;
    }

    public void setFailed(boolean a_failed)
    {
        this.m_failed = a_failed;
    }

    @JsonProperty("error_message")
    public String getError()
    {
        return this.m_error;
    }

    public void setError(String a_error)
    {
        this.m_error = a_error;
    }

    @JsonProperty("glytoucan_id")
    public String getGlyTouCanId()
    {
        return this.m_glyTouCanId;
    }

    public void setGlyTouCanId(String a_glyTouCanId)
    {
        this.m_glyTouCanId = a_glyTouCanId;
    }

    @JsonProperty("gws")
    public String getGws()
    {
        return this.m_gws;
    }

    public void setGws(String a_gwsOriginal)
    {
        this.m_gws = a_gwsOriginal;
    }

    @JsonProperty("gws_ordered")
    public String getGwsOrdered()
    {
        return this.m_gwsOrdered;
    }

    public void setGwsOrdered(String a_gws)
    {
        this.m_gwsOrdered = a_gws;
    }

    @JsonProperty("glycoct")
    public String getGlycoCt()
    {
        return this.m_glycoCt;
    }

    public void setGlycoCt(String a_glycoCtOriginal)
    {
        this.m_glycoCt = a_glycoCtOriginal;
    }

    @JsonProperty("wurcs")
    public String getWurcs()
    {
        return this.m_wurcs;
    }

    public void setWurcs(String a_wurcsOriginal)
    {
        this.m_wurcs = a_wurcsOriginal;
    }

    @JsonProperty("wurcs_after_registration")
    public String getWurcsAfterRegistration()
    {
        return this.m_wurcsAfterRegistration;
    }

    public void setWurcsAfterRegistration(String a_wurcsConverted)
    {
        this.m_wurcsAfterRegistration = a_wurcsConverted;
    }

    @JsonProperty("glycoct_after_registration")
    public String getGlycoCtAfterRegistration()
    {
        return this.m_glycoCtAfterRegistration;
    }

    public void setGlycoCtAfterRegistration(String a_glycoCtConverted)
    {
        this.m_glycoCtAfterRegistration = a_glycoCtConverted;
    }

    @JsonProperty("gws_after_registration")
    public String getGwsAfterRegistration()
    {
        return this.m_gwsAfterRegistration;
    }

    public void setGwsAfterRegistration(String a_gwsConverted)
    {
        this.m_gwsAfterRegistration = a_gwsConverted;
    }

    @JsonProperty("gws_ordered_after_registration")
    public String getGwsOrderedAfterRegistration()
    {
        return this.m_gwsOrderedAfterRegistration;
    }

    public void setGwsOrderedAfterRegistration(String a_gws)
    {
        this.m_gwsOrderedAfterRegistration = a_gws;
    }

    @JsonProperty("warnings")
    public List<String> getWarnings()
    {
        return this.m_warnings;
    }

    public void setWarnings(List<String> a_warnings)
    {
        this.m_warnings = a_warnings;
    }

    @JsonProperty("glycoct_recoded")
    public String getGlycoCtRecoded()
    {
        return this.m_glycoCtRecoded;
    }

    public void setGlycoCtRecoded(String a_glycoCtOriginalRecoded)
    {
        this.m_glycoCtRecoded = a_glycoCtOriginalRecoded;
    }

    @JsonProperty("errors")
    public List<String> getErrorInfo()
    {
        return this.m_errorInfo;
    }

    public void setErrorInfo(List<String> a_errorInfo)
    {
        this.m_errorInfo = a_errorInfo;
    }

    public void addWarnings(String a_warning)
    {
        this.m_warnings.add(a_warning);
    }

    public void addErrorInfo(String a_errorString)
    {
        this.m_errorInfo.add(a_errorString);
    }

    @JsonIgnore
    public ImageInformation getImageGWS()
    {
        return this.m_imageGWS;
    }

    public void setImageGWS(ImageInformation a_imageGWS)
    {
        this.m_imageGWS = a_imageGWS;
    }

    @JsonIgnore
    public ImageInformation getImageGlycoCT()
    {
        return this.m_imageGlycoCT;
    }

    public void setImageGlycoCT(ImageInformation a_imageGlycoCT)
    {
        this.m_imageGlycoCT = a_imageGlycoCT;
    }

    @JsonIgnore
    public ImageInformation getImageWURCS()
    {
        return this.m_imageWURCS;
    }

    public void setImageWURCS(ImageInformation a_imageWURCS)
    {
        this.m_imageWURCS = a_imageWURCS;
    }

    @JsonIgnore
    public ImageInformation getImageWURCSAfterRegistration()
    {
        return this.m_imageWURCSAfterRegistration;
    }

    @JsonIgnore
    public ImageInformation getImageGlycoCTAfterRegistration()
    {
        return this.m_imageGlycoCTAfterRegistration;
    }

    @JsonIgnore
    public ImageInformation getImageGWSAfterRegistration()
    {
        return this.m_imageGWSAfterRegistration;
    }

    public void setImageWURCSAfterRegistration(ImageInformation a_imageWURCSAfterRegistration)
    {
        this.m_imageWURCSAfterRegistration = a_imageWURCSAfterRegistration;
    }

    public void setImageGlycoCTAfterRegistration(ImageInformation a_imageGlycoCTAfterRegistration)
    {
        this.m_imageGlycoCTAfterRegistration = a_imageGlycoCTAfterRegistration;
    }

    public void setImageGWSAfterRegistration(ImageInformation a_imageGWSAfterRegistration)
    {
        this.m_imageGWSAfterRegistration = a_imageGWSAfterRegistration;
    }

    public ImageInformation findFinalImage()
    {
        if (this.m_imageGWSAfterRegistration != null)
        {
            return this.m_imageGWSAfterRegistration;
        }
        if (this.m_imageGlycoCTAfterRegistration != null)
        {
            return this.m_imageGlycoCTAfterRegistration;
        }
        if (this.m_imageWURCSAfterRegistration != null)
        {
            return this.m_imageGWSAfterRegistration;
        }
        return null;
    }

}
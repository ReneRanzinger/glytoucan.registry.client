package org.grits.toolbox.glytoucan.registry.client.ws;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GetAccessionResponse
{
    @JsonProperty("message")
    private String m_message = null;
    @JsonProperty("id")
    private String m_glycanId = null;
    @JsonProperty("wurcs")
    private String m_wurcs = null;
    @JsonProperty("type")
    private String m_errorType = null;
    @JsonIgnore
    private boolean m_error = false;
    @JsonIgnore
    private boolean m_jsonError = false;
    @JsonIgnore
    private boolean m_accessionFound = false;

    public String getMessage()
    {
        return this.m_message;
    }

    public void setMessage(String a_message)
    {
        this.m_message = a_message;
    }

    public String getGlycanId()
    {
        return this.m_glycanId;
    }

    public void setGlycanId(String a_glycanId)
    {
        this.m_glycanId = a_glycanId;
    }

    public String getWurcs()
    {
        return this.m_wurcs;
    }

    public void setWurcs(String a_wurcs)
    {
        this.m_wurcs = a_wurcs;
    }

    public String getErrorType()
    {
        return this.m_errorType;
    }

    public void setErrorType(String a_errorType)
    {
        this.m_errorType = a_errorType;
    }

    public boolean isError()
    {
        return this.m_error;
    }

    public void setError(boolean a_error)
    {
        this.m_error = a_error;
    }

    public boolean isJsonError()
    {
        return this.m_jsonError;
    }

    public void setJsonError(boolean a_jsonError)
    {
        this.m_jsonError = a_jsonError;
    }

    public boolean isAccessionFound()
    {
        return this.m_accessionFound;
    }

    public void setAccessionFound(boolean a_accessionFound)
    {
        this.m_accessionFound = a_accessionFound;
    }
}

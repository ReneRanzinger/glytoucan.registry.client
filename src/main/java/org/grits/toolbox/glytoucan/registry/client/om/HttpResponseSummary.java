package org.grits.toolbox.glytoucan.registry.client.om;

public class HttpResponseSummary
{
    private String m_body = null;
    private Integer m_httpCode = null;
    private String m_reasonPhrase = null;

    public String getBody()
    {
        return this.m_body;
    }

    public void setBody(String a_body)
    {
        this.m_body = a_body;
    }

    public Integer getHttpCode()
    {
        return this.m_httpCode;
    }

    public void setHttpCode(Integer a_httpCode)
    {
        this.m_httpCode = a_httpCode;
    }

    public String getReasonPhrase()
    {
        return this.m_reasonPhrase;
    }

    public void setReasonPhrase(String a_reasonPhrase)
    {
        this.m_reasonPhrase = a_reasonPhrase;
    }

}

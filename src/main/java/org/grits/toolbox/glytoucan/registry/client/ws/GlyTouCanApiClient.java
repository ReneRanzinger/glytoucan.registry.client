package org.grits.toolbox.glytoucan.registry.client.ws;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.grits.toolbox.glytoucan.registry.client.om.HttpResponseSummary;
import org.grits.toolbox.glytoucan.registry.client.om.WURCSSequence;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

public class GlyTouCanApiClient
{

    private String m_apiKey = null;
    private String m_userId = null;

    public static String GLYCAN_URL = "https://sparqlist.glycosmos.org/sparqlist/api/gtc_wurcs_by_accession?accNum=";
    public static String GET_ACCESSION_FOR_WURCS_URL = "https://api.glycosmos.org/glytoucan/sparql/wurcs2gtcids?wurcs=";
    public static String REGISTER_URL = "https://api.glytoucan.org/glycan/register";
    public static String GET_STATUS_BY_HASHKEY_URL = "https://sparqlist.glycosmos.org/sparqlist/api/gtc_select_acc_by_hashkey?hash=";

    private static RestTemplate restTemplate = new RestTemplate();

    public GlyTouCanApiClient(String a_userId, String a_apiKey)
    {
        this.m_apiKey = a_apiKey;
        this.m_userId = a_userId;
    }

    /**
     * Calls GlyTouCan API to retrieve the glycan with the given accession
     * number
     *
     * @param a_accessionNumber
     *            the GlyTouCan ID to search
     * @return response
     */
    public HttpResponseSummary retrieveGlycan(String a_accessionNumber)
    {
        String t_url = GlyTouCanApiClient.GLYCAN_URL + a_accessionNumber;
        HttpResponseSummary t_response = this.performGetRequest(t_url);
        return t_response;
    }

    public HttpResponseSummary getAccessionNumber(String a_wurcsSequence)
    {
        String t_url = GlyTouCanApiClient.GET_ACCESSION_FOR_WURCS_URL + a_wurcsSequence;
        HttpResponseSummary t_response = this.performGetRequest(t_url);
        return t_response;
    }

    private HttpResponseSummary performGetRequest(String a_url)
    {
        HttpEntity<Map<String, String>> t_requestEntity = new HttpEntity<>(
                createHeaders(this.m_userId, this.m_apiKey));
        ResponseEntity<String> t_response = restTemplate.exchange(a_url, HttpMethod.GET,
                t_requestEntity, String.class);
        HttpResponseSummary t_responseObject = new HttpResponseSummary();
        t_responseObject.setBody(t_response.getBody());
        t_responseObject.setHttpCode(t_response.getStatusCodeValue());
        t_responseObject.setReasonPhrase(t_response.getStatusCode().getReasonPhrase());
        return t_responseObject;
    }

    static HttpHeaders createHeaders(String a_username, String a_password)
    {
        return new HttpHeaders()
        {
            private static final long serialVersionUID = 1L;

            {
                String auth = a_username + ":" + a_password;
                byte[] encodedAuth = Base64
                        .encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
                String authHeader = "Basic " + new String(encodedAuth);
                this.set("Authorization", authHeader);
                this.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            }
        };
    }

    public HttpResponseSummary registerGlycan(String a_wurcs)
    {
        WURCSSequence t_payload = new WURCSSequence();
        t_payload.setSequence(a_wurcs);

        HttpEntity<WURCSSequence> requestEntity = new HttpEntity<WURCSSequence>(t_payload,
                createHeaders(this.m_userId, this.m_apiKey));
        try
        {
            ResponseEntity<String> t_response = restTemplate.exchange(
                    GlyTouCanApiClient.REGISTER_URL, HttpMethod.POST, requestEntity, String.class);
            HttpResponseSummary t_responseObject = new HttpResponseSummary();
            t_responseObject.setBody(t_response.getBody());
            t_responseObject.setHttpCode(t_response.getStatusCodeValue());
            t_responseObject.setReasonPhrase(t_response.getStatusCode().getReasonPhrase());
            return t_responseObject;
        }
        catch (HttpServerErrorException e)
        {
            HttpResponseSummary t_responseObject = new HttpResponseSummary();
            t_responseObject.setBody(e.getResponseBodyAsString());
            t_responseObject.setHttpCode(e.getStatusCode().value());
            t_responseObject.setReasonPhrase(e.getStatusCode().getReasonPhrase());
            return t_responseObject;
        }
    }
}
package org.grits.toolbox.glytoucan.registry.client.ws;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ResponseUtil
{
    public GetAccessionResponse processGetAccessionNumber(String a_json) throws IOException
    {
        // create the mapper to parse JSON and deserialize into response object
        ObjectMapper t_mapper = new ObjectMapper();
        GetAccessionResponse[] t_responseList = t_mapper.readValue(a_json,
                GetAccessionResponse[].class);
        // there should be only one response object
        if (t_responseList.length != 1)
        {
            throw new IOException("JSON does not contain one result object but: "
                    + Integer.toString(t_responseList.length));
        }
        // fill the other fields by analyzing the response
        GetAccessionResponse t_response = t_responseList[0];
        if (t_response.getGlycanId() != null && t_response.getWurcs() != null)
        {
            // success case
            t_response.setAccessionFound(true);
            t_response.setError(false);
            t_response.setJsonError(false);
        }
        else if (t_response.getErrorType() != null)
        {
            // if type is filled there was a JSON error
            if (t_response.getErrorType().equals("invalid-json"))
            {
                t_response.setAccessionFound(false);
                t_response.setError(true);
                t_response.setJsonError(true);
            }
            else
            {
                throw new IOException(
                        "Type field for getAccession contains: " + t_response.getErrorType());
            }
        }
        else if (t_response.getMessage() != null)
        {
            if (t_response.getMessage().equals("No accession found"))
            {
                // no accession found
                t_response.setAccessionFound(false);
                t_response.setError(false);
                t_response.setJsonError(false);
            }
            else
            {
                // other error, such as invalid WURCS
                t_response.setAccessionFound(false);
                t_response.setError(true);
                t_response.setJsonError(false);
            }
        }
        else
        {
            throw new IOException("Unknown state for the getAccession response.");
        }
        return t_response;
    }

}

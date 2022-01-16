package org.grits.toolbox.glytoucan.registry.client.ws;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ResponseUtilRun
{

    public static void main(String[] args) throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();

        Path t_fileName = Path.of("doc/JSON/getAccessionNumber/noAccession.json");
        String t_json = Files.readString(t_fileName);

        GetAccessionResponse[] myObjects = mapper.readValue(t_json, GetAccessionResponse[].class);

        System.out.println(myObjects[0]);
    }

}

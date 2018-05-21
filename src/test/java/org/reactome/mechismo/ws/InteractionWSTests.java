package org.reactome.mechismo.ws;

import java.net.URLEncoder;

import org.junit.Test;

public class InteractionWSTests extends WSTests {

    public InteractionWSTests() {
    }

    @Test
    public void testQueryInteraction() throws Exception {
        String url = HOST_URL + "interaction/";
        String name = "ACTB\tMYH15";
        name = "HLA-A\tIRS2"; // There are some analysis results attached
        name = "CDC20\tMAD2L1";
        name = URLEncoder.encode(name, "utf-8");
        System.out.println(name);
        String rtn = callHttp(url + name, HTTP_GET, "");
        System.out.println(rtn);
    }
    
    @Test
    public void testQueryInteractions() throws Exception {
        String url = HOST_URL + "interactions";
        String query = "EGFR\tPIK3CA,GRB2\tPIK3CA";
        String rtn = callHttp(url, HTTP_POST, query);
        System.out.println(rtn);
    }

}

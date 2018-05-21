package org.reactome.mechismo.ws;

import org.junit.Test;

public class ReactionWSTests extends WSTests {
    
    public ReactionWSTests() {
        
    }
    
    @Test
    public void testLoadReactions() throws Exception {
        String url = HOST_URL + "reactions";
        String parameters = "420661,419981,420019,68595,68849,68944";
        String output = callHttp(url, HTTP_POST, parameters);
        System.out.println(output);
    }

}

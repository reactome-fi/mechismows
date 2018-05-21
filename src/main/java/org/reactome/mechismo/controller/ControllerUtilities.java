package org.reactome.mechismo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ControllerUtilities {
    
    /**
     * Fill in values in the object for JSON export to avoid lazy intialization problem.
     * @param obj
     */
    public static void fillObject(Object obj) {
        // Call this to force to load all values
        ObjectMapper mappper = new ObjectMapper();
        try {
            mappper.writeValueAsString(obj);
        }
        catch(JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

}

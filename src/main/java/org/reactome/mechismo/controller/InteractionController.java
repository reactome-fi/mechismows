package org.reactome.mechismo.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.reactome.mechismo.model.Interaction;
import org.reactome.mechismo.service.InteractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * This controller is used to provide REST service related to interaction query.
 * Writing is not supported.
 * @author wug
 *
 */
@RestController
public class InteractionController {
    
    @Autowired
    private InteractionService interactionService;
    
    public InteractionController() {
    }
    
    /**
     * Based on this blog: http://javatar81.blogspot.com/2016/06/loading-strategies-for-object-graphs.html
     * added a transactional here so that we don't need to use EAGER loading.
     * Note: To make this approach work, we have to add @EnableTransactionManagement to WebConfig.java.
     * @param name
     * @return
     */
    @Transactional(readOnly = true)
    @GetMapping("/interaction/{name}")
    public Interaction queryInteraction(@PathVariable("name") String name) {
        Interaction interaction = interactionService.queryInteraction(name);
        ControllerUtilities.fillObject(interaction);
        return interaction;
    }
    
    /**
     * From this method, only id, name, and the whole set of AnalysisResults are returned.
     * @param text
     * @return
     */
    @Transactional(readOnly = true)
    @PostMapping("/interactions")
    public List<Interaction> queryInteractions(@RequestBody String text) {
        if (text == null || text.trim().length() == 0)
            return new ArrayList<>();
        String[] tokens = text.split(",");
        List<String> names = Arrays.asList(tokens)
                                 .stream()
                                 .collect(Collectors.toList());
        List<Interaction> interactions = interactionService.queryInteractions(names);
        List<Interaction> copy = interactions.stream()
                                             .map(interaction -> copyInteraction(interaction))
                                             .collect(Collectors.toList());
        return copy;
    }
    
    private Interaction copyInteraction(Interaction interaction) {
        Interaction iCopy = new Interaction();
        iCopy.setId(interaction.getId());
        iCopy.setName(interaction.getName());
        iCopy.setAnalysisResults(interaction.getAnalysisResults());
        // Need to fill in all analysis result.
        if (iCopy.getAnalysisResults() != null)
            iCopy.getAnalysisResults().forEach(analysis -> ControllerUtilities.fillObject(analysis));
        return iCopy;
    }

}

package org.reactome.mechismo.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.reactome.mechismo.model.Reaction;
import org.reactome.mechismo.service.ReactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReactionController {
    
    @Autowired
    private ReactionService reactionService;
    
    /**
     * It seems that Spring cannot unwrap a string containing Long. Therefore, use
     * String instead.
     * @param dbIds
     * @return
     */
    @Transactional(readOnly = true)
    @PostMapping("/reactions")
    public List<Reaction> fetchReactions(@RequestBody String text) {
        if (text == null || text.trim().length() == 0)
            return new ArrayList<>();
        String[] tokens = text.split(",");
        List<Long> dbIds = Arrays.asList(tokens)
                                 .stream()
                                 .map(token -> new Long(token))
                                 .collect(Collectors.toList());
        List<Reaction> reactions = reactionService.fetchReactions(dbIds);
        // We want to load all results. Therefore call the following
        ControllerUtilities.fillObject(reactions);
        return reactions;
    }

}

package org.reactome.mechismo.main;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.reactome.mechismo.model.AnalysisResult;
import org.reactome.mechismo.model.CancerType;
import org.reactome.mechismo.service.InteractionService;

/**
 * This class is used to load reaction analysis results.
 * @author wug
 *
 */
public abstract class MechismoResultLoader {
    public static final String PANCANCER = "PANCAN";
    protected InteractionService interactionService;
    private CancerType pancancer;
    
    public MechismoResultLoader() {
    }
    
    public InteractionService getInteractionService() {
        return interactionService;
    }
    
    public void parseResults(String resultFile,
                             boolean isPanCancer) throws Exception {
        // The first line is header. Skip it.
        try (Stream<String> lines = Files.lines(Paths.get(resultFile)).skip(1)) {
            lines.forEach(line -> {
                String[] tokens = line.split("\t");
                AnalysisResult result = createAnalysisResult(tokens, isPanCancer);
                attachAnalysisResult(tokens, isPanCancer, result);
            });
        }
    }
    
    public abstract void attachAnalysisResult(String[] tokens, 
                                              boolean isPanCancer,
                                              AnalysisResult result);
    
    public void setInteractionService(InteractionService interactionService) {
        this.interactionService = interactionService;
        pancancer = interactionService.loadCancerType(PANCANCER);
        if (pancancer == null) {
            pancancer = new CancerType();
            pancancer.setAbbreviation(PANCANCER);
            interactionService.save(pancancer);
        }
    }
    
    protected AnalysisResult createAnalysisResult(String[] tokens, boolean isPanCancer) {
        AnalysisResult result = new AnalysisResult();
        CancerType cancerType = null;
        if (isPanCancer)
            cancerType = pancancer;
        else
            cancerType = interactionService.loadCancerType(tokens[0]);
        result.setCancerType(cancerType);
        int pvalueIndex = 10;
        if (isPanCancer)
            pvalueIndex = 9;
        result.setPvalue(new Double(tokens[pvalueIndex]));
        result.setFdr(new Double(tokens[pvalueIndex + 1]));
        return result;
    }

}

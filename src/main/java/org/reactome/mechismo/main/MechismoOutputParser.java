package org.reactome.mechismo.main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.junit.Test;
import org.reactome.mechismo.model.CancerType;
import org.reactome.mechismo.model.Entity;
import org.reactome.mechismo.model.Interaction;
import org.reactome.mechismo.model.Mutation;
import org.reactome.mechismo.model.Residue;
import org.reactome.mechismo.model.ResidueStructure;
import org.reactome.mechismo.model.Sample;
import org.reactome.r3.util.InteractionUtilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * The actual parser used to parse output downloaded from mechsimo.
 * @author wug
 *
 */
/**
 * TODO for next version: 
 * 1). [PROT] may not be a self interaction. It may be related to residues inside a protein. 
 * This should be checked.
 * 2). Interactions between [DNA/RNA] are not listed in the output but displayed in the analysis
 * results. Need to find out how by checking with Francesco.
 * 3). PDB ids are not right for many interactions. Most likely they are involved in [PROT]. 
 * Probably they should be escaped for next version?
 * @author wug
 *
 */
public class MechismoOutputParser {
    // cached results
    private Map<String, Interaction> keyToFI;
    private Map<String, Entity> nameToEntity;
    private Map<String, CancerType> abbToCancerType;
    private Map<String, Sample> nameToSample;
    
    public MechismoOutputParser() {
    }
    
    private void reset() {
        if (keyToFI == null)
            keyToFI = new HashMap<>();
        else
            keyToFI.clear();
        if (nameToEntity == null)
            nameToEntity = new HashMap<>();
        else
            nameToEntity.clear();
        if (abbToCancerType == null)
            abbToCancerType = new HashMap<>();
        else
            abbToCancerType.clear();
        if (nameToSample == null)
            nameToSample = new HashMap<>();
        else
            nameToSample.clear();
    }
    
    /**
     * Use this method to grep all interactions from the mechismo output.
     * @return
     * @throws IOException
     */
    public void parseOutput(String outputFileName) throws IOException {
        reset();
        // The first line is header that should be escaped.
        try (Stream<String> lines = Files.lines(Paths.get(outputFileName)).skip(1)) {
            lines.forEach(line -> {
//                System.out.println(line);
//                // Just for test
//                if (keyToFI.size() == 10)
//                    return;
//                System.out.println(line);
                String[] tokens = line.split("\t");
                if (tokens.length < 33)
                    return; // If there is some truncated result
                // There are some weird header lines in the file
                if (tokens[0].equals("name_a1"))
                    return;
                // We want to exclude synonymous mutation
                if (tokens[4].equals(tokens[5]))
                    return;
                Interaction interaction = getFIForLine(tokens);
                Mutation mutation = parseMutation(tokens);
                interaction.addMutation(mutation);
            });
        }
    }
    
    private Mutation parseMutation(String[] tokens) {
        Mutation mutation = new Mutation();
        // Fill in information related to mutation
        String variant = tokens[5];
        mutation.setVariant(variant);
        Set<Sample> samples = parseSamples(tokens[6]);
        mutation.addSamples(samples);
        Residue residue = createResidue(tokens);
        mutation.setResidue(residue);
        return mutation;
    }
    
    /**
     * Residue is not shared and created for each line.
     * @param tokens
     * @return
     */
    private Residue createResidue(String[] tokens) {
        Residue residue = new Residue();
        residue.setPosition(new Integer(tokens[3]));
        residue.setResidue(tokens[4]);
        // Combined score is used
        residue.setMechismoScore(new Double(tokens[17]));
        Entity entity = getEntity(tokens[0], tokens[1]);
        residue.setProtein(entity);
        // ResidueStructure is not shared and each residue should have its
        // own ResidueStructure object
        ResidueStructure structure = new ResidueStructure();
        structure.setPdb(tokens[32]);
        // The position in PDB is provided by the following:
        // 40 - resseq_a2: PDB residue sequence identifier
        // However, for some entry, no position is provided. For example:
        // CASP1    P29466  397640  297 D   H   P29466/D297H 1 P29466/D297H  CASP1 ENST00000533400.1 CASP1_ENST00000528974.1_Missense_Mutation_p.D258H|CASP1_ENST00000594519.1_Intron|CASP1_ENST00000415981.2_Intron|CASP1_ENST00000393136.4_Missense_Mutation_p.D276H|CASP1_ENST00000534497.1_Intron|CASP1_ENST00000527979.1_Missense_Mutation_p.D260H|CASP1_ENST00000526568.1_Missense_Mutation_p.D204H|CASP1_ENST00000525825.1_Missense_Mutation_p.D276H|CASP1_ENST00000598974.1_Missense_Mutation_p.D297H|CASP1_ENST00000593315.1_Missense_Mutation_p.D276H|CASP1_ENST00000446369.1_Intron|CASP1_ENST00000353247.5_Intron|CASP1_ENST00000531166.1_Intron|CASP1_ENST00000436863.3_Missense_Mutation_p.D297H CASP1_HUMAN CESC:TCGA-Q1-A73O-01;    0   -1  0   1   3   1   0   1.94    1   0   2.94    [CHEM:Organic:PHQ]                              high    0   neutral 1       173826002   3d6h    0   100 0   0   174 P                       0   1               
        if (tokens[40].length() > 0) {
            structure.setPdbPosition(new Integer(tokens[40]));
            structure.setPdbChain(tokens[39]);
            structure.setPdbResidue(tokens[38]);
        }
        residue.setStructure(structure);
        return residue;
    }
    
    private Set<Sample> parseSamples(String input) {
        Set<Sample> samples = new HashSet<>();
        // Input should be something like this:
        // Q8IV61/K73N 1 Q8IV61/K73N  RASGRP3 ENST00000403687.3
        // RASGRP3_ENST00000402538.3_Missense_Mutation_p.K73N|RASGRP3_ENST00000407811.1_Missense_Mutation_p.K73N
        // GRP3_HUMAN READ:TCGA-AG-A002-01;COADREAD:TCGA-AG-A002-01;
        // The following pattern tries to get both cancer type and sample id
        String regExp = "([A-Z]+):(TCGA-[0-9A-Z-]+)";
        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher = pattern.matcher(input);
        int start = 0;
        while (matcher.find(start)) {
            String cancerType = matcher.group(1);
            String sampleId = matcher.group(2);
            Sample sample = getSample(cancerType, sampleId);
            samples.add(sample);
            start = matcher.end();
        }
        return samples;
    }
    
    private Sample getSample(String cancerType, String sampleId) {
        String name = cancerType + ":" + sampleId;
        Sample sample = nameToSample.get(name);
        if (sample != null)
            return sample;
        CancerType type = getCancerType(cancerType);
        sample = new Sample();
        sample.setCancerType(type);
        sample.setName(sampleId);
        sample.setSource("TCGA"); // For the time being, use TCGA
        nameToSample.put(name, sample);
        return sample;
    }
    
    private CancerType getCancerType(String abbreviation) {
        CancerType type = abbToCancerType.get(abbreviation);
        if (type != null)
            return type;
        type = new CancerType();
        type.setAbbreviation(abbreviation);
        abbToCancerType.put(abbreviation, type);
        return type;
    }
    
    @Test
    public void testParseSamples() {
        reset();
        String text = "Q8IV61/K73N 1 Q8IV61/K73N  "
                + "RASGRP3 ENST00000403687.3 RASGRP3_ENST00000402538.3_Missense_Mutation_"
                + "p.K73N|RASGRP3_ENST00000407811.1_Missense_Mutation_p.K73N GRP3_HUMAN READ:"
                + "TCGA-AG-A002-01;COADREAD:TCGA-AG-A002-01;";
        Set<Sample> samples = parseSamples(text);
        System.out.println("Total samples: " + samples.size());
        samples.forEach(sample -> System.out.println(sample));
    }
    
    private Interaction getFIForLine(String[] tokens) {
        String key = getFIKey(tokens);
        Interaction interaction = keyToFI.get(key);
        if (interaction != null)
            return interaction;
        interaction = createInteraction(tokens, key);
        keyToFI.put(key, interaction);
        return interaction;
    }
    
    private Interaction createInteraction(String[] tokens,
                                          String key) {
        Interaction interaction = new Interaction();
        interaction.setName(key);
        String[] entities = getEntityNames(tokens);
        Entity first = getEntity(entities[0], tokens[1]);
        Entity second = getEntity(entities[1], tokens[19].length() == 0 ? null : tokens[19]);
        Set<Entity> partners = new HashSet<>();
        partners.add(first);
        partners.add(second);
        interaction.setPartners(partners);
        return interaction;
    }
    
    private Entity getEntity(String name, String identifier) {
        Entity entity = nameToEntity.get(name);
        if (entity != null) {
            if (entity.getIdentifier() == null && identifier != null)
                entity.setIdentifier(identifier);
            return entity;
        }
        entity = new Entity();
        entity.setName(name);
        entity.setIdentifier(identifier);
        nameToEntity.put(name, entity);
        return entity;
    }
    
    private String getFIKey(String[] tokens) {
        String[] genes = getEntityNames(tokens);
        String fi = InteractionUtilities.generateFIFromGene(genes[0], genes[1]);
        return fi;
    }
    
    private String[] getEntityNames(String[] tokens) {
        String gene1 = tokens[0];
        String gene2 = tokens[18];
        if (gene2.equals("[PROT]"))
            gene2 = gene1; // Means self-interaction see http://mechismo.russelllab.org/help
        return new String[]{gene1, gene2};
    }
    
    @Test
    public void testParseOutput() throws IOException {
        String fileName = "datasets/Mechismo/TCGA/TCGA_mech_output.tsv";
        long time1 = System.currentTimeMillis();
        parseOutput(fileName);
        long time2 = System.currentTimeMillis();
        System.out.println("Total parsing time: " + (time2 - time1));
        Set<Interaction> interactions = getInteractions();
        System.out.println("Total interactions: " + interactions.size());
        Interaction interaction = interactions.stream().findAny().get();
        System.out.println("Check one interaction: " + interaction.getName());
        checkInteraction(interaction);
    }

    public void checkInteraction(Interaction interaction) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String output = mapper.writeValueAsString(interaction);
        System.out.println(output);
    }
    
    public Set<Interaction> getInteractions() {
        if (keyToFI == null)
            return new HashSet<>();
        else
            return new HashSet<>(keyToFI.values());
    }
    
    @Test
    public void checkOutput() throws IOException {
        String fileName = "datasets/Mechismo/TCGA/TCGA_mech_output.tsv";
        // The following is used to check the used PDB structures
        Map<String, Set<String>> interactionToStructures = new HashMap<>();
        Files.lines(Paths.get(fileName))
             .skip(1)
             .forEach(line -> {
                 String[] tokens = line.split("\t");
                 if (tokens.length < 33)
                     return;
                 String interactions = InteractionUtilities.generateFIFromGene(tokens[0], tokens[18]);
                 interactionToStructures.compute(interactions, (key, set) -> {
                     if (set == null)
                         set = new HashSet<>();
                     set.add(tokens[32]);
                     return set;
                 });
             });
        
        System.out.println("Total interactions in interactionsToStructures: " + interactionToStructures.size());
        
        interactionToStructures.forEach((key, set) -> {
            if (set.size() > 1)
                System.out.println(key + "\t" + set);
        });
        
//        // The following is used to check how many interactions are covered by the output file
//        Set<String> interactions = Files.lines(Paths.get(fileName))
//                                        .skip(1)
//                                        .map(line -> line.split("\t"))
//                                        .filter(tokens -> tokens.length > 18)
//                                        .map(tokens -> tokens[0] + "\t" + tokens[18])
//                                        .collect(Collectors.toSet());
//        System.out.println("Total interactions: " + interactions.size());
//        // Want to do a sorting
//        Set<String> sorted = interactions.stream()
//                                         .map(interaction -> {
//                                             String[] tokens = interaction.split("\t");
//                                             String newFI = InteractionUtilities.generateFIFromGene(tokens[0], tokens[1]);
//                                             return newFI;
//                                         })
//                                         .collect(Collectors.toSet());
//        System.out.println("Sorted interactions: " + sorted.size());
    }

}

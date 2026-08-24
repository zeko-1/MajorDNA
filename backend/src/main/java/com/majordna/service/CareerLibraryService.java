// Matches the scored profile to the editable career library using transparent weights.
package com.majordna.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.majordna.model.Domain.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Service
public class CareerLibraryService {
    private static final Map<String,Double> CATEGORY_WEIGHTS=Map.of("Personality",.30,"Intelligence Profile",.25,"Interests",.20,"Skills",.15,"Work Style",.10);
    private static final Set<String> PERSONALITY=Set.of("Openness","Conscientiousness","Extraversion","Agreeableness","Emotional Stability","Detail","Social");
    private static final Set<String> INTELLIGENCE=Set.of("Logical","Analytical","Numerical","Verbal","Creativity","Spatial Thinking","Memory","Data","Creative");
    private static final Set<String> SKILLS=Set.of("Programming","Mathematics","Communication","Writing","Speaking","Leadership Skill","Creative Skill","Problem Solving","Design Skill","Languages");
    private static final Set<String> WORK=Set.of("Leadership","Teamwork","Independence","Stress Tolerance","Flexible Hours","Active Environment","Structure","Risk Decisions");
    private final ObjectMapper mapper;private final Path file=Path.of("data","careers.json");private volatile List<Career> careers;
    public CareerLibraryService(ObjectMapper mapper){this.mapper=mapper;try{if(!Files.exists(file)){Files.createDirectories(file.getParent());Files.copy(new ClassPathResource("data/careers.json").getInputStream(),file);}careers=List.copyOf(mapper.readValue(file.toFile(),new TypeReference<List<Career>>(){}));validate(careers);}catch(IOException e){throw new IllegalStateException("Career library could not be loaded. Check data/careers.json.",e);}}
    public List<Career> all(){return careers;}
    public synchronized void replaceAll(List<Career>items){validate(items);try{mapper.writeValue(file.toFile(),items);careers=List.copyOf(items);}catch(IOException e){throw new IllegalStateException("Careers could not be saved.",e);}}
    public List<CareerMatch> top(Map<String,Integer> profile,int limit){return rank(profile).stream().limit(Math.max(1,limit)).toList();}
    public List<CareerMatch> exploreCarefully(Map<String,Integer> profile,int limit){List<CareerMatch> ranked=rank(profile);Collections.reverse(ranked);return ranked.stream().limit(Math.max(1,limit)).toList();}
    public List<MajorRanking> majorRankings(List<CareerMatch> matches){
        Map<String,List<CareerMatch>> grouped=new LinkedHashMap<>();matches.forEach(m->grouped.computeIfAbsent(m.career().associatedMajor(),k->new ArrayList<>()).add(m));
        return grouped.entrySet().stream().map(e->{List<CareerMatch> top=e.getValue().stream().sorted(Comparator.comparingInt(CareerMatch::compatibility).reversed()).limit(3).toList();int score=(int)Math.round(top.stream().mapToInt(CareerMatch::compatibility).average().orElse(0));return new MajorRanking(e.getKey(),score,top.stream().map(x->x.career().name()).toList());}).sorted(Comparator.comparingInt(MajorRanking::compatibility).reversed()).limit(5).toList();
    }
    private List<CareerMatch> rank(Map<String,Integer> rawProfile){
        // Aliases let career records use readable terms such as Data or Social.
        Map<String,Integer> profile=withAliases(rawProfile);List<CareerMatch> result=new ArrayList<>();
        // Every career is scored, then the final list is sorted from strongest to weakest.
        for(Career career:careers){Map<String,List<Integer>> matchesByCategory=new LinkedHashMap<>();List<Map.Entry<String,Integer>> dimensions=new ArrayList<>(career.targets().entrySet());
            for(var target:dimensions){int match=Math.max(0,100-Math.abs(profile.getOrDefault(target.getKey(),50)-target.getValue()));matchesByCategory.computeIfAbsent(categoryOf(target.getKey()),k->new ArrayList<>()).add(match);}
            Map<String,Integer> categoryMatches=new LinkedHashMap<>();double finalScore=0;for(var weight:CATEGORY_WEIGHTS.entrySet()){List<Integer> values=matchesByCategory.getOrDefault(weight.getKey(),List.of());int category=values.isEmpty()?50:(int)Math.round(values.stream().mapToInt(Integer::intValue).average().orElse(50));categoryMatches.put(weight.getKey(),category);finalScore+=category*weight.getValue();}
            dimensions.sort(Comparator.comparingInt(e->Math.abs(profile.getOrDefault(e.getKey(),50)-e.getValue())));List<String> strengths=dimensions.stream().limit(3).map(Map.Entry::getKey).toList();List<String> gaps=dimensions.stream().sorted(Comparator.comparingInt((Map.Entry<String,Integer> e)->Math.abs(profile.getOrDefault(e.getKey(),50)-e.getValue())).reversed()).limit(3).map(Map.Entry::getKey).toList();List<String> suggestions=gaps.stream().limit(3).map(d->"Strengthen "+d+" through a short course and one practical activity.").toList();result.add(new CareerMatch(career,(int)Math.round(finalScore),categoryMatches,strengths,gaps,suggestions));}
        result.sort(Comparator.comparingInt(CareerMatch::compatibility).reversed());return result;
    }
    private String categoryOf(String d){if(PERSONALITY.contains(d))return "Personality";if(INTELLIGENCE.contains(d))return "Intelligence Profile";if(SKILLS.contains(d))return "Skills";if(WORK.contains(d))return "Work Style";return "Interests";}
    private Map<String,Integer> withAliases(Map<String,Integer> source){Map<String,Integer> p=new LinkedHashMap<>(source);p.put("Detail",avg(p,"Conscientiousness","Structure"));p.put("Social",avg(p,"Agreeableness","Communication"));p.put("Data",avg(p,"Numerical","Analytical"));p.put("Creative",avg(p,"Openness","Creativity","Design Skill"));p.put("Health",p.getOrDefault("Healthcare",50));return p;}
    private int avg(Map<String,Integer> p,String...keys){return (int)Math.round(Arrays.stream(keys).mapToInt(k->p.getOrDefault(k,50)).average().orElse(50));}
    private void validate(List<Career>items){if(items.size()<25)throw new IllegalArgumentException("Career library must keep at least 25 reviewed careers.");if(items.stream().anyMatch(c->c.id()==null||c.id().isBlank()||c.name()==null||c.name().isBlank()||c.targets()==null||c.targets().isEmpty()||c.salaryMinMyrAnnual()<0||c.salaryMaxMyrAnnual()<c.salaryMinMyrAnnual()))throw new IllegalArgumentException("Each career requires valid identity, targets, and salary range.");if(items.stream().map(Career::id).distinct().count()!=items.size())throw new IllegalArgumentException("Career IDs must be unique.");}
}




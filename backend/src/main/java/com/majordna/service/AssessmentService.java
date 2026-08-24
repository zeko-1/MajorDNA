// Loads question data, applies scoring rules, and returns the profile used by the report.
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
public class AssessmentService {
    private static final List<String> CATEGORIES=List.of("Personality","Intelligence Profile","Interests","Skills","Work Style");
    private final ObjectMapper mapper; private final Path file=Path.of("data","questions.json"); private volatile List<Question> questions;
    public AssessmentService(ObjectMapper mapper){
        this.mapper=mapper;try{if(!Files.exists(file)){Files.createDirectories(file.getParent());Files.copy(new ClassPathResource("data/questions.json").getInputStream(),file);}questions=List.copyOf(mapper.readValue(file.toFile(),new TypeReference<List<Question>>(){}));validate(questions);
        }catch(IOException e){throw new IllegalStateException("Question library could not be loaded. Check data/questions.json.",e);}
    }
    public AssessmentService(){this(new ObjectMapper());}
    public List<Question> getQuestions(String mode){String test="SUB_TRACK".equalsIgnoreCase(mode)?"SUB_TRACK":"MAIN";return questions.stream().filter(q->q.test().equals(test)).toList();}
    public List<Question> getQuestions(){return getQuestions("CAREER_EXPLORER");}
    public List<Question> allQuestions(){return questions;}
    public synchronized void replaceAll(List<Question> items){validate(items);try{mapper.writeValue(file.toFile(),items);questions=List.copyOf(items);}catch(IOException e){throw new IllegalStateException("Questions could not be saved.",e);}}
    public AssessmentScores scoreDetailed(Map<String,Integer> answers,String mode){
        // Work only with the test selected by the student; the same scorer serves both modes.
        List<Question> selected=getQuestions(mode); Map<String,Double> totals=new LinkedHashMap<>(),weights=new LinkedHashMap<>(); Map<String,List<String>> categoryDimensions=new LinkedHashMap<>();
        for(Question q:selected){int raw=Math.max(1,Math.min(5,answers==null?3:answers.getOrDefault(q.id(),3)));// Reverse-keyed items turn a low agreement into the higher positive score.
            int keyed=q.reverse()?6-raw:raw;double normalized=(keyed-1)*25.0;
            for(var contribution:q.contributions().entrySet()){double w=q.weight()*Math.abs(contribution.getValue());totals.merge(contribution.getKey(),normalized*w,Double::sum);weights.merge(contribution.getKey(),w,Double::sum);categoryDimensions.computeIfAbsent(q.category(),k->new ArrayList<>()).add(contribution.getKey());}}
        Map<String,Integer> dimensions=new LinkedHashMap<>();totals.forEach((d,total)->dimensions.put(d,(int)Math.round(total/weights.get(d))));
        Map<String,Integer> categories=new LinkedHashMap<>();for(String c:CATEGORIES){List<String> dims=categoryDimensions.getOrDefault(c,List.of()).stream().distinct().toList();if(!dims.isEmpty())categories.put(c,(int)Math.round(dims.stream().mapToInt(d->dimensions.getOrDefault(d,50)).average().orElse(50)));}
        Map<String,Integer> bigFive=pick(dimensions,List.of("Openness","Conscientiousness","Extraversion","Agreeableness","Emotional Stability"));
        Map<String,Integer> intelligence=pick(dimensions,List.of("Logical","Analytical","Numerical","Verbal","Creativity","Spatial Thinking","Memory"));
        Map<String,Integer> workStyle=pick(dimensions,List.of("Leadership","Teamwork","Independence","Stress Tolerance","Flexible Hours","Active Environment","Structure","Risk Decisions"));
        return new AssessmentScores(dimensions,categories,bigFive,intelligence,workStyle);
    }
    public Map<String,Integer> score(Map<String,Integer> answers,String mode){return scoreDetailed(answers,mode).dimensions();}
    public Map<String,Integer> score(Map<String,Integer> answers){return score(answers,"CAREER_EXPLORER");}
    private Map<String,Integer> pick(Map<String,Integer> source,List<String> names){Map<String,Integer> out=new LinkedHashMap<>();names.forEach(n->out.put(n,source.getOrDefault(n,50)));return out;}
    private void validate(List<Question> items){long main=items.stream().filter(q->"MAIN".equals(q.test())).count();if(main<46)throw new IllegalArgumentException("MAIN assessment must keep at least the 46 validated core questions, found "+main);if(items.stream().anyMatch(q->q.id()==null||q.id().isBlank()||q.prompt()==null||q.prompt().isBlank()||q.contributions()==null||q.contributions().isEmpty()))throw new IllegalArgumentException("Every question requires id, prompt, and dimension contributions.");if(items.stream().map(Question::id).distinct().count()!=items.size())throw new IllegalArgumentException("Question IDs must be unique.");}
}




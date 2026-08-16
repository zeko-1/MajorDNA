package com.majordna.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.majordna.model.Domain.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.nio.file.*;
import java.util.*;

@Service
public class LocalAIAnalysisService implements AIAnalysisService {
    private final ObjectMapper mapper;
    private final boolean llmEnabled;
    private final String apiUrl;
    private final String model;
    private final String apiKey;
    private final HttpClient client=HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();
    public LocalAIAnalysisService(ObjectMapper mapper,
      @Value("${majordna.llm.enabled:false}") boolean enabled,
      @Value("${majordna.llm.url:https://api.groq.com/openai/v1/chat/completions}") String url,
      @Value("${majordna.llm.model:openai/gpt-oss-20b}") String model,
      @Value("${GROQ_API_KEY:}") String apiKey){this.mapper=mapper;llmEnabled=enabled;apiUrl=url;this.model=model;this.apiKey=apiKey.isBlank()?readEnvFile("GROQ_API_KEY"):apiKey;}

    public String explain(String name,Map<String,Integer> dna,List<Recommendation> recs){
        List<String> top=dna.entrySet().stream().sorted(Map.Entry.<String,Integer>comparingByValue().reversed()).limit(2).map(e->e.getKey()+" ("+e.getValue()+"%)").toList();
        return name+", your profile combines "+String.join(" and ",top)+". "+recs.get(0).name()+" is the strongest current match. Treat it as a direction to explore, not a fixed label.";
    }
    public ChatResponse advise(Report report,String message){
        if(llmEnabled&&!apiKey.isBlank()){try{return new ChatResponse(callCloudLlm(report,message),suggestions());}catch(Exception ignored){}}
        Recommendation best=report.recommendations().get(0); String lower=message==null?"":message.toLowerCase(); String answer;
        if(lower.contains("skill")||lower.contains("improve")) answer="Start with "+report.skillGaps().get(0).skill()+". "+report.skillGaps().get(0).action();
        else if(lower.contains("why")||lower.contains("suitable")) answer=best.name()+" currently matches because "+best.reason()+" Validate this through a small project and reflection.";
        else answer="Your current strongest match is "+best.name()+" at "+best.match()+"%. Ask me to explain the match, discuss a skill gap, or plan a next step.";
        return new ChatResponse(answer,suggestions());
    }
    public boolean configured(){return llmEnabled&&!apiKey.isBlank();}
    private String callCloudLlm(Report report,String question)throws Exception{
        String system="You are MajorDNA's career advisor. Use only the supplied report. Never calculate, change, or invent scores. Use respectful non deterministic language such as may suit and explore carefully. Do not claim guaranteed success. Keep the answer concise and practical. Write clean plain text with short headings and numbered steps. Do not use Markdown tables, pipe characters, asterisks, or hash heading symbols.";
        Map<String,Object> anonymousContext=new LinkedHashMap<>();
        anonymousContext.put("mode",report.mode()); anonymousContext.put("scores",report.techDna());
        anonymousContext.put("strengths",report.strengths()); anonymousContext.put("recommendations",report.recommendations());
        anonymousContext.put("skillGaps",report.skillGaps()); anonymousContext.put("roadmap",report.roadmap());
        String context="Anonymous assessment result: "+mapper.writeValueAsString(anonymousContext)+"\nStudent question: "+question;
        Map<String,Object> body=Map.of("model",model,"temperature",0.2,"max_completion_tokens",500,"messages",List.of(Map.of("role","system","content",system),Map.of("role","user","content",context)));
        HttpRequest request=HttpRequest.newBuilder(URI.create(apiUrl)).timeout(Duration.ofSeconds(45)).header("Content-Type","application/json").header("Authorization","Bearer "+apiKey).POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body))).build();
        HttpResponse<String> response=client.send(request,HttpResponse.BodyHandlers.ofString());
        if(response.statusCode()!=200)throw new IllegalStateException("LLM API returned "+response.statusCode());
        JsonNode json=mapper.readTree(response.body()); String answer=json.path("choices").path(0).path("message").path("content").asText();
        if(answer.isBlank())throw new IllegalStateException("LLM API returned an empty response"); return answer;
    }
    private List<String> suggestions(){return List.of("Why is my top match suitable?","What skill should I improve first?","What should I explore carefully?");}
    private String readEnvFile(String key){Path p=Path.of(".env");if(!Files.exists(p))return "";try{return Files.readAllLines(p).stream().map(String::trim).filter(x->!x.startsWith("#")&&x.startsWith(key+"=")).map(x->x.substring(key.length()+1).trim()).map(x->x.replaceAll("^[\"']|[\"']$","")).findFirst().orElse("");}catch(Exception e){return "";}}
}

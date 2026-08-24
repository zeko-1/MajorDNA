// Stores start and completion events used by the administrator dashboard.
package com.majordna.service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.majordna.model.Domain.*;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.*;
import java.time.Instant;
import java.util.*;

@Service
public class AssessmentAnalyticsService {
 private final ObjectMapper mapper;private final Path file=Path.of("data","assessment-analytics.json");private final List<AssessmentStart>sessions=new ArrayList<>();
 public AssessmentAnalyticsService(ObjectMapper mapper){this.mapper=mapper;try{if(Files.exists(file))sessions.addAll(mapper.readValue(file.toFile(),new TypeReference<List<AssessmentStart>>(){}));}catch(IOException e){throw new IllegalStateException("Could not load assessment analytics.",e);}}
 public synchronized AssessmentStart start(String mode){AssessmentStart s=new AssessmentStart(UUID.randomUUID().toString(),mode,Instant.now().toString(),false);sessions.add(s);save();return s;}
 public synchronized void complete(String id){if(id==null||id.isBlank())return;for(int i=0;i<sessions.size();i++){AssessmentStart s=sessions.get(i);if(s.sessionId().equals(id)){sessions.set(i,new AssessmentStart(s.sessionId(),s.mode(),s.startedAt(),true));save();return;}}}
 public synchronized AdminMetrics metrics(){int started=sessions.size(),completed=(int)sessions.stream().filter(AssessmentStart::completed).count();Map<String,Integer>byMode=new LinkedHashMap<>();sessions.stream().filter(AssessmentStart::completed).forEach(s->byMode.merge(s.mode(),1,Integer::sum));return new AdminMetrics(started,completed,started-completed,started==0?0:(int)Math.round(completed*100.0/started),byMode);}
 private void save(){try{Files.createDirectories(file.getParent());mapper.writeValue(file.toFile(),sessions);}catch(IOException e){throw new IllegalStateException("Could not save assessment analytics.",e);}}
}


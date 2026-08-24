// Orchestrates one complete assessment from validated answers to a saved report.
package com.majordna.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.majordna.model.*;
import com.majordna.model.Domain.*;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.*;
import java.time.Instant;
import java.util.*;

@Service
public class ReportService {
    private final AssessmentService assessment;
    private final RecommendationService recommendation;
    private final AIAnalysisService ai;
    private final CareerLibraryService careerLibrary;
    private final AssessmentAnalyticsService analytics;
    private final ObjectMapper mapper;
    private final Path file = Path.of("data","reports.json");
    private final List<Report> reports = new ArrayList<>();

    public ReportService(AssessmentService a, RecommendationService r, AIAnalysisService ai, ObjectMapper mapper, CareerLibraryService careerLibrary, AssessmentAnalyticsService analytics) {
        assessment=a; recommendation=r; this.ai=ai; this.mapper=mapper; this.careerLibrary=careerLibrary; this.analytics=analytics; load();
    }
    public synchronized Report create(AssessmentRequest req) {
        if(req.name()==null||req.name().isBlank())throw new IllegalArgumentException("Full name is required.");
        if(req.age()<12||req.age()>100)throw new IllegalArgumentException("Age must be between 12 and 100.");
        if(req.educationLevel()==null||req.educationLevel().isBlank())throw new IllegalArgumentException("Education level is required.");
        User user = "CITYU_STUDENT".equalsIgnoreCase(req.mode()) ? new CityUStudent(req.name(),req.age(),req.studentId()) : new CareerExplorer(req.name(),req.age(),req.educationLevel());
        UserProfile profile=new UserProfile(req.name().trim(),req.age(),req.educationLevel(),req.currentMajor(),req.country(),req.gender(),req.studentId());
        // Keep scoring deterministic; the AI service is called only after these values exist.
        AssessmentScores scores = assessment.scoreDetailed(req.answers(),req.mode());
        Map<String,Integer> dna = scores.dimensions();
        List<CareerMatch> allCareerMatches=careerLibrary.top(dna,careerLibrary.all().size());
        List<MajorRanking> majors=careerLibrary.majorRankings(allCareerMatches);
        List<Recommendation> recs = "SUB_TRACK".equalsIgnoreCase(req.mode()) ? recommendation.recommend(dna,req.mode()) : majors.stream().limit(3).map(m->new Recommendation(m.major().toLowerCase().replaceAll("[^a-z0-9]+","-"),m.major(),m.compatibility(),"This major is supported by your strongest matching career paths.",m.leadingCareers())).toList();
        Recommendation top = recs.get(0);
        List<String> strengths = dna.entrySet().stream().sorted(Map.Entry.<String,Integer>comparingByValue().reversed()).limit(3).map(Map.Entry::getKey).toList();
        List<ProgramMatch> programs = List.of(
          new ProgramMatch("Bachelor of Computer Science (Artificial Intelligence) (Honours)","City University Malaysia FOIT",match(recs,"ai","Artificial Intelligence"),List.of("Artificial Intelligence")),
          new ProgramMatch("Bachelor of Computer Science (Cyber Security) with Honours","City University Malaysia FOIT",match(recs,"cyber","Cyber Security"),List.of("Cyber Security")),
          new ProgramMatch("Bachelor in Software Engineering (Honours)","City University Malaysia FOIT",match(recs,"software","Software Engineering"),List.of("Software Engineering")),
          new ProgramMatch("Bachelor of Information Technology (Honours)","City University Malaysia FOIT",match(recs,"it","Information Technology"),List.of("Information Technology")));
        int current = Math.max(25,(int)dna.values().stream().mapToInt(Integer::intValue).average().orElse(55)-10);
        List<SkillGap> gaps = List.of(
          new SkillGap("Foundation knowledge",current,85,"Complete one introductory course in "+top.name()+"."),
          new SkillGap("Practical experience",Math.max(20,current-10),80,"Build a small project and document what you learned."),
          new SkillGap("Career awareness",Math.max(25,current-5),75,"Review real roles and speak with a student or professional."));
        List<RoadmapStep> roadmap = List.of(
          new RoadmapStep("Weeks 1 to 4","Build the foundation","Learn the fundamentals of "+top.name()+".","learn"),
          new RoadmapStep("Weeks 5 to 8","Create proof","Build one guided mini project.","build"),
          new RoadmapStep("Weeks 9 to 12","Connect and reflect","Present your project and request feedback.","connect"));
        List<CareerMatch> topCareers=allCareerMatches.stream().limit(5).toList();
        List<CareerMatch> careful=careerLibrary.exploreCarefully(dna,3);
        Report report = new Report(UUID.randomUUID().toString(),Instant.now().toString(),user.getName(),req.mode(),dna,strengths,recs,programs,gaps,roadmap,ai.explain(user.getName(),dna,recs),scores.categories(),scores.bigFive(),scores.intelligence(),scores.workStyle(),topCareers,careful,majors,profile);
        // New reports appear first so the dashboard and JavaFX viewer load the latest result.
        reports.add(0,report); save(); analytics.complete(req.sessionId()); return report;
    }
    private int match(List<Recommendation> recs,String id,String name){return recs.stream().filter(r->r.majorId().equals(id)||r.name().equalsIgnoreCase(name)).map(Recommendation::match).findFirst().orElse(50);}
    public List<Report> all(){return List.copyOf(reports);}
    public Report get(String id){return reports.stream().filter(r->r.reportId().equals(id)).findFirst().orElseThrow(()->new NoSuchElementException("Report not found"));}
    private void load(){try{if(Files.exists(file))reports.addAll(mapper.readValue(file.toFile(),new TypeReference<List<Report>>(){}));}catch(IOException ignored){}}
    private void save(){try{Files.createDirectories(file.getParent());mapper.writeValue(file.toFile(),reports);}catch(IOException e){throw new IllegalStateException("Could not save reports",e);}}
}




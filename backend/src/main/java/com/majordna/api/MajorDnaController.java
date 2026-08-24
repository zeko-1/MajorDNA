// Public MajorDNA endpoints: questions, assessments, reports, matching, simulation, and advisor.
package com.majordna.api;

import com.majordna.model.Domain.*;
import com.majordna.service.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController @RequestMapping("/api") @CrossOrigin(origins={"http://localhost:5173","http://127.0.0.1:5173","http://localhost:5188","http://127.0.0.1:5188"})
public class MajorDnaController {
    private final AssessmentService assessment; private final RecommendationService recommendation; private final ReportService reports; private final AIAnalysisService ai; private final CareerLibraryService careers; private final AssessmentAnalyticsService analytics;
    public MajorDnaController(AssessmentService a, RecommendationService r, ReportService reports, AIAnalysisService ai, CareerLibraryService careers, AssessmentAnalyticsService analytics){this.assessment=a;this.recommendation=r;this.reports=reports;this.ai=ai;this.careers=careers;this.analytics=analytics;}
    @GetMapping("/health") public Map<String,String> health(){return Map.of("status","ready","service","MajorDNA AI");}
    @GetMapping("/ai/status") public Map<String,Object> aiStatus(){return Map.of("provider","GroqCloud","model","openai/gpt-oss-20b","configured",ai.configured(),"fallback","deterministic Java advisor");}
    @GetMapping("/questions") public List<Question> questions(@RequestParam(defaultValue="CAREER_EXPLORER") String mode){return assessment.getQuestions(mode);}
    @PostMapping("/assessment/start") public AssessmentStart startAssessment(@RequestParam String mode){return analytics.start(mode);}
    @GetMapping("/majors") public List<Major> majors(){return recommendation.majors();}
    @GetMapping("/careers") public List<Career> careers(){return careers.all();}
    @PostMapping("/careers/rank") public List<CareerMatch> rankCareers(@RequestBody Map<String,Integer> profile,@RequestParam(defaultValue="5") int limit){return careers.top(profile,limit);}
    @PostMapping("/careers/explore-carefully") public List<CareerMatch> carefulCareers(@RequestBody Map<String,Integer> profile,@RequestParam(defaultValue="3") int limit){return careers.exploreCarefully(profile,limit);}
    @PostMapping("/assessment") public Report assess(@RequestBody AssessmentRequest req){return reports.create(req);}
    @GetMapping("/reports") public List<Report> allReports(){return reports.all();}
    @GetMapping("/reports/{id}") public Report report(@PathVariable String id){return reports.get(id);}
    @PostMapping("/advisor") public ChatResponse advisor(@RequestBody ChatRequest req){return ai.advise(reports.get(req.reportId()),req.message());}
    @PostMapping("/simulate") public Simulation simulate(@RequestBody SimulationRequest req){
      Major m=recommendation.majors().stream().filter(x->x.id().equals(req.majorId())).findFirst().orElseThrow();
      return new Simulation("A future in "+m.name(),"You could grow from foundational projects into roles such as "+String.join(" or ",m.careers())+".",List.of("Solve real user problems","Collaborate in multidisciplinary teams","Build a visible portfolio"),List.of("Keeping skills current","Balancing theory with practice"),List.of("Take one introductory course","Build a two-week mini project","Speak to a professional"));
    }
}


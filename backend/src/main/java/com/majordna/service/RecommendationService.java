package com.majordna.service;

import com.majordna.model.Domain.*;
import org.springframework.stereotype.Service;
import java.util.*;

public interface RecommendationService { List<Recommendation> recommend(Map<String,Integer> dna, String mode); default List<Recommendation> recommend(Map<String,Integer> dna){return recommend(dna,"CAREER_EXPLORER");} List<Major> majors(); }

@Service
class WeightedRecommendationService implements RecommendationService {
    private final List<Major> tech = List.of(
      new Major("ai","Artificial Intelligence","AI","Systems that learn and reason.",Map.of("Logic",92,"Coding",88,"Data",84,"AI",95),List.of("AI Engineer","ML Engineer"),List.of("Python","Machine Learning","Mathematics")),
      new Major("data","Data Science and Analytics","DA","Turn data into evidence and decisions.",Map.of("Data",95,"Logic",86,"Data Analytics",95,"Communication",70),List.of("Data Scientist","Data Analyst"),List.of("Statistics","Python","Data Visualization")),
      new Major("cyber","Cyber Security","CS","Protect systems through investigation and risk thinking.",Map.of("Security",97,"Logic",86,"Coding",70),List.of("Security Analyst","Penetration Tester"),List.of("Networks","Linux","Ethical Hacking")),
      new Major("software","Software Engineering","SE","Design reliable software products.",Map.of("Coding",96,"Logic",86,"Communication",68),List.of("Software Engineer","Solutions Architect"),List.of("Java","Algorithms","System Design")),
      new Major("it","Information Technology","IT","Operate practical technology systems.",Map.of("Coding",75,"Logic",76,"Communication",78,"Design",65),List.of("IT Specialist","Systems Analyst"),List.of("Networks","Databases","Cloud"))
    );
    private final List<Major> general = List.of(
      new Major("health","Health and Medicine","HM","Health science and clinical practice.",Map.of("Health",95,"Social",82,"Detail",80),List.of("Doctor","Nurse","Allied Health Professional"),List.of("Biology","Communication","Clinical Reasoning")),
      new Major("engineering","Engineering","EN","Physical systems, products, and infrastructure.",Map.of("Engineering",95,"Science",82,"Detail",72),List.of("Engineer","Project Engineer"),List.of("Mathematics","Physics","Design")),
      new Major("business","Business and Management","BM","Organizations, markets, and leadership.",Map.of("Business",95,"Communication",82,"Data",65),List.of("Business Analyst","Manager","Entrepreneur"),List.of("Finance","Strategy","Leadership")),
      new Major("creative","Arts, Design and Media","AD","Creative, visual, and media work.",Map.of("Creative",96,"Communication",76),List.of("Designer","Media Producer"),List.of("Design","Storytelling","Portfolio")),
      new Major("science","Natural and Applied Sciences","NS","Evidence, experiments, and discovery.",Map.of("Science",96,"Data",78,"Detail",74),List.of("Researcher","Laboratory Scientist"),List.of("Research Methods","Statistics","Laboratory Skills")),
      new Major("social","Education and Social Sciences","SS","Teach and support people and communities.",Map.of("Social",96,"Communication",88),List.of("Teacher","Counsellor"),List.of("Communication","Research","Facilitation")),
      new Major("law","Law and Public Policy","LP","Justice, policy, evidence, and argument.",Map.of("Law",96,"Communication",88,"Detail",76),List.of("Lawyer","Policy Analyst"),List.of("Writing","Research","Advocacy")),
      new Major("technology","Computing and Technology","CT","Software, data, and digital systems.",Map.of("Technology",96,"Data",76,"Detail",68),List.of("Software Engineer","Data Analyst","IT Specialist"),List.of("Programming","Data","Systems Thinking")),
      new Major("environment","Environment and Built World","EB","Places, resources, and sustainability.",Map.of("Environment",96,"Engineering",72,"Science",75),List.of("Architect","Environmental Scientist","Urban Planner"),List.of("Sustainability","Design","Planning"))
    );
    public List<Major> majors() { return general; }
    public List<Recommendation> recommend(Map<String,Integer> dna, String mode) {
      if ("SUB_TRACK".equalsIgnoreCase(mode)) return List.of(
        new Recommendation("cyber","Cyber Security",dna.getOrDefault("Cyber Security",50),"Your answers show your current fit with security investigation and protection work.",List.of("Security Analyst","Penetration Tester")),
        new Recommendation("ai","Artificial Intelligence",dna.getOrDefault("Artificial Intelligence",50),"Your answers show your current fit with model building and intelligent systems.",List.of("AI Engineer","ML Engineer")),
        new Recommendation("data","Data Science and Analytics",dna.getOrDefault("Data Science and Analytics",50),"Your answers show your current fit with evidence, statistics, and analytics.",List.of("Data Scientist","Data Analyst"))
      ).stream().sorted(Comparator.comparingInt(Recommendation::match).reversed()).toList();
      List<Major> pool = "CAREER_EXPLORER".equalsIgnoreCase(mode) ? general : tech;
      if ("SUB_TRACK".equalsIgnoreCase(mode)) pool = tech.stream().filter(m -> Set.of("ai","data","cyber").contains(m.id())).toList();
      if ("CITYU_STUDENT".equalsIgnoreCase(mode)) pool = tech.stream().filter(m -> Set.of("ai","cyber","software","it").contains(m.id())).toList();
      return pool.stream().map(m -> {
        int match = Math.max(35,Math.min(98,(int)Math.round(m.targets().keySet().stream().mapToInt(k -> dna.getOrDefault(k,45)).average().orElse(45))));
        String strongest = m.targets().keySet().stream().max(Comparator.comparingInt(k -> dna.getOrDefault(k,0))).orElse("interest");
        return new Recommendation(m.id(),m.name(),match,"Your "+strongest.toLowerCase()+" responses align with this path.",m.careers());
      }).sorted(Comparator.comparingInt(Recommendation::match).reversed()).limit(3).toList();
    }
}

package com.majordna.model;

import java.util.List;
import java.util.Map;

public final class Domain {
    private Domain() {}
    public record Question(String id, String test, String category, String prompt, Map<String,Double> contributions, boolean reverse, double weight) {
        public Question(String id,String category,String prompt){this(id,"MAIN",category,prompt,Map.of(category,1.0),false,1.0);}
    }
    public record AssessmentScores(Map<String,Integer> dimensions, Map<String,Integer> categories,
                                   Map<String,Integer> bigFive, Map<String,Integer> intelligence,
                                   Map<String,Integer> workStyle) {}
    public record AssessmentRequest(String name, int age, String mode, String studentId, String educationLevel,
                                    String currentMajor, String country, String gender, String sessionId, Map<String,Integer> answers) {}
    public record UserProfile(String fullName, int age, String educationLevel, String currentMajor,
                              String country, String gender, String studentId) {}
    public record Major(String id, String name, String icon, String description, Map<String,Integer> targets, List<String> careers, List<String> skills) {}
    public record Recommendation(String majorId, String name, int match, String reason, List<String> careers) {}
    public record ProgramMatch(String name, String school, int match, List<String> tracks) {}
    public record SkillGap(String skill, int current, int required, String action) {}
    public record RoadmapStep(String period, String title, String detail, String type) {}
    public record Report(String reportId, String createdAt, String userName, String mode, Map<String,Integer> techDna,
                         List<String> strengths, List<Recommendation> recommendations, List<ProgramMatch> cityuPrograms,
                         List<SkillGap> skillGaps, List<RoadmapStep> roadmap, String aiInsight,
                         Map<String,Integer> categoryScores, Map<String,Integer> bigFive,
                         Map<String,Integer> intelligenceProfile, Map<String,Integer> workStyle,
                         List<CareerMatch> careerMatches, List<CareerMatch> careersToExploreCarefully,
                         List<MajorRanking> majorRankings, UserProfile profile) {}
    public record ChatRequest(String reportId, String message) {}
    public record ChatResponse(String answer, List<String> suggestions) {}
    public record SimulationRequest(String reportId, String majorId) {}
    public record Simulation(String title, String outlook, List<String> workOn, List<String> challenges, List<String> preparation) {}
    public record Career(String id, String name, String category, String associatedMajor, String summary,
                         int salaryMinMyrAnnual, int salaryMaxMyrAnnual, String requiredEducation,
                         String futureDemand, String workEnvironment, List<String> keySkills,
                         Map<String,Integer> targets) {}
    public record CareerMatch(Career career, int compatibility, Map<String,Integer> categoryMatches,
                              List<String> strongestMatches, List<String> importantGaps,
                              List<String> growthSuggestions) {}
    public record MajorRanking(String major, int compatibility, List<String> leadingCareers) {}
    public record LoginRequest(String username, String password, String role) {}
    public record RegisterRequest(String username, String password, String fullName) {}
    public record CreateStudentRequest(String username, String temporaryPassword, String fullName) {}
    public record ChangePasswordRequest(String currentPassword, String newPassword) {}
    public record AuthResponse(String token, String username, String fullName, String role, boolean mustChangePassword) {}
    public record PublicUser(String username, String fullName, String role, boolean mustChangePassword) {}
    public record AdminMetrics(int startedAssessments, int completedAssessments, int incompleteAssessments,
                               int completionRate, Map<String,Integer> completedByMode) {}
    public record AssessmentStart(String sessionId, String mode, String startedAt, boolean completed) {}
}

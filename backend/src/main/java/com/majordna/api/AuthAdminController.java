package com.majordna.api;
import com.majordna.model.Domain.*;
import com.majordna.service.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController @RequestMapping("/api") @CrossOrigin(origins={"http://localhost:5188","http://127.0.0.1:5188","http://localhost:5173","http://127.0.0.1:5173"})
public class AuthAdminController {
 private final AuthService auth;private final AssessmentService assessments;private final CareerLibraryService careers;private final AssessmentAnalyticsService analytics;
 public AuthAdminController(AuthService auth,AssessmentService assessments,CareerLibraryService careers,AssessmentAnalyticsService analytics){this.auth=auth;this.assessments=assessments;this.careers=careers;this.analytics=analytics;}
 @PostMapping("/auth/login") public AuthResponse login(@RequestBody LoginRequest r){return auth.login(r);}
 @GetMapping("/auth/me") public AuthResponse me(@RequestHeader("X-Auth-Token")String token){return auth.require(token,null);}
 @PostMapping("/auth/logout") public Map<String,Boolean> logout(@RequestHeader("X-Auth-Token")String token){auth.logout(token);return Map.of("loggedOut",true);}
 @PostMapping("/auth/change-password") public AuthResponse changePassword(@RequestHeader("X-Auth-Token")String token,@RequestBody ChangePasswordRequest r){return auth.changePassword(token,r);}
 @GetMapping("/admin/metrics") public AdminMetrics metrics(@RequestHeader("X-Auth-Token")String token){auth.require(token,"ADMIN");return analytics.metrics();}
 @GetMapping("/admin/students") public List<PublicUser> students(@RequestHeader("X-Auth-Token")String token){auth.require(token,"ADMIN");return auth.allStudents();}
 @PostMapping("/admin/students") public AuthResponse createStudent(@RequestHeader("X-Auth-Token")String token,@RequestBody CreateStudentRequest r){auth.require(token,"ADMIN");return auth.createStudent(r);}
 @GetMapping("/admin/questions") public List<Question> questions(@RequestHeader("X-Auth-Token")String token){auth.require(token,"ADMIN");return assessments.allQuestions();}
 @PutMapping("/admin/questions") public Map<String,Object> saveQuestions(@RequestHeader("X-Auth-Token")String token,@RequestBody List<Question>items){auth.require(token,"ADMIN");assessments.replaceAll(items);return Map.of("saved",items.size());}
 @GetMapping("/admin/careers") public List<Career> careers(@RequestHeader("X-Auth-Token")String token){auth.require(token,"ADMIN");return careers.all();}
 @PutMapping("/admin/careers") public Map<String,Object> saveCareers(@RequestHeader("X-Auth-Token")String token,@RequestBody List<Career>items){auth.require(token,"ADMIN");careers.replaceAll(items);return Map.of("saved",items.size());}
}

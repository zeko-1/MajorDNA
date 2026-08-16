package com.majordna.service;
import com.majordna.model.CareerExplorer;
import com.majordna.model.CityUStudent;
import com.majordna.model.User;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
class AssessmentServiceTest {
 @Test void mainAssessmentHasRequiredDistribution(){var s=new AssessmentService();var q=s.getQuestions("CAREER_EXPLORER");assertEquals(46,q.size());assertEquals(10,q.stream().filter(x->x.category().equals("Personality")).count());assertEquals(10,q.stream().filter(x->x.category().equals("Intelligence Profile")).count());assertEquals(8,q.stream().filter(x->x.category().equals("Work Style")).count());assertEquals(8,q.stream().filter(x->x.category().equals("Interests")).count());assertEquals(10,q.stream().filter(x->x.category().equals("Skills")).count());}
 @Test void reverseScoringIsApplied(){var s=new AssessmentService();var scores=s.score(Map.of("p01",5,"p02",1),"CAREER_EXPLORER");assertEquals(100,scores.get("Openness"));}
 @Test void subTrackContainsThreeAreas(){var s=new AssessmentService();assertEquals(9,s.getQuestions("SUB_TRACK").size());var scores=s.score(Map.of(),"SUB_TRACK");assertTrue(scores.keySet().containsAll(java.util.List.of("Cyber Security","Artificial Intelligence","Data Science and Analytics")));}
 @Test void gettersAndSettersPreserveEncapsulation(){var student=new CityUStudent("Zakia",18,"C123");student.setName("Updated Student");student.setAge(19);student.setStudentId("C456");assertEquals("Updated Student",student.getName());assertEquals(19,student.getAge());assertEquals("C456",student.getStudentId());}
 @Test void runtimePolymorphismUsesOverriddenMethod(){User user=new CareerExplorer("Explorer",20,"Diploma");assertEquals("CAREER_EXPLORER",user.getUserType());}
 @Test void settersValidateInvalidState(){var user=new CareerExplorer("Explorer",20,"Diploma");assertThrows(IllegalArgumentException.class,()->user.setAge(5));assertThrows(IllegalArgumentException.class,()->user.setName(" "));}
}

// JavaFX companion dashboard that reads the latest report saved by the Spring Boot backend.
package com.majordna.desktop;
import com.fasterxml.jackson.databind.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class MajorDnaDesktop extends Application {
 private final ReportRepository repository=new JsonReportRepository();
 @Override public void start(Stage stage){BorderPane root=new BorderPane();root.setStyle("-fx-background-color:#eee5d7");Label title=new Label("MajorDNA Report Dashboard");title.setStyle("-fx-font-size:25px;-fx-font-weight:bold;-fx-text-fill:#2c211b");Button reload=new Button("Load latest saved assessment");HBox top=new HBox(18,title,reload);top.setPadding(new Insets(20));root.setTop(top);TabPane tabs=new TabPane();root.setCenter(tabs);Runnable load=()->{tabs.getTabs().clear();try{ReportView r=repository.loadLatest();tabs.getTabs().add(tab("Five Category Radar",new RadarChart(r.categoryScores())));tabs.getTabs().add(tab("Big Five",barChart(r.bigFive(),"Personality score")));tabs.getTabs().add(tab("Intelligence",barChart(r.intelligence(),"Intelligence profile")));tabs.getTabs().add(tab("Work Style",barChart(r.workStyle(),"Work style preference")));tabs.getTabs().add(tab("Top Careers",barChart(r.careers(),"Compatibility")));tabs.getTabs().add(tab("Salary Range",salaryChart(r.salaryMin(),r.salaryMax())));}catch(Exception e){tabs.getTabs().add(tab("No report",new Label("Could not load backend/data/reports.json\n"+e.getMessage()+"\nComplete a web assessment first.")));}};reload.setOnAction(e->load.run());load.run();stage.setTitle("MajorDNA JavaFX Dashboard");stage.setScene(new Scene(root,1050,720));stage.show();}
 private Tab tab(String name,Node content){Tab t=new Tab(name,content);t.setClosable(false);return t;}
 private BarChart<String,Number> barChart(Map<String,Integer> data,String title){CategoryAxis x=new CategoryAxis();NumberAxis y=new NumberAxis(0,100,10);BarChart<String,Number> c=new BarChart<>(x,y);c.setTitle(title);c.setLegendVisible(false);XYChart.Series<String,Number>s=new XYChart.Series<>();data.forEach((k,v)->s.getData().add(new XYChart.Data<>(k,v)));c.getData().add(s);return c;}
 private BarChart<String,Number> salaryChart(Map<String,Integer> min,Map<String,Integer> max){CategoryAxis x=new CategoryAxis();NumberAxis y=new NumberAxis();BarChart<String,Number> c=new BarChart<>(x,y);c.setTitle("Indicative annual salary range in MYR");XYChart.Series<String,Number>a=new XYChart.Series<>();a.setName("Minimum");XYChart.Series<String,Number>b=new XYChart.Series<>();b.setName("Maximum");min.forEach((k,v)->a.getData().add(new XYChart.Data<>(k,v)));max.forEach((k,v)->b.getData().add(new XYChart.Data<>(k,v)));c.getData().addAll(a,b);return c;}
 public static void main(String[]args){launch(args);}
}
interface ReportRepository{ReportView loadLatest()throws IOException;}
class JsonReportRepository implements ReportRepository{
 private final ObjectMapper mapper=new ObjectMapper();
 public ReportView loadLatest()throws IOException{Path p=Path.of("..","backend","data","reports.json");if(!Files.exists(p))throw new IOException("Report file not found at "+p.toAbsolutePath());JsonNode all=mapper.readTree(p.toFile());if(!all.isArray()||all.isEmpty())throw new IOException("No saved reports found");JsonNode r=all.get(0);return new ReportView(map(r,"categoryScores"),map(r,"bigFive"),map(r,"intelligenceProfile"),map(r,"workStyle"),careerMap(r,"compatibility"),salary(r,"salaryMinMyrAnnual"),salary(r,"salaryMaxMyrAnnual"));}
 private Map<String,Integer> map(JsonNode r,String field){Map<String,Integer> out=new LinkedHashMap<>();r.path(field).fields().forEachRemaining(e->out.put(e.getKey(),e.getValue().asInt()));return out;}
 private Map<String,Integer> careerMap(JsonNode r,String field){Map<String,Integer> out=new LinkedHashMap<>();for(JsonNode c:r.path("careerMatches"))out.put(c.path("career").path("name").asText(),c.path(field).asInt());return out;}
 private Map<String,Integer> salary(JsonNode r,String field){Map<String,Integer> out=new LinkedHashMap<>();for(JsonNode c:r.path("careerMatches"))out.put(c.path("career").path("name").asText(),c.path("career").path(field).asInt());return out;}
}
record ReportView(Map<String,Integer> categoryScores,Map<String,Integer> bigFive,Map<String,Integer> intelligence,Map<String,Integer> workStyle,Map<String,Integer> careers,Map<String,Integer> salaryMin,Map<String,Integer> salaryMax){}
class RadarChart extends Pane{
 private final Map<String,Integer> data;RadarChart(Map<String,Integer>d){data=d;setMinSize(600,500);widthProperty().addListener(e->draw());heightProperty().addListener(e->draw());}
 private void draw(){getChildren().clear();Canvas canvas=new Canvas(Math.max(600,getWidth()),Math.max(500,getHeight()));GraphicsContext g=canvas.getGraphicsContext2D();double cx=canvas.getWidth()/2,cy=canvas.getHeight()/2,r=Math.min(cx,cy)*.68;List<String>labels=new ArrayList<>(data.keySet());if(labels.isEmpty())return;g.setStroke(Color.web("#9b7660"));for(int ring=1;ring<=4;ring++){double rr=r*ring/4;polygon(g,cx,cy,rr,labels.size(),false,null);}double[]values=labels.stream().mapToDouble(k->data.get(k)/100.0).toArray();g.setFill(Color.web("#6f5546",.42));g.setStroke(Color.web("#2c211b"));polygon(g,cx,cy,r,labels.size(),true,values);g.setFill(Color.web("#2c211b"));for(int i=0;i<labels.size();i++){double a=-Math.PI/2+2*Math.PI*i/labels.size();g.fillText(labels.get(i)+" "+data.get(labels.get(i)),cx+Math.cos(a)*(r+45)-35,cy+Math.sin(a)*(r+35));}getChildren().add(canvas);}
 private void polygon(GraphicsContext g,double cx,double cy,double r,int n,boolean fill,double[]values){g.beginPath();for(int i=0;i<n;i++){double rr=values==null?r:r*values[i],a=-Math.PI/2+2*Math.PI*i/n,x=cx+Math.cos(a)*rr,y=cy+Math.sin(a)*rr;if(i==0)g.moveTo(x,y);else g.lineTo(x,y);}g.closePath();if(fill)g.fill();g.stroke();}
}


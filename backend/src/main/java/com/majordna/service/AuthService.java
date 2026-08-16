package com.majordna.service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.majordna.model.Domain.*;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {
 public record StoredUser(String username,String passwordHash,String fullName,String role,boolean mustChangePassword){}
 private final ObjectMapper mapper;private final Path file=Path.of("data","users.json");private final List<StoredUser>users=new ArrayList<>();private final Map<String,StoredUser>sessions=new ConcurrentHashMap<>();
 public AuthService(ObjectMapper mapper){this.mapper=mapper;load();}
 public synchronized AuthResponse login(LoginRequest request){StoredUser u=users.stream().filter(x->x.username().equalsIgnoreCase(safe(request.username()))).findFirst().orElseThrow(()->new IllegalArgumentException("Invalid username or password."));if(!u.passwordHash().equals(hash(request.password()))||request.role()!=null&&!request.role().isBlank()&&!u.role().equalsIgnoreCase(request.role()))throw new IllegalArgumentException("Invalid username, password, or account type.");return createSession(u);}
 public synchronized AuthResponse createStudent(CreateStudentRequest r){if(safe(r.username()).isBlank()||safe(r.fullName()).isBlank()||safe(r.temporaryPassword()).length()<8)throw new IllegalArgumentException("Full name, username, and a temporary password of at least 8 characters are required.");if(users.stream().anyMatch(x->x.username().equalsIgnoreCase(r.username())))throw new IllegalArgumentException("Username already exists.");StoredUser u=new StoredUser(r.username().trim(),hash(r.temporaryPassword()),r.fullName().trim(),"STUDENT",true);users.add(u);save();return new AuthResponse("",u.username(),u.fullName(),u.role(),true);}
 public synchronized AuthResponse changePassword(String token,ChangePasswordRequest r){StoredUser current=session(token);if(!current.passwordHash().equals(hash(r.currentPassword())))throw new IllegalArgumentException("Current password is incorrect.");if(safe(r.newPassword()).length()<8)throw new IllegalArgumentException("New password must contain at least 8 characters.");StoredUser updated=new StoredUser(current.username(),hash(r.newPassword()),current.fullName(),current.role(),false);int i=users.indexOf(current);users.set(i,updated);sessions.put(token,updated);save();return response(token,updated);}
 public synchronized List<PublicUser> allStudents(){return users.stream().filter(u->u.role().equals("STUDENT")).map(u->new PublicUser(u.username(),u.fullName(),u.role(),u.mustChangePassword())).toList();}
 public AuthResponse require(String token,String role){StoredUser u=session(token);if(role!=null&&!u.role().equals(role))throw new SecurityException("This action requires "+role+" permission.");return response(token,u);}
 public void logout(String token){sessions.remove(token);}
 private StoredUser session(String token){StoredUser u=sessions.get(token);if(u==null)throw new SecurityException("Login required.");return u;}
 private AuthResponse createSession(StoredUser u){String token=UUID.randomUUID().toString();sessions.put(token,u);return response(token,u);}
 private AuthResponse response(String token,StoredUser u){return new AuthResponse(token,u.username(),u.fullName(),u.role(),u.mustChangePassword());}
 private void load(){try{if(Files.exists(file))users.addAll(mapper.readValue(file.toFile(),new TypeReference<List<StoredUser>>(){}));if(users.stream().noneMatch(u->u.role().equals("ADMIN")))users.add(new StoredUser("admin",hash("Admin123!"),"MajorDNA Administrator","ADMIN",false));save();}catch(IOException e){throw new IllegalStateException("Could not load local users.",e);}}
 private void save(){try{Files.createDirectories(file.getParent());mapper.writeValue(file.toFile(),users);}catch(IOException e){throw new IllegalStateException("Could not save local users.",e);}}
 private String safe(String v){return v==null?"":v;}
 private String hash(String value){try{byte[]bytes=MessageDigest.getInstance("SHA-256").digest(safe(value).getBytes(StandardCharsets.UTF_8));return HexFormat.of().formatHex(bytes);}catch(Exception e){throw new IllegalStateException(e);}}
}

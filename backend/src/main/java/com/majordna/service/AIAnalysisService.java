package com.majordna.service;

import com.majordna.model.Domain.*;
import java.util.*;

public interface AIAnalysisService {
    String explain(String name, Map<String,Integer> dna, List<Recommendation> recommendations);
    ChatResponse advise(Report report, String message);
    boolean configured();
}

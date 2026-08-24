// The interface lets the report service use either a hosted model or the Java fallback.
package com.majordna.service;

import com.majordna.model.Domain.*;
import java.util.*;

public interface AIAnalysisService {
    String explain(String name, Map<String,Integer> dna, List<Recommendation> recommendations);
    ChatResponse advise(Report report, String message);
    boolean configured();
}


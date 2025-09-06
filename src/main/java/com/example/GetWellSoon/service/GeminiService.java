//package com.example.GetWellSoon.service;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//import java.util.*;
//
//@Service
//public class GeminiService {
//
//    @Value("${gemini.api.key}")
//    private String apiKey;
//
//    @Value("${gemini.model}")
//    private String model;
//
//    private static final String BASE_URL =
//            "https://generativelanguage.googleapis.com/v1beta/models/";
//
//    // Conversation history (simple in-memory, can use DB for persistence)
//    private final List<Map<String, Object>> conversation = new ArrayList<>();
//
//    public String chat(String userMessage) {
//        String medicalPrompt = """
//        You are a medical consultant chatbot.
//        The user will describe their symptoms.
//        Your job:
//        1. Suggest possible common conditions (not exact diagnosis).
//        2. Give safe, general and more accurate advice in simple and short sentences.
//        3. Always end with: "⚠️ This is not a medical diagnosis. Please consult a doctor for confirmation."
//
//        User symptoms: %s
//        """.formatted(userMessage);
//
//        // Add user message (wrapped)
//        conversation.add(Map.of(
//                "role", "user",
//                "parts", List.of(Map.of("text", medicalPrompt))
//        ));
//
//        // Build request
//        String url = BASE_URL + model + ":generateContent?key=" + apiKey;
//
//        Map<String, Object> request = Map.of("contents", conversation);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
//
//        RestTemplate restTemplate = new RestTemplate();
//        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
//
//        // Extract model response text
//        Map candidate = (Map) ((List) response.getBody().get("candidates")).get(0);
//        Map content = (Map) candidate.get("content");
//        List<Map> parts = (List<Map>) content.get("parts");
//        String modelReply = (String) parts.get(0).get("text");
//
//        // Add model reply to history
//        conversation.add(Map.of(
//                "role", "model",
//                "parts", List.of(Map.of("text", modelReply))
//        ));
//
//        return modelReply;
//    }
//}


package com.example.GetWellSoon.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.model}")
    private String model;

    private static final String BASE_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/";

    // In-memory session store: sessionId -> conversation history
    private final Map<String, List<Map<String, Object>>> conversations = new ConcurrentHashMap<>();

    public String chat(String sessionId, String userMessage) {
        String medicalPrompt = """
        You are a medical consultant.
        The user will describe their symptoms.
        Your job:
        1. Suggest possible common conditions in simple way.
        2. Give safe, general and accurate advice in simple and short sentences or short description.
        3. Symptoms and solution should be in structured manner and easily understandable. 
        4. Always end with: "⚠️ This is not a medical diagnosis. Please consult a doctor for confirmation."

        User symptoms: %s
        """.formatted(userMessage);

        // Get or create conversation for this session
        List<Map<String, Object>> conversation =
                conversations.computeIfAbsent(sessionId, k -> new ArrayList<>());

        // Add user message
        conversation.add(Map.of(
                "role", "user",
                "parts", List.of(Map.of("text", medicalPrompt))
        ));

        // Build request
        String url = BASE_URL + model + ":generateContent?key=" + apiKey;
        Map<String, Object> request = Map.of("contents", conversation);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Map> response =
                restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        // Extract response
        Map candidate = (Map) ((List) response.getBody().get("candidates")).get(0);
        Map content = (Map) candidate.get("content");
        List<Map> parts = (List<Map>) content.get("parts");
        String modelReply = (String) parts.get(0).get("text");

        // Add model reply
        conversation.add(Map.of(
                "role", "model",
                "parts", List.of(Map.of("text", modelReply))
        ));

        return modelReply;
    }

    public void resetConversation(String sessionId) {
        conversations.remove(sessionId);
    }
}

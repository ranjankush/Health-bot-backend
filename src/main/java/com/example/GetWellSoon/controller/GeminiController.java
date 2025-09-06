//package com.example.GetWellSoon.controller;
//
//import com.example.GetWellSoon.service.GeminiService;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/chat")
//public class GeminiController {
//
//    private final GeminiService geminiService;
//
//    public GeminiController(GeminiService geminiService) {
//        this.geminiService = geminiService;
//    }
//
//    @GetMapping
//    public String chat(@RequestParam String message) {
//        return geminiService.chat(message);
//    }
//}


package com.example.GetWellSoon.controller;

import com.example.GetWellSoon.service.GeminiService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin(origins ="${FRONTEND_API}")
@RestController
@RequestMapping("/chat")
public class GeminiController {

    private final GeminiService geminiService;

    public GeminiController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    // Chat endpoint
    @GetMapping
    public String chat(
            @RequestParam(required = false) String sessionId,
            @RequestParam String message) {

        if (sessionId == null || sessionId.isBlank()) {
            sessionId = UUID.randomUUID().toString();
        }

        String reply = geminiService.chat(sessionId, message);
        return "Session: " + sessionId + "\nBot: " + reply;
    }

    // Reset endpoint
    @DeleteMapping
    public String reset(@RequestParam String sessionId) {
        geminiService.resetConversation(sessionId);
        return "Conversation reset for session: " + sessionId;
    }
}

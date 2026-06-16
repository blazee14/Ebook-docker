package com.api.capas.infrastructure.web;

import com.api.capas.domain.dto.ChatRequest;
import com.api.capas.infrastructure.ChatbotService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    private final ChatbotService chatbotService;

    // Inyección de dependencias por constructor
    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping("/preguntar")
    public Map<String, String> preguntar(@RequestBody ChatRequest request) {
        // Llama a tu servicio pasándole el objeto ChatRequest completo
        String respuesta = chatbotService.generarRespuesta(request);
        
        // Retorna la respuesta mapeada como JSON para que Angular la lea sin problemas
        return Map.of("respuesta", respuesta);
    }
}
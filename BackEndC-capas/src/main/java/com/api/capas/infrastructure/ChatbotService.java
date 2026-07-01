package com.api.capas.infrastructure;

import com.api.capas.domain.dto.ChatRequest;
import com.api.capas.infrastructure.persistence.entities.Producto;
import com.api.capas.infrastructure.persistence.repositories.ProductoRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.lang.NonNull;

import java.util.Objects;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class ChatbotService {

    @Value("${groq.api.url:https://api.groq.com/openai/v1/chat/completions}")
    private String apiUrl;

    @Value("${groq.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ProductoRepository productoRepository; 

    // Constructor para Inyección de Dependencias
    public ChatbotService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
        
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000); 
        factory.setReadTimeout(5000);  
        
        this.restTemplate = new RestTemplate(factory);
    }

    public String generarRespuesta(@NonNull ChatRequest chatRequest) {
        String userMessage = chatRequest.getMessage();
        try {
            // 1. Extraer los libros directo de tu base de datos real
            List<Producto> listaProductos = productoRepository.findAll();
            
            // 2. Construir el catálogo dinámico
            StringBuilder catalogoBuilder = new StringBuilder();
            for (Producto p : listaProductos) {
                catalogoBuilder.append(String.format("- %s: S/.%.2f\n", 
                    p.getTitulo(), p.getPrecio())); 
            }

            // 3. Crear el System Prompt alimentado por tu BD
            String systemPrompt = "Eres un asistente amigable de la librería virtual EBOOK (Perú). " +
                    "Responde siempre en español, de forma muy breve (máximo 3 oraciones). " +
                    "Este es el catálogo real y actualizado extraído de nuestra base de datos en este instante:\n" + 
                    catalogoBuilder.toString() +
                    "\nAyuda al usuario basándote estrictamente en esta lista de libros disponibles.";

            // 4. Preparar la petición HTTP para Groq
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            Map<String, Object> body = new HashMap<>();
            body.put("model", "llama-3.3-70b-versatile");
            body.put("temperature", 0.5);

            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content", systemPrompt));
            messages.add(Map.of("role", "user", "content", userMessage));
            body.put("messages", messages);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            String requestUrl = Objects.requireNonNull(apiUrl, "groq.api.url must not be null");
            HttpMethod httpMethod = Objects.requireNonNull(HttpMethod.POST, "HTTP method must not be null");
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    requestUrl,
                    httpMethod,
                    entity,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            // 5. Retornar la respuesta de la IA
            Map<String, Object> responseBody = response.getBody();
            if (response.getStatusCode() == HttpStatus.OK && responseBody != null) {
                Object choicesObject = responseBody.get("choices");
                if (choicesObject instanceof List<?> choicesList && !choicesList.isEmpty()) {
                    Object firstChoice = choicesList.get(0);
                    if (firstChoice instanceof Map<?, ?> choiceMap) {
                        Object messageObject = choiceMap.get("message");
                        if (messageObject instanceof Map<?, ?> messageMap) {
                            Object content = messageMap.get("content");
                            if (content instanceof String contentText) {
                                return contentText;
                            }
                        }
                    }
                }
            }

            return "Lo siento, en este momento tengo problemas para procesar la lista de libros.";
        } catch (Exception e) {
            // Si la API falla por falta de saldo, URL incorrecta o timeout, caerá aquí inmediatamente en lugar de colgarse
            System.err.println("❌ Error en ChatbotService al consultar la API externa:");
            e.printStackTrace();
            return "Ocurrió un error al conectar con el asistente de IA. Por favor, verifica tus credenciales y saldo en application.properties.";
        }
    }
}
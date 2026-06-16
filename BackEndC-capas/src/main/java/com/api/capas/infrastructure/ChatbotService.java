package com.api.capas.infrastructure;

import com.api.capas.domain.dto.ChatRequest;
import com.api.capas.infrastructure.persistence.entities.Producto;
import com.api.capas.infrastructure.persistence.repositories.ProductoRepository;

import org.springframework.beans.factory.annotation.Value;
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

    public String generarRespuesta(ChatRequest chatRequest) {
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
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);

            // 5. Retornar la respuesta de la IA
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                return (String) message.get("content");
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
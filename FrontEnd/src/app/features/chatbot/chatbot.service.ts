import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { environment } from '../../../environments/environment'; // 🌟 Importamos el environment

export interface ChatMessage {
  role: 'user' | 'model';
  text: string;
}

@Injectable({
  providedIn: 'root'
})
export class ChatbotService {

  // ✅ CORREGIDO: Ahora usa la variable global del environment. 
  // Si estás en desarrollo usa localhost, si estás en producción cambia sola.
  private readonly apiUrl = `${environment.apiUrl}/chatbot/preguntar`;

  constructor(private http: HttpClient) { }

  async sendMessage(userText: string): Promise<string> {
    try {
      const response = await firstValueFrom(
        this.http.post<{ respuesta: string }>(this.apiUrl, { message: userText })
      );
      
      return response?.respuesta || 'No encontré una respuesta válida.';
    } catch (error) {
      console.error('Error al conectar con el backend de Spring Boot:', error);
      throw new Error('API_ERROR');
    }
  }

  resetChat(): void {
    // Manejado por el servidor
  }
}
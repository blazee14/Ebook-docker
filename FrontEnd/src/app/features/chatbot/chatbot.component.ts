import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChatbotService, ChatMessage } from './chatbot.service';

@Component({
  selector: 'app-chatbot',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chatbot.component.html',
  styleUrls: ['./chatbot.component.css']
  })
export class ChatbotComponent implements OnInit {
  isOpen = false;
  userInput = '';
  messages: ChatMessage[] = [];
  isLoading = false;

  quickSuggestions = [
    '¿Qué libros tienen?',
    '¿Qué me recomiendas?',
    '¿Cómo comprar?'
  ];

  constructor(private chatbotService: ChatbotService) {}

  ngOnInit() {
    this.messages.push({
      role: 'model',
      text: '¡Hola! 👋 Soy tu asistente de libros. ¿En qué te puedo ayudar hoy?'
    });
  }

  toggleChat() {
    this.isOpen = !this.isOpen;
  }

  async sendMessage(text?: string) {
    const msg = text || this.userInput.trim();
    if (!msg || this.isLoading) return;

    this.userInput = '';
    this.messages.push({ role: 'user', text: msg });
    this.isLoading = true;

    try {
      const reply = await this.chatbotService.sendMessage(msg);
      this.messages.push({ role: 'model', text: reply });
    } catch (error: any) {
      // ✅ Sincronizado con los errores que lanza el nuevo SDK de Gemini
      let errorMsg = 'Ocurrió un error. Por favor intenta de nuevo.';

      if (error.message === 'API_ERROR') {
        errorMsg = '🔑 Error de acceso. Tu API Key (AQ) no tiene permisos de IA activos o la cuota en tu cuenta está en cero.';
      } else if (error.message === 'RATE_LIMIT') {
        errorMsg = '⏳ Demasiadas consultas seguidas. Espera unos segundos e intenta de nuevo.';
      } else if (error.message === 'BAD_REQUEST') {
        errorMsg = '❌ Estructura de solicitud no válida o incompatible.';
      }

      this.messages.push({ role: 'model', text: errorMsg });
    }

    this.isLoading = false;
    setTimeout(() => this.scrollToBottom(), 50);
  }

  handleKey(event: KeyboardEvent) {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.sendMessage();
    }
  }

  private scrollToBottom() {
    const el = document.getElementById('chat-messages');
    if (el) el.scrollTop = el.scrollHeight;
  }
}
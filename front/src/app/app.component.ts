import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';
import { ChatService } from './chat.service';
import { ChatMessage } from './models';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit, OnDestroy {
  author = '';
  content = '';
  messages: ChatMessage[] = [];
  loading = false;
  connected = false;
  errorMessage = '';

  private readonly subscriptions = new Subscription();

  constructor(private readonly chatService: ChatService) {}

  ngOnInit(): void {
    this.loadMessages();
    this.chatService.connect();

    this.subscriptions.add(
      this.chatService.connected$.subscribe((connected) => {
        this.connected = connected;
      })
    );

    this.subscriptions.add(
      this.chatService.incomingMessages$.subscribe((message) => {
        this.addOrUpdateMessage(message);
        this.errorMessage = '';
      })
    );

    this.subscriptions.add(
      this.chatService.errors$.subscribe((message) => {
        this.errorMessage = message;
      })
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
    this.chatService.disconnect();
  }


  isCurrentUserMessage(message: ChatMessage): boolean {
    return this.normalizeAuthor(message.author) === this.normalizeAuthor(this.author);
  }

  trackByMessageId(_: number, message: ChatMessage): number {
    return message.id;
  }

  sendMessage(): void {
    const author = this.author.trim();
    const content = this.content.trim();

    if (!author || !content) {
      this.errorMessage = 'Le pseudo et le message sont obligatoires.';
      return;
    }

    this.loading = true;
    const sent = this.chatService.sendMessage({ author, content });
    this.loading = false;

    if (sent) {
      this.content = '';
      this.errorMessage = '';
    }
  }


  private normalizeAuthor(author: string): string {
    return author.trim().toLowerCase();
  }

  private loadMessages(): void {
    this.chatService.findMessages().subscribe({
      next: (messages) => {
        this.messages = messages;
        this.errorMessage = '';
      },
      error: () => {
        this.errorMessage = 'Impossible de récupérer l’historique. Vérifiez que le backend est démarré.';
      }
    });
  }

  private addOrUpdateMessage(message: ChatMessage): void {
    const existingMessageIndex = this.messages.findIndex((currentMessage) => currentMessage.id === message.id);

    if (existingMessageIndex >= 0) {
      this.messages[existingMessageIndex] = message;
      return;
    }

    this.messages = [...this.messages, message].sort((first, second) => first.id - second.id);
  }
}

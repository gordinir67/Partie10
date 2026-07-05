import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { ChatMessage, CreateChatMessageRequest, WebSocketErrorMessage } from './models';

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private readonly apiUrl = '/api/v1/chat/messages';
  private socket?: WebSocket;

  private readonly incomingMessageSubject = new Subject<ChatMessage>();
  private readonly errorSubject = new Subject<string>();
  private readonly connectedSubject = new BehaviorSubject<boolean>(false);

  readonly incomingMessages$ = this.incomingMessageSubject.asObservable();
  readonly errors$ = this.errorSubject.asObservable();
  readonly connected$ = this.connectedSubject.asObservable();

  constructor(private readonly http: HttpClient) {}

  findMessages(afterId?: number): Observable<ChatMessage[]> {
    let params = new HttpParams();

    if (afterId && afterId > 0) {
      params = params.set('afterId', afterId);
    }

    return this.http.get<ChatMessage[]>(this.apiUrl, { params });
  }

  connect(): void {
    if (this.socket && (this.socket.readyState === WebSocket.OPEN || this.socket.readyState === WebSocket.CONNECTING)) {
      return;
    }

    this.socket = new WebSocket(this.buildWebSocketUrl());

    this.socket.onopen = () => {
      this.connectedSubject.next(true);
      this.errorSubject.next('');
    };

    this.socket.onmessage = (event) => {
      const data = JSON.parse(event.data) as ChatMessage | WebSocketErrorMessage;

      if ('error' in data) {
        this.errorSubject.next(data.error);
        return;
      }

      this.incomingMessageSubject.next(data);
    };

    this.socket.onerror = () => {
      this.errorSubject.next('Connexion WebSocket impossible. Vérifiez que le backend est démarré.');
    };

    this.socket.onclose = () => {
      this.connectedSubject.next(false);
    };
  }

  sendMessage(request: CreateChatMessageRequest): boolean {
    if (!this.socket || this.socket.readyState !== WebSocket.OPEN) {
      this.errorSubject.next('La connexion WebSocket n’est pas encore ouverte. Réessayez dans quelques secondes.');
      this.connect();
      return false;
    }

    this.socket.send(JSON.stringify(request));
    return true;
  }

  disconnect(): void {
    this.socket?.close();
    this.socket = undefined;
    this.connectedSubject.next(false);
  }

  private buildWebSocketUrl(): string {
    const isLocalAngularDevServer = window.location.hostname === 'localhost' && window.location.port === '4200';

    if (isLocalAngularDevServer) {
      return 'ws://localhost:8080/ws/chat';
    }

    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    return `${protocol}//${window.location.host}/ws/chat`;
  }
}

import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { ChatMessage } from './models';

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private readonly apiUrl = '/api/v1/chat/messages';

  constructor(private readonly http: HttpClient) {}

  findMessages(afterId?: number): Observable<ChatMessage[]> {
    let params = new HttpParams();

    if (afterId && afterId > 0) {
      params = params.set('afterId', afterId);
    }

    return this.http.get<ChatMessage[]>(this.apiUrl, { params });
  }
}
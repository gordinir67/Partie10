export interface ChatMessage {
  id: number;
  author: string;
  content: string;
  createdAt: string;
}

export interface CreateChatMessageRequest {
  author: string;
  content: string;
}

export interface WebSocketErrorMessage {
  error: string;
}

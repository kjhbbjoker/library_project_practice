import axios from 'axios';
import { Book, User, Loan, PageResponse } from '../types';

const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
});

// Request interceptor
api.interceptors.request.use(
  (config) => {
    console.log(`API Request: ${config.method?.toUpperCase()} ${config.url}`);
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor
api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    console.error('API Error:', error.response?.data || error.message);
    return Promise.reject(error);
  }
);

// Book API
export const bookApi = {
  getBooks: (params?: {
    keyword?: string;
    author?: string;
    page?: number;
    size?: number;
    sort?: string;
  }) => api.get<PageResponse<Book>>('/books', { params }),
  
  getBook: (id: number) => api.get<Book>(`/books/${id}`),
  
  createBook: (book: Omit<Book, 'id' | 'createdAt' | 'updatedAt'>) => 
    api.post<Book>('/books', book),
  
  updateBook: (id: number, book: Omit<Book, 'id' | 'createdAt' | 'updatedAt'>) => 
    api.put<Book>(`/books/${id}`, book),
  
  deleteBook: (id: number) => api.delete(`/books/${id}`),
  
  getBookByIsbn: (isbn: string) => api.get<Book>(`/books/isbn/${isbn}`),
  
  getBookCount: () => api.get<number>('/books/count'),
};

// User API
export const userApi = {
  getUsers: (params?: {
    keyword?: string;
    page?: number;
    size?: number;
    sort?: string;
  }) => api.get<PageResponse<User>>('/users', { params }),
  
  getUser: (id: number) => api.get<User>(`/users/${id}`),
  
  createUser: (user: Omit<User, 'id' | 'createdAt' | 'updatedAt'>) => 
    api.post<User>('/users', user),
  
  updateUser: (id: number, user: Omit<User, 'id' | 'createdAt' | 'updatedAt'>) => 
    api.put<User>(`/users/${id}`, user),
  
  deleteUser: (id: number) => api.delete(`/users/${id}`),
  
  getUserByEmail: (email: string) => api.get<User>(`/users/email/${email}`),
  
  checkEmailExists: (email: string) => api.get(`/users/email/${email}/exists`),
  
  getUserCount: () => api.get<number>('/users/count'),
};

// Loan API
export const loanApi = {
  getLoans: (params?: {
    status?: 'ACTIVE' | 'RETURNED' | 'OVERDUE';
    page?: number;
    size?: number;
    sort?: string;
  }) => api.get<PageResponse<Loan>>('/loans', { params }),
  
  getLoan: (id: number) => api.get<Loan>(`/loans/${id}`),
  
  createLoan: (userId: number, bookId: number) => 
    api.post<Loan>('/loans', null, { params: { userId, bookId } }),
  
  returnBook: (id: number) => api.put<Loan>(`/loans/${id}/return`),
  
  getLoansByUser: (userId: number) => api.get<Loan[]>(`/loans/user/${userId}`),
  
  getLoansByBook: (bookId: number) => api.get<Loan[]>(`/loans/book/${bookId}`),
  
  getOverdueLoans: () => api.get<Loan[]>('/loans/overdue'),
  
  updateOverdueLoans: () => api.put('/loans/update-overdue'),
  
  getActiveLoanCount: (userId: number) => api.get<number>(`/loans/user/${userId}/active-count`),
};

export default api;

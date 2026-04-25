import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { ApiError, apiRequest } from './client'
import type {
  Book,
  BookPayload,
  BookStatus,
  BookUpdatePayload,
  DashboardSummary,
  QuoteCard,
  QuotePayload,
  Review,
  ReviewPayload,
  Tag,
} from '../types/api'

export const queryKeys = {
  books: (status?: BookStatus) => ['books', status ?? 'ALL'] as const,
  book: (bookId: number) => ['book', bookId] as const,
  bookQuotes: (bookId: number) => ['bookQuotes', bookId] as const,
  bookReview: (bookId: number) => ['bookReview', bookId] as const,
  quoteSearch: (params: QuoteSearchParams) => ['quoteSearch', params] as const,
  tags: () => ['tags'] as const,
  dashboard: (year?: number, month?: number) => ['dashboard', year, month] as const,
}

export type QuoteSearchParams = {
  q?: string
  tag?: string
  bookId?: number
}

export function useBooks(status?: BookStatus) {
  return useQuery({
    queryKey: queryKeys.books(status),
    queryFn: () => {
      const search = status ? `?status=${status}` : ''
      return apiRequest<Book[]>(`/api/books${search}`)
    },
  })
}

export function useBook(bookId: number) {
  return useQuery({
    queryKey: queryKeys.book(bookId),
    queryFn: () => apiRequest<Book>(`/api/books/${bookId}`),
    enabled: Number.isFinite(bookId),
  })
}

export function useBookQuotes(bookId: number) {
  return useQuery({
    queryKey: queryKeys.bookQuotes(bookId),
    queryFn: () => apiRequest<QuoteCard[]>(`/api/books/${bookId}/quotes`),
    enabled: Number.isFinite(bookId),
  })
}

export function useBookReview(bookId: number) {
  return useQuery({
    queryKey: queryKeys.bookReview(bookId),
    queryFn: async () => {
      try {
        return await apiRequest<Review>(`/api/books/${bookId}/review`)
      } catch (error) {
        if (error instanceof ApiError && error.status === 404) {
          return null
        }
        throw error
      }
    },
    enabled: Number.isFinite(bookId),
    retry: false,
  })
}

export function useQuoteSearch(params: QuoteSearchParams) {
  return useQuery({
    queryKey: queryKeys.quoteSearch(params),
    queryFn: () => apiRequest<QuoteCard[]>(`/api/quotes/search${toSearchParams(params)}`),
  })
}

export function useTags() {
  return useQuery({
    queryKey: queryKeys.tags(),
    queryFn: () => apiRequest<Tag[]>('/api/tags'),
  })
}

export function useDashboardSummary(year?: number, month?: number) {
  return useQuery({
    queryKey: queryKeys.dashboard(year, month),
    queryFn: () => apiRequest<DashboardSummary>(`/api/dashboard/summary${toSearchParams({ year, month })}`),
  })
}

export function useCreateBook() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (payload: BookPayload) => apiRequest<Book>('/api/books', { method: 'POST', body: payload }),
    onSuccess: (book) => {
      void queryClient.invalidateQueries({ queryKey: ['books'] })
      void queryClient.invalidateQueries({ queryKey: ['dashboard'] })
      void queryClient.setQueryData(queryKeys.book(book.id), book)
    },
  })
}

export function useUpdateBook(bookId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (payload: BookUpdatePayload) => apiRequest<Book>(`/api/books/${bookId}`, { method: 'PATCH', body: payload }),
    onSuccess: (book) => {
      void queryClient.invalidateQueries({ queryKey: ['books'] })
      void queryClient.invalidateQueries({ queryKey: ['dashboard'] })
      void queryClient.setQueryData(queryKeys.book(book.id), book)
    },
  })
}

export function useDeleteBook() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (bookId: number) => apiRequest<void>(`/api/books/${bookId}`, { method: 'DELETE' }),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ['books'] })
      void queryClient.invalidateQueries({ queryKey: ['dashboard'] })
      void queryClient.invalidateQueries({ queryKey: ['quoteSearch'] })
    },
  })
}

export function useCreateQuote(bookId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (payload: QuotePayload) => apiRequest<QuoteCard>(`/api/books/${bookId}/quotes`, { method: 'POST', body: payload }),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: queryKeys.bookQuotes(bookId) })
      void queryClient.invalidateQueries({ queryKey: ['quoteSearch'] })
      void queryClient.invalidateQueries({ queryKey: ['tags'] })
      void queryClient.invalidateQueries({ queryKey: ['dashboard'] })
    },
  })
}

export function useUpdateQuote(bookId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ quoteId, payload }: { quoteId: number; payload: QuotePayload }) =>
      apiRequest<QuoteCard>(`/api/quotes/${quoteId}`, { method: 'PATCH', body: payload }),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: queryKeys.bookQuotes(bookId) })
      void queryClient.invalidateQueries({ queryKey: ['quoteSearch'] })
      void queryClient.invalidateQueries({ queryKey: ['tags'] })
      void queryClient.invalidateQueries({ queryKey: ['dashboard'] })
    },
  })
}

export function useDeleteQuote(bookId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (quoteId: number) => apiRequest<void>(`/api/quotes/${quoteId}`, { method: 'DELETE' }),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: queryKeys.bookQuotes(bookId) })
      void queryClient.invalidateQueries({ queryKey: ['quoteSearch'] })
      void queryClient.invalidateQueries({ queryKey: ['dashboard'] })
    },
  })
}

export function useUpsertReview(bookId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (payload: ReviewPayload) => apiRequest<Review>(`/api/books/${bookId}/review`, { method: 'PUT', body: payload }),
    onSuccess: (review) => {
      void queryClient.setQueryData(queryKeys.bookReview(bookId), review)
      void queryClient.invalidateQueries({ queryKey: ['dashboard'] })
    },
  })
}

function toSearchParams(params: Record<string, string | number | undefined>) {
  const searchParams = new URLSearchParams()
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== '') {
      searchParams.set(key, String(value))
    }
  })

  const query = searchParams.toString()
  return query ? `?${query}` : ''
}

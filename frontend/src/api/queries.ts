import { useQuery } from '@tanstack/react-query'
import { apiRequest } from './client'
import type { Book, BookStatus, DashboardSummary, QuoteCard, Tag } from '../types/api'

export const queryKeys = {
  books: (status?: BookStatus) => ['books', status ?? 'ALL'] as const,
  book: (bookId: number) => ['book', bookId] as const,
  bookQuotes: (bookId: number) => ['bookQuotes', bookId] as const,
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

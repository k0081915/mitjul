export type BookStatus = 'READING' | 'COMPLETED' | 'PAUSED'

export type Book = {
  id: number
  title: string
  author: string
  isbn?: string | null
  coverImageUrl?: string | null
  presetCoverKey?: string | null
  status: BookStatus
  startedAt: string
  finishedAt?: string | null
  createdAt?: string
  updatedAt?: string
}

export type QuoteCard = {
  id: number
  bookId: number
  bookTitle: string
  page?: number | null
  content: string
  memo?: string | null
  tags: string[]
  createdAt: string
  updatedAt: string
}

export type Review = {
  id: number
  bookId: number
  bookTitle: string
  rating: number
  oneLiner: string
  body: string
  createdAt: string
  updatedAt: string
}

export type RecentReview = Pick<Review, 'id' | 'bookId' | 'bookTitle' | 'rating' | 'oneLiner' | 'createdAt'>

export type Tag = {
  id: number
  name: string
}

export type DashboardSummary = {
  year: number
  month: number
  activeBookCount: number
  completedBookCount: number
  quoteCount: number
  reviewCount: number
  recentQuotes: QuoteCard[]
  recentReviews: RecentReview[]
  yearlyQuotes: QuoteCard[]
}

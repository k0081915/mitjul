export type BookStatus = 'READING' | 'COMPLETED' | 'PAUSED'
export type CoverStyle = 'CLASSIC' | 'MODERN' | 'MINIMAL'
export type OrderStatus = 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'CANCELLED'

export type Book = {
  id: number
  title: string
  author: string
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

export type BookPayload = {
  title: string
  author: string
  coverImageUrl?: string | null
  presetCoverKey?: string | null
  status?: BookStatus | null
  startedAt: string
  finishedAt?: string | null
}

export type BookUpdatePayload = Partial<BookPayload>

export type QuotePayload = {
  page?: number | null
  content: string
  memo?: string | null
  tagNames: string[]
}

export type ReviewPayload = {
  rating: number
  oneLiner: string
  body: string
  markCompleted?: boolean
}

export type OrderPayload = {
  periodStart: string
  periodEnd: string
  coverStyle: CoverStyle
  ownerName: string
}

export type OrderBookPreview = {
  bookId: number
  title: string
  author: string
  quoteCount: number
  quotes: OrderQuoteSnapshot[]
}

export type OrderQuoteSnapshot = {
  id: number
  page?: number | null
  content: string
  memo?: string | null
  tags: string[]
  createdAt: string
}

export type OrderPreview = {
  periodStart: string
  periodEnd: string
  coverStyle: CoverStyle
  ownerName: string
  bookCount: number
  quoteCount: number
  books: OrderBookPreview[]
}

export type OrderItem = {
  bookId: number
  title: string
  author: string
  quoteCount: number
  displayOrder: number
}

export type OrderSummary = {
  id: number
  orderNumber: string
  periodStart: string
  periodEnd: string
  coverStyle: CoverStyle
  ownerName: string
  status: OrderStatus
  bookCount: number
  quoteCount: number
  createdAt: string
  updatedAt: string
}

export type Order = OrderSummary & {
  items: OrderItem[]
  snapshotJson: string
}

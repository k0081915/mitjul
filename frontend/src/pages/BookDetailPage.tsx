import { type FormEvent, type MouseEvent, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { ApiError } from '../api/client'
import {
  useBook,
  useBookQuotes,
  useBookReview,
  useCreateQuote,
  useDeleteQuote,
  useTags,
  useUpdateQuote,
  useUpsertReview,
} from '../api/queries'
import type { QuoteCard } from '../types/api'

const defaultQuoteForm = {
  page: '',
  content: '',
  memo: '',
}

export function BookDetailPage() {
  const bookId = Number(useParams().bookId)
  const { data: book, isLoading: isBookLoading } = useBook(bookId)
  const { data: quotes } = useBookQuotes(bookId)
  const { data: review } = useBookReview(bookId)
  const { data: tags } = useTags()
  const createQuote = useCreateQuote(bookId)
  const updateQuote = useUpdateQuote(bookId)
  const deleteQuote = useDeleteQuote(bookId)
  const upsertReview = useUpsertReview(bookId)
  const [quoteForm, setQuoteForm] = useState(defaultQuoteForm)
  const [editingQuote, setEditingQuote] = useState<QuoteCard | null>(null)
  const [deleteQuoteTarget, setDeleteQuoteTarget] = useState<QuoteCard | null>(null)
  const [selectedTags, setSelectedTags] = useState<string[]>([])
  const [rating, setRating] = useState<number | null>(null)
  const [isReviewEditing, setIsReviewEditing] = useState(false)
  const [quoteMessage, setQuoteMessage] = useState<string | null>(null)
  const [reviewMessage, setReviewMessage] = useState<string | null>(null)
  const selectedRating = rating ?? review?.rating ?? 4

  const handleQuoteSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    setQuoteMessage(null)
    const payload = toQuotePayload(quoteForm, selectedTags)
    if (!payload) {
      setQuoteMessage('페이지는 1 이상의 숫자로 입력해 주세요.')
      return
    }

    try {
      if (editingQuote) {
        await updateQuote.mutateAsync({ quoteId: editingQuote.id, payload })
        setQuoteMessage('인용문을 수정했습니다.')
      } else {
        await createQuote.mutateAsync(payload)
        setQuoteMessage('인용문을 추가했습니다.')
      }
      setEditingQuote(null)
      setQuoteForm(defaultQuoteForm)
      setSelectedTags([])
    } catch (error) {
      setQuoteMessage(getErrorMessage(error))
    }
  }

  const startQuoteEdit = (event: MouseEvent<HTMLButtonElement>, quote: QuoteCard) => {
    event.stopPropagation()
    setEditingQuote(quote)
    setQuoteForm({
      page: quote.page ? String(quote.page) : '',
      content: quote.content,
      memo: quote.memo ?? '',
    })
    setSelectedTags(quote.tags)
    setQuoteMessage(null)
  }

  const cancelQuoteEdit = () => {
    setEditingQuote(null)
    setQuoteForm(defaultQuoteForm)
    setSelectedTags([])
    setQuoteMessage(null)
  }

  const handleDeleteQuote = async (quote: QuoteCard) => {
    setDeleteQuoteTarget(quote)
  }

  const confirmDeleteQuote = async () => {
    if (!deleteQuoteTarget) return
    try {
      await deleteQuote.mutateAsync(deleteQuoteTarget.id)
      if (editingQuote?.id === deleteQuoteTarget.id) cancelQuoteEdit()
      setDeleteQuoteTarget(null)
    } catch (error) {
      setQuoteMessage(getErrorMessage(error))
    }
  }

  const handleReviewSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    setReviewMessage(null)
    const formData = new FormData(event.currentTarget)

    try {
      await upsertReview.mutateAsync({
        rating: selectedRating,
        oneLiner: String(formData.get('oneLiner') ?? '').trim(),
        body: String(formData.get('body') ?? '').trim(),
        markCompleted: formData.get('markCompleted') === 'on',
      })
      setReviewMessage('리뷰를 저장했습니다.')
      setIsReviewEditing(false)
    } catch (error) {
      setReviewMessage(getErrorMessage(error))
    }
  }

  return (
    <section className="page-shell">
      <div className="section section-dark hero-section">
        <div className="hero-composed">
          <div>
            <p className="page-kicker">Book Detail</p>
            <h1 className="page-title">{isBookLoading ? '책을 불러오는 중' : book?.title ?? '책 상세'}</h1>
            {book && <p className="page-description">{book.author} · {book.status}</p>}
            <div className="hero-actions">
              <Link className="cta-link secondary" to="/books">
                책장으로 돌아가기
              </Link>
            </div>
          </div>
          <div className="hero-object" aria-hidden="true">
            <div className="book-stack">
              <div className="book-slab tertiary">
                <strong>Memo</strong>
                <span>책에서 나온 생각</span>
              </div>
              <div className="book-slab primary">
                <strong>Quote</strong>
                <span>책 안의 밑줄</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="section section-light">
        <div className="content-wide detail-layout">
          <div className="section-stack">
            <div className="panel panel-subtle">
              <h2>인용문</h2>
              <p>{quotes ? `${quotes.length}개의 인용문이 있습니다.` : '인용문을 불러오는 중입니다.'}</p>
            </div>

            <form className="form-panel" onSubmit={handleQuoteSubmit}>
              <div className="form-heading">
                <div>
                  <h2>{editingQuote ? '인용문 수정' : '인용문 추가'}</h2>
                  <p>책에서 남기고 싶은 문장과 메모를 기록합니다.</p>
                </div>
                {editingQuote && (
                  <button className="button-secondary" type="button" onClick={cancelQuoteEdit}>
                    취소
                  </button>
                )}
              </div>
              <div className="form-grid form-grid-compact">
                <label className="field">
                  <span>페이지</span>
                  <input
                    min={1}
                    type="number"
                    value={quoteForm.page}
                    onChange={(event) => setQuoteForm((prev) => ({ ...prev, page: event.target.value }))}
                  />
                </label>
                <div className="field">
                  <span>태그</span>
                  <div className="tag-selector">
                    {tags?.map((tag) => (
                      <button
                        className={selectedTags.includes(tag.name) ? 'tag-choice selected' : 'tag-choice'}
                        key={tag.id}
                        type="button"
                        onClick={() => setSelectedTags((prev) => toggleTag(prev, tag.name))}
                      >
                        {tag.name}
                      </button>
                    ))}
                  </div>
                </div>
              </div>
              <label className="field">
                <span>인용문</span>
                <textarea
                  required
                  rows={4}
                  value={quoteForm.content}
                  onChange={(event) => setQuoteForm((prev) => ({ ...prev, content: event.target.value }))}
                />
              </label>
              <label className="field">
                <span>메모</span>
                <textarea
                  rows={3}
                  value={quoteForm.memo}
                  onChange={(event) => setQuoteForm((prev) => ({ ...prev, memo: event.target.value }))}
                />
              </label>
              <div className="form-actions">
                <button className="cta-link" type="submit" disabled={createQuote.isPending || updateQuote.isPending}>
                  {editingQuote ? '수정하기' : '추가하기'}
                </button>
                {quoteMessage && <p className="form-message">{quoteMessage}</p>}
              </div>
            </form>

            {quotes && quotes.length > 0 && (
              <div className="card-list">
                {quotes.map((quote) => (
                  <article className="list-card" key={quote.id}>
                    <p className="quote-text">{quote.content}</p>
                    {quote.memo && <p>{quote.memo}</p>}
                    <div className="tag-row">
                      {quote.tags.map((tag) => (
                        <span className="badge" key={tag}>
                          {tag}
                        </span>
                      ))}
                    </div>
                    <div className="card-actions">
                      <button className="button-secondary" type="button" onClick={(event) => startQuoteEdit(event, quote)}>
                        수정
                      </button>
                      <button className="button-danger" type="button" onClick={() => handleDeleteQuote(quote)}>
                        삭제
                      </button>
                    </div>
                  </article>
                ))}
              </div>
            )}
          </div>

          <aside className="section-stack">
            {review && !isReviewEditing ? (
              <article className="form-panel review-display">
                <div className="form-heading">
                  <div>
                    <h2>리뷰</h2>
                    <p>{review.bookTitle} · {review.rating}점</p>
                  </div>
                  <button
                    className="button-secondary"
                    type="button"
                    onClick={() => {
                      setRating(review.rating)
                      setReviewMessage(null)
                      setIsReviewEditing(true)
                    }}
                  >
                    리뷰 수정
                  </button>
                </div>
                <div className="star-rating readonly" aria-label={`별점 ${review.rating}점`}>
                  {[1, 2, 3, 4, 5].map((score) => (
                    <span className={score <= review.rating ? 'star-button selected' : 'star-button'} key={score}>
                      ★
                    </span>
                  ))}
                </div>
                <h3>{review.oneLiner}</h3>
                <p>{review.body}</p>
              </article>
            ) : (
            <form className="form-panel" key={review?.id ?? 'new-review'} onSubmit={handleReviewSubmit}>
              <div className="form-heading">
                <div>
                  <h2>리뷰</h2>
                  <p>{review ? '기존 리뷰를 수정합니다.' : '완독 표시와 함께 리뷰를 작성합니다.'}</p>
                </div>
              </div>
              <div className="field">
                <span>별점</span>
                <div className="star-rating" role="radiogroup" aria-label="별점">
                  {[1, 2, 3, 4, 5].map((score) => (
                    <button
                      aria-checked={selectedRating === score}
                      className={score <= selectedRating ? 'star-button selected' : 'star-button'}
                      key={score}
                      role="radio"
                      type="button"
                      onClick={() => setRating(score)}
                    >
                      ★
                    </button>
                  ))}
                  <span>{selectedRating}점</span>
                </div>
              </div>
              <label className="completion-check">
                <input defaultChecked={book?.status === 'COMPLETED'} name="markCompleted" type="checkbox" />
                <span>이 책을 완독으로 표시</span>
              </label>
              <label className="field">
                <span>한줄평</span>
                <input
                  required
                  maxLength={200}
                  name="oneLiner"
                  defaultValue={review?.oneLiner ?? ''}
                />
              </label>
              <label className="field">
                <span>본문</span>
                <textarea
                  required
                  rows={6}
                  name="body"
                  defaultValue={review?.body ?? ''}
                />
              </label>
              <div className="form-actions">
                <button className="cta-link" type="submit" disabled={upsertReview.isPending}>
                  {review ? '수정하기' : '저장하기'}
                </button>
                {review && (
                  <button className="button-secondary" type="button" onClick={() => setIsReviewEditing(false)}>
                    취소
                  </button>
                )}
                {reviewMessage && <p className="form-message">{reviewMessage}</p>}
              </div>
            </form>
            )}
          </aside>
        </div>
      </div>
      {deleteQuoteTarget && (
        <div className="modal-backdrop" role="presentation">
          <div aria-labelledby="delete-quote-title" aria-modal="true" className="confirm-dialog" role="dialog">
            <h2 id="delete-quote-title">인용문을 삭제할까요?</h2>
            <p>삭제한 인용문과 메모는 되돌릴 수 없습니다.</p>
            <div className="modal-actions">
              <button className="button-secondary" type="button" onClick={() => setDeleteQuoteTarget(null)}>
                취소
              </button>
              <button className="button-danger filled" disabled={deleteQuote.isPending} type="button" onClick={confirmDeleteQuote}>
                삭제하기
              </button>
            </div>
          </div>
        </div>
      )}
    </section>
  )
}

function toQuotePayload(form: typeof defaultQuoteForm, tagNames: string[]) {
  const page = parsePage(form.page)
  if (page === undefined) {
    return null
  }

  return {
    page,
    content: form.content.trim(),
    memo: nullable(form.memo),
    tagNames,
  }
}

function parsePage(value: string) {
  const trimmed = value.trim()
  if (trimmed === '') {
    return null
  }

  const page = Number(trimmed)
  return Number.isInteger(page) && page > 0 ? page : undefined
}

function toggleTag(tags: string[], tagName: string) {
  if (tags.includes(tagName)) {
    return tags.filter((tag) => tag !== tagName)
  }
  if (tags.length >= 8) {
    return tags
  }
  return [...tags, tagName]
}

function nullable(value: string) {
  const trimmed = value.trim()
  return trimmed === '' ? null : trimmed
}

function getErrorMessage(error: unknown) {
  if (error instanceof ApiError) {
    return error.message
  }
  return '요청 처리 중 오류가 발생했습니다.'
}

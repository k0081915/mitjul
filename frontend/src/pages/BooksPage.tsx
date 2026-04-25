import { type FormEvent, type MouseEvent, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { ApiError } from '../api/client'
import { useBooks, useCreateBook, useDeleteBook, useUpdateBook } from '../api/queries'
import type { Book, BookPayload, BookStatus } from '../types/api'

const defaultBookForm = {
  title: '',
  author: '',
  status: 'READING' as BookStatus,
  startedAt: new Date().toISOString().slice(0, 10),
  finishedAt: '',
}

export function BooksPage() {
  const { data: books, isLoading, isError } = useBooks()
  const createBook = useCreateBook()
  const deleteBook = useDeleteBook()
  const navigate = useNavigate()
  const [bookForm, setBookForm] = useState(defaultBookForm)
  const [editingBook, setEditingBook] = useState<Book | null>(null)
  const [deleteTarget, setDeleteTarget] = useState<Book | null>(null)
  const [formMessage, setFormMessage] = useState<string | null>(null)

  const updateBook = useUpdateBook(editingBook?.id ?? 0)

  const handleCreateBook = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    setFormMessage(null)

    try {
      await createBook.mutateAsync(toBookPayload(bookForm))
      setBookForm(defaultBookForm)
      setFormMessage('책을 추가했습니다.')
    } catch (error) {
      setFormMessage(getErrorMessage(error))
    }
  }

  const handleUpdateBook = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    if (!editingBook) return
    setFormMessage(null)

    try {
      await updateBook.mutateAsync(toBookPayload(bookForm))
      setEditingBook(null)
      setBookForm(defaultBookForm)
      setFormMessage('책 정보를 수정했습니다.')
    } catch (error) {
      setFormMessage(getErrorMessage(error))
    }
  }

  const startEdit = (event: MouseEvent<HTMLButtonElement>, book: Book) => {
    event.preventDefault()
    event.stopPropagation()
    setEditingBook(book)
    setBookForm({
      title: book.title,
      author: book.author,
      status: book.status,
      startedAt: book.startedAt,
      finishedAt: book.finishedAt ?? '',
    })
    setFormMessage(null)
    requestAnimationFrame(() => {
      document.getElementById('book-form')?.scrollIntoView({ behavior: 'smooth', block: 'center' })
    })
  }

  const cancelEdit = () => {
    setEditingBook(null)
    setBookForm(defaultBookForm)
    setFormMessage(null)
  }

  const handleDeleteBook = async (event: MouseEvent<HTMLButtonElement>, book: Book) => {
    event.preventDefault()
    event.stopPropagation()
    setDeleteTarget(book)
  }

  const confirmDeleteBook = async () => {
    if (!deleteTarget) return
    try {
      await deleteBook.mutateAsync(deleteTarget.id)
      if (editingBook?.id === deleteTarget.id) cancelEdit()
      setDeleteTarget(null)
    } catch (error) {
      setFormMessage(getErrorMessage(error))
    }
  }

  return (
    <section className="page-shell">
      <div className="section section-dark hero-section">
        <div className="hero-composed">
          <div>
            <p className="page-kicker">Library</p>
            <h1 className="page-title">내 책장.</h1>
            <p className="page-description">읽는 중인 책과 완독한 책을 한 곳에서 확인합니다.</p>
          </div>
          <div className="hero-object" aria-hidden="true">
            <div className="book-stack">
              <div className="book-slab secondary">
                <strong>Reading</strong>
                <span>진행 중인 책</span>
              </div>
              <div className="book-slab primary">
                <strong>Done</strong>
                <span>완독한 책</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="section section-white">
        <div className="content-wide">
          <div className="section-stack">
            <form className="form-panel" id="book-form" onSubmit={editingBook ? handleUpdateBook : handleCreateBook}>
              <div className="form-heading">
                <div>
                  <h2>{editingBook ? '책 정보 수정' : '책 추가'}</h2>
                  <p>{editingBook ? '선택한 책의 기록 상태를 업데이트합니다.' : '새로 읽기 시작한 책을 책장에 추가합니다.'}</p>
                </div>
                {editingBook && (
                  <button className="button-secondary" type="button" onClick={cancelEdit}>
                    취소
                  </button>
                )}
              </div>
              <div className="form-grid">
                <label className="field">
                  <span>제목</span>
                  <input
                    required
                    maxLength={200}
                    value={bookForm.title}
                    onChange={(event) => setBookForm((prev) => ({ ...prev, title: event.target.value }))}
                  />
                </label>
                <label className="field">
                  <span>저자</span>
                  <input
                    required
                    maxLength={100}
                    value={bookForm.author}
                    onChange={(event) => setBookForm((prev) => ({ ...prev, author: event.target.value }))}
                  />
                </label>
                <label className="field">
                  <span>상태</span>
                  <select
                    value={bookForm.status}
                    onChange={(event) => setBookForm((prev) => ({ ...prev, status: event.target.value as BookStatus }))}
                  >
                    <option value="READING">읽는 중</option>
                    <option value="COMPLETED">완독</option>
                    <option value="PAUSED">잠시 멈춤</option>
                  </select>
                </label>
                <label className="field">
                  <span>시작일</span>
                  <input
                    required
                    type="date"
                    value={bookForm.startedAt}
                    onChange={(event) => setBookForm((prev) => ({ ...prev, startedAt: event.target.value }))}
                  />
                </label>
                <label className="field">
                  <span>완독일</span>
                  <input
                    type="date"
                    value={bookForm.finishedAt}
                    onChange={(event) => setBookForm((prev) => ({ ...prev, finishedAt: event.target.value }))}
                  />
                </label>
              </div>
              <div className="form-actions">
                <button className="cta-link" type="submit" disabled={createBook.isPending || updateBook.isPending}>
                  {editingBook ? '수정하기' : '추가하기'}
                </button>
                {formMessage && <p className="form-message">{formMessage}</p>}
              </div>
            </form>
          </div>

          {isLoading && <div className="empty-state">책을 불러오는 중입니다.</div>}
          {isError && <div className="empty-state">책 목록을 불러오지 못했습니다.</div>}
          {books && (
            <div className="grid grid-3">
              {books.map((book) => (
                <article
                  className="book-tile"
                  key={book.id}
                  onClick={() => navigate(`/books/${book.id}`)}
                  onKeyDown={(event) => {
                    if (event.key === 'Enter') navigate(`/books/${book.id}`)
                  }}
                  role="button"
                  tabIndex={0}
                >
                  <div className="book-tile-cover" aria-hidden="true" />
                  <div>
                    <p className="page-kicker">{book.status}</p>
                    <h2>{book.title}</h2>
                    <p>{book.author}</p>
                    <p>
                      {book.startedAt}
                      {book.finishedAt ? ` - ${book.finishedAt}` : ''}
                    </p>
                    <div className="card-actions">
                      <button className="button-secondary" type="button" onClick={(event) => startEdit(event, book)}>
                        수정
                      </button>
                      <button className="button-danger" type="button" onClick={(event) => handleDeleteBook(event, book)}>
                        삭제
                      </button>
                    </div>
                  </div>
                </article>
              ))}
            </div>
          )}
        </div>
      </div>
      {deleteTarget && (
        <div className="modal-backdrop" role="presentation">
          <div aria-labelledby="delete-book-title" aria-modal="true" className="confirm-dialog" role="dialog">
            <h2 id="delete-book-title">책을 삭제할까요?</h2>
            <p>'{deleteTarget.title}'과 연결된 인용문, 리뷰도 함께 삭제됩니다.</p>
            <div className="modal-actions">
              <button className="button-secondary" type="button" onClick={() => setDeleteTarget(null)}>
                취소
              </button>
              <button className="button-danger filled" disabled={deleteBook.isPending} type="button" onClick={confirmDeleteBook}>
                삭제하기
              </button>
            </div>
          </div>
        </div>
      )}
    </section>
  )
}

function toBookPayload(form: typeof defaultBookForm): BookPayload {
  return {
    title: form.title.trim(),
    author: form.author.trim(),
    status: form.status,
    startedAt: form.startedAt,
    finishedAt: nullable(form.finishedAt),
  }
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

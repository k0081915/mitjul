import { Link } from 'react-router-dom'
import { useBooks } from '../api/queries'

export function BooksPage() {
  const { data: books, isLoading, isError } = useBooks()

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
          {isLoading && <div className="empty-state">책을 불러오는 중입니다.</div>}
          {isError && <div className="empty-state">책 목록을 불러오지 못했습니다.</div>}
          {books && (
            <div className="grid grid-3">
              {books.map((book) => (
                <Link className="book-tile" key={book.id} to={`/books/${book.id}`}>
                  <div className="book-tile-cover" aria-hidden="true" />
                  <div>
                    <p className="page-kicker">{book.status}</p>
                    <h2>{book.title}</h2>
                    <p>{book.author}</p>
                    <p>
                      {book.startedAt}
                      {book.finishedAt ? ` - ${book.finishedAt}` : ''}
                    </p>
                    <span className="text-link">자세히 보기</span>
                  </div>
                </Link>
              ))}
            </div>
          )}
        </div>
      </div>
    </section>
  )
}

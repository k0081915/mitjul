import { Link } from 'react-router-dom'
import { useBooks } from '../api/queries'

export function BooksPage() {
  const { data: books, isLoading, isError } = useBooks()

  return (
    <section className="page-shell">
      <div className="section section-light hero-section">
        <div className="content-narrow">
          <p className="page-kicker">Library</p>
          <h1 className="page-title">내 책장.</h1>
          <p className="page-description">읽는 중인 책과 완독한 책을 한 곳에서 확인합니다.</p>
        </div>
      </div>

      <div className="section section-white">
        <div className="content-wide">
          {isLoading && <div className="empty-state">책을 불러오는 중입니다.</div>}
          {isError && <div className="empty-state">책 목록을 불러오지 못했습니다.</div>}
          {books && (
            <div className="grid grid-3">
              {books.map((book) => (
                <article className="panel" key={book.id}>
                  <p className="page-kicker">{book.status}</p>
                  <h2>{book.title}</h2>
                  <p>{book.author}</p>
                  <p>
                    {book.startedAt}
                    {book.finishedAt ? ` - ${book.finishedAt}` : ''}
                  </p>
                  <Link className="text-link" to={`/books/${book.id}`}>
                    자세히 보기
                  </Link>
                </article>
              ))}
            </div>
          )}
        </div>
      </div>
    </section>
  )
}

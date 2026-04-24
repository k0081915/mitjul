import { Link } from 'react-router-dom'
import { useBooks } from '../api/queries'

export function BooksPage() {
  const { data: books, isLoading, isError } = useBooks()

  return (
    <section className="page-shell">
      <div>
        <p className="page-kicker">Library</p>
        <h1 className="page-title">내 책장</h1>
        <p className="page-description">책 목록 API를 연결한 뒤 카드형 책장 화면으로 확장합니다.</p>
      </div>
      <div className="panel">
        <h2>책 목록</h2>
        {isLoading && <p className="status-text">책을 불러오는 중입니다.</p>}
        {isError && <p className="status-text">책 목록을 불러오지 못했습니다.</p>}
        {books && (
          <p>
            총 {books.length}권 · 첫 번째 책:{' '}
            {books[0] ? <Link to={`/books/${books[0].id}`}>{books[0].title}</Link> : '없음'}
          </p>
        )}
      </div>
    </section>
  )
}

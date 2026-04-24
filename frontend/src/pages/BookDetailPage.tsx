import { useParams } from 'react-router-dom'
import { useBook, useBookQuotes } from '../api/queries'

export function BookDetailPage() {
  const bookId = Number(useParams().bookId)
  const { data: book, isLoading: isBookLoading } = useBook(bookId)
  const { data: quotes } = useBookQuotes(bookId)

  return (
    <section className="page-shell">
      <div>
        <p className="page-kicker">Book Detail</p>
        <h1 className="page-title">{isBookLoading ? '책을 불러오는 중' : book?.title ?? '책 상세'}</h1>
        {book && <p className="page-description">{book.author} · {book.status}</p>}
      </div>
      <div className="panel">
        <h2>인용문</h2>
        <p>{quotes ? `${quotes.length}개의 인용문이 있습니다.` : '인용문을 불러오는 중입니다.'}</p>
      </div>
    </section>
  )
}

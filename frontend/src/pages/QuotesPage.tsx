import { useQuoteSearch, useTags } from '../api/queries'

export function QuotesPage() {
  const { data: quotes, isLoading } = useQuoteSearch({})
  const { data: tags } = useTags()

  return (
    <section className="page-shell">
      <div className="section section-dark hero-section">
        <div className="hero-composed">
          <div>
            <p className="page-kicker">Quotes</p>
            <h1 className="page-title">문장 모음.</h1>
            <p className="page-description">책마다 흩어진 밑줄과 생각을 하나의 목록으로 다시 봅니다.</p>
          </div>
          <div className="hero-object" aria-hidden="true">
            <div className="quote-sheet">
              <p>“좋은 문장은 다시 읽을 때 다른 얼굴을 보여준다.”</p>
              <span>Mitjul archive</span>
            </div>
          </div>
        </div>
      </div>

      <div className="section section-white">
        <div className="content-wide bento-grid">
          <div className="bento-feature">
            <h2 className="section-title">전체 인용문</h2>
            {isLoading && <div className="empty-state">문장을 불러오는 중입니다.</div>}
            {quotes && (
              <div className="card-list">
                {quotes.slice(0, 8).map((quote) => (
                  <article className="list-card" key={quote.id}>
                    <p className="quote-text">{quote.content}</p>
                    <p className="list-card-meta">{quote.bookTitle}</p>
                  </article>
                ))}
              </div>
            )}
          </div>
          <aside className="panel panel-subtle bento-side">
            <h2>감정 태그</h2>
            <p>사용 가능한 태그 {tags?.length ?? 0}개</p>
            {tags && (
              <div className="tag-row">
                {tags.map((tag) => (
                  <span className="badge" key={tag.id}>
                    {tag.name}
                  </span>
                ))}
              </div>
            )}
          </aside>
        </div>
      </div>
    </section>
  )
}

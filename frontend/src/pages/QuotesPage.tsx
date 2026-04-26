import { type FormEvent, useState } from 'react'
import { useQuoteSearch, useTags } from '../api/queries'

export function QuotesPage() {
  const [keywordInput, setKeywordInput] = useState('')
  const [tagInput, setTagInput] = useState('')
  const [searchParams, setSearchParams] = useState({})
  const { data: quotes, isLoading } = useQuoteSearch(searchParams)
  const { data: tags } = useTags()

  const handleSearch = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    setSearchParams({
      q: keywordInput.trim() || undefined,
      tag: tagInput || undefined,
    })
  }

  const clearSearch = () => {
    setKeywordInput('')
    setTagInput('')
    setSearchParams({})
  }

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
            <form className="filter-bar" onSubmit={handleSearch}>
              <label className="field field-inline">
                <span>검색어</span>
                <input
                  value={keywordInput}
                  onChange={(event) => setKeywordInput(event.target.value)}
                  placeholder="문장이나 메모 검색"
                />
              </label>
              <label className="field field-inline">
                <span>태그</span>
                <select value={tagInput} onChange={(event) => setTagInput(event.target.value)}>
                  <option value="">전체</option>
                  {tags?.map((tag) => (
                    <option key={tag.id} value={tag.name}>
                      {tag.name}
                    </option>
                  ))}
                </select>
              </label>
              <button className="cta-link" type="submit">
                검색
              </button>
              <button className="button-secondary" type="button" onClick={clearSearch}>
                초기화
              </button>
            </form>
            {isLoading && <div className="empty-state">문장을 불러오는 중입니다.</div>}
            {quotes && (
              <div className="card-list">
                {quotes.map((quote) => (
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

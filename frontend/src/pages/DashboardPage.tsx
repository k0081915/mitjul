import { Link } from 'react-router-dom'
import { useDashboardSummary } from '../api/queries'

export function DashboardPage() {
  const { data, isLoading, isError } = useDashboardSummary(2026, 4)

  return (
    <section className="page-shell">
      <div className="section section-dark hero-section">
        <div className="hero-composed">
          <div>
            <p className="page-kicker">Mitjul</p>
            <h1 className="page-title">이번 달 독서 회고.</h1>
            <p className="page-description">읽은 책과 남긴 문장을 조용히 모아 다시 볼 수 있게 정리합니다.</p>
            <div className="hero-actions">
              <Link className="cta-link" to="/books">
                책장 보기
              </Link>
              <Link className="cta-link secondary" to="/quotes">
                문장 모음
              </Link>
            </div>
          </div>
          <div className="hero-object" aria-hidden="true">
            <div className="book-stack">
              <div className="book-slab secondary">
                <strong>Read</strong>
                <span>책장에 쌓이는 기록</span>
              </div>
              <div className="book-slab tertiary">
                <strong>Quote</strong>
                <span>다시 꺼내는 문장</span>
              </div>
              <div className="book-slab primary">
                <strong>Review</strong>
                <span>이번 달의 회고</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="section section-light">
        <div className="content-wide">
          {isLoading && <div className="empty-state">대시보드 데이터를 불러오는 중입니다.</div>}
          {isError && <div className="empty-state">대시보드 데이터를 불러오지 못했습니다.</div>}
          {data && (
            <div className="grid grid-3">
              <div className="panel panel-immersive">
                <h2>활성 책</h2>
                <p>{data.year}년 {data.month}월 기준</p>
                <span className="metric-value">{data.activeBookCount}</span>
              </div>
              <div className="panel">
                <h2>남긴 문장</h2>
                <p>이번 달에 저장한 인용문</p>
                <span className="metric-value">{data.quoteCount}</span>
              </div>
              <div className="panel">
                <h2>작성한 리뷰</h2>
                <p>완독 후 남긴 기록</p>
                <span className="metric-value">{data.reviewCount}</span>
              </div>
            </div>
          )}
        </div>
      </div>

      {data && (
        <div className="section section-white">
          <div className="content-wide grid grid-2">
            <div>
              <h2 className="section-title">최근 문장</h2>
              <div className="card-list">
                {data.recentQuotes.slice(0, 3).map((quote) => (
                  <article className="list-card" key={quote.id}>
                    <p className="quote-text">{quote.content}</p>
                    <p className="list-card-meta">{quote.bookTitle}</p>
                  </article>
                ))}
              </div>
            </div>
            <div>
              <h2 className="section-title">최근 리뷰</h2>
              <div className="card-list">
                {data.recentReviews.slice(0, 3).map((review) => (
                  <article className="list-card" key={review.id}>
                    <h3 className="list-card-title">{review.oneLiner}</h3>
                    <p className="list-card-meta">{review.bookTitle} · {review.rating}점</p>
                  </article>
                ))}
              </div>
            </div>
          </div>
        </div>
      )}
    </section>
  )
}

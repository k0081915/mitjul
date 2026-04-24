import { useQuoteSearch, useTags } from '../api/queries'

export function QuotesPage() {
  const { data: quotes, isLoading } = useQuoteSearch({})
  const { data: tags } = useTags()

  return (
    <section className="page-shell">
      <div>
        <p className="page-kicker">Quotes</p>
        <h1 className="page-title">문장 모음</h1>
        <p className="page-description">전체 인용문 검색과 감정 태그 필터 화면으로 확장합니다.</p>
      </div>
      <div className="panel">
        <h2>검색 준비</h2>
        {isLoading && <p className="status-text">문장을 불러오는 중입니다.</p>}
        {quotes && <p>검색 대상 인용문 {quotes.length}개</p>}
        {tags && <p>사용 가능한 태그 {tags.length}개</p>}
      </div>
    </section>
  )
}

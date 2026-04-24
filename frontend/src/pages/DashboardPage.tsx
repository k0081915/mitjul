import { useDashboardSummary } from '../api/queries'

export function DashboardPage() {
  const { data, isLoading, isError } = useDashboardSummary(2026, 4)

  return (
    <section className="page-shell">
      <div>
        <p className="page-kicker">Monthly Retrospective</p>
        <h1 className="page-title">이번 달 독서 회고</h1>
        <p className="page-description">
          저장된 책, 인용문, 리뷰 데이터를 바탕으로 홈 화면을 구성합니다.
        </p>
      </div>
      <div className="panel">
        <h2>API 연결 상태</h2>
        {isLoading && <p className="status-text">대시보드 데이터를 불러오는 중입니다.</p>}
        {isError && <p className="status-text">대시보드 데이터를 불러오지 못했습니다.</p>}
        {data && (
          <p>
            {data.year}년 {data.month}월 · 활성 책 {data.activeBookCount}권 · 인용문 {data.quoteCount}개
          </p>
        )}
      </div>
    </section>
  )
}

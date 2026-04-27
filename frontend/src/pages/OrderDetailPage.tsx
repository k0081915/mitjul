import { Link, useParams } from 'react-router-dom'
import { useOrder } from '../api/queries'
import type { OrderStatus } from '../types/api'

export function OrderDetailPage() {
  const orderId = Number(useParams().orderId)
  const { data: order, isLoading, isError } = useOrder(orderId)

  return (
    <section className="page-shell">
      <div className="section section-dark hero-section">
        <div className="hero-composed">
          <div>
            <p className="page-kicker">Archive Detail</p>
            <h1 className="page-title">{order?.orderNumber ?? '기록책 상세.'}</h1>
            {order && (
              <p className="page-description">
                {order.periodStart}부터 {order.periodEnd}까지의 독서 기록을 묶었습니다.
              </p>
            )}
            <div className="hero-actions">
              <Link className="cta-link secondary" to="/orders">
                주문 내역으로 돌아가기
              </Link>
            </div>
          </div>
          <div className="hero-object" aria-hidden="true">
            <div className="book-stack">
              <div className="book-slab secondary">
                <strong>Snapshot</strong>
                <span>주문 당시의 기록</span>
              </div>
              <div className="book-slab primary">
                <strong>Archive</strong>
                <span>책으로 묶은 문장</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="section section-light">
        <div className="content-wide">
          {isLoading && <div className="empty-state">주문 상세를 불러오는 중입니다.</div>}
          {isError && <div className="empty-state">주문 상세를 불러오지 못했습니다.</div>}
          {order && (
            <div className="detail-layout">
              <div className="section-stack">
                <div className="panel panel-immersive">
                  <div className="form-heading">
                    <div>
                      <h2>{order.orderNumber}</h2>
                      <p>{order.ownerName}님의 기록책</p>
                    </div>
                    <span className={`badge status-${order.status.toLowerCase()}`}>{getStatusLabel(order.status)}</span>
                  </div>
                  <div className="order-preview-metrics">
                    <div>
                      <span>책</span>
                      <strong>{order.bookCount}</strong>
                    </div>
                    <div>
                      <span>문장</span>
                      <strong>{order.quoteCount}</strong>
                    </div>
                  </div>
                </div>

                <div className="card-list">
                  {order.items.map((item) => (
                    <article className="list-card order-detail-item" key={`${item.displayOrder}-${item.bookId}`}>
                      <span className="page-kicker">{String(item.displayOrder).padStart(2, '0')}</span>
                      <h2>{item.title}</h2>
                      <p>{item.author}</p>
                      <p className="list-card-meta">수록 문장 {item.quoteCount}개</p>
                    </article>
                  ))}
                </div>
              </div>

              <aside className="section-stack">
                <div className="panel">
                  <h2>주문 정보</h2>
                  <dl className="order-meta-list">
                    <div>
                      <dt>기간</dt>
                      <dd>{order.periodStart} - {order.periodEnd}</dd>
                    </div>
                    <div>
                      <dt>표지</dt>
                      <dd>{getCoverStyleLabel(order.coverStyle)}</dd>
                    </div>
                    <div>
                      <dt>생성일</dt>
                      <dd>{formatDateTime(order.createdAt)}</dd>
                    </div>
                    <div>
                      <dt>수정일</dt>
                      <dd>{formatDateTime(order.updatedAt)}</dd>
                    </div>
                  </dl>
                </div>
              </aside>
            </div>
          )}
        </div>
      </div>
    </section>
  )
}

function getStatusLabel(status: OrderStatus) {
  const labels: Record<OrderStatus, string> = {
    PENDING: '대기',
    PROCESSING: '제작 중',
    COMPLETED: '완료',
    CANCELLED: '취소',
  }
  return labels[status]
}

function getCoverStyleLabel(style: string) {
  const labels: Record<string, string> = {
    CLASSIC: 'Classic',
    MODERN: 'Modern',
    MINIMAL: 'Minimal',
  }
  return labels[style] ?? style
}

function formatDateTime(value: string) {
  return new Intl.DateTimeFormat('ko-KR', {
    dateStyle: 'medium',
    timeStyle: 'short',
  }).format(new Date(value))
}

import { type FormEvent, useMemo, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { ApiError } from '../api/client'
import { useCreateOrder, useOrderPreview, useOrders } from '../api/queries'
import type { CoverStyle, OrderPayload, OrderPreview } from '../types/api'

const coverStyleOptions: Array<{ value: CoverStyle; label: string; description: string }> = [
  { value: 'CLASSIC', label: 'Classic', description: '차분한 하드커버' },
  { value: 'MODERN', label: 'Modern', description: '선명한 기록집' },
  { value: 'MINIMAL', label: 'Minimal', description: '단정한 아카이브' },
]

const defaultOrderForm = {
  periodStart: firstDayOfMonth(),
  periodEnd: today(),
  coverStyle: 'MINIMAL' as CoverStyle,
  ownerName: 'Mitjul',
}

export function OrdersPage() {
  const navigate = useNavigate()
  const { data: orders, isLoading: isOrdersLoading, isError: isOrdersError } = useOrders()
  const previewOrder = useOrderPreview()
  const createOrder = useCreateOrder()
  const [orderForm, setOrderForm] = useState(defaultOrderForm)
  const [preview, setPreview] = useState<OrderPreview | null>(null)
  const [previewPayload, setPreviewPayload] = useState<OrderPayload | null>(null)
  const [formMessage, setFormMessage] = useState<string | null>(null)

  const currentPayload = useMemo(() => toOrderPayload(orderForm), [orderForm])
  const validationMessage = validateOrderPayload(currentPayload)
  const isPreviewCurrent = previewPayload !== null && payloadKey(previewPayload) === payloadKey(currentPayload)
  const canCreateOrder = preview !== null && isPreviewCurrent && preview.bookCount > 0 && !validationMessage

  const handlePreview = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    setFormMessage(null)

    if (validationMessage) {
      setFormMessage(validationMessage)
      return
    }

    try {
      const result = await previewOrder.mutateAsync(currentPayload)
      setPreview(result)
      setPreviewPayload(currentPayload)
      setFormMessage(result.bookCount > 0 ? '미리보기를 생성했습니다.' : '선택한 기간에 포함할 인용문이 없습니다.')
    } catch (error) {
      setPreview(null)
      setPreviewPayload(null)
      setFormMessage(getErrorMessage(error))
    }
  }

  const handleCreateOrder = async () => {
    setFormMessage(null)
    if (!canCreateOrder) {
      setFormMessage('주문 전 최신 미리보기를 먼저 생성해 주세요.')
      return
    }

    try {
      const order = await createOrder.mutateAsync(currentPayload)
      setFormMessage('기록책 주문을 생성했습니다.')
      void navigate(`/orders/${order.id}`)
    } catch (error) {
      setFormMessage(getErrorMessage(error))
    }
  }

  return (
    <section className="page-shell">
      <div className="section section-dark hero-section">
        <div className="hero-composed">
          <div>
            <p className="page-kicker">Archive Book</p>
            <h1 className="page-title">기록책 주문.</h1>
            <p className="page-description">기간을 고르면 남긴 문장을 책 단위로 묶어 기록책 제작 요청을 만듭니다.</p>
          </div>
          <div className="hero-object" aria-hidden="true">
            <div className="book-stack">
              <div className="book-slab secondary">
                <strong>Period</strong>
                <span>기록을 묶는 시간</span>
              </div>
              <div className="book-slab primary">
                <strong>Book</strong>
                <span>문장으로 만든 기록책</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="section section-light">
        <div className="content-wide order-layout">
          <form className="form-panel" onSubmit={handlePreview}>
            <div className="form-heading">
              <div>
                <h2>기록책 만들기</h2>
                <p>주문을 생성하기 전에 포함될 책과 문장 수를 먼저 확인합니다.</p>
              </div>
            </div>

            <div className="form-grid">
              <label className="field">
                <span>시작일</span>
                <input
                  required
                  type="date"
                  value={orderForm.periodStart}
                  onChange={(event) => setOrderForm((prev) => ({ ...prev, periodStart: event.target.value }))}
                />
              </label>
              <label className="field">
                <span>종료일</span>
                <input
                  required
                  type="date"
                  value={orderForm.periodEnd}
                  onChange={(event) => setOrderForm((prev) => ({ ...prev, periodEnd: event.target.value }))}
                />
              </label>
              <label className="field">
                <span>소유자 이름</span>
                <input
                  required
                  maxLength={50}
                  value={orderForm.ownerName}
                  onChange={(event) => setOrderForm((prev) => ({ ...prev, ownerName: event.target.value }))}
                />
              </label>
            </div>

            <div className="field">
              <span>표지 스타일</span>
              <div className="cover-style-grid">
                {coverStyleOptions.map((option) => (
                  <button
                    className={orderForm.coverStyle === option.value ? 'cover-style-option selected' : 'cover-style-option'}
                    key={option.value}
                    type="button"
                    onClick={() => setOrderForm((prev) => ({ ...prev, coverStyle: option.value }))}
                  >
                    <span className={`cover-style-swatch cover-style-${option.value.toLowerCase()}`} />
                    <strong>{option.label}</strong>
                    <small>{option.description}</small>
                  </button>
                ))}
              </div>
            </div>

            <div className="form-actions">
              <button className="cta-link" type="submit" disabled={previewOrder.isPending}>
                {previewOrder.isPending ? '생성 중' : '미리보기'}
              </button>
              <button
                className="button-secondary"
                type="button"
                disabled={!canCreateOrder || createOrder.isPending}
                onClick={handleCreateOrder}
              >
                {createOrder.isPending ? '주문 중' : '주문 만들기'}
              </button>
              {formMessage && <p className="form-message">{formMessage}</p>}
            </div>
          </form>

          <PreviewPanel preview={preview} isPreviewCurrent={isPreviewCurrent} />
        </div>
      </div>

      <div className="section section-white">
        <div className="content-wide">
          <h2 className="section-title">주문 내역</h2>
          {isOrdersLoading && <div className="empty-state">주문 내역을 불러오는 중입니다.</div>}
          {isOrdersError && <div className="empty-state">주문 내역을 불러오지 못했습니다.</div>}
          {orders && orders.length === 0 && <div className="empty-state">아직 만든 기록책 주문이 없습니다.</div>}
          {orders && orders.length > 0 && (
            <div className="grid grid-3 order-card-grid">
              {orders.map((order) => (
                <Link className="list-card order-summary-card" key={order.id} to={`/orders/${order.id}`}>
                  <span className={`badge status-${order.status.toLowerCase()}`}>{getStatusLabel(order.status)}</span>
                  <h3 className="list-card-title">{order.orderNumber}</h3>
                  <p>{order.periodStart} - {order.periodEnd}</p>
                  <p className="list-card-meta">
                    {order.ownerName} · 책 {order.bookCount}권 · 문장 {order.quoteCount}개
                  </p>
                </Link>
              ))}
            </div>
          )}
        </div>
      </div>
    </section>
  )
}

function PreviewPanel({ preview, isPreviewCurrent }: { preview: OrderPreview | null; isPreviewCurrent: boolean }) {
  if (!preview) {
    return (
      <aside className="panel order-preview-panel">
        <h2>미리보기</h2>
        <p>기간과 표지를 선택한 뒤 미리보기를 생성하면 포함될 기록을 확인할 수 있습니다.</p>
      </aside>
    )
  }

  return (
    <aside className="panel order-preview-panel">
      <div className="form-heading">
        <div>
          <h2>미리보기</h2>
          <p>{preview.periodStart} - {preview.periodEnd}</p>
        </div>
        {!isPreviewCurrent && <span className="badge">다시 생성 필요</span>}
      </div>
      <div className="order-preview-metrics">
        <div>
          <span>책</span>
          <strong>{preview.bookCount}</strong>
        </div>
        <div>
          <span>문장</span>
          <strong>{preview.quoteCount}</strong>
        </div>
      </div>
      {preview.books.length === 0 ? (
        <p>선택한 기간에 포함할 인용문이 없습니다.</p>
      ) : (
        <div className="card-list">
          {preview.books.map((book) => (
            <article className="order-book-row" key={book.bookId}>
              <div>
                <strong>{book.title}</strong>
                <span>{book.author}</span>
              </div>
              <span>{book.quoteCount}개</span>
            </article>
          ))}
        </div>
      )}
    </aside>
  )
}

function toOrderPayload(form: typeof defaultOrderForm): OrderPayload {
  return {
    periodStart: form.periodStart,
    periodEnd: form.periodEnd,
    coverStyle: form.coverStyle,
    ownerName: form.ownerName.trim(),
  }
}

function validateOrderPayload(payload: OrderPayload) {
  if (!payload.periodStart || !payload.periodEnd) {
    return '주문 기간을 입력해 주세요.'
  }
  if (payload.periodEnd < payload.periodStart) {
    return '종료일은 시작일보다 빠를 수 없습니다.'
  }
  if (payload.ownerName.length === 0) {
    return '소유자 이름을 입력해 주세요.'
  }
  if (payload.ownerName.length > 50) {
    return '소유자 이름은 50자 이하로 입력해 주세요.'
  }
  return null
}

function payloadKey(payload: OrderPayload) {
  return `${payload.periodStart}|${payload.periodEnd}|${payload.coverStyle}|${payload.ownerName}`
}

function today() {
  return toLocalDateString(new Date())
}

function firstDayOfMonth() {
  const date = new Date()
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-01`
}

function toLocalDateString(date: Date) {
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

function getStatusLabel(status: string) {
  const labels: Record<string, string> = {
    PENDING: '대기',
    PROCESSING: '제작 중',
    COMPLETED: '완료',
    CANCELLED: '취소',
  }
  return labels[status] ?? status
}

function getErrorMessage(error: unknown) {
  if (error instanceof ApiError) {
    return error.message
  }
  return '요청 처리 중 오류가 발생했습니다.'
}

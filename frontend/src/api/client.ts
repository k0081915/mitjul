export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'

export class ApiError extends Error {
  status: number
  code?: string

  constructor(status: number, message: string, code?: string) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.code = code
  }
}

type RequestOptions = Omit<RequestInit, 'body'> & {
  body?: unknown
}

export async function apiRequest<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const headers = new Headers(options.headers)
  headers.set('Accept', 'application/json')

  let body: BodyInit | undefined
  if (options.body !== undefined) {
    headers.set('Content-Type', 'application/json')
    body = JSON.stringify(options.body)
  }

  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers,
    body,
  })

  if (!response.ok) {
    const errorBody = await readErrorBody(response)
    throw new ApiError(response.status, errorBody.message, errorBody.code)
  }

  if (response.status === 204) {
    return undefined as T
  }

  return response.json() as Promise<T>
}

async function readErrorBody(response: Response): Promise<{ code?: string; message: string }> {
  try {
    const body = (await response.json()) as { code?: string; message?: string }
    return {
      code: body.code,
      message: body.message ?? '요청 처리 중 오류가 발생했습니다.',
    }
  } catch {
    return { message: '요청 처리 중 오류가 발생했습니다.' }
  }
}

import { createBrowserRouter } from 'react-router-dom'
import App from '../App'
import { BookDetailPage } from '../pages/BookDetailPage'
import { BooksPage } from '../pages/BooksPage'
import { DashboardPage } from '../pages/DashboardPage'
import { OrderDetailPage } from '../pages/OrderDetailPage'
import { OrdersPage } from '../pages/OrdersPage'
import { QuotesPage } from '../pages/QuotesPage'

export const router = createBrowserRouter([
  {
    path: '/',
    element: <App />,
    children: [
      { index: true, element: <DashboardPage /> },
      { path: 'books', element: <BooksPage /> },
      { path: 'books/:bookId', element: <BookDetailPage /> },
      { path: 'quotes', element: <QuotesPage /> },
      { path: 'orders', element: <OrdersPage /> },
      { path: 'orders/:orderId', element: <OrderDetailPage /> },
    ],
  },
])

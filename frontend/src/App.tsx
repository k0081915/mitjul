import { NavLink, Outlet } from 'react-router-dom'

const navItems = [
  { to: '/', label: '회고 홈', end: true },
  { to: '/books', label: '내 책장' },
  { to: '/quotes', label: '문장 모음' },
  { to: '/orders', label: '기록책 주문' },
]

function App() {
  return (
    <div className="app-shell">
      <header className="app-header">
        <div className="nav-inner">
          <NavLink to="/" className="brand" aria-label="밑줄 홈">
            밑줄
          </NavLink>
          <nav className="app-nav" aria-label="주요 메뉴">
            {navItems.map((item) => (
              <NavLink
                key={item.to}
                to={item.to}
                end={item.end}
                className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}
              >
                {item.label}
              </NavLink>
            ))}
          </nav>
        </div>
      </header>
      <main className="app-main">
        <Outlet />
      </main>
    </div>
  )
}

export default App

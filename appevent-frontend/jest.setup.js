import '@testing-library/jest-dom'

// Mock useRouter
jest.mock('next/navigation', () => ({
  useRouter() {
    return {
      push: jest.fn(),
      pathname: '',
    }
  }
}))

// Mock shadcn/ui komponenter
jest.mock('@/components/ui/card', () => ({
  Card: ({ children, className }) => <div className={className}>{children}</div>,
  CardHeader: ({ children }) => <div>{children}</div>,
  CardTitle: ({ children }) => <div>{children}</div>,
  CardContent: ({ children, className }) => <div className={className}>{children}</div>,
  CardFooter: ({ children }) => <div>{children}</div>,
}))

jest.mock('@/components/ui/button', () => ({
  Button: ({ children, className, variant }) => (
    <button className={className}>{children}</button>
  ),
}))

jest.mock('lucide-react', () => ({
  Calendar: () => <div data-testid="calendar-icon" />,
  MapPin: () => <div data-testid="map-pin-icon" />,
  Users: () => <div data-testid="users-icon" />,
}))
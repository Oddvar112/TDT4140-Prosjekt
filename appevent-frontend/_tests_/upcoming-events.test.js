import { render, screen } from '@testing-library/react'
import { UpcomingEventPage } from '../app/upcoming-events/page'
import { UpcomingEventsFetch } from '../api/events/getUpcomingEvents'

// Mock API-kallet
jest.mock('../api/events/getUpcomingEvents', () => ({
  UpcomingEventsFetch: jest.fn()
}))

// Mock next/link
jest.mock('next/link', () => ({
  __esModule: true,
  default: ({ children, href }) => (
    <a href={href}>{children}</a>
  )
}))

// Test data
const mockEvents = [
  {
    id: 1,
    title: 'Test Event 1',
    dateTime: '2024-02-14T18:00:00',
    location: 'Oslo',
    participants: ['user1', 'user2']
  },
  {
    id: 2,
    title: 'Test Event 2',
    dateTime: '2024-02-15T19:00:00',
    location: 'Bergen',
    participants: ['user3']
  }
]

describe('UpcomingEventPage', () => {
  // Test for vellykket lasting av events
  it('renders events successfully', () => {
    // Setup mock return value
    UpcomingEventsFetch.mockReturnValue({
      events: mockEvents,
      isLoading: false,
      error: null
    })

    render(<UpcomingEventPage />)

    // Sjekk header
    expect(screen.getByText('Utforsk unike arrangementer')).toBeInTheDocument()
    expect(screen.getByText('Meld på arrangmenter eller opprett din egen')).toBeInTheDocument()

    // Sjekk at events blir rendret
    mockEvents.forEach(event => {
      expect(screen.getByText(event.title)).toBeInTheDocument()
      expect(screen.getByText(event.location)).toBeInTheDocument()
      // Sjekk at antall deltakere vises
      expect(screen.getByText(event.participants.length.toString())).toBeInTheDocument()
    })

    // Sjekk at alle "Se Detaljer" knapper finnes
    const detailButtons = screen.getAllByText('Se Detaljer')
    expect(detailButtons).toHaveLength(mockEvents.length)
  })

  // Test for loading state
  it('shows loading state', () => {
    UpcomingEventsFetch.mockReturnValue({
      events: [],
      isLoading: true,
      error: null
    })

    render(<UpcomingEventPage />)
    expect(screen.getByText('Loading events...')).toBeInTheDocument()
  })

  // Test for error state
  it('shows error message when fetch fails', () => {
    const errorMessage = 'Failed to fetch events'
    UpcomingEventsFetch.mockReturnValue({
      events: [],
      isLoading: false,
      error: errorMessage
    })

    render(<UpcomingEventPage />)
    expect(screen.getByText(`Error: ${errorMessage}`)).toBeInTheDocument()
  })

  // Test at events rendres med riktig format
  it('renders event dates in correct format', () => {
    UpcomingEventsFetch.mockReturnValue({
      events: mockEvents,
      isLoading: false,
      error: null
    })

    render(<UpcomingEventPage />)
    
    mockEvents.forEach(event => {
      const formattedDate = new Date(event.dateTime).toLocaleString("en-GB", {
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
      })
      expect(screen.getByText(formattedDate)).toBeInTheDocument()
    })
  })

  // Test at lenker har riktige URLs
  it('renders correct event detail links', () => {
    UpcomingEventsFetch.mockReturnValue({
      events: mockEvents,
      isLoading: false,
      error: null
    })

    render(<UpcomingEventPage />)
    
    mockEvents.forEach(event => {
      const link = screen.getAllByRole('link').find(
        link => link.getAttribute('href') === `/events/${event.id}`
      )
      expect(link).toBeInTheDocument()
    })
  })
})
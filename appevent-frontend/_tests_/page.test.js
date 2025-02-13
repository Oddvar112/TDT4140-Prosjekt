import { render, screen } from '@testing-library/react'
import Home from '../app/page'

jest.mock('next/image', () => ({
    __esModule: true,
    default: (props) => {
      const { priority, ...rest } = props;
      return <img {...rest} />
    },
  }))

describe('Home', () => {
  it('renders main elements', () => {
    render(<Home />)
    
    // Test for hovedoverskriften
    expect(screen.getByAltText('Next.js logo')).toBeInTheDocument()
    
    // Test for "Get started" teksten
    expect(screen.getByText(/Get started by editing/i)).toBeInTheDocument()
    
    // Test for lenker
    expect(screen.getByText('Deploy now')).toBeInTheDocument()
    expect(screen.getByText('Read our docs')).toBeInTheDocument()
    
    // Test footer lenker
    expect(screen.getByText('Learn')).toBeInTheDocument()
    expect(screen.getByText('Examples')).toBeInTheDocument()
    expect(screen.getByText('Go to nextjs.org →')).toBeInTheDocument()
  })

  it('has correct link attributes', () => {
    render(<Home />)
    
    // Test at "Deploy now" lenken har riktig href og target
    const deployLink = screen.getByText('Deploy now').closest('a')
    expect(deployLink).toHaveAttribute('href', 'https://vercel.com/new?utm_source=create-next-app&utm_medium=appdir-template-tw&utm_campaign=create-next-app')
    expect(deployLink).toHaveAttribute('target', '_blank')
    
    // Test at "Read our docs" lenken har riktig href
    const docsLink = screen.getByText('Read our docs').closest('a')
    expect(docsLink).toHaveAttribute('href', 'https://nextjs.org/docs?utm_source=create-next-app&utm_medium=appdir-template-tw&utm_campaign=create-next-app')
  })
})
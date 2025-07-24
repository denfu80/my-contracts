# Personal Document Management Service - Requirements

## Project Overview
A self-hosted document management service that automatically processes contracts, bills, and forwarded emails using LLM technology to organize, analyze, and provide insights on personal documents.

## Core Features

### Document Processing
- **Upload Interface**: Web-based upload for contracts, bills, and documents
- **Email Integration**: Forward emails to the service for automatic processing
- **LLM Processing**: Automatic content analysis and data extraction
- **Smart Categorization**: AI-powered document classification and tagging

### Organization & Search
- **Automatic Labeling**: Intelligent tagging system for easy document retrieval
- **Search Functionality**: Full-text search with label-based filtering
- **Document Relationships**: Link related documents (e.g., contract renewals)

### Dashboard & Insights
- **Quick Insights**: Overview of important financial and contractual information
- **Important Dates**: Alerts for contract termination dates, payment due dates
- **Cost Analysis**: Track and visualize contract costs and expenses
- **Activity Feed**: Recent document processing and updates

### Data Management
- **Local Storage**: All data stored locally in containerized database
- **Data Privacy**: No external data sharing, complete local control
- **Backup & Export**: Data export capabilities for portability

## Technical Requirements

### Architecture
- **Backend**: Node.js/TypeScript API server
- **Frontend**: Modern reactive UI (React/Vue with TypeScript) - **Mobile-First Design**
- **Database**: PostgreSQL in Docker container
- **LLM Integration**: Local or API-based language model for document processing
- **Containerization**: Full Docker setup for easy deployment

### Deployment
- **Self-Hosted**: Personal network deployment
- **Docker Compose**: Multi-container setup
- **Environment Configuration**: Easy setup and configuration
- **Network Security**: Secure local network access

## User Interface Requirements

### Dashboard
- Clean, modern design with responsive layout
- Quick access to recent documents and insights
- Visual charts for cost analysis and document statistics
- Important date notifications and alerts

### Document Management
- Drag-and-drop upload interface
- Document preview and metadata editing
- Bulk operations for multiple documents
- Advanced search with filters and sorting

### Mobile Interface Requirements
- **Responsive Design**: Single UI optimized for both desktop and mobile
- **Touch-First Interface**: Mobile-optimized controls and navigation
- **Android Share Integration**: Native share API support
  - Accept documents shared from other apps
  - File picker integration with device storage
  - Camera integration for document capture
- **Progressive Web App (PWA)**: Offline capabilities and app-like experience
- **Performance**: Fast loading and smooth interactions on mobile devices

### Document Format Support
- **PDF Processing**: Full PDF support with text extraction
  - PDF parsing and OCR for scanned documents
  - Metadata extraction from PDF properties
  - Multi-page document handling
- **Image Formats**: JPG, PNG, HEIC support with OCR
- **Text Formats**: TXT, DOC, DOCX document processing
- **Email Formats**: EML, MSG file support for forwarded emails

## Data Processing Pipeline

### Document Ingestion
1. File upload or email forwarding
2. File format detection and conversion
3. Text extraction and OCR if needed
4. Initial metadata capture

### LLM Analysis
1. Content analysis and summarization
2. Entity extraction (dates, amounts, parties)
3. Document type classification
4. Smart tagging and labeling

### Storage & Indexing
1. Structured data storage in database
2. Full-text indexing for search
3. File storage with backup
4. Relationship mapping between documents

## Security & Privacy

### Data Protection
- All processing happens locally
- No external data transmission
- Encrypted storage options
- Access control and authentication

### Network Security
- HTTPS/TLS for web interface
- Network isolation options
- Regular security updates

## Future Enhancements (Phase 2)
- Mobile app for document capture
- Advanced analytics and reporting
- Integration with calendar systems
- Automated bill payment reminders
- Contract negotiation insights
- Multi-user support for family accounts

## Settings & Configuration

### LLM Provider Configuration
The system will initially focus on two primary LLM providers, with extensible architecture for future additions.

#### Primary LLM Providers (Phase 1)
- **Google Gemini Flash 1.5**: Free tier implementation
  - **Free Tier Limits**: 15 requests/minute, 1M tokens/minute, 1,500 requests/day
  - **Token Pricing**: Input/output tokens free of charge
  - **Context Caching**: Free up to 1M tokens storage/hour
  - **Note**: ~4 characters = 1 token for Gemini models
- **Ollama**: Local model hosting with Docker network integration
  - Model selection interface (llama3, mistral, codellama, phi, etc.)
  - Resource allocation settings (RAM, GPU usage)
  - Container-to-container network configuration
  - Model management (download, update, remove)

#### LLM Service Architecture
- **Strategy/Proxy Pattern**: Abstract LLM service layer for provider switching
- **Unified Interface**: Common API for all LLM providers
- **Fallback Mechanism**: Automatic switching between providers on failure
- **Rate Limit Management**: Built-in request throttling and queuing
- **Token Usage Tracking**: Monitor consumption across providers

#### Configuration Interface
- **Setup Wizard**: First-time configuration with provider selection
- **Settings Panel**: Runtime provider switching and key management
- **Environment Variables**: Docker-compatible configuration
- **Validation**: API key testing and model availability checks
- **Fallback Options**: Multiple provider configuration with automatic failover

#### Security Considerations
- **API Key Storage**: Encrypted storage of sensitive credentials
- **Network Isolation**: Secure communication between containers
- **Local Model Security**: Proper access controls for Ollama endpoints
- **Configuration Backup**: Secure backup of settings (excluding keys)

## Success Criteria
- Automatic processing of 95%+ of uploaded documents
- Sub-second search response times
- Intuitive UI requiring minimal training
- Zero data loss with reliable backup system
- Easy deployment and maintenance

## Discussion Points
1. **LLM Choice**: Local model (privacy) vs API-based (accuracy/features)
2. **Email Integration**: IMAP polling vs dedicated email address
3. **File Storage**: Local filesystem vs object storage
4. **Authentication**: Simple password vs OAuth integration
5. **Backup Strategy**: Automated vs manual backup options
6. **Performance**: Expected document volume and processing speed requirements
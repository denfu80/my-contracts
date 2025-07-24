# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a **Personal Document Management Service** - a self-hosted solution for automatically processing contracts, bills, and forwarded emails using LLM technology. The project is currently in the planning/requirements phase.

## Planned Architecture

### Technology Stack
- **Backend**: Node.js/TypeScript API server
- **Frontend**: Modern reactive UI (React/Vue with TypeScript)
- **Database**: PostgreSQL in Docker container
- **LLM Integration**: Local or API-based language model for document processing
- **Deployment**: Docker Compose multi-container setup

### Core Components (Planned)
- **Document Processing Pipeline**: File upload → LLM analysis → structured storage
- **Email Integration**: Forward emails for automatic processing
- **Dashboard**: Document insights, cost analysis, important date tracking
- **Search System**: Full-text search with AI-powered categorization
- **Data Management**: Local storage with backup/export capabilities

## Development Status

**Current State**: Requirements definition phase - only `REQUIREMENTS.md` exists
**Next Steps**: Project initialization and architecture setup needed

## Key Features to Implement

### Document Processing
- Web-based upload interface with drag-and-drop
- Email forwarding integration (IMAP or dedicated address)
- OCR and text extraction capabilities
- LLM-powered content analysis and entity extraction

### Organization & Search
- AI-powered document classification and tagging
- Full-text search with label-based filtering
- Document relationship mapping
- Advanced search with filters and sorting

### Dashboard & Analytics
- Financial and contractual insights overview
- Contract termination and payment due date alerts
- Cost analysis and visualization
- Activity feed for recent processing

## Security & Privacy Requirements

- **Local-First Approach**: All data processing happens locally
- **No External Data Transmission**: Complete local control
- **Containerized Deployment**: Docker setup for security isolation
- **HTTPS/TLS**: Secure web interface
- **Access Control**: Authentication system

## Data Flow (Planned)

1. **Document Ingestion**: Upload/email → format detection → text extraction
2. **LLM Analysis**: Content analysis → entity extraction → classification → tagging
3. **Storage & Indexing**: Structured DB storage → full-text indexing → file storage
4. **Retrieval**: Search interface → filtered results → document preview

## Development Considerations

- Prioritize data privacy and local processing
- Design for easy Docker deployment and configuration
- Plan for high document processing accuracy (95%+ target)
- Ensure sub-second search response times
- Build intuitive UI requiring minimal user training
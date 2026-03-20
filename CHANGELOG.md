# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2026-03-20

### Added
- **Project Structure**: Complete Java 17 + Maven project setup
- **Web Interface**: JSF 2.3 + PrimeFaces 12.0.0 components
- **Database Layer**: Hibernate 5.6.15 with JPA 2.2
- **Entities**: Client, Vehicle, ServiceOrder, Service models
- **DAO Layer**: GenericDAO pattern with specific implementations
- **Service Layer**: Business logic with validation and orchestration
- **Controllers**: JSF ManagedBeans for web interface
- **Testing**: JUnit 5 + Mockito for unit tests
- **Database**: MySQL 8.0.33 with migration scripts
- **Documentation**: Comprehensive docs in `docs/` folder
- **Deployment**: Automated scripts for local and production
- **Logging**: Log4j 2 configuration
- **Configuration**: Application properties and persistence setup

### Changed
- Initial project structure established

### Technical Details
- **Java Version**: 17
- **Build Tool**: Maven 3.8.1+
- **Web Framework**: JSF 2.3 + PrimeFaces 12.0.0
- **ORM**: Hibernate 5.6.15
- **Database**: MySQL 8.0.33
- **Testing**: JUnit 5.10.2 + Mockito 5.8.0
- **Logging**: Log4j 2.23.1

### Files Added
- 34 files including source code, configuration, documentation, and scripts
- 4,219 lines of code and documentation

### Breaking Changes
- Initial project structure - no previous versions to break compatibility with

---

## Types of changes
- `Added` for new features
- `Changed` for changes in existing functionality
- `Deprecated` for soon-to-be removed features
- `Removed` for now removed features
- `Fixed` for any bug fixes
- `Security` in case of vulnerabilities

## Version Format
This project uses [Semantic Versioning](https://semver.org/):
- **MAJOR.MINOR.PATCH** (e.g., 1.0.0)
- **MAJOR**: Breaking changes
- **MINOR**: New features, backward compatible
- **PATCH**: Bug fixes, backward compatible

## Commit Convention
This project follows [Conventional Commits](https://conventionalcommits.org/):
- `feat:` - New features
- `fix:` - Bug fixes
- `docs:` - Documentation changes
- `style:` - Code style changes
- `refactor:` - Code refactoring
- `test:` - Testing changes
- `chore:` - Maintenance tasks

Example: `feat: add user authentication system`

---

## Future Releases
- [ ] v1.1.0 - Add user management and authentication
- [ ] v1.2.0 - Implement order workflow
- [ ] v2.0.0 - Migrate to Jakarta EE 9+

---

[1.0.0]: https://github.com/your-repo/osmanager/releases/tag/v1.0.0

# Amphora-BeautySalon Booking System CHANGELOG

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/).

This file should describe all the changes, additions and fixes that went into a specific release.
Start with the git commit logs, quote Jira ticket numbers where possible, and ensure it reads as proper English instead of shorthand or dev-sp33k

## [3.0.1] - 2018-05-22
### Added
 - Added new data attributes on the Booking model to capture more information around the points of booking creation and cancellation
 
### Changed
 - Changed the areas in code impacted by the new booking model changes. Mostly in the booking services layer, reporting services layer and cashup services 
 
## [3.0.0] - 2018-03-31
### Added
 - Added invoice sending feature from reporting view
 - Added quick email feature from bookings report feature
 - Added new email template for bookings invoicing
 - Added new sales cash up feature
 - Added new crud services for Sales and Daily Cashup
 - Added more utility classes to understand where users login from
 - Added new permissions model to user model
 - Added new seeder to migrate all users and employees to have permissions
 
### Changed
 - Updated entire UI to bootstrap 4
 - Moved old web api resources to new Rest package
 - Modified authentication filter to do better validation based on user permissions 
 - Converted ui to pure html and JS
 
### Removed 
 - Removed all jsps
 - Removed usage of servlets as controllers

## [2.1.2] - 2018-02-23
### Added 
 - Added new api endpoints for mobile application

### Changed
 - Updated the Authentication Filter to return the correct error details model
 
## [2.1.1] - 2018-01-09
### Added 
 - Added new schedule view options to make the bookings more visible
 
### Fixed 
 - Fixed issues with join cancellation queue modal not showing up for clients
 - Fixed issue with ordering of days open in scheduling view
 - Fixed issue with clients being able to see bulk booking notification feature

## [2.1.0] - 2017-12-28
### Added 
 - Added new feature for booking cancellation queue
 - Added new feature for bulk notifications for an admin/employee

### Changed 
 - Changed emailing service to use new template framework and designs

## [2.0.1] - 2017-11-28
### Added 
 - Added new price list image

### Changed 
 - Changed the cancellation feature to allow administrators/employees to cancel any booking at any time.

### Fixed 
 - Fixed alphabetical issues for loading client names in schedule view
 - Nullpointer exception when creating a new employee.

## [2.0.0] - 2017-10-18
### Added 
 - Added new price list image
 - Added new feedback service which was previously done in scala now to java

### Changed 
 - Changed implementation of email service for marketing campaigns to use BCC and not TO
 - Cleaned up Entities to use lombok instead of boiler plate code 
 
### Deprecated 
 - for once-stable features removed in upcoming releases.

### Removed 
 - Completely stripped out scala from project as majority of it was not used.

### Fixed 
 - Fixed issues on analytics service to ensure correct results are achieved

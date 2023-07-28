## Release 2.3.12

* Do not retry job trigger when Job is already/still running. Edison-jobs is returning 409, when a job is running. 
  * See
    documentation in edison-jobs https://github.com/otto-de/edison-microservice/blob/b503629b09b522e06deea998b44472d20f3f6bc3/edison-jobs/src/main/java/de/otto/edison/jobs/controller/JobsController.java#L97)

## Release 2.3.11

* Log trigger failed after no more retries with jobType and as warning.

## Release 2.3.10

* fix jquery links in html templates

## Release 2.3.6

* fix bootstrap and jquery links in html templates

## Release 2.3.5

* Dependency updates, Spring Boot 2.5.0 and recent libs

## Release 2.3.4

* Update bootstrap to 4.6.0 and fix links in html templates
* Dependency updates

## Release 2.3.3

* Dependency updates

## Release 2.3.2

* Use gradle 6.6.1
* Update some dependencies

## Release 2.3.1

* Use Edison 2.3.1, Spring Boot 2.3.x and recent libs

## Snapshot 2.1.0

* Add LDAP-Authorization: New properties edison.jobtrigger.security.basicAuthUser and
  edison.jobtrigger.security.basicAuthPasswd
* Add possibility to set values for version.properties via environment variables instead of git information

## Release 2.0.0

* Updated to edison-microservice 1.0.0
* Use ConfigurationProperties
* Bugfix: configure scheduler pool size
* Make size of scheduler pool configurable through edison.jobtrigger.scheduler.poolsize
* Bugfix: discover for notTriggerableJobDefintions
* Make size of jobresult history configurable through edison.jobtrigger.jobresults.max

## Release 1.1.0

* Updated to edison-microservice 0.48.0

## Release 1.0.0

* Initial Release

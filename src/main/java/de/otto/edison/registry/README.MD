# Edison Registry

A library to support registration of external microservices in different environments.

Clients of a registry are expected to update their registration on a regular base, for example every 10 Minutes.
This way, the registry does not need to persist registered information.

# API

## /environments

The collection of all known environments.

### Mediatype:

application/vnd.otto.edison.links+json

### Methods:

GET

### Example:

```json
{
    "groups" : ["TeamOne", "TeamTwo", "TeamThree"],
    "links" : [ {
        "rel" : "item",
        "href" : "http://example.com/edison-registry/environments/develop",
        "title" : "Develop environment"
    } , {
        "rel" : "item",
        "href" : "http://example.com/edison-registry/environments/prelive",
        "title" : "Prelive environment"
    } , {
        "rel" : "item",
        "href" : "http://example.com/edison-registry/environments/live",
        "title" : "Live environment"
    } , {
        "rel" : "self",
        "href" : "http://example.com/edison-registry/environments",
        "title" : "Standard 'self' link-relation type."
    }]
}
```

## /environments/develop

Information and links about a single environment, possibly containing a number of registered services

### Mediatype:

application/vnd.otto.edison.links+json

### Methods:

GET

Environments are implicitly created or deleted, when services are registered, or when the last service
of an environment is deleted from the registry.

### Query Parameter

Optional parameter 'groups' to get only items for one or more selected groups of services.

### Example:

GET http://example.com/edison-registry/environments/develop?groups=TeamOne

```json
{
    "groups" : ["TeamOne", "TeamTwo"],
    "links" : [ {
        "rel" : "item",
        "href" : "http://example.com/edison-registry/environments/develop/product-reco",
        "title" : "Item in the collection of services registered in develop"
    } , {
        "rel" : "item",
        "href" : "http://example.com/edison-registry/environments/develop/p13n-search",
        "title" : "Another item in the collection of services registered in develop"
    } , {
        "rel" : "item",
        "href" : "http://example.com/edison-registry/environments/develop/campaign-presentation",
        "title" : "Yet another item in the collection of services registered in develop"
    } , {
        "rel" : "self",
        "href" : "http://example.com/edison-registry/environments/develop",
        "title" : "Standard 'self' link-relation type."
    } , {
        "rel" : "collection",
        "href" : "http://example.com/edison-registry/environments,
        "title" : "Standard 'collection' link-relation type: the collection of all known environments."
    }]
}
```

## /environments/develop/product-reco

Information and links about a single service in an environment.

### Mediatype:

application/vnd.otto.edison.links+json

### Methods:

GET, PUT, DELETE

### Example: GET a registry item for a service

GET http://example.com/edison-registry/environments/develop/product-reco

```json
{
    "groups" : ["TeamOne"],
    "expire" : 60,
    "links" : [ {
        "rel" : "http://github.com/otto-de/edison/link-relations/microservice",
        "href" : "http://example.com/myservice",
        "title" : "An example microservice that is registered in this Edison Registry."
    } , {
        "rel" : "self",
        "href" : "http://example.com/edison-registry/environments/develop/product-reco",
        "title" : "Standard 'self' link-relation type."
    } , {
        "rel" : "collection",
        "href" : "http://example.com/edison-registry/environments/develop",
        "title" : "Standard 'collection' link-relation type: the collection of all services in develop."
    }]
}
```

### Example: PUT a service to the registry

The registered service belongs to service group 'TeamOne'. The registration expires after 60 minutes.

PUT http://example.com/edison-registry/environments/develop/product-reco

```json
{
    "groups" : ["TeamOne"],
    "expire" : 60,
    "links" : [ {
        "rel" : "http://github.com/otto-de/edison/link-relations/microservice",
        "href" : "http://example.com/myservice",
        "title" : "An example microservice that is registered in this Edison Registry."
    }]
}
```

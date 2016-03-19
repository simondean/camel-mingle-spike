# camel-mingle-spike

## Timelion

Timelion query:

```
.es('propertyChanges.Status.newValue:New', index='mingle-events-*', metric='cardinality:card.number').cusum(), .es('propertyChanges.Status.newValue:In progress', index='mingle-events-*', metric='cardinality:card.number').cusum(), .es('propertyChanges.Status.newValue:Complete', index='mingle-events-*', metric='cardinality:card.number').cusum()
```

## Useful Links

* http://camel.apache.org/spring-java-config.html

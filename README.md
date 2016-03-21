# camel-mingle-spike

## Installation

The following instructions are for Mac OS X. 

### Update Homebrew

``` shell
$ brew update
```

### Install Elasticsearch

``` shell
$ brew install elasticsearch
$ elasticsearch --version
Version: 2.2.0, Build: 8ff36d1/2016-01-27T13:32:39Z, JVM: 1.8.0_40
```

### Install Kibana

``` shell
$ brew install kibana
$ kibana --version
4.4.2
```

### Install Kopf

``` shell
"$(brew --prefix elasticsearch)/libexec/bin/plugin" install lmenezes/elasticsearch-kopf/2.0
```

### Install Timelion

``` shell
kibana plugin -i kibana/timelion
```

## Run the spike

``` shell
cd mingle-elasticsearch-spike
../gradlew run
```

## Timelion

Timelion query:

```
.es('propertyChanges.Status.newValue:New', index='mingle-events-*', metric='cardinality:card.number').cusum().label('New'), .es('propertyChanges.Status.newValue:In progress', index='mingle-events-*', metric='cardinality:card.number').cusum().label('In progress'), .es('propertyChanges.Status.newValue:Complete', index='mingle-events-*', metric='cardinality:card.number').cusum().label('Complete')
```

## Useful Links

* http://camel.apache.org/spring-java-config.html
* https://www.elastic.co/blog/timelion-timeline
* https://github.com/elastic/timelion
* https://github.com/elastic/timelion/blob/master/FUNCTIONS.md
* https://github.com/lmenezes/elasticsearch-kopf
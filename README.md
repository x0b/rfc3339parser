# RFC 3339 Date Parser
RFC 3339 Date Parser is a Java 7 parser to parse date strings as specified in [RFC 3339](https://tools.ietf.org/html/rfc3339).

## Installation ##
Add jitpack as repository:
```
allprojects {
 repositories {
   ...
   maven { url 'https://jitpack.io' }
  }
}
```
Add module as dependecy:
```
dependencies {
 implementation 'com.github.x0b.rfc3339parser.v1.0-preview' 
}
```

## Usage / Examples ##
```java
// Supports UTC/Zulu time   
Date date1 = Rfc3339.parse("1985-04-12T23:20:50Z");

// Supports arbitrary time offsets
Date date2 = Rfc3339.parse("1996-12-19T16:39:57.123456+01:30");
```

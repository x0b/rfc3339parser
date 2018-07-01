# RFC 3339 Date Parser
[![MIT Licence](https://badges.frapsoft.com/os/mit/mit.png?v=103)](https://github.com/x0b/rfc3339parser/blob/master/LICENSE)
<!---
[![Build Status](https://travis-ci.org/x0b/rfc3339parser.svg?branch=master)](https://travis-ci.org/x0b/rfc3339parser)
[![](https://img.shields.io/codecov/c/github/x0b/rfc3339parser/master.svg)](https://codecov.io/gh/x0b/rfc3339parser)
[![](https://jitpack.io/v/x0b/rfc3339parser.svg)](https://jitpack.io/#x0b/rfc3339parser) 
-->
[![Semver](http://img.shields.io/SemVer/2.0.0.png)](http://semver.org/spec/v2.0.0.html)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](http://makeapullrequest.com)

RFC 3339 Date Parser is a Java 7+ / Android compatible parser to parse date strings as specified in [RFC 3339](https://tools.ietf.org/html/rfc3339).

## Installation ##
Add JCenter as repository in your project/module ```build.gradle```:
```gradle
allprojects {
  repositories {
    ...
    jcenter()
  }
}
```
Add module as dependecy (current version):
```gradle
dependencies {
  implementation 'com.github.x0b:rfc3339parser:2.0.0' 
}
```
For versions **1.x**
```gradle
dependencies {
  implementation 'com.github.x0b:rfc3339parser:1.1.4' 
}
```
## Usage / Examples ##
```java
Rfc3339Parser parser = new Rfc3339Strict();

// Identify if a string is a RFC 3339 date
String timestamp = "...";
if(parser.isValid(timestamp.trim())) {
    // time stamp is probably rfc 3339 formatted
}

// Supports UTC/Zulu time and arbitrary time offsets
Date date1 = parser.parse("1985-04-12T23:20:50Z");
Date date2 = parser.parse("1996-12-19T16:39:57.123456+01:30");

// Get time zone information
TimeZone timeZone = parser.parseTimezone("1996-12-19T16:39:57.123456+01:30")

// Get a Calendar from date string.
Calendar calendar = parser.parseCalendar("1996-12-19T16:39:57.123456+01:30");

// Get a arbitrary-precision time stamp
BigDecimal timestamp = parser.parsePrecise("1996-12-19T16:39:57.123456789Z");
```

## Implementation Limitations ##
* Fractional second precision is limited to millisecond precision (3 digits). Any further digits are not supported by ```java.util.Date```. To retrieve more precise time stamps use ```parsePrecise(...)```.
* Dates returned by the main ```parse(...)``` function do not contain a time zone and will be formatted according to default Locale and TimeZone. Use ```parseCalendar(...)``` if the time strings own time zone is required.
* Java's ``Date`` and ``Calendar`` classes do not recognise leap seconds. Since these are defined in RFC 3339, the time stamp  ```2016-12-31T23:59:60Z``` is equivalent to ```2017-01-01T00:00:00Z```. Conversely, a time stamp of a second which was skipped due to a (theoretical) negative leap second would not be recognised as invalid, which is an accepted standard deviance due to the impossibility of knowing leap seconds in advance. Future versions may include functionality to test the supplied input for leap seconds prior to the latest update.
* If UTC time is known yet local time is unknown RFC 3339 allows this to be signaled as ```-00:00``` without expressing a preference for UTC. Thus, this implementation treats this special time zone as a form of UTC. <br />The resulting time zone will identify using the common convention of 
```java
TimeZone.getID().equals("Etc/Unknown")
```

## Versions ##
### 1.x ###
The initial implementation as released as 1.x and used a mostly ```SimpleDateFormatter``` based parsing logic with additional custom parsing on top. However, due to the inflexibility of this approach on Java 7, this required manual adjustments and was not very performant. It is recommended to use version 2+ since 1.x is no longer maintained.

### 2.x ###
For the 2.0 release, the implementation was split into two parts:
* A slightly modified original version as ```Rfc3339Lenient``` 
* A partially reimplemented version as ```Rfc3339Strict```

Strict was initially planned to close gaps in the original versions validation which caused a performance penalty of 20-30%. However, this validation required a fast parsing method which has now been adapted to be used for ```parse(...)``` and ```parseCalendar(...)```. **It is recommended to use Strict** because it is generally **2-2.5 times faster.**
## Contributing ##
* Feel free to open an issue if you spot any specification deviance (or any implementation bug)
* Pull requests are welcome

## Completeness ##
* The implementation of strict completely validates the time stamp format as specified by the ABNF. However, there may be edge cases of valid time stamps that are decoded into a wrong value. Please open an issue if you spot such a case.
* Building and testing for different platforms is currently lacking.

# RFC 3339 Date Parser
[![MIT Licence](https://badges.frapsoft.com/os/mit/mit.png?v=103)](https://github.com/x0b/rfc3339parser/blob/master/LICENSE)
[![Build Status](https://travis-ci.org/x0b/rfc3339parser.svg?branch=master)](https://travis-ci.org/x0b/rfc3339parser)
[![](https://img.shields.io/codecov/c/github/x0b/rfc3339parser/master.svg)](https://codecov.io/gh/x0b/rfc3339parser)
[![](https://jitpack.io/v/x0b/rfc3339parser.svg)](https://jitpack.io/#x0b/rfc3339parser) 
[![Semver](http://img.shields.io/SemVer/2.0.0.png)](http://semver.org/spec/v2.0.0.html)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](http://makeapullrequest.com)

RFC 3339 Date Parser is a Java 7 compatible parser to parse date strings as specified in [RFC 3339](https://tools.ietf.org/html/rfc3339).

## Installation ##
Add jitpack as repository in your project/module ```build.gradle```:
```gradle
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```
Add module as dependecy:
```gradle
dependencies {
  implementation 'com.github.x0b.rfc3339parser:1.1.0' 
}
```
## Usage / Examples ##
```java
// Supports UTC/Zulu time and arbitrary time offsets
Date date1 = Rfc3339.parse("1985-04-12T23:20:50Z");
Date date2 = Rfc3339.parse("1996-12-19T16:39:57.123456+01:30");

// Get time zone information
TimeZone timeZone = Rfc3339.parseTimezone("1996-12-19T16:39:57.123456+01:30")

// Get a Calendar from date string.
Calendar calendar = Rfc3339.parseCalendar("1996-12-19T16:39:57.123456+01:30");

// Get a arbitrary-precision time stamp
BigDecimal timestamp = Rfc3339.parsePrecise("1996-12-19T16:39:57.123456789Z");
```

## Limitations ##
* Fractional second precision is limited to 3 digits. Any further digits are not supported by ```java.util.Date```. To retrieve more precise time stamps use ```parsePrecise(...)```.
* Dates returned by the main ```parse(...)``` function do not contain a time zone and will be formatted according to default Locale and TimeZone. Use ```parseCalendar(...)``` if the time strings own time zone is required.

## Contributing ##
* Feel free to open an issue if you spot any specification deviance (or any implementation bug)
* Pull requests are welcome

vdm-reader
==========

A sample Groovy script for ISIMA students (http://www.isima.fr)

# About
This is a sample application aimed to getting started with Groovy.

The goal of the [vdm-reader](https://github.com/danielpetisme/vdm-reader/) application is to read the latest [viedemerde](http://www.viedemerde.fr/). To do so,
the application poll [viedemerde RSS feed](http://feeds.uri.lv/viedemerde) and parse the XML stream to get the entries. To simulate a cache behavior, only the newest
stories are added to a local storage.
Then randomly, a viedemerde story is picked and written into an output file.

# Structure
## Groovy
In the [groovy package](https://github.com/danielpetisme/vdm-reader/tree/master/src/main/groovy/fr/isima/devweb/vdmreader/groovy), you will found the Groovy way to implement the
application. It fits in a 101 lines long simple Groovy script (we could call it Groovy [101](http://en.wikipedia.org/wiki/101_(term)).
Inside that script you can found a
* *def* usage
* *GString*s
* *POGO* example
* list, map usage
* XML Parsing
* Groovy operators like _*._
* Operator overloading (+= on a map)
* closures (used with _findAll_ or _inject_
* Duck Typing (*as* usage)
* Anonymous class substitute
* File handling
* etc

## Java
In the [java package](https://github.com/danielpetisme/vdm-reader/tree/master/src/main/java/fr/isima/devweb/vdmreader/java), you will found the Java way to also implement the application
but in a classic Java way. *It's a quick & dirty implementation!!*. I tried as much as possible to keep the same organization as the Groovy script (variables & methods name).

## Test
In the [groovy test package](https://github.com/danielpetisme/vdm-reader/tree/master/src/test/groovy/fr/isima/devweb/vdmreader/groovy/test), you will found an example of [Spock](http://code.google.com/p/spock/), an excellent test/specification framework.
The framework embeds a powerful DSL with match my favorite testing approach (*given*,*when*,*then*).


# Results
This the console output of the Groovy version:
>

# Disclaimer
This project aims to present some Groovy usage a tricks. I did not try to do the perfect Groovy/Java code. I guess (and I'm sure) there are plenty of other way to achieve the application goal (with some well-known libraries)
and cleaner ways also (a proper exception handling for instance). My goal, I repeat, is just to present Groovy and compare it with a classic Java approach.

# Licence
>The MIT License

 Copyright (c) 2012, Daniel Petisme

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.

